$(document).ready(function() {

   populateClientTables();

});

function showPopup1() {
    $('#clients-popup1').modal("show");
}


var clientInfoG = {};
function populateClientTables() {

    $.ajax({
        'type': "GET",
        'url': "v1/api/client/info/all",

        'success': function (response) {
            console.log(response);
            var data = [];

            var actions =

            $.each(response, function(index, client){
                var row = {
                  name: client['name'],
                  group: client['group'],
                  status: client['status'],
                  health: client['health'],
                  stashed_mps: client['stashed_mps'],
                  stash_duration: client['stash_duration'],
                  actions: "<span clientname='" + client['name'] + "' class='edit link1'>Edit</span>" +
                            "<span clientname='" + client['name'] + "' class='delete link1'>Delete</span>"
                };

                data.push(row);
                clientInfoG[row['name']] = row;
            });

            $('#clients-table1').bootstrapTable({
                data: data
            }).find('.edit').click(function() {
                var clientName = $(this).attr('clientname');
                fetchClientDetails(clientName);
            });
        }
    });

}


function fetchClientDetails(clientName) {

    var params = {
        'name': clientName
    };

    $.ajax({
        type: "GET",
        url: "v1/api/client/info/single",
        data: params,
        'success': function (data) {
            console.log(data);
            var status = data['status'];
            var stashDuration = data['stash_duration'];
            var stashedMps = data['stashed_mps'];

            var popup = $('#clients-edit1');

            //set the status radio
            if(status == "enabled") {
                popup.find('#status-active').prop('checked', true);
            } else {
                popup.find('#status-stashed').prop('checked', true);
            }

            //set stashed mps radio
            if(stashedMps.toLowerCase() == "none") {
                popup.find('#stashed-mps-none').prop('checked', true);
            } else {
                if(stashedMps == "all") {
                    popup.find('#stashed-mps-all').prop('checked', true);
                } else {
                    popup.find('#stashed-mps-selected').prop('checked', true);
                    popup.find('#stashed-mps-selected-list').val(stashedMps);

                }
                popup.find('#stashed-duration').find("[value='" + stashDuration + "']").first().prop('checked', true);
            }

            popup.find('#heading').html("Edit client <b>" + clientName + "</b></span>");
            popup.modal("show");

            popup.find('#"client-edit-apply-change').click(function() {
                var stashDuration = popup.find('[name=stash_duration]').filter(":checked").val();
                var status = popup.find('[name=client-status]').filter(":checked").val();
                var stashedMps = popup.find('[name=stashed-mps]').filter(":checked").val();
                var stashedMpsList = popup.find('#stashed-mps-selected-list').val();

                updateClient(); //send request to update client table in H2
            })
        }
    });

}


function updateClient() {


}