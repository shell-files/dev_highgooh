import React, { useEffect, useRef } from 'react';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

const AnomalyBarChart = ({ dataValues = { label: [], data: [] } }) => {
  const chartRef = useRef(null);
  const chartInstance = useRef(null);

  useEffect(() => {
    // 캔버스가 존재하지 않으면 실행 안 함 (에러 방지)
    if (!chartRef.current) return;

    // 1. 기존 차트 인스턴스 파괴
    if (chartInstance.current) {
      chartInstance.current.destroy();
      chartInstance.current = null;
    }

    // 2. 차트 생성
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
        maintainAspectRatio: false, // 이 설정이 차트의 가로세로비를 부모에 맞춤
        plugins: {
          legend: { display: false }
        },
        scales: {
          y: {
            beginAtZero: true,
            grid: { drawBorder: false }
          },
          x: {
            grid: { display: false },
            ticks: {
              maxRotation: 45, // 최대 기울기 (45도)
              minRotation: 45, // 최소 기울기 (45도)
              autoSkip: false  // 라벨이 많아도 생략하지 않고 다 표시
            }
          }
        }
      }
    });

    // 3. 언마운트 시 에러 방지용 파괴
    return () => {
      if (chartInstance.current) {
        chartInstance.current.destroy();
        chartInstance.current = null;
      }
    };
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