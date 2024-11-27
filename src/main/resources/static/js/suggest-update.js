function editTitle() {

    document.getElementById('detail-title').style.display = 'none';
    document.getElementById('titleInput').style.display = 'block';

    document.getElementById('titleInput').value = document.getElementById('detail-title').textContent;

    document.getElementById('editTitleButton').style.display = 'none';
    document.getElementById('saveTitleButton').style.display = 'inline-block';
}

function saveTitle() {
    const newTitle = document.getElementById('titleInput').value.trim();

    if (newTitle === '') {
        alert('제목을 입력해주세요.');
        return;
    }

    document.getElementById('detail-title').textContent = newTitle;

    document.getElementById('titleInput').style.display = 'none';
    document.getElementById('detail-title').style.display = 'block';

    document.getElementById('saveTitleButton').style.display = 'none';
    document.getElementById('editTitleButton').style.display = 'inline-block';

}

function editContent() {
    document.getElementById('detail-content').style.display = 'none';
    document.getElementById('contentTextarea').style.display = 'block';

    document.getElementById('contentTextarea').value = document.getElementById('detail-content').textContent;

    document.getElementById('editContentButton').style.display = 'none';
    document.getElementById('saveContentButton').style.display = 'inline-block';
}

function saveContent() {
    const newContent = document.getElementById('contentTextarea').value.trim();

    if (newContent === '') {
        alert('내용을 입력해주세요.');
        return;
    }

    document.getElementById('detail-content').textContent = newContent;

    document.getElementById('contentTextarea').style.display = 'none';
    document.getElementById('detail-content').style.display = 'block';

    document.getElementById('saveContentButton').style.display = 'none';
    document.getElementById('editContentButton').style.display = 'inline-block';

}

function uploadThumbnail() {
    const input = document.getElementById('thumbnailUpload');
    const thumbnailElement = document.getElementById('detailThumbnail');

    const file = input.files[0];
    if (!file) {
        alert('썸네일 이미지를 선택해주세요.');
        return;
    }

    if (!file.type.startsWith('image/')) {
        alert('이미지 파일만 업로드할 수 있습니다.');
        input.value = '';
        return;
    }

    if (file) {
        // 이미지 미리보기 업데이트
        const reader = new FileReader();
        reader.onload = function(e) {
            thumbnailElement.src = e.target.result;
        };
        reader.readAsDataURL(file);

    }
}

function goUpdate() {
    const id = getIdFromURL(); // URL에서 ID를 가져오는 함수
    if (!id) {
        alert('유효하지 않은 요청입니다.');
        return;
    }

    // 수정된 제목과 설명 가져오기
    const title = document.getElementById('detail-title').textContent.trim();
    const description = document.getElementById('detail-content').textContent.trim();

    // 유효성 검사
    if (title === '' || description === '') {
        alert('제목과 내용을 모두 입력해주세요.');
        return;
    }

    // FormData 생성
    const formData = new FormData();
    formData.append('title', title);
    formData.append('description', description);

    // 썸네일 사진
    const thumbnailInput = document.getElementById('thumbnailUpload');
    if (thumbnailInput.files[0]) {
        formData.append('thumbnail', thumbnailInput.files[0]);
    }

    // 선택지 사진
    const choiceImageWrappers = document.querySelectorAll('.choice-image-wrapper');
    choiceImageWrappers.forEach((wrapper, index) => {
        const input = wrapper.querySelector('input[type="file"]');
        const choiceImageId = wrapper.dataset.choiceImageId;
        formData.append(`choiceImages[${index}].id`, choiceImageId); // 항상 id를 추가
        if (input.files[0]) {
            formData.append(`choiceImages[${index}].choiceImage`, input.files[0]);
        }
    });

   // FormData 내용 확인
    for (let pair of formData.entries()) {
        console.log(`${pair[0]}:`, pair[1]);
    }

    fetch(`/api/v1/topic-suggests/${id}`, {
        method: 'PATCH',
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('업데이트 중 오류 발생');
        }
        return response.json();
    })
    .then(data => {
        alert('수정이 완료되었습니다.');
        location.reload();
    })
    .catch(error => {
        console.error('Error updating topic suggest:', error);
        alert('수정 중 오류가 발생했습니다. 다시 시도해주세요.');
    });
}

// URL에서 ID를 추출하는 함수
function getIdFromURL() {
    const pathParts = window.location.pathname.split('/');
    const id = pathParts[pathParts.length - 1];
    return id;
}
