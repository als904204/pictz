document.addEventListener('DOMContentLoaded', () => {
    loadTopicSuggestDetail();
});

function loadTopicSuggestDetail() {
    const errorElement = document.getElementById('error-message');
    const detailContainer = document.getElementById('detail-container');

    const pathParts = window.location.pathname.split('/');
    const id = pathParts[pathParts.length - 1];

    if (!id) {
        showError();
        return;
    }

    // 상세 정보 불러오기
    fetch(`/api/v1/topic-suggests/${id}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(suggest => {
            renderDetail(suggest);
            detailContainer.style.display = 'block';
        })
        .catch(error => {
            console.error('Error loading topic suggest detail:', error);
            showError();
        });
}

function renderDetail(suggest) {
    document.getElementById('detail-title').textContent = suggest.title;
    document.getElementById('detail-content').textContent = suggest.content;
    document.getElementById('detail-status').textContent = suggest.status;

    const thumbnailElement = document.getElementById('detailThumbnail');
    thumbnailElement.src = suggest.thumbnailUrl;
    thumbnailElement.alt = '썸네일 이미지';

    // 선택지 이미지 컨테이너 초기화
    const detailImagesContainer = document.getElementById('detailImages');
    detailImagesContainer.innerHTML = '';

    suggest.choiceImages.forEach((choiceImage, index) => {
                // 이미지와 파일 이름을 감싸는 래퍼 div 생성
                const wrapper = document.createElement('div');
                wrapper.className = 'choice-image-wrapper';
                wrapper.dataset.choiceImageId = choiceImage.id;

                // 이미지 요소 생성
                const img = document.createElement('img');
                img.src = choiceImage.imageUrl;
                img.alt = `선택지 이미지 ${index + 1}`;
                img.className = 'choice-image';

                // 파일 이름 요소 생성
                const filename = document.createElement('div');
                filename.className = 'choice-image-filename';
                filename.textContent = choiceImage.fileName;

                // 파일 업로드 input 생성
                const input = document.createElement('input');
                input.type = 'file';
                input.accept = 'image/*';
                input.className = 'form-control mt-2 edit-function';

                input.style.display = suggest.status === "거부됨" ? 'block' : 'none';

                input.dataset.choiceImageId = choiceImage.id;
                // 파일 업로드 input에 이벤트 리스너 추가
                input.addEventListener('change', function(event) {
                    const file = event.target.files[0];
                    if (file) {
                        // 이미지 미리보기 업데이트
                        const reader = new FileReader();
                        reader.onload = function(e) {
                            img.src = e.target.result;
                        };
                        reader.readAsDataURL(file);

                        // 파일 이름 업데이트
                        filename.textContent = file.name;

                    }
                });

                // 래퍼에 이미지와 파일 이름 추가
                wrapper.appendChild(img);
                wrapper.appendChild(filename);
                wrapper.appendChild(input);
                // 선택지 이미지 컨테이너에 래퍼 추가
                detailImagesContainer.appendChild(wrapper);
    });

    if (suggest.status === "거부됨") {
        showEditFunctions(true);
        document.getElementById('updateButtonContainer').style.cssText = 'display: flex !important';
    } else {
        showEditFunctions(false);
        document.getElementById('updateButtonContainer').style.cssText = 'display: none !important';
    }

    // 상태에 따라 배지 색상 설정
    const statusElement = document.getElementById('detail-status');
    const statusColorClass = getStatusColor(suggest.status);
    statusElement.classList.add(statusColorClass, 'text-white');

    if (suggest.rejectReason) {
        const rejectReasonSection = document.getElementById('rejectReason');
        const rejectReasonText = document.getElementById('detailRejectReason');
        rejectReasonText.textContent = suggest.rejectReason;
        rejectReasonSection.style.display = 'block';
    }
}



function showEditFunctions(show) {
    const editButtons = document.querySelectorAll('.edit-button');
    const saveButtons = document.querySelectorAll('.save-button');

    editButtons.forEach(button => {
        button.style.display = show ? 'inline-block' : 'none';
    });

    saveButtons.forEach(button => {
        button.style.display = 'none';
    });
}

function showError() {
    document.getElementById('error-message').style.display = 'block';
    document.getElementById('detail-container').style.display = 'none';
}

