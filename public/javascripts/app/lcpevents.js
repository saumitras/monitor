$(document).ready(function() {

    var initializer = setInterval(function() {
        if(USERS != undefined) {
            clearTimeout(initializer);
            new LcpEventData();
        }
    },50);

});


var LcpEventData = function() {


    var eventDataOpen = undefined;
    var eventDataClosed = undefined;

    var members = Object.keys(USERS);

    //data refresher which will run for lifetime of app
    var refresher = setInterval(function() {
      updateEventData(GLOBALS.autoRefresh)
    }, 5000);
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

        $('#lcp-events-table-open').find('.event-update').unbind('click');
        $('#lcp-events-table-open').find('.event-update').click(function () {
            var eventId = $(this).attr("event-id");
            showEventClosePopUp(eventId)
        });

        $('#lcp-events-table-open').find('.event-owner').unbind('change');
        $('#lcp-events-table-open').find('.event-owner').change(function () {
            console.log($(this).val());
            var id = $(this).parent().parent().find('td').first().html();
            var owner = $(this).val();
            $.when(ajax_changeOwner(id,owner)).then(function() {
                alertify.success("<b>Event-" + id + "</b> assigned to <b>" + owner + "</b>")
            })
        });


        $('#lcp-event-close-popup').find('.close-event').unbind('click');
        $('#lcp-event-close-popup').find('.close-event').click(function () {
            var eventId = $('#lcp-event-close-popup').attr("event-id");
            var component = $('#lcp-event-close-popup').find('.event-component').val();
            var kb = $('#lcp-event-close-popup').find('.event-kb').val();
            var bug = $('#lcp-event-close-popup').find('.event-bugid').val();
            var owner = GLOBALS.userEmail

            closeEvent({
                "id":eventId,
                "component": component,
                "kb":kb,
                "bug":bug,
                "owner":owner
            })
        });

    }

    function showEventClosePopUp(eventId)  {
        $('#lcp-event-close-popup').attr("event-id",eventId);
        $('#lcp-event-close-popup').find("#heading").html(eventId);
        $('#lcp-event-close-popup').modal('show');
    }

    function closeEvent(data) {
        console.log("Closing event " + data.id);

        $.when(ajax_closeEvent(data), updateEventData()).then(function() {
            updateEventData(true);
            $('#lcp-event-close-popup').modal('hide');
            alertify.success("<b>Event-" + data.id + " closed successfully.")
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
                        component: EVENT_COMPONENT_LABELS[value.component],
                        escalation_level: value.escalation_level
                    };

                    if(row.status == "open") {
                      row['actions'] =
                          "<span class='link1 event-escalate' event-id='" + value.id + "'>Escalate</span>" +
                          "<span class='link1 event-details' event-id='" + value.id + "'>Details</span>" +
                        "<span class='link1 event-update' event-id='" + value.id + "'>Close</span>";
                      row['owner'] = populateOwnerList(value.owner);
                      open.push(row);
                    } else {
                      row['owner'] = USERS[(value.owner)] == undefined ? "NA" : USERS[(value.owner)]['name'];
                      closed.push(row);
                    }

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

        var ownerHtml = "<select class='event-owner'>" +
            "<option selected='selected' value='"+ current +"'>"
                + USERS[current]['name'] +
            "</option>";

        $.each(members,function(index, member) {
            if(member != current) {
                ownerHtml += "<option value='"+ member +"'>" + USERS[member]['name'] + "</option>";
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

    function ajax_changeOwner(id, owner) {
        var params = {};
        return ($.ajax({
            type: "GET",
            url: "v1/api/event/lcp/setowner/" + id + "/" + owner,
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


