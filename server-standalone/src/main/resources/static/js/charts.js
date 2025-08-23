/*
MIT License

Copyright (c) 2025 Kenya JUG

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
// Dashboard All Today Logs Time Series Chart
const ctx = document.getElementById('todayLogsChart').getContext('2d');
const logsChart = new Chart(ctx, {
    type: 'line',
    data: {
        labels: /*[[${logStats.timestamps}]]*/ ["08:00:00", "12:09:00", "16:11:00","17:00:00", "21:09:00", "22:11:00","22:20:00", "22:30:00", "22:30:11","22:31:00", "23:00:00", "23:40:00"],
        datasets: [{
            label: 'Logs per Day',
            data: /*[[${logStats.counts}]]*/ [5, 7, 7, 5, 9, 4, 5, 10, 14, 18, 25, 23],
            fill: false,
            borderColor: 'rgba(30, 144, 255, 1)',
            tension: 0.3
        }]
    },
    options: {
        responsive: true,
        plugins: {
            legend: { labels: { color: '#f5f5f5' } },
            title: {
                display: false
            }
        },
        scales: {
            x: {
                ticks: { color: '#f5f5f5' },
                grid: { color: '#333' }
            },
            y: {
                ticks: { color: '#f5f5f5' },
                grid: { color: '#333' }
            }
        }
    }
});