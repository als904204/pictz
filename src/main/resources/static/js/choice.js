document.addEventListener('DOMContentLoaded', () => {
    loadChoices();
});

function loadChoices(){
    const path = window.location.pathname;
    const slug = path.split("/").pop();

    // 로딩 인디케이터 표시
    document.getElementById('loading').style.display = 'block';
    document.getElementById('error-message').style.display = 'none';

    fetch(`/api/v1/topics/${slug}/choices`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Error fetching choices data! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            document.getElementById('loading').style.display = 'none';
            renderChoices(data);
            setupChoicesPolling(data);
        })
        .catch(error => {
            console.error('Error fetching choices:', error);
            document.getElementById('loading').style.display = 'none';
            document.getElementById('error-message').style.display = 'block';
        });
}

/**
 * 선택지별 총 투표 수 일정시간마다 요청
 */
function loadChoicesTotalCounts(choiceIds) {
  fetch(`/api/v1/choices/count?choiceIds=${choiceIds.join(',')}`)
    .then(response => {
      if(!response.ok) {
        throw new Error(`Error fetching choices counts! status: ${response.status}`);
      }
      return response.json();
    })
    .then(data => {
      data.forEach(count => {
         const voteCountElement = document.getElementById(`vote-count-${count.choiceId}`);
            if (voteCountElement) {
                const queuedVotes = voteQueue[count.choiceId] || 0;
                voteCountElement.textContent = count.voteCount + queuedVotes;
            }
      });
    })
    .catch(error => console.error('Error fetching choices counts:', error));
}

function renderChoices(choices) {
    const choicesContainer = document.getElementById('choices');
    choicesContainer.innerHTML = ''; // 기존 내용 초기화


    const itemCount = choices.length;

     // 선택지의 개수에 따라 컬럼 클래스 설정
    let colClass = 'col-12 col-sm-6 mb-4 mt-3 card-hover';


    if (itemCount === 4) {
        colClass = 'col-12 col-sm-6 col-md-4 col-lg-3 mb-4 mt-3 card-hover'; // 4개일 때
    } else if (itemCount === 3) {
        colClass = 'col-12 col-sm-6 col-md-4 mb-4 mt-3 card-hover'; // 3개일 때
    }

    choices.forEach(choice => {
        const colDiv = document.createElement('div');
        colDiv.className = colClass;

        colDiv.style.cursor = 'pointer';

        const cardDiv = document.createElement('div');
        cardDiv.className = 'card shadow-sm';

        const img = document.createElement('img');
        img.src = choice.imageUrl || 'https://t3.ftcdn.net/jpg/01/35/88/24/360_F_135882430_6Ytw6sJKC5jg8ovh18XjAHuMXcS7mlai.jpg';
        img.className = 'card-img-top card-img-fixed';
        img.alt = choice.name;

        const cardBody = document.createElement('div');
        cardBody.className = 'card-body text-center d-flex flex-column';

        const title = document.createElement('h5');
        title.className = 'card-title font-weight-bold h2';
        title.textContent = choice.name;

        const voteCount = document.createElement('p');
        voteCount.className = 'card-text h3';
        voteCount.innerHTML = `<i class="bi bi-hand-thumbs-up"></i> <span id="vote-count-${choice.choiceId}">${choice.voteCount}</span>`;


        cardBody.appendChild(title);
        cardBody.appendChild(voteCount);
        cardDiv.appendChild(img);
        cardDiv.appendChild(cardBody);
        colDiv.appendChild(cardDiv);
        choicesContainer.appendChild(colDiv);

        // 투표
        colDiv.addEventListener('click', () => {
            handleVote(choice.choiceId);
        });
    });
}

// 투표수 Queue
let voteQueue = {};

/**
* 투표 처리
**/
function handleVote(choiceId) {

  /**
  * UI 업데이트
  **/
  const voteCountSpan = document.getElementById(`vote-count-${choiceId}`)
  if(voteCountSpan) {
    voteCountSpan.textContent = parseInt(voteCountSpan.textContent) + 1;
  }

  // add vote to queue
  if(voteQueue[choiceId]) {
    voteQueue[choiceId] += 1;
  } else {
    voteQueue[choiceId] = 1;
  }


  // 디바운스된 전송 호출
  debouncedSendVoteBatch();

  // send to server if more than 50
  if(getTotalQueuedVotes() >= 50) {
    sendVoteBatch();
  }
}

// 클릭 멈추고 0.5초 뒤 전송
const debouncedSendVoteBatch = debounce(sendVoteBatch, 500);

// 현재 큐에 쌓인 총 투표 수를 반환하는 함수
function getTotalQueuedVotes() {
    return Object.values(voteQueue).reduce((a, b) => a + b, 0);
}

// 쌓인 투표 서버에 전송
function sendVoteBatch() {

  // 요청 보낼 객체 생성
  const batch = Object.keys(voteQueue).map(choiceId => ({
      choiceId: parseInt(choiceId),
      count: voteQueue[choiceId]
  }));

  fetch('/api/v1/votes', {
    method: 'POST',
    headers: {
      'Content-Type' : 'application/json'
    },
    body: JSON.stringify(batch)
  })
  .then(response => {
    if(!response.noContent) {
      voteQueue = {};
      throw new Error(`Error sending vote batch! status: ${response.status}`);
    }
    return response.json();
  })
  .then(data => {
    voteQueue = {};
  })
  .catch(error => {
    voteQueue = {};
  })

}

function setupChoicesPolling(choices) {
    const choiceIds = choices.map(choice => choice.choiceId);

    setInterval(() => {
        loadChoicesTotalCounts(choiceIds);
    }, 3000);
}

