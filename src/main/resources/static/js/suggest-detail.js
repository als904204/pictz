// suggest-detail.js

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
    document.getElementById('detail-createdAt').textContent = new Date(suggest.createdAt).toLocaleString();
    document.getElementById('detail-updatedAt').textContent = suggest.updatedAt ? new Date(suggest.updatedAt).toLocaleString() : '없음';
    document.getElementById('detail-nickname').textContent = suggest.nickname;

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

                // 이미지 요소 생성
                const img = document.createElement('img');
                img.src = choiceImage.imageUrl;
                img.alt = `선택지 이미지 ${index + 1}`;
                img.className = 'choice-image';

                // 파일 이름 요소 생성
                const filename = document.createElement('div');
                filename.className = 'choice-image-filename';
                filename.textContent = choiceImage.fileName;

                // 래퍼에 이미지와 파일 이름 추가
                wrapper.appendChild(img);
                wrapper.appendChild(filename);

                // 선택지 이미지 컨테이너에 래퍼 추가
                detailImagesContainer.appendChild(wrapper);
    });

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

function showError() {
    document.getElementById('error-message').style.display = 'block';
    document.getElementById('detail-container').style.display = 'none';
}

