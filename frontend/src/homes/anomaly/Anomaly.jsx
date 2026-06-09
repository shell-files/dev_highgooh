import React from 'react';
import '@styles/anomaly.css';
import { useState, useRef, useEffect } from 'react';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);


const Anomaly = () => {
    const [activeStep, setActiveStep] = useState(null);

    const chartRef = useRef(null);
    const chartInstanceRef = useRef(null);

    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 10;
    
    // 모달 제어 상태 관리
    const [selectedLog, setSelectedLog] = useState(null); 
    const [isModalOpen, setIsModalOpen] = useState(false); 
    const [modalStatus, setModalStatus] = useState("0");
    const actionOptions = [
        { value: "0", label: "미조치" },
        { value: "1", label: "조치완료" }
    ];

    // 상태 변경 모달 열기 함수
    const openStatusModal = (e, log) => {
        e.preventDefault();
        setSelectedLog(log);
        setModalStatus(log.isActioned ? "1" : "0"); 
        setIsModalOpen(true);
    };
    // 상태 변경 모달 닫기 함수
    const closeStatusModal = () => {
        setSelectedLog(null);
        setIsModalOpen(false);
    };

    // 상태 저장 함수
    const saveStatus = () => {
        if (!selectedLog) return;
        const updatedIsActioned = modalStatus === "1"; 

        setLogs(prevLogs =>
        prevLogs.map(item =>
            item.id === selectedLog.id ? { ...item, isActioned: updatedIsActioned } : item
        )
        );
        closeStatusModal();
    };

    // 필터 상태 관리
    const [selectedYear, setSelectedYear] = useState("all");
    const [selectedQuarter, setSelectedQuarter] = useState("all");
    const [selectedMonth, setSelectedMonth] = useState("none");
    
    // 분기 선택시 월 선택 초기화
    const quarterChoice = (quarter) => {
    setSelectedQuarter(quarter);
    setSelectedMonth("none");
    };
    // 월 선택시 분기 선택 초기화
    const monthChoice = (e) => {
        const month = e.target.value;
        setSelectedMonth(month);
        if (month !== "none") {
        setSelectedQuarter("none");
        }
        };
    
    // 조회 버튼 이벤트
    const periodSearch = () => {
        const filterPayload = {
        year: selectedYear,
        quarter: selectedQuarter,
        month: selectedMonth
        }};

    // 차트 초기화 및 업데이트
    useEffect(() => {
            if (chartRef.current) {
            if (chartInstanceRef.current) {
                chartInstanceRef.current.destroy();
            }

            chartInstanceRef.current = new Chart(chartRef.current, {
                type: 'bar',
                data: {
                labels: [
                    '설비 01', '설비 02', '설비 03', '설비 04', '설비 05', '설비 06',
                    '설비 07', '설비 08'],
                datasets: [{
                    label: '이상치 발생 횟수 (건)',
                    data: [4, 12, 2, 5, 8, 1, 0, 3],
                    backgroundColor: '#dd6b20',
                    borderRadius: 4,
                    borderWidth: 0
                }]
                },
                options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                    callbacks: {
                        label: function (context) { return `발생 횟수: ${context.raw} 건`; }
                    }
                    }
                },
                scales: {
                    y: { beginAtZero: true, title: { display: true, text: '발생 건수' } },
                    x: { grid: { display: false } }
                }
                }
            });
            }
            return () => {
            if (chartInstanceRef.current) {
                chartInstanceRef.current.destroy();
            }
            };
        }, []);

    // STEP 카드 데이터 예시 (실제 데이터는 API 연동 후 동적으로 관리)
    const stepData = [
    {
      id: 1,
      badge: "STEP 1",
      title: "빌릿 가열 공정",
      dotClass: "green",
      isCritical: false,
      indirectEmission: "67.5456 tCO₂e/h",
      currentEmission: "64.2560 tCO₂e/h",
      anomalyCount: "0건",
      countHighlight: false
    },
    {
      id: 2,
      badge: "STEP 2",
      title: "간접 압출 공정",
      dotClass: "red",
      isCritical: true, // critical 클래스 적용 여부
      indirectEmission: "33.7705 tCO₂e",
      currentEmission: "53.6 tCO₂e",
      anomalyCount: "2건 (미조치 1)",
      countHighlight: true // highlight 클래스 적용 여부
    },
    {
      id: 3,
      badge: "STEP 3",
      title: "인발 공정",
      dotClass: "green",
      isCritical: false,
      indirectEmission: "13.3272 tCO₂e",
      currentEmission: "15.1180 tCO₂e",
      anomalyCount: "1건 (조치완료)",
      countHighlight: false
    },
    {
      id: 4,
      badge: "STEP 4",
      title: "알루미늄 시효",
      dotClass: "green",
      isCritical: false,
      indirectEmission: "13.3272 tCO₂e",
      currentEmission: "15.1180 tCO₂e",
      anomalyCount: "1건 (조치완료)",
      countHighlight: false
    },
    {
      id: 5,
      badge: "STEP 5",
      title: "4축 CNC 가공 공정",
      dotClass: "green",
      isCritical: false,
      indirectEmission: "13.3272 tCO₂e",
      currentEmission: "15.1180 tCO₂e",
      anomalyCount: "1건 (조치완료)",
      countHighlight: false
    },
    {
      id: 6,
      badge: "STEP 6",
      title: "알루미늄 절단 공정",
      dotClass: "green",
      isCritical: false,
      indirectEmission: "13.3272 tCO₂e",
      currentEmission: "15.1180 tCO₂e",
      anomalyCount: "1건 (조치완료)",
      countHighlight: false
    },
    {
      id: 7,
      badge: "STEP 7",
      title: "아노다이징 공정",
      dotClass: "red",
      isCritical: true,
      indirectEmission: "13.3272 tCO₂e",
      currentEmission: "15.1180 tCO₂e",
      directEmission: "13.3272 tCO₂e",
      directCurrentEmission:"13.3000 tCO₂e",
      anomalyCount: "1건 (조치완료)",
      countHighlight: true
    },
    {
      id: 8,
      badge: "STEP 8",
      title: "구리스 디스펜싱 공정",
      dotClass: "green",
      isCritical: false,
      indirectEmission: "13.3272 tCO₂e",
      currentEmission: "15.1180 tCO₂e",
      anomalyCount: "1건 (조치완료)",
      countHighlight: false
    }
        ];

    // 상세 정보 데이터 예시 (실제 데이터는 API 연동 후 동적으로 관리)
    const detailsData = [
            {
            id: 1,
            title: "STEP 1 · 빌릿 가열 공정",
            headerClass: "text-success",
            icon: "✓",
            metrics: [
                {
                name: "전력 사용량 (kWh)",
                standard: "147.03",
                actual: "139.8389",
                deviation: "-4.89%",
                status: "정상",
                isCritical: false
                }
            ]
            },
            {
            id: 2,
            title: "STEP 2 · 간접 압출 공정",
            headerClass: "text-critical",
            icon: "⚠",
            metrics: [
                {
                name: "전력 사용량 (kWh)",
                standard: "73.51",
                actual: "121.001",
                deviation: "+64.60%",
                status: "Critical",
                isCritical: true
                }
            ]
            },
            {
            id: 3, 
            title: "STEP 3 · 인발 공정",
            headerClass: "text-success",
            icon: "✓",
            metrics: [
                {
                name: "전력 사용량 (kWh)",
                standard: "147.03",
                actual: "139.8389",
                deviation: "-4.89%",
                status: "정상",
                isCritical: false
                }
            ]
            },
            {
            id: 4, 
            title: "STEP 4 · 알루미늄 시효 공정",
            headerClass: "text-success",
            icon: "✓",
            metrics: [
                {
                name: "전력 사용량 (kWh)",
                standard: "73.51",
                actual: "121.001",
                deviation: "+64.60%",
                status: "정상",
                isCritical: false
                }
            ]
            },
            {
            id: 5, 
            title: "STEP 5 · 4축 CNC 가공 공정",
            headerClass: "text-success",
            icon: "✓",
            metrics: [
                {
                name: "전력 사용량 (kWh)",
                standard: "73.51",
                actual: "121.001",
                deviation: "+64.60%",
                status: "정상",
                isCritical: false
                }
            ]
            },
            {
            id: 6, 
            title: "STEP 6 · 알루미늄 절단 공정",
            headerClass: "text-success",
            icon: "✓",
            metrics: [
                {
                name: "전력 사용량 (kWh)",
                standard: "73.51",
                actual: "121.001",
                deviation: "+64.60%",
                status: "정상",
                isCritical: false
                }
            ]
            },
            {
            id: 7, 
            title: "STEP 7 · 아노다이징 공정",
            headerClass: "text-critical",
            icon: "⚠",
            metrics: [
                {
                name: "전력 사용량 (kWh)",
                standard: "73.51",
                actual: "121.001",
                deviation: "+64.60%",
                status: "critical",
                isCritical: true
                }
            ]
            },
            {
            id: 8, 
            title: "STEP 8 · 구리스 디스펜싱 공정",
            headerClass: "text-success",
            icon: "✓",
            metrics: [
                {
                name: "전력 사용량 (kWh)",
                standard: "73.51",
                actual: "121.001",
                deviation: "+64.60%",
                status: "정상",
                isCritical: false
                }
            ]
            }
        ];
    // 상세 정보 열기 함수
    const pipelineStepClick = (step) => {
        setActiveStep(prevStep => prevStep === step ? null : step);
        };
    // 상세 정보 닫기 함수
    const closeDetail = () => {
                    setActiveStep(null);
                };

    // 로그 데이터 예시 (실제 데이터는 API 연동 후 동적으로 관리)
    const [logs, setLogs] = useState([
    {
      id: 1,
      date: "2026-06-05 14:15:22",
      machineName: "용해로 #02 (MC-002)",
      scope: "1",
      metrics: "342.173(67.5456)",
      level: "Critical",
      levelColor: "#e53e3e",
      isActioned: false
    },
    {
      id: 2,
      date: "2026-06-05 11:02:45",
      machineName: "CNC 가공기 #05 (MC-011)",
      scope: "2",
      metrics: "101.4179(84.6399)",
      level: "Warning",
      levelColor: "#dd6b20",
      isActioned: true
    }
  ]);


  return (
   <>
    <div className="main-wrapper">

      {/* 본문 영역 */}
      <div className="content-area">
        <div className="page-header-flex">
          <h2 className="page-title">이상치 탐지</h2>
        </div>   
        
        <div className="pipeline-section-wrapper">
          <div className="pipeline-header-flex">
            <h2>작일 공정 파이프라인 이상치 현황</h2>
          </div>

          <div className="pipeline-flex-container">
            {stepData.map((step, index) => (
                <React.Fragment key={step.id}>
                {/* STEP 카드 영역 */}
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
                    <span className={`value ${step.id === 2 ? 'highlight' : ''}`}>{step.indirectEmission}</span>
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
                {index < stepData.length - 1 && <div className="arrow-icon">➔</div>}
                </React.Fragment>
            ))}
            </div>

            {detailsData.map((detail) => {
            if (activeStep !== detail.id) return null;
            return (
            <div className="pipeline-detail-box" key={detail.id}>
                {/* 헤더 영역 */}
                <div className={`detail-header ${detail.headerClass}`}>
                <span>{detail.icon} {detail.title}</span>
                <button type="button" className="close-btn" onClick={(e) => {
                    e.stopPropagation();
                    setActiveStep(null);
                }}>✕</button>
                </div>

                {/* 본문 테이블 영역 */}
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
                        <td className={`status-text ${metric.isCritical ? "critical" : "normal"}`}>
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
                <div className="select_label"><span>연도 선택</span></div>
                <div className="toggle-content">
                    <select 
                    className="year-select btn-filter-tab" 
                    value={selectedYear}
                    onChange={(e) => {
                        setSelectedYear(e.target.value);
                        setSelectedQuarter("all");
                        setSelectedMonth("none");
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
                    <div className="select_label"><span>분기 선택</span></div>
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
                    <div className="select_label"><span>월 선택</span></div>
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
                    onClick={periodSearch}>
                    조회
                </button>
                </div>
            </div>
            </div> 

        {/* 차트 영역 */}
        <div className="dashboard-chart-grid full-width-grid">
          <div className="chart-card">
            <h3 className="chart-title">설비별 이상치 발생 횟수 현황</h3>
            <div className="chart-container chart-height-machine">
                <canvas ref={chartRef} id="anomalyMachineChart"></canvas>
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
                  <th>발생 일시</th>
                  <th>설비명 (ID)</th>
                  <th>SCOPE</th>
                  <th>측정값 (기준치)</th>
                  <th>위험 등급</th>
                  <th>조치 상태</th>
                </tr>
              </thead>
             <tbody>
                {logs.map((log) => (
                    <tr key={log.id}>
                    <td className="text-center">{log.date}</td>
                    <td className="font-bold text-center">{log.machineName}</td>
                    <td className="text-center font-bold">{log.scope}</td>
                    <td className="text-center">{log.metrics}</td>
                    <td className="text-center">
                        <span style={{ color: log.levelColor, fontWeight: 600 }}>{log.level}</span>
                    </td>
                    <td className="text-center">
                        <button 
                        onClick={(e) => openStatusModal(e, log)}
                        className={`btn-filter-tab ${!log.isActioned ? "active" : ""}`} 
                        style={{ 
                            padding: '2px 6px', 
                            fontSize: '11px', 
                            backgroundColor: !log.isActioned ? '#e53e3e' : 'transparent',
                            borderColor: !log.isActioned ? '#e53e3e' : '#ccc',
                            color: !log.isActioned ? '#fff' : '#333',
                            cursor: 'pointer'
                        }}
                        >
                        {log.isActioned ? actionOptions[1].label : actionOptions[0].label}
                        </button>
                    </td>
                    </tr>
                ))}
                </tbody>
              <tfoot>
                <tr className="table-summary-row">
                  <td colSpan={3} className="text-center">선택기간 총 이상 발생 건수</td>
                  <td className="text-center">-</td>
                  <td className="text-center total-amount">74 건</td>
                  <td className="text-center" style={{ color: '#e53e3e' }}>미조치 1건</td>
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
                <option value="0">미조치</option>
                <option value="1">조치완료</option>
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
          </div>
        </div>
        {/* 페이지네이션 */}
    </div>
          <div className="pagination-info">
            전체 <span>74</span>건
          </div>
        <div className="pagination-container">
          <div className="pagination-buttons">
            <button type="button" className="btn-page first" title="첫 페이지" disabled>&laquo;</button>
            <button type="button" className="btn-page prev" title="이전 페이지" disabled>&lsaquo;</button>
            <button type="button" className="btn-page-num active">1</button>
            <button type="button" className="btn-page-num">2</button>
            <button type="button" className="btn-page-num">3</button>
            <button type="button" className="btn-page-num">4</button>
            <button type="button" className="btn-page-num">5</button>
            <button type="button" className="btn-page next" title="다음 페이지">&rsaquo;</button>
            <button type="button" className="btn-page last" title="마지막 페이지">&raquo;</button>
        </div>
      </div>
  </> 
  )
};

export default Anomaly;