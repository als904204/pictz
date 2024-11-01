document.addEventListener('DOMContentLoaded', () => {
    loadProfile();
    loadInquiries();
});

function loadProfile() {
    const loadingElement = document.getElementById('loading');
    const errorElement = document.getElementById('error-message');

    loadingElement.style.display = 'block';

    fetch('/api/v1/users/profile')
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            document.getElementById('userNickname').textContent = data.nickname;
            loadingElement.style.display = 'none';
        })
        .catch(error => {
            console.error('Error loading profile:', error);
            errorElement.style.display = 'block';
            loadingElement.style.display = 'none';
        });
}

function loadInquiries() {
    const loadingElement = document.getElementById('loading');
    const errorElement = document.getElementById('error-message');
    const inquiryListElement = document.getElementById('inquiryList');
    const emptyMessage = document.getElementById('emptyMessage');

    loadingElement.style.display = 'block';

    fetch('/api/v1/users/inquiries')
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(inquiries => {
            loadingElement.style.display = 'none';

            if (inquiries.length === 0) {
                emptyMessage.style.display = 'block';
                return;
            }

            inquiries.forEach(inquiry => {
                const item = document.createElement('div');
                item.className = 'list-group-item';
                item.innerHTML = `
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="mb-1">${inquiry.title}</h6>
                            <small class="text-muted">
                                <i class="bi bi-clock me-1"></i>${new Date(inquiry.createdAt).toLocaleDateString()}
                            </small>
                        </div>
                        <span class="badge bg-${getStatusColor(inquiry.status)}">${inquiry.status}</span>
                    </div>
                `;
                inquiryListElement.appendChild(item);
            });
        })
        .catch(error => {
            console.error('Error loading inquiries:', error);
            errorElement.style.display = 'block';
            loadingElement.style.display = 'none';
        });
}

function getStatusColor(status) {
    switch (status) {
        case 'PENDING': return 'warning';
        case 'IN_PROGRESS': return 'info';
        case 'COMPLETED': return 'success';
        default: return 'secondary';
    }
}