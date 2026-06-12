

// 1. 라인 차트 데이터
  export const getLineChartData = (data) => {
    if (!data || !Array.isArray(data) || data.length === 0) {
      console.log("없음")
    return { data: [], labels: [] };
  }
    return null
  }

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