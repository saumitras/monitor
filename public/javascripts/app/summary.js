$(document).ready(function(){
    //new SummaryTiles();
    initBarChart1()
    initBarChart2()
});


var Summary = function() {

    var summaryData = {};
    function updateTiles() {
        $('#tile1').html()
    }

    function updateCharts() {

    }

    function updateSummaryData(mode) {

        $.when(ajax_getSummary()).then(function(){

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






function initBarChart1() {

    $('#summary-chart1-div').highcharts({
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
            categories: ["aruba-aruba-pod", "vce-vce-pod", "aruba-airwave-pod", "ibm-ibm-pod","ibm-ibm-v7000"]
        },
        yAxis: {
            min: 0,
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
            data: [5, 3, 4, 7, 2]
        }, {
            name: 'Closed',
            data: [2, 2, 3, 2, 1]
        }]
    });

}


function initBarChart2() {

    $('#summary-chart2-div').highcharts({
        chart: {
            type: 'bar'
        },
        credits: {
                enabled: false
        },
        title: {
            text: 'Events per System'
        },
        xAxis: {
            categories: ["lcp-01","lcp-03","lcp-09","lcp-17","lcp-19","solr-01","zk-01"]
        },
        yAxis: {
            min: 0,
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
            data: [5, 3, 4, 7, 2, 1, 1]
        }, {
            name: 'Closed',
            data: [2, 2, 3, 2, 1, 1, 3]
        }]
    });

}



function runningFormatter(value, row, index) {
    return index;
}

function showCollectionDetails() {
    $('#collection-details-modal').modal("show")
}

function populateCollectionTable() {

    var data = [];

    for(var i=1;i<200;i++) {
        var obj = {
            "collection": "collection " + i,
            "state": "active",
            "doc-count": i*100,
            "size": i*3 + "MB",
            "details":"<span class='details'>Details</span>"
        }

        data.push(obj);
    }

    $('#alerts-table-unack').bootstrapTable({
        data: data
    });
    $('.temp-values').bootstrapTable({
        data: data
    });

}

function populateAliasTable() {


    $.ajax({
        'type': "GET",
        'url': "/api/solr/alias",
        'success':function(response) {
            console.log(response);
            var data = [];

            response = {}
            $.each(response, function(aliasName, collectionList) {
                console.log(aliasName);
                var cols = collectionList.split(",");

                data.push({
                    "alias": aliasName,
                    "count": cols.length,
                    "collections": cols.join(", ")
                });
            });

            $('#table-alias').bootstrapTable({
                data: data
            });
        }
    });


}
