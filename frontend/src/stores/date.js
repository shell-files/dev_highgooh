export const getFirstDay = () => {
    const today = new Date();
    // 이번 달 1일 구하기 ("YYYY-MM-01")
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const firstDayStr = `${year}-${month}-01`;
    return firstDayStr;
}


export const getLastDayOfMonth = () => {
    const today = new Date();
    const year = today.getFullYear();
    // today.getMonth() + 1 은 '다음 달'의 인덱스가 됩니다.
    // 일(Day) 자리에 0을 주면 '이번 달의 마지막 날' 객체가 생성됩니다.
    const lastDay = new Date(year, today.getMonth() + 1, 0);

    const lastYear = lastDay.getFullYear();
    const lastMonth = String(lastDay.getMonth() + 1).padStart(2, '0');
    const lastDate = String(lastDay.getDate()).padStart(2, '0');

    const lastDayStr = `${lastYear}-${lastMonth}-${lastDate}`;
    return lastDayStr; // 예: "2026-06-30" 또는 "2026-02-28" 등 자동 계산
};




// console.log("첫날:", getFirstDay())
// console.log("마지막날:", getLastDayOfMonth())


export const getToday = () => {
    const today = new Date();
    const year = today.getFullYear();
    // getMonth()는 0부터 시작하므로 항상 +1을 해줘야 합니다.
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const date = String(today.getDate()).padStart(2, '0');

    const todayStr = `${year}-${month}-${date}`;
    return todayStr; // 예: "2026-06-14" 등 자동 계산
};

export const addOneDay = (dateStr) => {
    if (!dateStr) return "";

    const date = new Date(dateStr);
    date.setDate(date.getDate() + 1); // 하루 더하기

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
};

export const getNow = () => {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const date = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');

    return `${year}-${month}-${date}T${hours}:${minutes}`; // "2026-06-15T14:30"
};
