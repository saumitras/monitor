$(document).ready(function() {

   populateOpenEventsTable();
   populateClosedEventsTable();

   $('.details').click(function() {
       showPopup1();
   });

   $('.update').click(function(){
       showPopup2();
   });

});

function showPopup1() {
    $('#lcpevents-popup1').modal("show");
}
function showPopup2() {
    $('#lcpevents-popup2').modal("show");
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



