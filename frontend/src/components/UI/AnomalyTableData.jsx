const processAnomalyData = (backendList) => {
  if (!backendList || !Array.isArray(backendList)) return [];

  const result = [];

  backendList.forEach((item) => {
    const cleanDate = item.create_at ? item.create_at.replace('T', ' ') : '-';
    
    // 1. 상태("조치완료")와 상관없이 오직 점수와 원본 state 기준으로만 위험 등급 및 색상 결정
    let level = "정상";
    let levelColor = "#319795"; // 정상 (녹색)

    // 백엔드 문자열(위험, 주의) 또는 점수(90점 이상, 70점 이상) 기준으로 판정
    if (item.anomaly_score >= 90 || item.state === "위험") {
      level = "위험";
      levelColor = "#e53e3e"; // 위험 (빨간색)
    } else if (item.anomaly_score >= 70 || item.state === "주의") {
      level = "주의";
      levelColor = "#dd6b20"; // 주의 (주황색)
    } else if (item.anomaly_score < 70) {
      level = "저위험";
      levelColor = "#4A5568"; // 저위험 (회색 또는 별도 지정 색상)
    }

    // 2. 조치 상태 여부는 오직 버튼 스타일과 텍스트 판단에만 활용
    const isActioned = item.state === "조치완료";

    // 3. 간접 배출 (Scope 2 - 전력) 행 추가
    if (item.electricity_used !== undefined && item.electricity_used !== null) {
      result.push({
        id: item.id,
        date: cleanDate,
        machineName: item.process,
        scope: "2",
        score: item.anomaly_score ? item.anomaly_score.toFixed(1) : "0.0", // 점수 추가
        metrics: `${item.electricity_used.toFixed(2)}(${item.proper_electricity_used.toFixed(2)})`,
        level: level,
        levelColor: levelColor,
        isActioned: isActioned
      });
    }

    // 4. 직접 배출 (Scope 1 - 가스 등) 행 추가
    if (item.direct_emission && item.direct_emission > 0) {
      result.push({
        id: item.id,
        date: cleanDate,
        machineName: item.process,
        scope: "1",
        score: item.anomaly_score ? item.anomaly_score.toFixed(1) : "0.0", // 점수 추가
        metrics: `${item.direct_emission.toFixed(2)}(${item.proper_direct_emission.toFixed(2)})`,
        level: level,
        levelColor: levelColor,
        isActioned: isActioned
      });
    }
  });
  return result;
};

export default processAnomalyData;