$(document).ready(function() {

   var lcpChecksData = new LcpCheckData();
   /*$('#lcp-checks-table-default').show();
   $('#lcp-checks-table-all').hide();
*/
/*

 $('#lcp-checks-table-all').bootstrapTable({
 data: rows
 });

   $('.popup1').click(function() {
       showPopup1();
   });
*/

});


var LcpCheckData = function() {

    var defaultChecks = undefined;
    var custChecks = undefined;

    updateChecksData();

    /*//data refresher which will run for lifetime of app
    var refresher = setInterval(function() {
        updateChecksData();
    }, 10000);
*/
    //scheduler which will kill itself after init is done
    var initializer = setInterval(function() {
      if(defaultChecks != undefined && custChecks != undefined) {
        toggleCheckTable("default");
        clearTimeout(initializer);
      }
    },50);

    $('#lcp-check-current-mps').change(function(event) {
        var selectedMps = event.currentTarget.value;
        console.log("selectedMps=" + selectedMps);
        toggleCheckTable(selectedMps)
    });

    function toggleCheckTable(dType) {
        console.log("Dtyep = " + dType)
        $("#lcp-checks-heading1").html(dType.toUpperCase());
        if(dType == "default") {
            $('#lcp-checks-table-all').closest('.parent-l1').hide();
            $('#lcp-checks-table-default').closest('.parent-l1').show();
            $('#lcp-checks-table-default').bootstrapTable({
                data: defaultChecks
            });
        } else {
            $('#lcp-checks-table-default').closest('.parent-l1').hide();
            $('#lcp-checks-table-all').closest('.parent-l1').show();
            $('#lcp-checks-table-all').bootstrapTable({
                data: custChecks
            });
        }
    }

    function updateChecksData() {

        var data1 = getLcpChecksData("default");
        var data2 = getLcpChecksData("cust");

        $.when(data1,data2).then(function(resp1,resp2) {

            console.log(defaultChecks);
            defaultChecks = respToRows(resp1[0],"default");
            custChecks = respToRows(resp2[0],"cust");
            console.log(defaultChecks);
            console.log(custChecks);

            function respToRows(resp,mode) {
                var rows = [];
                $.each(resp,function(index, value) {
                    var row = {
                        "id": value['id'],
                        "name": value['desc'],
                        "frequency": value['frequency'],
                        "critical_threshold": value['critical_threshold'],
                        "warning_threshold":value['warning_threshold'],
                        "wait_time": value['wait_time'],
                        "threshold_unit":value['threshold_unit'],
                        "status":value['status'],
                        "details":"<a href='#' class='popup1'>Details</a>",
                        "actions":"<a href='#' class='popup1'>Edit</a>"
                    };
                    if(mode == "cust") {
                        row['cid'] = value['cid']
                    }

                    rows.push(row);
                });

                return rows;
            }

        })

    }



    function getLcpChecksData(dType) {

        var params = {};
        return ($.ajax({
            type: "GET",
            url: "v1/api/checks/info/" + dType,
            data: params/*,
            'success': function (data) {
                console.log(data)
            }*/
        }))
    }
}

