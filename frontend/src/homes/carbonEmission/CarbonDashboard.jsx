import { React, useState } from 'react';
import '@styles/pcf.css';
import CarbonBarChart from '@homes/carbonEmission/CarbonBarChart.jsx';
import CarbonDoughnutChart from '@homes/carbonEmission/CarbonDoughnutChart.jsx';
import CarbonLineChart from '@homes/carbonEmission/CarbonLineChart.jsx';


const CarbonDashboard = () => {

  const dashboardData = {
    "all": {
      bar: [390.5, 95.2, 205.8, 3.2, 45.6, 85.0, 30.5, 7.8], // 4년 치 합산치
      line: [65.2, 78.5, 72.1, 88.5, 95.8, 75.2, 85.0, 92.5, 76.8, 85.6, 80.2, 90.5],
      doughnut: [205.8, 664.2] // [Scope1 합계, Scope2 합계]
    },
    "2026": {
      bar: [128.55, 29.75, 68.91, 0.92, 13.4, 27.56, 9.2, 2.3],
      line: [18.2, 22.4, 19.8, 25.1, 28.6, 21.3, 24.7, 26.2, 20.1, 23.5, 21.8, 24.77],
      doughnut: [68.91, 185.86]
    },
    "2025": {
      bar: [110.0, 28.0, 62.0, 0.8, 12.0, 24.0, 8.5, 2.0],
      line: [16.5, 20.1, 18.2, 23.5, 25.8, 19.5, 22.0, 24.5, 18.5, 21.0, 19.5, 23.0],
      doughnut: [62.0, 167.5]
    },
    "2024": {
      bar: [85.0, 22.0, 45.0, 0.7, 10.0, 19.0, 7.0, 1.8],
      line: [14.2, 17.8, 16.5, 20.1, 22.5, 17.5, 19.5, 21.0, 17.0, 19.5, 18.0, 21.5],
      doughnut: [45.0, 135.5]
    },
    "2023": {
      bar: [66.95, 15.45, 29.89, 0.78, 10.2, 14.44, 5.8, 1.7],
      line: [16.3, 18.2, 17.6, 19.8, 18.9, 16.9, 18.8, 20.8, 21.2, 21.6, 20.9, 21.23],
      doughnut: [29.89, 175.34]
    }
  };

  // 필터 상태 관리
  const [selectedYear, setSelectedYear] = useState("all");
  const [selectedQuarter, setSelectedQuarter] = useState("all");
  const [selectedMonth, setSelectedMonth] = useState("none");
  const [confirmedYear, setConfirmedYear] = useState('all');

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
    };
    setConfirmedYear(selectedYear);
  };

  // 공정별 상세 데이터 (예시)
  const processData = [
    { process: '용해/성형', scope: 'Scope 2 (간접)', source: '전력 (LNG 컴비네이션)', amount: '245,310', unit: 'kWh', emission: 128.55, percent: '51.1%' },
    { process: '시효경화', scope: 'Scope 1 (직접)', source: '도시가스 (LNG)', amount: '31,250', unit: 'Nm³', emission: 68.91, percent: '27.4%' },
    { process: '인장/절단', scope: 'Scope 2 (간접)', source: '전력', amount: '56,770', unit: 'kWh', emission: 29.75, percent: '11.8%' },
    { process: '아노다이징', scope: 'Scope 2 (간접)', source: '전력', amount: '52,590', unit: 'kWh', emission: 27.56, percent: '10.9%' },
  ];


  return (
    <div id="carbon-page">
      <div className="page-header-flex">
        <h2 className="page-title">탄소배출량 대시보드</h2>
      </div>

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
              onClick={periodSearch}>
              조회
            </button>
          </div>
        </div>
      </div>

      <div className="dashboard-chart-grid" style={{ gridTemplateColumns: '1fr', marginBottom: '1.25rem' }}>
        <div className="chart-card">
          <h3 className="chart-title">선택기간별 탄소 배출량 변경 추이 (tCO₂eq)</h3>
          <div className="chart-container" style={{ height: '300px' }}>
            <CarbonLineChart chartData={dashboardData[confirmedYear]?.line || []} />
          </div>
        </div>
      </div>

      <div className="dashboard-chart-grid">
        <div className="chart-card">
          <h3 className="chart-title">공정별 탄소 배출량 현황 (tCO₂eq)</h3>
          <div className="chart-container" style={{ height: '300px' }}>
            <CarbonBarChart chartData={dashboardData[confirmedYear]?.bar || []} />
          </div>
        </div>

        <div className="chart-card">
          <h3 className="chart-title">Scope별 배출 비율</h3>
          <div className="chart-container" style={{ height: '300px' }}>
            <CarbonDoughnutChart chartData={dashboardData[confirmedYear]?.doughnut || []} />
          </div>
        </div>
      </div>

      <div className="dashboard-table-card">
        <div className="table-header-flex">
          <h3 className="table-title">공정별 상세 탄소 배출량 명세</h3>
          <button className="btn-excel-download">액셀 다운로드</button>
        </div>
        <div className="table-responsive">
          <table className="dashboard-data-table">
            <thead>
              <tr>
                <th>공정명</th>
                <th>Scope 구분</th>
                <th>에너지원</th>
                <th>활동 데이터(사용량)</th>
                <th>단위</th>
                <th>배출량 (tCO₂eq)</th>
                <th>비율 (%)</th>
              </tr>
            </thead>
            <tbody>
              {processData.map((item, index) => (
                <tr key={index}>
                  <td className="font-bold">{item.process}</td>
                  <td>{item.scope}</td>
                  <td>{item.source}</td>
                  <td className="text-center">{item.amount}</td>
                  <td className="text-center">{item.unit}</td>
                  <td className="text-center font-bold">{item.emission}</td>
                  <td className="text-center color-green">{item.percent}</td>
                </tr>
              ))}
            </tbody>
            <tfoot>
              <tr className="table-summary-row">
                <td colSpan="3" className="text-center">합 계</td>
                <td className="text-center">-</td>
                <td className="text-center">-</td>
                <td className="text-center total-amount">254.77</td>
                <td className="text-center">100.0%</td>
              </tr>
            </tfoot>
          </table>
        </div>
      </div>
    </div>
  );
};

export default CarbonDashboard;