document.addEventListener('DOMContentLoaded', () => {
    setupSearch();
});

function setupSearch() {
    const searchInput = document.getElementById('search-input');
    const searchButton = document.getElementById('search-button');

    // Click
    searchButton.addEventListener('click', () => {
        const query = searchInput.value.trim();
        performSearch(query, 0);
    });

    // Enter
    searchInput.addEventListener('keypress', (event) => {
        if (event.key === 'Enter') {
            const query = searchInput.value.trim();
            performSearch(query, 0);
        }
    });

}

function performSearch(query, page) {
    document.getElementById('search-loading').style.display = 'block';
    document.getElementById('search-error-message').style.display = 'none';

    fetch(`/api/v1/topics/search?q=${encodeURIComponent(query)}&sortBy=${currentSortBy}&page=${page}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Error searching topics! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            document.getElementById('search-loading').style.display = 'none';

            if (data.content.length === 0) {
                document.getElementById('search-status').textContent = `"${query}" 검색 결과가 없습니다.`;
            } else {
                document.getElementById('search-status').textContent = `"${query}" 검색 결과`;
            }

            renderTopicList(data.content);
            updatePagination(data.currentPage, data.totalPages);
            currentPage = data.currentPage;
        })
        .catch(error => {
            console.error('Error searching topics:', error);
            document.getElementById('search-loading').style.display = 'none';
            document.getElementById('search-error-message').style.display = 'block';
            document.getElementById('search-status').textContent = '';
        });
}