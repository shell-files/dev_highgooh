import { React, useState, useEffect, useRef } from 'react';
import { POST } from "@utils/Network";
import '@styles/pcf.css';
import CarbonBarChart from '@components/UI/CarbonBarChart.jsx';
import CarbonDoughnutChart from '@components/UI/CarbonDoughnutChart.jsx';
import CarbonLineChart from '@components/UI/CarbonLineChart.jsx';
import processCarbonData from '@components/UI/CarbonTableData.jsx';
import { getLineChartData, getBarChartData, getDoughnutChartData } from '@components/UI/CarbonChartData.jsx';
import * as XLSX from 'xlsx';




const CarbonDashboard = () => {

  const currentYear = new Date().getFullYear();
  // 필터 상태 관리
  const [selectedYear, setSelectedYear] = useState(String(currentYear));
  const [selectedQuarter, setSelectedQuarter] = useState("");
  const [selectedMonth, setSelectedMonth] = useState("");
  const [confirmedFilter, setConfirmedFilter] = useState({ year: "2026", quarter: "", month: "" });
  const tableColList = ["공정명", "Scope 구분", "에너지원", "활동 데이터(사용량)", "단위", "배출량 (tCO₂eq)", "비율 (%)"]
  const [yearData, setYearData] = useState(String(currentYear))
  const [quarterData, setQuarterData] = useState("")
  const [monthData, setMonthData] = useState("")

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
    setYearData(selectedYear)
    setQuarterData(selectedQuarter)
    setMonthData(selectedMonth)

    POST("/carbon", params).then(res => {
      if (res.status === true) {
        setSelectedYear(params.selectedYear);
        setSelectedQuarter(params.selectedQuarter);
        setSelectedMonth(params.selectedMonth);
        setConfirmedFilter({
          year: selectedYear,
          quarter: selectedQuarter,
          month: selectedMonth
        });
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
    confirmedFilter.year === ''
      ? '전체 기간 탄소 배출량 변경 추이 (tCO₂eq)'
      : confirmedFilter.month
        ? `${confirmedFilter.year}년 ${confirmedFilter.month}월 탄소 배출량 변경 추이 (tCO₂eq)`
        : confirmedFilter.quarter
          ? `${confirmedFilter.year}년 ${confirmedFilter.quarter}분기 탄소 배출량 변경 추이 (tCO₂eq)`
          : `${confirmedFilter.year}년 탄소 배출량 변경 추이 (tCO₂eq)`;

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

  const handleDownload = (data) => {
    // 1. 데이터를 기반으로 워크시트(Worksheet) 생성

    const replaceData = []
    data.map((v) => { replaceData.push({ "공정명": v.process, "Scope 구분": v.scope, "에너지원": v.source, "활동 데이터(사용량)": v.amount, "단위": v.unit, "배출량 (tCO₂eq)": v.emission, "비율 (%)": v.percent }) })

    const worksheet = XLSX.utils.json_to_sheet(replaceData);

    // (선택) 엑셀 시트의 헤더(열 이름)를 한글로 예쁘게 변경하고 싶을 때
    XLSX.utils.sheet_add_aoa(worksheet, [tableColList], { origin: "A1" });

    const period = quarterData ? `${quarterData}분기` : (monthData ? `${monthData}월` : "");
    const fileName = [
      "공정별 상세 탄소 배출량 명세",
      `${yearData}년도`,
      period
    ].filter(Boolean).join("_"); // 빈 문자열은 제거하고 '_'로 연결

    // 2. 새로운 워크북(Workbook)을 생성하고 워크시트 추가
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, fileName);
    
    XLSX.writeFile(workbook, `${fileName}.xlsx`);
    // 3. 엑셀 파일 작성 및 다운로드 실행
    // 파일명은 원하는 대로 지정할 수 있습니다.
  };

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
          <button className="btn-excel-download" onClick={() => { handleDownload(finalData) }}>액셀 다운로드</button>
        </div>
        <div className="table-responsive">
          <table className="dashboard-data-table">
            <thead>
              <tr>
                {
                  tableColList.map((v, i) => <th key={i}>{v}</th>)
                }
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