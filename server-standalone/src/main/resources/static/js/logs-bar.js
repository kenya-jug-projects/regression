const ctx = document.getElementById('logSeverityChart').getContext('2d');
const hours = [...Array(24).keys()].map(h => `${h.toString().padStart(2, '0')}:00`);

const logChartData = [[${logChartData.hourlyLogStatsList}]];

const datasets = logChartData.map(entry => ({
    label: entry.severity,
    data: entry.hourlyCounts,
    backgroundColor: entry.severity === 'ERROR' ? '#e53935' :
                     entry.severity === 'WARN' ? '#fbc02d' : '#1e88e5',
    stack: 'logs'
}));

new Chart(ctx, {
    type: 'bar',
    data: {
        labels: hours,
        datasets: datasets
    },
    options: {
        responsive: true,
        plugins: {
            legend: {
                labels: {
                    color: '#f5f5f5'
                }
            },
            title: {
                display: true,
                text: 'Hourly Log Count by Severity (Today)',
                color: '#f5f5f5'
            }
        },
        scales: {
            x: {
                stacked: true,
                ticks: {
                    color: '#ccc'
                },
                grid: {
                    color: '#444'
                }
            },
            y: {
                stacked: true,
                beginAtZero: true,
                ticks: {
                    color: '#ccc'
                },
                grid: {
                    color: '#444'
                }
            }
        }
    }
});
