import React from 'react';
import { Bar } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const CarbonBarChart = ({ chartData }) => {
  const data = {
    labels: ['빌릿 가열기', '간접 압출기', '인발기', '알루미늄 시효로', '4축 CNC', '알루미늄 절단기', '아노다이징', '자동 구리스 디스펜서 시스템'],
    datasets: [{
      label: '탄소 배출량 (tCO2eq/yr)',
      data: chartData,
      backgroundColor: '#03a94d',
      borderRadius: 4
    }]
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false }
    }
  };

  return <Bar data={data} options={options} />;
};

export default CarbonBarChart;