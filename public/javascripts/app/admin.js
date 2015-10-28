$(document).ready(function () {

    $("div[id^='admin-client-input']").each(function (i, el) {
        var group = this.id.replace(/.*-/,"");

        $(this).find('span[action]').click(function() {
            var nodes = $(this).siblings().filter('.client-list').first().val();
            var action = $(this).attr("action");
            clientAdmin({
                group: group,
                action: action,
                nodes: nodes
            })
        });
    });

});



function clientAdmin(params) {
    console.log(params);
    var action = params['action'];
    var group = params['group'];
    var nodes = params['nodes'];

    if(nodes == "") {
        alertify.log("<b>" + group.toUpperCase() +" client list can not be empty.</b>" +
            "<li>Provide at least 1 client.</li>" +
            "<li>Use double-comma(,,) delimiter for mutiple client</li>", 10000);
        return;
    }

    $.ajax({
      'type': "GET",
      'url': "v1/api/admin/client",
        'data':params,

      'success': function (response) {
        if(response.hasOwnProperty("status") && response["status"] == "0") {
            alertify.success("Successfully " + action + "ed client(s)")
        }

      }
    });

}