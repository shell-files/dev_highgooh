

// 1. 라인 차트 데이터
export const getLineChartData = (
  rawData,
  selectedYear,
  selectedMonth
) => {

  if (!rawData || rawData.length === 0) {
    return {
      labels: [],
      data: []
    };
  }
  const groupedData = {};
  // 월 선택 여부
  const isDailyView =
    selectedMonth !== null &&
    selectedMonth !== undefined &&
    selectedMonth !== '';

  rawData.forEach(item => {

    let label;

    if (isDailyView) {
      // YYYY-MM-DD -> DD
      label = item.date.split('-')[2];
    } else {
      // 월 단위
      label = item.date;
    }
    groupedData[label] =
      (groupedData[label] || 0) +
      Number(item.total_emission || 0);
  });

  const labels = Object.keys(groupedData).sort(
    (a, b) => Number(a) - Number(b)
  );

  const data = labels.map(label => groupedData[label]);

  return {
    labels,
    data
  };
};

// 2. 바 차트: 특정 공정별 배출량 합계 배열 생성
export const getBarChartData = (data, processNames) => {
  return processNames.map(name => {
    return data
      .filter(item => item.process === name)
      .reduce((sum, item) => sum + item.total_emission, 0);
  });
};

// 3. 도넛 차트: Scope 1, Scope 2 합계 배열 생성
export const getDoughnutChartData = (data) => {
  const scope1 = data.filter(item => item.direct_emission > 0).reduce((sum, item) => sum + item.total_emission, 0);
  const scope2 = data.filter(item => item.direct_emission === 0).reduce((sum, item) => sum + item.total_emission, 0);
  return [scope1, scope2];
};