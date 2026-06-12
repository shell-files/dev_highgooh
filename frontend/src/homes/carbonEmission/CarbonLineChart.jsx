import React, { useMemo } from 'react';
import { Line } from 'react-chartjs-2';
import { getLineChartData, getBarChartData, getDoughnutChartData } from '@homes/carbonEmission/ChartData.jsx';

const CarbonLineChart = ({ chartData }) => {

    console.log(chartData);

    const data = {
        labels: chartData?.labels ?? [],
        datasets: [
            {
                label: '탄소 배출량',
                data: chartData?.data ?? [],
                borderColor: '#03a94d',
                backgroundColor: 'rgba(3,169,77,0.08)',
                borderWidth: 3,
                tension: 0.3,
                fill: true,
                pointBackgroundColor: '#03a94d',
                pointRadius: 4,
            }
        ]
    };

    const options = {
    responsive: true,
    maintainAspectRatio: false,
    animation: {
        duration: 500
    }
    };

    return (
        <Line
            data={data}
            options={options}
        />
    );
};

export default CarbonLineChart;
