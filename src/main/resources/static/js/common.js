/**
 * 숫자를 천 단위로 포맷팅
 * "10000" -> "10,000"
 * @param {number} num - 포맷할 숫자
 * @returns {string} 포맷된 숫자 문자열
 */
function formatNumber(num) {
    return num.toLocaleString();
}