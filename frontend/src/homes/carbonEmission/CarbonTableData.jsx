const processCarbonData = (data) => {
  if (!Array.isArray(data) || data.length === 0) return [];

  // 1. 그룹핑 (이 단계는 문제가 없습니다)
  const grouped = data.reduce((acc, item) => {
    const { process, indirect_emission, direct_emission, total_emission } = item;
    
    // 만약 여기서 acc를 확인했을 때 이미 값이 들어있다면, 
    // 그건 함수 외부 어딘가에서 acc가 참조되고 있다는 뜻입니다.
    if (!acc[process]) {
      acc[process] = { process, indirect: 0, direct: 0, total: 0 };
    }
    
    acc[process].indirect += indirect_emission;
    acc[process].direct += direct_emission;
    acc[process].total += total_emission;
    return acc;
  }, {});

  const processedList = Object.values(grouped);
  
  // 2. 중요: percent 계산을 위한 합계는 전체 배출량의 합이어야 합니다.
  const totalSum = processedList.reduce((sum, item) => sum + item.total, 0);

  // 3. 최종 데이터 생성
  const result = [];
  processedList.forEach(item => {
    // [아노다이징 특별 처리]
    if (item.process === '아노다이징') {
      // 간접(Scope 2)
      result.push({ 
        process: item.process, scope: 'Scope 2 (간접)', source: '전력', 
        amount: item.indirect, emission: item.indirect, unit: 'kWh', 
        percent: `${((item.indirect / totalSum) * 100).toFixed(1)}%` 
      });
      // 직접(Scope 1)
      result.push({ 
        process: item.process, scope: 'Scope 1 (직접)', source: '가스', 
        amount: item.direct, emission: item.direct, unit: 'Nm3', 
        percent: `${((item.direct / totalSum) * 100).toFixed(1)}%` 
      });
    } else {
      const isDirect = item.direct > 0;
      result.push({ 
        process: item.process,
        scope: isDirect ? 'Scope 1 (직접)' : 'Scope 2 (간접)',
        source: isDirect ? '가스' : '전력',
        amount: isDirect ? item.direct : item.indirect,
        unit: isDirect ? 'Nm3' : 'kWh',
        emission: item.total,
        percent: `${((item.total / totalSum) * 100).toFixed(1)}%`
      });
    }
  });
  return result;
};

export default processCarbonData;