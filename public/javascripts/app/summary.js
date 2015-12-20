$(document).ready(function(){
    new Summary();
    initBarChart1()
    initBarChart2()
});

var summaryData = undefined;

var Summary = function() {


    updateSummaryData();
    var initializer = setInterval(function() {
        if(summaryData != undefined) {
            console.log(summaryData);
            resetData();
            clearTimeout(initializer);
        }
    },50);


    function updateTiles() {
        for(var i=1;i<=7;i++) {
            $('#tile' + i).html(summaryData['tile' + i])
        }

    }

    function updateCharts() {
        var chart1 = $('#summary-chart1-div').highcharts();
        chart1.xAxis[0].setCategories(summaryData.chart1.mpsList , true, true);
        chart1.series[0].setData(summaryData.chart1.openData);
        chart1.series[1].setData(summaryData.chart1.closedData);

        var chart2 = $('#summary-chart2-div').highcharts();
        chart2.xAxis[0].setCategories(summaryData.chart2.mpsList , true, true);
        chart2.series[0].setData(summaryData.chart2.openData);
        chart2.series[1].setData(summaryData.chart2.closedData);
    }

    function resetData() {
        updateTiles();
        updateCharts();
    }


    function updateSummaryData(mode) {

        $.when(ajax_getSummary()).then(function(data){
            summaryData = {};
            var totalEvents = data.openEventsCount +  data.closedEventsCount
            var tile1 = totalEvents + " events" + " (0 warnings, " + totalEvents + " critical)";
            var tile2 = data.openEventsCount + " open" + " (" + data.openEventsOwnerCount + " owners)";
            var tile3 = data.closedEventsCount + " closed" + " (" + data.closedEventsOwnerCount + " owners)";

            var numPlatformIssues = (data['issueGroup'] != undefined) ?
                (data['issueGroup']['platform'] == undefined ? 0 : data.issueGroup.platform) : 0;
            var numSolutionsIssues = (data['issueGroup'] != undefined) ?
                (data['issueGroup']['solution'] == undefined ? 0 : data.issueGroup.platform) : 0;
            var numOpsIssues = (data['issueGroup'] != undefined) ?
                (data['issueGroup']['ops'] == undefined ? 0 : data.issueGroup.platform) : 0;
            var tile4 = numPlatformIssues + " Platform issues";
            var tile5 = numSolutionsIssues + " Solution issues";
            var tile6 = numOpsIssues + " Ops issues";
            var tile7 = data.bugOpenedCount + " new bug opened";

            summaryData.tile1 = tile1;
            summaryData.tile2 = tile2;
            summaryData.tile3 = tile3;
            summaryData.tile4 = tile4;
            summaryData.tile5 = tile5;
            summaryData.tile6 = tile6;
            summaryData.tile7 = tile7;


            //setting data for Events-By-Customer chart
            var openEventsByMps = data['openEventsByMps'];
            var closedEventsByMps = data['closedEventsByMps'];
            var mpsList = (Object.keys(openEventsByMps)).merge(Object.keys(closedEventsByMps));

            var openData = [];
            var closedData = [];
            $.each(mpsList, function(index,mps) {
                openData.push(openEventsByMps[mps] == undefined ? 0 : openEventsByMps[mps]);
                closedData.push((closedEventsByMps[mps] == undefined ? 0 : closedEventsByMps[mps]));
            });

            summaryData['chart1'] = {
                mpsList: mpsList,
                openData: openData,
                closedData: closedData
            };


            //setting data for events-by-clients chart
            var openEventsByClient = data['openEventsByClient'];
            var closedEventsByClient = data['closedEventsByClient'];
            mpsList = (Object.keys(openEventsByClient)).merge(Object.keys(closedEventsByClient));

            openData = [];
            closedData = [];
            $.each(mpsList, function(index,mps) {
                openData.push(openEventsByClient[mps] == undefined ? 0 : openEventsByClient[mps]);
                closedData.push((closedEventsByClient[mps] == undefined ? 0 : closedEventsByClient[mps]));
            });

            summaryData['chart2'] = {
                mpsList: mpsList,
                openData: openData,
                closedData: closedData
            };


            resetData();


        });

    }


    function ajax_getSummary(data) {
        return ($.ajax({
            type: "GET",
            url: "v1/api/summary/all",
            data: data
        }))
    }

};


function getChart1Template()  {

    var data = {
        chart: {
            type: 'bar'
        },
        credits: {
            enabled: false
        },
        title: {
            text: 'Events per Customer'
        },
        xAxis: {
            min: 0,
            minTickInterval:1,
            tickInterval: 1,
            allowDecimals: false,
            categories: []//["aruba-aruba-pod", "vce-vce-pod", "aruba-airwave-pod", "ibm-ibm-pod","ibm-ibm-v7000"]
        },
        yAxis: {
            min: 0,
            minTickInterval:1,
            title: {
                text: 'Event count'
            }
        },
        legend: {
            reversed: true
        },
        plotOptions: {
            series: {
                stacking: 'normal'
            }
        },
        series: [{
            name: 'Open',
            data: []//[5, 3, 4, 7, 2]
        }, {
            name: 'Closed',
            data: []//[2, 2, 3, 2, 1]
        }]
    };

    return data;
}


function getChart2Template() {
    var data  = {
        chart: {
            /*borderColor: '#FFF',
            borderWidth: 1,*/
            type: 'bar'
        },
        credits: {
            enabled: false
        },
        title: {
            text: 'Events Per System'
        },
        xAxis: {
            /*min: 0,
            minTickInterval:1,
            tickInterval: 1,
            allowDecimals: false,*/
            categories: []//["lcp-01","lcp-03","lcp-09","lcp-17","lcp-19","solr-01","zk-01"]
        },
        yAxis: {
            min: 0,
            minTickInterval:1,
            title: {
                text: 'Event count'
            }
        },
        legend: {
            reversed: true
        },
        plotOptions: {
            series: {
                stacking: 'normal'
            }
        },
        series: [{
            name: 'Open',
            data: []//[5, 3, 4, 7, 2, 1, 1]
        }, {
            name: 'Closed',
            data: []//[2, 2, 3, 2, 1, 1, 3]
        }]
    };
    return data;
}

function initBarChart1() {

    $('#summary-chart1-div').highcharts(getChart1Template());

}


function initBarChart2() {

    $('#summary-chart2-div').highcharts(getChart2Template());

}