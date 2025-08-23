// Applications List Pie Chart
const connectedAppLogsCtx = document.getElementById('connectedAppLogsChart').getContext('2d');
const myPieChart = new Chart(connectedAppLogsCtx, {
    type: 'pie',
    data: {
        labels: ['Android', 'Windows', 'Linux', 'macOS', 'iOS'],
        datasets: [{
            label: 'Logs Distribution',
            data: [30, 25, 20, 15, 10],
            backgroundColor: [
                'rgba(75, 192, 192, 0.7)',
                'rgba(255, 99, 132, 0.7)',
                'rgba(255, 206, 86, 0.7)',
                'rgba(153, 102, 255, 0.7)',
                'rgba(54, 162, 235, 0.7)'
            ],
            borderColor: [
                'rgba(255, 255, 255, 1)'
            ],
            borderWidth: 2
        }]
    },
    options: {
        responsive: true,
        plugins: {
            legend: {
                position: 'right'
            },
            title: {
                display: true,
                text: 'Qty of Logs by Connected Apps'
            }
        }
    }
});