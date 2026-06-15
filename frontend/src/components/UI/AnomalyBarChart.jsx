import React, { useEffect, useRef } from 'react';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

const AnomalyBarChart = ({ dataValues = { label: [], data: [] } }) => {
  const chartRef = useRef(null);
  const chartInstance = useRef(null);

  useEffect(() => {
    if (!chartRef.current) return;

    // 1. 차트 인스턴스가 이미 존재하면 파괴 대신 데이터만 업데이트
    if (chartInstance.current) {
      chartInstance.current.data.labels = dataValues.label;
      chartInstance.current.data.datasets[0].data = dataValues.data;
      
      // 'none' 옵션 없이 update() 호출 시 애니메이션이 자동 실행됨
      chartInstance.current.update(); 
    } 
    // 2. 차트가 없으면 새로 생성
    else {
      const ctx = chartRef.current.getContext('2d');
      chartInstance.current = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: dataValues.label,
          datasets: [{
            label: '이상치 발생 횟수 (건)',
            data: dataValues.data,
            backgroundColor: '#dd6b20',
            borderRadius: 4,
            borderWidth: 0
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          animation: {
            duration: 1000,
            easing: 'easeOutQuart'
          },
          plugins: {
            legend: { display: false }
          },
          scales: {
            y: { beginAtZero: true, grid: { drawBorder: false } },
            x: { 
              grid: { display: false },
              ticks: { maxRotation: 45, minRotation: 45, autoSkip: false }
            }
          }
        }
      });
    }
  }, [dataValues]);

  return (
    <div style={{
      position: 'relative',
      width: '100%',
      height: '300px',
      display: 'block'
    }}>
      <canvas ref={chartRef} />
    </div>
  );
};

export default AnomalyBarChart;