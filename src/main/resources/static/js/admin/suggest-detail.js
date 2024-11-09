document.addEventListener('DOMContentLoaded', () => {
    const suggestId = getSuggestIdFromURL();
    if (suggestId) {
        loadSuggestDetail(suggestId);
        approvedBtn(suggestId);
        rejectedBtn(suggestId);
    } else {
        alert('유효한 토픽 문의 ID가 없습니다.');
    }
});

function getSuggestIdFromURL() {
    const pathSegments = window.location.pathname.split('/');
    const idIndex = pathSegments.indexOf('suggests') + 1;
    if (idIndex > 0 && idIndex < pathSegments.length) {
        return pathSegments[idIndex];
    }
    return null;
}

// 문의 정보를 로드하고 화면에 표시하는 함수
function loadSuggestDetail(id) {
    fetch(`/api/v1/admin/topic-suggests/${id}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(suggest => {
            renderSuggestDetail(suggest);
        })
        .catch(error => {
            console.error('Error loading suggest detail:', error);
            alert('Suggest 상세 정보를 불러오는 데 실패했습니다.');
        });
}

function renderSuggestDetail(suggest) {
    document.getElementById('detailTitle').textContent = suggest.title;
    document.getElementById('detailNickname').textContent = suggest.nickname;
    document.getElementById('detailCreatedAt').textContent = formatDate(suggest.createdAt);

    const statusElement = document.getElementById('detailStatus');
    statusElement.textContent = suggest.status;
    updateStatusBadgeColor(statusElement, suggest.status);

    document.getElementById('detailDescription').textContent = suggest.description;
    document.getElementById('detailThumbnail').src = suggest.thumbnailUrl;

    const detailImagesContainer = document.getElementById('detailImages');
    detailImagesContainer.innerHTML = '';
    suggest.choiceImageUrls.forEach((url, index) => {
        const img = document.createElement('img');
        img.src = url;
        img.alt = `선택지 이미지 ${index + 1}`;
        img.className = 'choice-image';
        detailImagesContainer.appendChild(img);
    });

    // 상태에 따라 버튼 비활성화
    const approveBtn = document.getElementById('approvedBtn');
    const rejectBtn = document.getElementById('rejectedBtn');
    if (suggest.status === 'APPROVED') {
        approveBtn.disabled = true;
    }
    if (suggest.status === 'REJECTED') {
        rejectBtn.disabled = true;
    }

  if (suggest.rejectReason) {
      const rejectReasonSection = document.getElementById('rejectReasonSection');
      const rejectReasonText = document.getElementById('detailRejectReason');
      rejectReasonText.textContent = suggest.rejectReason;
      rejectReasonSection.style.display = 'block';
  }
}

function approvedBtn(suggestId) {
   const approvedBtn = document.getElementById('approvedBtn');
   approvedBtn.addEventListener('click', () => {
          const isConfirmed = confirm('정말로 승인하시겠습니까?');
          if (isConfirmed) {
              fetch(`/api/v1/admin/topic-suggests/${suggestId}`, {
                method: 'PATCH',
                headers: {
                  'Content-Type': 'application/json',
                },
                body: JSON.stringify({ status: 'APPROVED' })
              })
              .then(response => {
                if(!response.ok) {
                  throw new Error('승인 처리 중 오류가 발생했습니다.');
                }
                return response.json();
              })
              .then(data => {
                  alert('성공적으로 승인되었습니다.');
                  window.location.reload();
              })
              .catch(error => {
                  console.error('Error:', error);
                  alert('승인 처리 중 오류가 발생했습니다.');
              });
          }
      });
}

function rejectedBtn(suggestId) {
    const rejectBtn = document.getElementById('rejectedBtn');
    if (!rejectBtn) {
        console.error('Reject button not found.');
        return;
    }
    rejectBtn.addEventListener('click', () => {
        const rejectModal = new bootstrap.Modal(document.getElementById('rejectModal'));
        rejectModal.show();
    });

    const rejectForm = document.getElementById('rejectForm');
    if (!rejectForm) {
        console.error('Reject form not found.');
        return;
    }
    rejectForm.addEventListener('submit', (event) => {
        event.preventDefault();
        const rejectReason = document.getElementById('rejectReason').value.trim();
        if (!rejectReason) {
            alert('거부 이유를 입력해주세요.');
            return;
        }

        fetch(`/api/v1/admin/topic-suggests/${suggestId}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ status: 'REJECTED', reason: rejectReason })
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('거부 처리 중 오류가 발생했습니다.');
            }
            return response.json();
        })
        .then(data => {
            alert('성공적으로 거부되었습니다.');
            window.location.reload();
        })
        .catch(error => {
            console.error('Error:', error);
            alert('거부 처리 중 오류가 발생했습니다.');
        });
    });
}


function updateStatusBadgeColor(element, status) {
    element.classList.remove('bg-primary', 'bg-success', 'bg-warning', 'bg-danger');

    switch(status.toLowerCase()) {
        case 'pending':
            element.classList.add('bg-warning');
            break;
        case 'approved':
            element.classList.add('bg-success');
            break;
        case 'rejected':
            element.classList.add('bg-danger');
            break;
        default:
            element.classList.add('bg-primary');
    }
}

