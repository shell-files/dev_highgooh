import { React, useState, useEffect, useRef } from 'react';
import { POST } from "@utils/Network";
import '@styles/pcf.css';
import CarbonBarChart from '@components/UI/CarbonBarChart.jsx';
import CarbonDoughnutChart from '@components/UI/CarbonDoughnutChart.jsx';
import CarbonLineChart from '@components/UI/CarbonLineChart.jsx';
import processCarbonData from '@components/UI/CarbonTableData.jsx';
import { getLineChartData, getBarChartData, getDoughnutChartData } from '@components/UI/CarbonChartData.jsx';





const CarbonDashboard = () => {

  // 필터 상태 관리
  const [selectedYear, setSelectedYear] = useState("2026");
  const [selectedQuarter, setSelectedQuarter] = useState("");
  const [selectedMonth, setSelectedMonth] = useState("");
  const [searchYear, setSearchYear] = useState("2026");
  const [searchQuarter, setSearchQuarter] = useState("");
  const [searchMonth, setSearchMonth] = useState("");


  // 기본 조회 데이터
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [finalData, setFinalData] = useState([]);

  // 연도 드롭박스 데이터
  const [yearOptions, setYearOptions] = useState([])

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

  // 차트 데이터
  const [chartData, setChartData] = useState({ line: [], bar: [], doughnut: [] });


  // 조회 버튼 이벤트
  const periodSearch = () => {
    const params = { selectedYear, selectedQuarter, selectedMonth };
    POST("/carbon", params).then(res => {
      if (res.status === true) {
        setSelectedYear(params.selectedYear);
        setSelectedQuarter(params.selectedQuarter);
        setSelectedMonth(params.selectedMonth);
        setSearchYear(params.selectedYear);
        setSearchQuarter(params.selectedQuarter);
        setSearchMonth(params.selectedMonth);
        const line = getLineChartData(res.data);
        const bar = getBarChartData(res.data, ['빌릿 가열기', '간접 압출기', '인발기', '알루미늄 시효로', '4축 CNC', '알루미늄 절단기', '아노다이징', '자동 구리스 디스펜서 시스템']);
        const doughnut = getDoughnutChartData(res.data);
        
        setChartData({ line, bar, doughnut });

        const processed = processCarbonData(res.data);
        setFinalData(processed);
      }
    })
  };
  // 총계 계산
  const totalEmissionSum = finalData.reduce((acc, item) => acc + item.emission, 0);

  // 차트 타이틀
  const chartTitle =
  searchYear === ''
    ? '전체 기간 탄소 배출량 변경 추이 (tCO₂eq)'
    : searchMonth
      ? `${searchYear}년 ${searchMonth}월 탄소 배출량 변경 추이 (tCO₂eq)`
      : searchQuarter
        ? `${searchYear}년 ${searchQuarter}분기 탄소 배출량 변경 추이 (tCO₂eq)`
        : `${searchYear}년 탄소 배출량 변경 추이 (tCO₂eq)`;

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const response = await POST('/carbon', {
          selectedYear: '2026',
          selectedQuarter: '',
          selectedMonth: ''
        });

        if (response.status === true) {
          // 1. 원본 데이터 저장
          setData(response.data);

          // 2. 가공 로직 바로 실행
          const processed = processCarbonData(response.data);
          setFinalData(processed);

        }
      } catch (error) {
        console.error("데이터 조회 실패:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
    periodSearch();
  }, []);

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
                  setSelectedQuarter("");
                  setSelectedMonth("");
                }}
              >
                <option value="">전체</option>
                <option value="2026">2026년</option>
                <option value="2025">2025년</option>
                <option value="2024">2024년</option>
                <option value="2023">2023년</option>
              </select>
            </div>
          </div>

          {selectedYear !== "" && (
            <>
              {/* 분기 선택 영역 */}
              <div className="toggle-group-item id_quarter">
                <div className="select_label"><label>분기 선택</label></div>
                <div className="toggle-content">
                  {[
                    { code: "", name: "전체" },
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
                    <option value="">선택</option>
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
          <h3 className="chart-title">{chartTitle}</h3>
          <div className="chart-container" style={{ height: '300px' }}>
            <CarbonLineChart chartData={chartData.line} />
          </div>
        </div>
      </div>

      <div className="dashboard-chart-grid">
        <div className="chart-card">
          <h3 className="chart-title">공정별 탄소 배출량 현황 (tCO₂eq)</h3>
          <div className="chart-container" style={{ height: '300px' }}>
            <CarbonBarChart chartData={chartData.bar} />
          </div>
        </div>

        <div className="chart-card">
          <h3 className="chart-title">Scope별 배출 비율</h3>
         <div className="chart-container" style={{ height: '300px' }}>
            <CarbonDoughnutChart chartData={chartData.doughnut} />
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
              {finalData && finalData.map((item, index) => (
                <tr key={index}>
                  <td className="font-bold">{item.process}</td>
                  <td>{item.scope}</td>
                  <td>{item.source}</td>
                  <td className="text-center">{item.amount.toLocaleString()}</td>
                  <td className="text-center">{item.unit}</td>
                  <td className="text-center font-bold">{item.emission.toLocaleString()}</td>
                  <td className="text-center color-green">{item.percent}</td>
                </tr>
              ))}
            </tbody>
            <tfoot>
              <tr className="table-summary-row">
                <td colSpan="3" className="text-center">합 계</td>
                <td className="text-center">-</td>
                <td className="text-center">-</td>
                <td className="text-center total-amount">
                  {totalEmissionSum.toLocaleString(undefined, { minimumFractionDigits: 2 })}
                </td>
                <td className="text-center">{finalData.length > 0 ? "100.0%" : "0.0%"}</td>
              </tr>
            </tfoot>
          </table>
        </div>
      </div>
    </div>
  );
};

export default CarbonDashboard;