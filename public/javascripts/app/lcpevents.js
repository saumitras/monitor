$(document).ready(function() {

 new LcpEventData();
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
    var members = ['Saumitra','Bharath','Aklank','Raj'];

    //data refresher which will run for lifetime of app
     /*var refresher = setInterval(function() {
        updateEventData()
     }, 10000);
*/
    //scheduler which will kill itself after init is done
    updateEventData();
    var initializer = setInterval(function() {
        if(eventDataOpen != undefined && eventDataClosed != undefined) {
            //console.log(eventDataOpen);
            //console.log(eventDataClosed);
            resetData();
            clearTimeout(initializer);
        }
    },50);

    function resetData() {
        console.log("Reset data");
        $('#lcp-events-table-open').bootstrapTable("destroy");
        $('#lcp-events-table-open').bootstrapTable({
            data: eventDataOpen
        });

        $('#lcp-events-table-closed').bootstrapTable("destroy");
        $('#lcp-events-table-closed').bootstrapTable({
            data: eventDataClosed
        });

        $('#lcp-events-table-open').find('.event-update').click(function () {
            var eventId = $(this).attr("event-id");
            showEventClosePopUp(eventId)
        });


        $('#lcp-event-close-popup').find('.close-event').click(function () {
            var eventId = $('#lcp-event-close-popup').attr("event-id");
            var component = $('#lcp-event-close-popup').find('.event-component').val();
            var kb = $('#lcp-event-close-popup').find('.event-kb').val();
            var bug = $('#lcp-event-close-popup').find('.event-bugid').val();

            closeEvent({
                "id":eventId,
                "component": component,
                "kb":kb,
                "bug":bug
            })
        });


    }

    function showEventClosePopUp(eventId)  {
        $('#lcp-event-close-popup').attr("event-id",eventId);
        $('#lcp-event-close-popup').modal('show');
    }

    function closeEvent(data) {
        console.log("Closing event " + data.id);

        $.when(ajax_closeEvent(data), updateEventData()).then(function() {
            $('#lcp-event-close-popup').modal('hide');
            updateEventData(true);
        });


    }


    function updateEventData(reset) {
        $.when(ajax_getLcpEventData()).then(function(resp) {

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
                        load_id: (value.load_id).replace(/,/g,', '),
                        occurred_at: value.occurred_at,
                        kb: value.kb,
                        owner: populateOwnerList(value.owner),
                        component: value.component,
                        escalation_level: value.escalation_level,
                        actions:"<span class='link1 event-details' event-id='" + value.id + "'>Details</span>" +
                                "<span class='link1 event-update' event-id='" + value.id + "'>Update</span>"
                    };

                    if(row.status == "open")
                      open.push(row);
                    else
                      closed.push(row);

                });

                eventDataOpen = open;
                eventDataClosed = closed;

                if(reset != undefined && reset == true) {
                    resetData()
                }

            }
        })
    }


    function populateOwnerList(current) {

        var ownerHtml = "<select><option>" + current + "</option>";
        //console.log(members);
        $.each(members,function(index, member) {
            if(member != current) {
                //console.log(member);
                ownerHtml += "<option>" + member + "</option>";
            }

        });
        ownerHtml += "</select>";
        //console.log(ownerHtml);

        return ownerHtml;

    }

    function ajax_getLcpEventData() {

        var params = {};
        return ($.ajax({
            type: "GET",
            url: "v1/api/event/lcp/info/all",
            data: params
        }))
    }

    function ajax_closeEvent(data) {

        var id = data['id'];
        var closed_at = getDateTime;

        var params = data;
        params['closed_at'] = closed_at;

        return ($.ajax({
            type: "GET",
            url: "v1/api/event/lcp/close/" + id,
            data: params
        }))
    }
};


function getDateTime() {
    var now     = new Date();
    var year    = now.getFullYear();
    var month   = now.getMonth()+1;
    var day     = now.getDate();
    var hour    = now.getHours();
    var minute  = now.getMinutes();
    var second  = now.getSeconds();
    if(month.toString().length == 1) {
        month = '0'+month;
    }
    if(day.toString().length == 1) {
        day = '0'+day;
    }
    if(hour.toString().length == 1) {
        hour = '0'+hour;
    }
    if(minute.toString().length == 1) {
        minute = '0'+minute;
    }
    if(second.toString().length == 1) {
        second = '0'+second;
    }
    var dateTime = year+'-'+month+'-'+day+' '+hour+':'+minute+':'+second;
    return dateTime;
}