$(document).ready(function() {

 new LcpEventData()
/*

   populateOpenEventsTable();
   populateClosedEventsTable();

   $('.details').click(function() {
       showPopup1();
   });

   $('.update').click(function(){
       showPopup2();
   });
*/

});

/*
function showPopup1() {
    $('#lcpevents-popup1').modal("show");
}
function showPopup2() {
    $('#lcpevents-popup2').modal("show");
}
*/

var LcpEventData = function() {

    var eventDataOpen = undefined;
    var eventDataClosed = undefined;

    //data refresher which will run for lifetime of app
     var refresher = setInterval(function() {
        updateEventData()
     }, 10000);

    //scheduler which will kill itself after init is done
    updateEventData();
    var initializer = setInterval(function() {
        if(eventDataOpen != undefined && eventDataClosed != undefined) {
            console.log(eventDataOpen)
            console.log(eventDataClosed)
            $('#lcp-events-table-open').bootstrapTable({
                data: eventDataOpen
            });
            $('#lcp-events-table-closed').bootstrapTable({
                data: eventDataClosed
            });
            clearTimeout(initializer);
        }
    },50);


    function updateEventData() {
        $.when(getLcpEventData()).then(function(resp) {

            respToRows(resp);

            function respToRows(resp) {
                var open = [];
                var closed = [];
                $.each(resp,function(index, value) {
                    var row = {
                        name: value.name,
                        source: value.source,
                        mps: value.mps,
                        signature: value.signature,
                        h2: value.h2,
                        id: value.id,
                        bug: value.bug,
                        resolution: value.resolution,
                        status: value.status,
                        closed_at: value.closed_at,
                        load_id: value.load_id,
                        occurred_at: value.occurred_at,
                        kb: value.kb,
                        owner: value.owner,
                        component: value.component,
                        escalation_level: value.escalation_level,
                        actions:"<span class='details'>Show Details</span><span class='update'>Update</span>"
                    };

                    if(row.status == "open")
                      open.push(row);
                    else
                      closed.push(row);

                });
                console.log("respToRows");

                console.log(open);
                console.log(closed);

                eventDataOpen = open;
                eventDataClosed = closed;
            }
        })
    }

    function getLcpEventData() {

        var params = {};
        return ($.ajax({
            type: "GET",
            url: "v1/api/event/lcp/info/all",
            data: params
        }))
    }
}



function populateOpenEventsTable() {

    var data = [];
    for(var i=1;i<200;i++) {
        var obj = {
                "id": "E" + i,
                "name": "File Stuck In Parsing",
                "customer": "aruba-aruba-pod",
                "h2": "h2-02",
                "load_id": "2172",
                "source":"lcp-11",
                "timestamp":"2012/08/20 11:08:56",
                "owner":"Amar",
                "status":"P3",
                "actions":"<span class='details'>Show Details</span><span class='update'>Update</span>"
            };
        if(i % 3 == 0) obj['status'] = "P1"
        if(i % 5 == 0) obj['status'] = "Un-Acknowledged"
        if(i % 5 == 0) obj['owner'] = "-"


        data.push(obj);
    }

    $('#lcpevents-open-events').bootstrapTable({
        data: data
    });
}


function populateClosedEventsTable() {

    var data = [];
    for(var i=1;i<200;i++) {
        var obj = {
            "id": "E" + i,
            "name": "File Stuck In Parsing",
            "customer": "aruba-aruba-pod",
            "is-bug": "NO",
            "component": "Solution",
            "load_id": "2172",
            "source":"lcp-11",
            "timestamp":"2012/08/20 11:08:56",
            "owner":"Amar",
            "kb": "<a href='#'>Knowledge Base Link</a>",
            "status":"Resolved In Prod",
            "actions":"<span class='details'>Show Details</span><span class='update'>Update</span>"
        };
        if(i % 3 == 0) obj['kb'] = "NA"
        if(i % 3 == 0) obj['is-bug'] = "BG-1082"
        if(i % 3 == 0) obj['component'] = "Platform"
        if(i % 3 == 0) obj['status'] = "Unresolved. Bug opened."

        data.push(obj);
    }

    $('#lcpevents-closed-events').bootstrapTable({
        data: data
    });
}



