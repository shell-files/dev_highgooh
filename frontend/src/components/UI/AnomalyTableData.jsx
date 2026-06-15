const processAnomalyData = (backendList) => {
  if (!backendList || !Array.isArray(backendList)) return [];

  const result = [];

  backendList.forEach((item) => {
    // 1. 날짜 시간 제거
    const rawDate = item.create_at ? item.create_at.toString().replace('T', ' ') : '-';
    const cleanDate = rawDate !== '-' ? rawDate.split(" ")[0] : '-';

    // 2. 등급 및 색상 결정
    let level = "정상";
    let levelColor = "#2E7D32"; // 텍스트 색상
    let bgColor = "#E8F5E9";    // 배경 색상
    let icon = "✓";             // 아이콘

    if (item.anomaly_score >= 90 || item.state === "위험") {
      level = "위험";
      levelColor = "#C62828";
      bgColor = "#FFEBEE";
      icon = "✕";
    } else if (item.anomaly_score >= 50 || item.state === "주의") {
      level = "주의";
      levelColor = "#EF6C00";
      bgColor = "#FFF3E0";
      icon = "⚠";
    } else if (item.anomaly_score >= 10) {
      level = "저위험";
      levelColor = "#c2a216";
      bgColor = "#FFFBEB";
      icon = "-";
    } else {
      // 10 미만인 경우 (정상)
      level = "정상";
      levelColor = "#2E7D32";
      bgColor = "#E8F5E9";
      icon = "✓";
    }

    // 3. 상태 처리
    const state = item.state || "조치대기";
    const isActioned = state === "조치완료";

    // 4. 스코프 로직
    const hasScope2 = item.electricity_used !== undefined && item.electricity_used !== null;
    const hasScope1 = item.direct_emission !== undefined && item.direct_emission !== null && item.direct_emission > 0;

    // 공통 객체
    const baseData = {
      id: item.id,
      date: cleanDate,
      machineName: item.process,
      score: item.anomaly_score ? item.anomaly_score.toFixed(1) : "0.0",
      level: level,
      levelColor: levelColor,
      state: state,
      isActioned: isActioned,
      bgColor: bgColor,
      icon: icon,
    };

    // 5. 스코프별 데이터 push (spread 연산자 사용)
    if (hasScope1 && hasScope2) {
      result.push({
        ...baseData,
        scope: "1, 2",
        metrics: (
          <>
            S1: {item.direct_emission.toFixed(2)}({item.proper_direct_emission.toFixed(2)})
            <br />
            S2: {item.indirect_emission.toFixed(2)}({item.proper_indirect_emission.toFixed(2)})
          </>
        )
      });
    } else if (hasScope2) {
      result.push({
        ...baseData,
        scope: "2",
        metrics: `${item.indirect_emission.toFixed(2)}(${item.proper_indirect_emission.toFixed(2)})`
      });
    } else if (hasScope1) {
      result.push({
        ...baseData,
        scope: "1",
        metrics: `${item.direct_emission.toFixed(2)}(${item.proper_direct_emission.toFixed(2)})`
      });
    }
  });
  return result;
};

export default processAnomalyData;