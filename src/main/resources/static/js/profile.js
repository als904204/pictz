document.addEventListener('DOMContentLoaded', () => {
    loadProfile();
    loadSuggests();
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

function loadSuggests() {
    const loadingElement = document.getElementById('loading');
    const errorElement = document.getElementById('error-message');
    const mySuggestListElement = document.getElementById('my-suggest-list');
    const emptyMessage = document.getElementById('emptyMessage');

    loadingElement.style.display = 'block';

    fetch('/api/v1/topic-suggests')
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(suggests => {
            loadingElement.style.display = 'none';

            if (suggests.length === 0) {
                emptyMessage.style.display = 'block';
                return;
            }

            suggests.forEach(suggest => {
                const item = document.createElement('a');
                item.href = `/topic-suggests/${suggest.id}`;
                item.className = 'list-group-item list-group-item-action';

                const statusColorClass = getStatusColor(suggest.status);


                item.innerHTML = `
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="mb-1">${suggest.title}</h6>
                            <small class="text-muted">
                                <i class="bi bi-clock me-1"></i>${new Date(suggest.createdAt).toLocaleDateString()}
                            </small>
                        </div>
                         <span class="badge ${statusColorClass}">${suggest.status}</span>
                    </div>
                `;
                mySuggestListElement.appendChild(item);
            });
        })
        .catch(error => {
            console.error('Error loading suggests:', error);
            errorElement.style.display = 'block';
            loadingElement.style.display = 'none';
        });
}
