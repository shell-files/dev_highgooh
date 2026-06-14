const getActiveStepDetail = (yesterdayData, activeStep) => {
  // 1. 공정 매핑 설정
  const stepMapping = {
    1: { title: "빌릿 가열 공정", processKey: "빌릿 가열기", icon: "🔥", headerClass: "heating" },
    2: { title: "간접 압출 공정", processKey: "간접 압출기", icon: "⚙️", headerClass: "extrusion" },
    3: { title: "인발 공정", processKey: "인발기", icon: "⛓️", headerClass: "drawing" },
    4: { title: "알루미늄 시효", processKey: "알루미늄 시효로", icon: "⏳", headerClass: "aging" },
    5: { title: "4축 CNC 가공 공정", processKey: "4축 CNC", icon: "📐", headerClass: "cnc" },
    6: { title: "알루미늄 절단 공정", processKey: "알루미늄 절단기", icon: "✂️", headerClass: "cutting" },
    7: { title: "아노다이징 공정", processKey: "아노다이징", icon: "🧪", headerClass: "anodizing" },
    8: { title: "구리스 디스펜싱 공정", processKey: "자동 구리스 디스펜서 시스템", icon: "💧", headerClass: "dispensing" },
  };

  const currentConfig = stepMapping[activeStep];
  if (!currentConfig) return null;

  // 2. 색상 및 상태 판별 로직
  const COLORS = {
    critical: "#e53e3e", // 위험 (5% 이상)
    warning: "#dd6b20",  // 주의 (2%~5%)
    normal: "#3182ce"    // 정상 (2% 미만)
  };

  const getStatusData = (deviation) => {
    if (deviation >= 5) return { status: "위험", color: COLORS.critical, isCritical: true };
    if (deviation >= 2) return { status: "주의", color: COLORS.warning, isCritical: false };
    return { status: "정상", color: COLORS.normal, isCritical: false };
  };

  // 3. 데이터 가공
  const item = Array.isArray(yesterdayData) ? yesterdayData.find(b => b.process === currentConfig.processKey) : null;
  const metrics = [];

  if (item) {
    // [간접 탄소 배출량]
    const stdIndirect = item.proper_indirect_emission || 0;
    const actIndirect = item.sum_indirect_emission || 0;
    const devIndirect = stdIndirect > 0 ? ((actIndirect - stdIndirect) / stdIndirect) * 100 : 0;
    const statusIndirect = getStatusData(devIndirect);

    metrics.push({
      name: "간접 탄소 배출량 (전력)",
      standard: `${stdIndirect.toFixed(4)} tCO₂e`,
      actual: `${actIndirect.toFixed(4)} tCO₂e`,
      deviation: `${devIndirect > 0 ? "+" : ""}${devIndirect.toFixed(2)}%`,
      status: statusIndirect.status,
      statusColor: statusIndirect.color,
      isCritical: statusIndirect.isCritical
    });

    // [전력 사용량]
    const stdElec = item.proper_electricity_used || 0;
    const actElec = item.sum_electricity_used || 0;
    const devElec = stdElec > 0 ? ((actElec - stdElec) / stdElec) * 100 : 0;
    const statusElec = getStatusData(devElec);

    metrics.push({
      name: "전력 사용량 통계",
      standard: `${stdElec.toFixed(2)} kWh`,
      actual: `${actElec.toFixed(2)} kWh`,
      deviation: `${devElec > 0 ? "+" : ""}${devElec.toFixed(2)}%`,
      status: statusElec.status,
      statusColor: statusElec.color,
      isCritical: statusElec.isCritical
    });

    // [직접 탄소 배출량]
    if (activeStep === 7 || item.proper_direct_emission > 0) {
      const stdDirect = item.proper_direct_emission || 0;
      const actDirect = item.sum_direct_emission || 0;
      const devDirect = stdDirect > 0 ? ((actDirect - stdDirect) / stdDirect) * 100 : 0;
      const statusDirect = getStatusData(devDirect);

      metrics.unshift({
        name: "직접 탄소 배출량 (가스)",
        standard: `${stdDirect.toFixed(4)} tCO₂e`,
        actual: `${actDirect.toFixed(4)} tCO₂e`,
        deviation: `${devDirect > 0 ? "+" : ""}${devDirect.toFixed(2)}%`,
        status: statusDirect.status,
        statusColor: statusDirect.color,
        isCritical: statusDirect.isCritical
      });
    }
  } else {
    // 데이터가 없을 경우
    metrics.push({
      name: "공정 탄소 데이터",
      standard: "- tCO₂e",
      actual: "- tCO₂e",
      deviation: "0.00%",
      status: "데이터 없음",
      statusColor: "#718096",
      isCritical: false
    });
  }

  return {
    id: activeStep,
    title: currentConfig.title,
    icon: currentConfig.icon,
    headerClass: currentConfig.headerClass,
    metrics: metrics
  };
};

export default getActiveStepDetail;