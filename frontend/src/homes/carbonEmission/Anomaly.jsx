import React from 'react';
import '@styles/anomaly.css';
import { useState, useRef, useEffect } from 'react';
import AnomalyBarChart from '@components/UI/AnomalyBarChart.jsx';
import processAnomalyData from '@components/UI/AnomalyTableData.jsx';
import getActiveStepDetail from '@components/UI/AnomalyStepData.jsx';
import { POST, PATCH } from "@utils/Network";



const Anomaly = () => {
  // 페이지네이션 상태 관리
  const [pageNumber, setPageNumber] = useState(1);

  // 공정 선택 상태 관리
  const [activeStep, setActiveStep] = useState(null);

  // 모달 제어 상태 관리
  const [selectedLog, setSelectedLog] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalStatus, setModalStatus] = useState("15");
  const actionOptions = [
    { value: "15", label: "조치대기" },
    { value: "16", label: "조치중" },
    { value: "17", label: "조치완료" }
  ];

  const [waitCount, setWaitCount] = useState(0)
  const [actioningCount, setActioningCount] = useState(0)
  const [actionedCount, setActionedCount] = useState(0)

  // 상태 변경 모달 열기 함수
  const openStatusModal = (e, log) => {
    e.preventDefault();
    setSelectedLog(log);
    const matchedOption = actionOptions.find(opt => opt.label === log.state?.trim());
    setModalStatus(matchedOption ? matchedOption.value : "15");
    setIsModalOpen(true);
    
  };
  // 상태 변경 모달 닫기 함수
  const closeStatusModal = () => {
    setSelectedLog(null);
    setIsModalOpen(false);
  };


  const [chartData, setChartData] = useState({
    label: [],
    data: []
  });

  // 로그 데이터
  const [logs, setLogs] = useState([]);
  const [totalCount, setTotalCount] = useState(0)



  // 데이터 새로고침 함수
  const fetchData = (page = 1) => {
    const offset = (page - 1) * 10;
    const param = {
      selectedYear,
      selectedQuarter,
      selectedMonth,
      page: page,
      limit: 10,
      offset: offset
    };

    POST("/anomaly", param).then(res => {
      if (res && res.status === true) {
        setLogs(processAnomalyData(res.data.list));

        //차트데이터
        const stats = res.data.stats;
        const label = stats.map(item => item.process);
        const data = stats.map(item => item.anomaly_count);
        const waitActionData = stats.map(item => item.waiting_count);
        const actioningData = stats.map(item => item.actioning_count);
        const actionedData = stats.map(item => item.actioned_count);

        // 합계데이터
        const total = data.reduce((sum, current) => sum + current, 0);
        setTotalCount(total);
        const waitingAction = waitActionData.reduce((sum, current) => sum + current, 0);
        const actioning = actioningData.reduce((sum, current) => sum + current, 0);
        const actioned = actionedData.reduce((sum, current) => sum + current, 0);

        setActioningCount(actioning)
        setActionedCount(actioned)
        setWaitCount(waitingAction)

        setChartData({ label, data });

        setPageNumber(page);
      } else {
        console.error("조회 실패:", res);
      }
    }).catch(err => console.error("조회 에러:", err));
  };

  // 페이지네이션
  const totalPages = Math.ceil(totalCount / 10);
  const pageGroup = Math.ceil(pageNumber / 10);
  const lastPageInGroup = Math.min(pageGroup * 10, totalPages);
  const firstPageInGroup = (pageGroup - 1) * 10 + 1;

  // 조회 함수
  const periodSearch = () => {
    fetchData(1);
  }

  // 필터 상태 관리
  const [selectedYear, setSelectedYear] = useState("2026");
  const [selectedQuarter, setSelectedQuarter] = useState("");
  const [selectedMonth, setSelectedMonth] = useState("");

  // 분기 선택시 월 선택 초기화
  const quarterChoice = (quarter) => {
    setSelectedQuarter(quarter);
    setSelectedMonth("");
  };
  // 월 선택시 분기 선택 초기화
  const monthChoice = (e) => {
    const month = e.target.value;
    setSelectedMonth(month);
    if (month !== "") {
      setSelectedQuarter("");
    }
  };



  // STEP 카드 데이터
  const [stepData, setStepData] = useState([]);


  // 상세 정보 데이터
  const [yesterdayRawData, setYesterdayRawData] = useState([]);
  const processYesterdayStepData = (yesterdayData) => {
    const stepConfigs = [
      { id: 1, badge: "STEP 01", title: "빌릿 가열 공정", processKey: "빌릿 가열기", dotClass: "blue-dot" },
      { id: 2, badge: "STEP 02", title: "간접 압출 공정", processKey: "간접 압출기", dotClass: "green-dot" },
      { id: 3, badge: "STEP 03", title: "인발 공정", processKey: "인발기", dotClass: "purple-dot" },
      { id: 4, badge: "STEP 04", title: "알루미늄 시효", processKey: "알루미늄 시효로", dotClass: "orange-dot" },
      { id: 5, badge: "STEP 05", title: "4축 CNC 가공 공정", processKey: "4축 CNC", dotClass: "cyan-dot" },
      { id: 6, badge: "STEP 06", title: "알루미늄 절단 공정", processKey: "알루미늄 절단기", dotClass: "magenta-dot" },
      { id: 7, badge: "STEP 07", title: "아노다이징 공정", processKey: "아노다이징", dotClass: "yellow-dot" },
      { id: 8, badge: "STEP 08", title: "구리스 디스펜싱 공정", processKey: "자동 구리스 디스펜서 시스템", dotClass: "teal-dot" },
    ];

    return stepConfigs.map((config) => {
      const match = Array.isArray(yesterdayData)
        ? yesterdayData.find((item) => item.process === config.processKey)
        : null;

      if (!match) {
        return {
          ...config,
          indirectEmission: "- tCO₂e",
          currentEmission: "- tCO₂e",
          directEmission: config.id === 7 ? "- tCO₂e" : null,
          directCurrentEmission: config.id === 7 ? "- tCO₂e" : null,
          anomalyCount: 0,
          isCritical: false,
          countHighlight: false,
        };
      }
      const indirectStd = match.proper_indirect_emission || match.properIndirectEmission || 0;
      const indirectAct = match.sum_indirect_emission || match.sumIndirectEmission || 0;
      const anomalyCnt = match.anomaly_count || match.anomalyCount || 0;

      const directStd = match.proper_direct_emission || match.properDirectEmission || 0;
      const directAct = match.sum_direct_emission || match.sumDirectEmission || 0;

      return {
        ...config,
        indirectEmission: `${indirectStd.toFixed(4)} tCO₂e`,
        currentEmission: `${indirectAct.toFixed(4)} tCO₂e`,
        // 직접 배출량 데이터 바인딩 분기 방어
        directEmission: (config.id === 7 || directStd > 0) ? `${directStd.toFixed(4)} tCO₂e` : null,
        directCurrentEmission: (config.id === 7 || directAct > 0) ? `${directAct.toFixed(4)} tCO₂e` : null,
        anomalyCount: `${anomalyCnt} 건`,
        isCritical: Number(anomalyCnt) > 0,
        countHighlight: Number(anomalyCnt) > 0,
      };
    });
  };

  // 상세 정보 열기 함수
  const pipelineStepClick = (step) => {
    setActiveStep(prevStep => prevStep === step ? null : step);
  };
  // 상세 정보 닫기 함수
  const closeDetail = () => {
    setActiveStep(null);
  };

  // 조치 상태 저장 함수
  const saveStatus = () => {
    if (!selectedLog) return;

    PATCH("/anomaly", {
      id: selectedLog.id,
      state_code: modalStatus
    }).then(res => {
      if (res && res.status === true) {
        fetchData(pageNumber);

        // 2. 모달 닫기
        closeStatusModal();

        console.log("상태 변경 성공");
      } else {
        console.error("상태 변경 실패:", res);
        alert("상태 변경에 실패했습니다: " + (res.message || "알 수 없는 오류"));
      }
    }).catch(err => {
      console.error("상태 변경 에러:", err);
      alert("서버 연결에 실패했습니다.");
    });
  };

  // 작일 날짜
  const [yesterday, setYesterday] = useState('')

  useEffect(() => {
    POST("/anomaly/yesterday", {})
      .then((res) => {
        if (res && res.status === true) {
          // 작일 공정 섹션
          const yesterdayData = res.data;
          setYesterdayRawData(yesterdayData);
          const yesterday = res.data[0].process_start
          const cleanYesterday = yesterday.split(" ")[0];
          setYesterday(cleanYesterday);

          const formattedData = processYesterdayStepData(yesterdayData);
          setStepData(formattedData);

        } else {
          console.error("전날 공정 통계 조회 실패:", res.message);
        }
      })
      .catch((err) => {
        console.error("전날 공정 API 네트워크 에러:", err);
      });

    // 기본적으로 2026년 데이터 조회
    fetchData(1);
  }, []);


  return (
    <div id="anomaly-page">


      {/* 본문 영역 */}

      <div className="page-header-flex">
        <h2 className="page-title">이상치 탐지</h2>
      </div>

      <div className="pipeline-section-wrapper">
        <div className="pipeline-header-flex">
          <h2>작일 공정 파이프라인 이상치 현황 ({yesterday})</h2>
        </div>

        {/* 작일 스텝 카드 영역 */}
        <div className="pipeline-flex-container">
          {stepData.map((step, index) => (
            <React.Fragment key={step.id}>
              <div
                className={`pipeline-step-card ${step.isCritical ? 'critical' : ''} ${activeStep === step.id ? 'active' : ''}`}
                onClick={() => pipelineStepClick(step.id)}
              >
                <div className="step-header">
                  <span className="step-badge">{step.badge}</span>
                  <span className="step-title">
                    <span className={`dot ${step.dotClass}`}></span> {step.title}
                  </span>
                </div>

                <div className="step-info-row">
                  <span className="label">적정 간접 탄소 배출량</span>
                  <span className={`value ${step.countHighlight ? 'highlight' : ''}`}>{step.indirectEmission}</span>
                </div>

                <div className="step-info-row">
                  <span className="label">간접 탄소 배출량</span>
                  <span className="value">{step.currentEmission}</span>
                </div>
                {step.directEmission && (
                  <>
                    <div className="step-info-row">
                      <span className="label">적정 직접 탄소 배출량</span>
                      <span className="value">{step.directEmission}</span>
                    </div>
                    <div className="step-info-row">
                      <span className="label">직접 탄소 배출량</span>
                      <span className="value">{step.directCurrentEmission}</span>
                    </div>
                  </>
                )}

                <div className="step-info-row">
                  <span className="label">이상치 건수</span>
                  <span className={`value ${step.countHighlight ? 'highlight' : ''}`}>{step.anomalyCount}</span>
                </div>
              </div>

              {/* 단계 사이의 화살표 아이콘 */}
              {index < stepData.length - 1 && <div className="arrow-icon">➔</div>}
            </React.Fragment>
          ))}
        </div>

        {/* 작일 공정 디테일 카드 */}
        {[getActiveStepDetail(yesterdayRawData, activeStep)].map((detail) => {
          if (!activeStep || !detail || !detail.metrics) return null;
          if (activeStep !== detail.id) return null;

          return (
            <div className="pipeline-detail-box" key={detail.id}>
              {/* 헤더 영역 */}
              <div className={`detail-header ${detail.headerClass}`}>
                <span>{detail.icon} {detail.title}</span>
                <button
                  type="button"
                  className="close-btn"
                  onClick={(e) => {
                    e.stopPropagation();
                    setActiveStep(null);
                  }}
                >
                  ✕
                </button>
              </div>

              {/* 작일 공정 상세보기 */}
              <div className="detail-content">
                <table className="status-table">
                  <thead>
                    <tr>
                      <th>측정 항목</th>
                      <th>기준치 (표준)</th>
                      <th>실측값</th>
                      <th>편차율</th>
                      <th>상태</th>
                    </tr>
                  </thead>
                  <tbody>
                    {/* 실시간으로 가공된 내부 스펙 항목 루프 */}
                    {detail.metrics.map((metric, index) => (
                      <tr key={index} className={metric.isCritical ? "row-critical" : ""}>
                        <td>
                          {metric.isCritical ? <strong>{metric.name}</strong> : metric.name}
                        </td>
                        <td>{metric.standard}</td>
                        <td className={`status-text ${metric.isCritical ? "critical" : "normal"}`}>
                          {metric.actual}
                        </td>
                        <td className={`status-text ${metric.isCritical ? "critical" : "normal"}`}>
                          {metric.deviation}
                        </td>
                        <td className="status-text" style={{ color: metric.statusColor, fontWeight: 'bold' }}>
                          {metric.status}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          );
        })}

        <h2 className="period-section-title">기간 별 공정 이상치 현황</h2>
      </div>

      {/* 필터 바 */}
      <div className="dashboard-filter-bar">
        <div className="toggle-group-wrapper">

          {/* 연도 선택 */}
          <div className="toggle-group-item id_quarter">
            <div className="select_label"><label>연도 선택</label></div>
            <div className="toggle-content">
              <select
                className="year-select btn-filter-tab"
                value={selectedYear}
                onChange={(e) => {
                  setSelectedYear(e.target.value);
                  setSelectedQuarter("");
                  setSelectedMonth("");
                }}
              >
                <option value="all">전체</option>
                <option value="2026">2026년</option>
                <option value="2025">2025년</option>
                <option value="2024">2024년</option>
                <option value="2023">2023년</option>
              </select>
            </div>
          </div>

          {selectedYear !== "all" && (
            <>
              {/* 분기 선택 영역 */}
              <div className="toggle-group-item id_quarter">
                <div className="select_label"><label>분기 선택</label></div>
                <div className="toggle-content">
                  {[
                    { code: "all", name: "전체" },
                    { code: "1", name: "1분기" },
                    { code: "2", name: "2분기" },
                    { code: "3", name: "3분기" },
                    { code: "4", name: "4분기" }
                  ].map((q) => (
                    <button
                      key={q.code}
                      type="button"
                      className={`btn-filter-tab ${selectedQuarter === q.code ? 'active' : ''}`}
                      onClick={() => quarterChoice(q.code)}
                    >
                      {q.name}
                    </button>
                  ))}
                </div>
              </div>

              {/* 월 선택 영역 */}
              <div className="toggle-group-item id_quarter">
                <div className="select_label"><label>월 선택</label></div>
                <div className="toggle-content">
                  <select
                    className="year-select btn-filter-tab"
                    value={selectedMonth}
                    onChange={monthChoice}
                  >
                    <option value="none">선택</option>
                    {Array.from({ length: 12 }, (_, i) => i + 1).map(month => (
                      <option key={month} value={month}>{month}월</option>
                    ))}
                  </select>
                </div>
              </div>
            </>
          )}
          <div className="toggle-group-item" style={{ marginLeft: 'auto' }}>
            <button
              type="button"
              className="btn-filter-tab active"
              onClick={() => periodSearch(1)}>
              조회
            </button>
          </div>
        </div>
      </div>

      {/* 차트 영역 */}
      <div style={{ marginBottom: '40px' }}>
        <div className="chart-card">
          <h3 className="chart-title">설비별 이상치 발생 횟수 현황</h3>
          <div className="chart-container chart-height-machine">
            <AnomalyBarChart dataValues={chartData} />
          </div>
        </div>
      </div>

      {/* 테이블 로그 영역 */}
      <div className="dashboard-table-card">
        <div className="table-header-flex">
          <h3 className="table-title">실시간 이상치 탐지 및 발생 로그 이력</h3>
          <button className="btn-excel-download">액셀 다운로드</button>
        </div>
        <div className="table-responsive">
          <table className="dashboard-data-table">
            <thead>
              <tr>
                <th>이상치 ID</th>
                <th>발생 일시</th>
                <th>설비명 (ID)</th>
                <th>SCOPE</th>
                <th>측정값 (기준치)</th>
                <th>이상치 점수</th>
                <th>위험 등급</th>
                <th>조치 상태</th>
              </tr>
            </thead>
            <tbody>
              {logs.map((log) => (
                <tr key={`${log.id}-${log.scope}`}>
                  <td className="text-center">{log.id}</td>
                  <td className="text-center">{log.date}</td>
                  <td className="font-bold text-center">{log.machineName}</td>
                  <td className="text-center font-bold">{log.scope}</td>
                  <td className="text-center">{log.metrics}</td>
                  <td className="text-center">{log.score}</td>
                  <td className="text-center">
                    <span style={{ color: log.levelColor, fontWeight: 600 }}>{log.level}</span>
                  </td>
                  <td className="text-center">
                    <button
                      onClick={(e) => openStatusModal(e, log)}
                      className="btn-filter-tab"
                      style={{
                        padding: '2px 8px',
                        fontSize: '11px',
                        cursor: 'pointer',
                        fontWeight: 'bold',
                        // 상태별 색상 분기
                        backgroundColor: log.state === "조치완료" ? '#319795' : (log.state === "조치중" ? '#dd6b20' : '#e53e3e'),
                        borderColor: log.state === "조치완료" ? '#319795' : (log.state === "조치중" ? '#dd6b20' : '#e53e3e'),
                        color: '#fff',
                        borderRadius: '4px'
                      }}
                    >
                      {log.state}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
            <tfoot>
              <tr className="table-summary-row">
                <td colSpan={3} className="text-center">선택기간 총 이상 발생 건수</td>
                <td className="text-center total-amount">{totalCount}</td>
                <td className="text-center">-</td>
                <td className="text-center" style={{ color: '#e53e3e' }}>조치대기 {waitCount}건</td>
                <td className="text-center" style={{ color: '#dd6b20' }}>조치중 {actioningCount}건</td>
                <td className="text-center" style={{ color: '#319795' }}>조치완료 {actionedCount}건</td>
              </tr>
            </tfoot>
          </table>
          {isModalOpen && selectedLog && (
            <div id="anomalyStatusModal" className="anomaly-modal" style={{ display: 'flex' }}>
              <div className="anomaly-modal-content">
                <div className="anomaly-modal-header">조치 상태 변경</div>
                <div className="anomaly-modal-body">
                  <select
                    id="statusSelect"
                    className="anomaly-select-control"
                    value={modalStatus}
                    onChange={(e) => setModalStatus(e.target.value)}
                  >
                    {actionOptions.map(opt => (
                      <option key={opt.value} value={opt.value}>{opt.label}</option>
                    ))}
                  </select>
                </div>
                <div className="anomaly-modal-footer">
                  <button type="button" className="btn-modal-action" onClick={closeStatusModal}>취소</button>
                  <button type="button" className="btn-modal-action save" onClick={saveStatus}>저장</button>
                </div>
              </div>
            </div>
          )}
        </div>

        {/* 페이지네이션 */}
        <div className="pagination-container">
          <div className="pagination-info">
            전체 <span>{totalCount}</span>건
          </div>
          <div className="pagination-buttons">
            {/* 첫 페이지로 이동 */}
            <button
              type="button" className="btn-page first" title="첫 페이지"
              onClick={() => fetchData(1)}
              disabled={pageNumber === 1}>&laquo;</button>

            {/* 이전 페이지로 이동 */}
            <button
              type="button" className="btn-page prev" title="이전 페이지"
              onClick={() => fetchData(pageNumber - 1)}
              disabled={pageNumber === 1}>&lsaquo;</button>

            {/* 페이지 번호 동적 생성 */}
            {Array.from({ length: lastPageInGroup - firstPageInGroup + 1 }, (_, i) => firstPageInGroup + i).map((num) => (
              <button
                key={num}
                className={`btn-page-num ${pageNumber === num ? 'active' : ''}`}
                onClick={() => fetchData(num)}
              >
                {num}
              </button>
            ))}

            {/* 다음 페이지로 이동 */}
            <button
              type="button" className="btn-page next" title="다음 페이지"
              onClick={() => fetchData(pageNumber + 1)}
              disabled={pageNumber === totalPages || totalPages === 0}>&rsaquo;</button>

            {/* 마지막 페이지로 이동 */}
            <button
              type="button" className="btn-page last" title="마지막 페이지"
              onClick={() => fetchData(totalPages)}
              disabled={pageNumber === totalPages || totalPages === 0}>&raquo;</button>
          </div>
        </div>
      </div>
    </div>
  )
};

export default Anomaly;