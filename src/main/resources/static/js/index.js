document.addEventListener('DOMContentLoaded', () => {
    loadTopics();
});


/**
 * Topics 목록 api 요청
 */
function loadTopics(){
  fetch('/api/v1/topics')
    .then(response => {
      if(!response.ok) {
        throw new Error(`Error fetching topic list data! status: ${response.status}`);
      }
        return response.json();
    })
     .then(data => {
        renderTopicList(data);
        // 토픽 ID 추출 후 선택지 데이터 요청
        const topicIds = data.map(topic => topic.id);
        loadChoicesForTopics(topicIds);
     })
     .catch(error => console.error('Error fetching topics:', error));
}

/** 
* Topic 정보 랜더링
*/
function renderTopicList(topics) {
  const container = document.getElementById('topics-container');
  container.innerHTML = '';

  topics.forEach(topic => {
      const col = document.createElement('div');
      col.className = 'col-lg-4 col-md-6 col-sm-12';

      const card = document.createElement('div');
      card.className = 'card h-100';
      card.setAttribute('data-topic-id', topic.id); // 토픽 ID를 데이터 속성으로 설정

      const img = document.createElement('img');
      img.className = 'card-img-top card-img-fixed';
      img.src = topic.thumbnailImageUrl || 'https://t3.ftcdn.net/jpg/01/35/88/24/360_F_135882430_6Ytw6sJKC5jg8ovh18XjAHuMXcS7mlai.jpg';
      img.alt = topic.title;

      const cardBody = document.createElement('div');
      cardBody.className = 'card-body d-flex flex-column';

      // Topic 제목
      const title = document.createElement('h5');
      title.className = 'card-title text-center';
      title.textContent = topic.title;

      // 투표 버튼
      const detailButton = document.createElement('a');
      detailButton.href = `/topics/${topic.slug}`; // 상세 페이지 URL (필요 시 수정)
      detailButton.className = 'btn btn-primary mt-auto';
      detailButton.textContent = '투표하기';

      // 선택지 컨테이너 추가
      const choicesContainer = document.createElement('div');
      choicesContainer.className = 'choices-container mt-2'; // 선택지 표시를 위한 컨테이너

      // 카드 구성
      cardBody.appendChild(title);
      cardBody.appendChild(choicesContainer);
      cardBody.appendChild(detailButton);

      card.appendChild(img);
      card.appendChild(cardBody);
      col.appendChild(card);
      container.appendChild(col);
  });
}

/** 
* Topic에 해당하는 투표 정보 결과 요청
*/
function loadChoicesForTopics(topicIds) {
  if (topicIds.length === 0) return;

  // 토픽 ID를 쿼리 파라미터로 변환
  const params = topicIds.map(id => `topicIds=${encodeURIComponent(id)}`).join('&');
  const url = `/api/v1/choices/by-topics?${params}`;

  // 선택지 데이터 요청
  fetch(url)
      .then(response => {
          if (!response.ok) {
              throw new Error(`Failed to fetch choices: ${response.status}`);
          }
          return response.json();
      })
      .then(choices => {
          renderChoices(choices);
      })
      .catch(error => {
          console.error('Error fetching choices:', error);
      });
}

/** 
* Topic에 해당하는 투표 정보 결과 랜더링
*/
function renderChoices(choices) {
  choices.forEach(choice => {
      // 선택지에 해당하는 토픽 카드 찾기
      const topicCard = document.querySelector(`[data-topic-id="${choice.topicId}"]`);
      if (topicCard) {
          const choicesContainer = topicCard.querySelector('.choices-container');
          if (choicesContainer) {
              const choiceElement = document.createElement('div');
              choiceElement.className = 'd-flex justify-content-between align-items-center mb-2';
              choiceElement.textContent = `${choice.name}: ${choice.voteCount}표`;
              choicesContainer.appendChild(choiceElement);
          }
      }
  });
}

