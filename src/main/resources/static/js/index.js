document.addEventListener('DOMContentLoaded', () => {
    loadTopics();
    setupSortButtons();
    setupPagination();
    setupTotalCountPolling();
});

let currentSortBy = 'LATEST';
let currentPage = 0;

/**
 * Topics 목록 api 요청
 */
function loadTopics(){
    fetch(`/api/v1/topics?sortBy=${currentSortBy}&page=${currentPage}`)
        .then(response => {
            if(!response.ok) {
                throw new Error(`Error fetching topic list data! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            renderTopicList(data.content);
            updatePagination(data.currentPage, data.totalPages);
        })
        .catch(error => console.error('Error fetching topics:', error));
}

/**
 *  토픽 총 투표 수 일정시간마다 요청
 */
function loadTopicTotalCounts() {
  fetch(`/api/v1/topics/counts?page=${currentPage}`)
    .then(response => {
      if(!response.ok) {
         throw new Error(`Error fetching counts! status: ${response.status}`);
      }
      return response.json();
    })
    .then(data => {
      data.forEach(count => {
        const totalCountElement = document.querySelector(`h6[data-topic-id='${count.id}']`);
        if (totalCountElement) {
            totalCountElement.textContent = `${count.totalCount}`;
        }
      });
    })
    .catch(error => console.error('Error fetching total counts:', error));
}

function setupTotalCountPolling() {
    setInterval(loadTopicTotalCounts, 5000);
}

/**
* Topic 정보 랜더링
*/
function renderTopicList(topics) {
  const container = document.getElementById('topics-container');
  container.innerHTML = '';

  topics.forEach(topic => {
      const col = document.createElement('div');
      col.className = 'col-lg-4 col-md-4 col-sm-12';

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


      let totalCount = document.createElement('h6');
      totalCount.className = 'card-title text-center text-secondary';
      totalCount.setAttribute('data-topic-id', topic.id);
      totalCount.textContent = topic.totalCount;

      // 투표 버튼
      const detailButton = document.createElement('a');
      detailButton.href = `/topics/${topic.slug}`; // 상세 페이지 URL (필요 시 수정)
      detailButton.className = 'btn btn-primary mt-auto';
      detailButton.textContent = '투표하러가기';

      // 선택지 컨테이너 추가
      const choicesContainer = document.createElement('div');
      choicesContainer.className = 'choices-container mt-2'; // 선택지 표시를 위한 컨테이너

      // 카드 구성
      cardBody.appendChild(title);
      cardBody.appendChild(totalCount);
      cardBody.appendChild(choicesContainer);
      cardBody.appendChild(detailButton);

      card.appendChild(img);
      card.appendChild(cardBody);
      col.appendChild(card);
      container.appendChild(col);
  });
}


function setupSortButtons() {
  const sortButtons = document.querySelectorAll('.sort-session .btn');
  sortButtons.forEach(button => {
    button.addEventListener('click', () => {
      // 현재 활성화된 버튼 비활성화
      sortButtons.forEach(btn => btn.classList.remove('active', 'btn-primary'));
      sortButtons.forEach(btn => btn.classList.add('btn-light', 'border-primary', 'text-primary'));

       // 클릭한 버튼 활성화
      button.classList.add('active', 'btn-primary');
      button.classList.remove('btn-light', 'border-primary', 'text-primary');

      // 정렬 기준 설정
      currentSortBy = button.textContent === '인기순' ? 'POPULAR' : 'LATEST';

      // 페이지 초기화
      currentPage = 0;

      // 데이터 다시 로드
      loadTopics();

    })
  })
}

/**
 * 페이지 네비게이션 이벤트 리스너 설정
 */
function setupPagination() {
    const pagination = document.querySelector('.pagination');
    pagination.querySelectorAll('a').forEach(link => {
        link.addEventListener('click', (event) => {
            event.preventDefault();
            const text = event.target.textContent;

            if (text === 'Previous') {
                if (currentPage > 0) {
                    currentPage -= 1;
                    loadTopics();
                }
            } else if (text === 'Next') {
                if (currentPage < totalPages - 1) { // 'Next' 버튼 클릭 시 페이지 범위 체크
                    currentPage += 1;
                    loadTopics();
                }
            } else {
                const selectedPage = parseInt(text) - 1;
                if (!isNaN(selectedPage)) {
                    currentPage = selectedPage;
                    loadTopics();
                }
            }
        });
    });
}

/**
 * 페이징 정보 기반으로 페이지 네비게이션 UI 업데이트
 */
function updatePagination(currentPage, totalPages) {
    const pagination = document.querySelector('.pagination');
    pagination.innerHTML = ''; // 기존 페이지 버튼 초기화

    // 'Previous' 버튼 생성
    const prevClass = currentPage === 0 ? 'page-item disabled' : 'page-item';
    const prevItem = document.createElement('li');
    prevItem.className = prevClass;
    const prevLink = document.createElement('a');
    prevLink.className = 'page-link';
    prevLink.href = '#';
    prevLink.textContent = 'Previous';
    prevItem.appendChild(prevLink);
    pagination.appendChild(prevItem);

    // 페이지 번호 버튼 생성
    for (let i = 1; i <= totalPages; i++) {
        const pageClass = (i - 1 === currentPage) ? 'page-item active' : 'page-item';
        const pageItem = document.createElement('li');
        pageItem.className = pageClass;
        const pageLink = document.createElement('a');
        pageLink.className = 'page-link';
        pageLink.href = '#';
        pageLink.textContent = i;
        pageItem.appendChild(pageLink);
        pagination.appendChild(pageItem);
    }

    // 'Next' 버튼 생성
    const nextClass = currentPage >= totalPages - 1 ? 'page-item disabled' : 'page-item';
    const nextItem = document.createElement('li');
    nextItem.className = nextClass;
    const nextLink = document.createElement('a');
    nextLink.className = 'page-link';
    nextLink.href = '#';
    nextLink.textContent = 'Next';
    nextItem.appendChild(nextLink);
    pagination.appendChild(nextItem);

    // 이벤트 리스너 재설정
    setupPagination(); // 페이징 UI 업데이트 후 이벤트 리스너 재설정
}