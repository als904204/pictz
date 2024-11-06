document.addEventListener('DOMContentLoaded', () => {
    loadSuggestList();
});

function loadSuggestList() {
    fetch('/api/v1/admin/topic-suggests')
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            renderSuggestsTable(data);
        })
        .catch(error => {
            console.error('Error loading suggests:', error);
            alert('Suggest 목록을 불러오는 데 실패했습니다.');
        });
}

function renderSuggestsTable(suggests) {
    const tableBody = document.querySelector('#suggestsTable tbody');
    tableBody.innerHTML = '';

    suggests.forEach((suggest, index) => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${index + 1}</td>
            <td><a href="/admin/suggests/${suggest.id}" class="suggest-link">${suggest.title}</a></td>
            <td>${suggest.nickname}</td>
            <td>${formatDate(suggest.createdAt)}</td>
            <td>${suggest.status}</td>
        `;
        tableBody.appendChild(row);
    });
}