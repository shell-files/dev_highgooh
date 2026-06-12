import React from 'react';
import { Doughnut } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend
} from 'chart.js';

ChartJS.register(ArcElement, Tooltip, Legend);

const CarbonDoughnutChart = ({ chartData }) => {
  // 데이터 설정
  const data = {
    labels: ['Scope 1 (직접배출)', 'Scope 2 (간접배출)'],
    datasets: [{
      data: chartData,
      backgroundColor: ['#dd6b20', '#03a94d'],
      borderWidth: 2,
      hoverOffset: 4
    }]
  };

  // 차트 옵션 설정
  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'bottom',
        labels: { 
          boxWidth: 12, 
          font: { size: 11 } 
        }
      }
    },
    cutout: '65%' // 도넛 두께 조절
  };

  return <Doughnut data={data} options={options} />;
};

export default CarbonDoughnutChart;