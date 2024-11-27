document.addEventListener('DOMContentLoaded', () => {
    initializeChoiceImageFields();
    sendForm();
});

function sendForm() {

  const form = document.getElementById('suggest-form');

  form.addEventListener('submit', (e) => {
    e.preventDefault();

   const formData = new FormData(form);

    const submitBtn = form.querySelector('button[type="submit"]');

    submitBtn.disabled = true;

    fetch('/api/v1/topic-suggests', {
      method: 'POST',
      body: formData
    })
    .then(response => {
      if(!response.ok) {
         if(response.status === 409) {
            alert('이미 존재하는 문의 제목입니다.')
            throw new Error(`Error submitting form! status : ${response.status}`);
         }
        alert('에러가 발생했습니다')
        throw new Error(`Error submitting form! status : ${response.status}`);
      }
      return response.json();
    })
    .then(data => {
      form.reset();
      alert('성공적으로 문의가 완료되었습니다')
      window.location.href = '/';
    })
    .finally(() => {
      submitBtn.disabled = false;

    })
  })

}

function initializeChoiceImageFields() {
    const addChoiceImageBtn = document.getElementById('add-choice-image-btn');
    const choiceImagesContainer = document.getElementById('choice-images-container');

    addChoiceImageBtn.addEventListener('click', () => {
        const currentChoiceImages = choiceImagesContainer.querySelectorAll('.choice-image-group').length;
        if (currentChoiceImages < 4) {
            addChoiceImageField(choiceImagesContainer);
            updateRemoveButtons(choiceImagesContainer);
            updateAddButtonState(choiceImagesContainer);
        }
    });

    // 최소 2개의 선택지 이미지 필드만 제거 버튼 비활성화
    updateRemoveButtons(choiceImagesContainer);
    updateAddButtonState(choiceImagesContainer);
}

function addChoiceImageField(container) {
    const choiceImageGroup = document.createElement('div');
    choiceImageGroup.className = 'input-group mb-2 choice-image-group';

    const fileInput = document.createElement('input');
    fileInput.type = 'file';
    fileInput.className = 'form-control choice-image-input';
    fileInput.name = 'choiceImages';
    fileInput.accept = 'image/*';
    fileInput.required = true;

    const removeBtn = document.createElement('button');
    removeBtn.type = 'button';
    removeBtn.className = 'btn btn-danger remove-choice-image-btn';
    removeBtn.textContent = '제거';

    removeBtn.addEventListener('click', () => {
        container.removeChild(choiceImageGroup);
        updateRemoveButtons(container);
        updateAddButtonState(container);
    });

    choiceImageGroup.appendChild(fileInput);
    choiceImageGroup.appendChild(removeBtn);
    container.appendChild(choiceImageGroup);
}
function updateRemoveButtons(container) {
    const choiceImageGroups = container.querySelectorAll('.choice-image-group');
    if (choiceImageGroups.length <= 2) {
        choiceImageGroups.forEach(group => {
            const removeBtn = group.querySelector('.remove-choice-image-btn');
            if (removeBtn) { // 제거 버튼이 존재하는지 확인
                removeBtn.disabled = true;
            }
        });
    } else {
        choiceImageGroups.forEach(group => {
            const removeBtn = group.querySelector('.remove-choice-image-btn');
            if (removeBtn) { // 제거 버튼이 존재하는지 확인
                removeBtn.disabled = false;
            }
        });
    }
}

function updateAddButtonState(container) {
    const addChoiceImageBtn = document.getElementById('add-choice-image-btn');
    const currentChoiceImages = container.querySelectorAll('.choice-image-group').length;
    if (currentChoiceImages >= 4) {
        addChoiceImageBtn.disabled = true;
    } else {
        addChoiceImageBtn.disabled = false;
    }
}

function addInitialChoiceImageFields(container) {
    for (let i = 0; i < 2; i++) {
        addChoiceImageField(container);
    }
    updateRemoveButtons(container);
    updateAddButtonState(container);
}