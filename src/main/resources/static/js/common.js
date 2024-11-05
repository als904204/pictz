/**
 * 숫자를 천 단위로 포맷팅
 * "10000" -> "10,000"
 * @param {number} num - 포맷할 숫자
 * @returns {string} 포맷된 숫자 문자열
 */
function formatNumber(num) {
    return num.toLocaleString();
}

/**
* Debounce
* 지정한 시간 동안 함수 이벤트가 발생하지 않으면 debounce 함수 실행
**/
function debounce(func, delay) {
    let timeoutId;
    return function(...args) {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => {
            func.apply(this, args);
        }, delay);
    };
}

// 날짜 형식을 'yyyy-MM-dd HH:mm'으로 변환하는 함수
function formatDate(dateString) {
    const options = { year: 'numeric', month: '2-digit', day: '2-digit',
                      hour: '2-digit', minute: '2-digit' };
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR', options).replace(',', '');
}