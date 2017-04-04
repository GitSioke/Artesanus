//a separate function for drawing data. Just call it wherever you need to redraw the chart
function drawChart(chartData){
    data = new google.visualization.DataTable();
    data.addColumn('string', 'Col1');
    data.addColumn('number', 'Col2');

    for (var i = 0; i < 5; i++)
    {
        ideasChart.data.addRow("data1", "data2");
    }

    chart = new google.visualization.ComboChart(document.getElementById('my-chart'));
    chart.draw(data, chartOptions);
}

//chartOptions to customize your chart, its text color, etc.

chartOptions = {
    chartType:"ComboChart",
    containerId:"visualization",
    stackSeries: true,
    isStacked : true,
    backgroundColor: '#242424',
    legend: 'none',
    tooltip:{
        trigger:'none'
    },
    enableInteractivity: false,
    colors : ['#6DB1E2','#FDCB34'],
    chartArea: {
        backgroundColor: {
            stroke: '#fff',
            strokeWidth: 1
        }
    },
    seriesDefaults: {

        rendererOptions: {
            barPadding: 0,
            barMargin: 10
        },
    },

    seriesType: "bars",
    bar:
    {
        groupWidth:"80%"
    },

    viewWindowMode : {
        max: 'pretty'
    }
};