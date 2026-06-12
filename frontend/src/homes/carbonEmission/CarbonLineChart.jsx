import React, { useMemo } from 'react';
import { Line } from 'react-chartjs-2';
import { getLineChartData, getBarChartData, getDoughnutChartData } from '@homes/carbonEmission/ChartData.jsx';

// ... 기존 ChartJS 등록 코드 동일

const CarbonLineChart = ({ rawData, selectedYear, selectedMonth }) => {
  // useMemo를 사용하여 selectedMonth나 rawData가 바뀔 때만 데이터를 재계산
  const chartData = useMemo(() => {
    const { data, labels } = getLineChartData(rawData, selectedYear,selectedMonth);
    return {
      labels,
      datasets: [{
        label: '탄소 배출량',
        data: data,
        borderColor: '#03a94d',
        backgroundColor: 'rgba(3, 169, 77, 0.08)',
        borderWidth: 3,
        tension: 0.3,
        fill: true,
        pointBackgroundColor: '#03a94d',
        pointRadius: 4,
      }]
    };
    console.log("차트 컴포넌트가 받는 데이터:", chartData)
  }, [rawData, selectedYear, selectedMonth]);

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    // 데이터 변경 시 애니메이션 처리
    animation: { duration: 500 }
  };

  return <Line data={chartData} options={options} />;
};

export default CarbonLineChart;
