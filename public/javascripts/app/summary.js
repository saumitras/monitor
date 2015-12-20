$(document).ready(function(){
    new Summary();
    //initBarChart1()
    //initBarChart2()
});


var Summary = function() {

    var summaryData = undefined;

    //scheduler which will kill itself after init is done
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

    }

    function resetData() {
        updateTiles();
        updateCharts();
    }

    /*
     {
     openEventsCount: 2,
     openEventsOwnerCount: 2,
     closedEventsCount: 11,
     closedEventsOwnerCount: 1,
     issueGroup: {
     Platform: 11,
     NA: 2
     },
     bugOpenedCount: 1,
     openEventsByMps: {
     storvisor/storvisor/storvisor_pod: 2
     },
     closedEventsByMps: {
     storvisor/storvisor/storvisor_pod: 11
     },
     openEventsByClient: {
     127.0.0.1: "2"
     },
     closedEventsByClient: {
     127.0.0.1: "11"
     }
     }
     */

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
