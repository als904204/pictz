document.addEventListener('DOMContentLoaded', () => {
    sendForm();
});

function sendForm() {

  const form = document.getElementById('suggest-form');

  form.addEventListener('submit', (e) => {
    e.preventDefault();

   const formData = new FormData(form);

    const submitBtn = form.querySelector('button[type="submit"]');

    submitBtn.disabled = true;

    fetch('/api/v1/topic_suggests', {
      method: 'POST',
      body: formData
    })
    .then(response => {
      if(!response.ok) {
        alert('에러가 발생했습니다')
        throw new Error(`Error submitting form! status : ${response.status}`);
      }
      return response.json();
    })
    .then(data => {
      form.reset();
      alert('성공!')
    })
    .finally(() => {
      submitBtn.disabled = false;
    })
  })

}