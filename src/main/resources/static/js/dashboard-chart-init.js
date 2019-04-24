// Use Morris.Area instead of Morris.Line
var first = 50;
var second = 35;
var third = 10;
var fourth = 5;

Morris.Donut({
    element: 'graph-donut',
    data: [
        {value: first, label: '最受欢迎', formatted: '50%' },
        {value: second, label: '第二', formatted: '35%' },
        {value: third, label: '第三', formatted: '10%' },
        {value: fourth, label: '第四', formatted: '5%' }
    ],
    backgroundColor: false,
    labelColor: '#fff',
    colors: [
        '#4acacb','#6a8bc0','#5ab6df','#fe8676'
    ],
    formatter: function (x, data) { return data.formatted; }
});


$(function() {

    var d1 = [
        [1, 620],
        [2, 437],
        [3, 361],
        [4, 549],
        [5, 618],
        [6, 570],
        [7, 758]
    ];
  /*  var d2 = [
        [0, 401],
        [1, 520],
        [2, 337],
        [3, 261],
        [4, 449],
        [5, 518],
        [6, 470],
        [7, 658],
        [8, 558],
        [9, 438],
        [10, 388]
    ];
*/
    var data = ([{
       /* label: "New Visitors",*/
        data: d1,
        lines: {
            show: true,
            fill: true,
            fillColor: {
                colors: ["rgba(255,255,255,.4)", "rgba(183,236,240,.4)"]
            }
        }
    },
      /*  {
            label: "Unique Visitors",
            data: d2,
            lines: {
                show: true,
                fill: true,
                fillColor: {
                    colors: ["rgba(255,255,255,.0)", "rgba(253,96,91,.7)"]
                }
            }
        }*/
    ]);

    var options = {
        grid: {
            backgroundColor:
            {
                colors: ["#ffffff", "#f4f4f6"]
            },
            hoverable: true,
            clickable: true,
            tickColor: "#eeeeee",
            borderWidth: 1,
            borderColor: "#eeeeee"
        },
        // Tooltip
        tooltip: true,
        tooltipOpts: {
            content: "%s X: %x Y: %y",
            shifts: {
                x: -60,
                y: 25
            },
            defaultTheme: false
        },
        legend: {
            labelBoxBorderColor: "#000000",
            container: $("#main-chart-legend"), //remove to show in the chart
            noColumns: 0
        },
        series: {
            stack: true,
            shadowSize: 0,
            highlightColor: 'rgba(000,000,000,.2)'
        },
//        lines: {
//            show: true,
//            fill: true
//
//        },
        points: {
            show: true,
            radius: 3,
            symbol: "circle"
        },
        colors: ["#5abcdf", "#ff8673"]
    };
    var plot = $.plot($("#main-chart #main-chart-container"), data, options);
});