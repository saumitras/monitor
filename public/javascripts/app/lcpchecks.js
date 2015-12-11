var checkData = undefined;

$(document).ready(function() {
   new LcpChecksData();
});

var LcpChecksData = function() {

    $('#lcp-check-current-mps').change(function() {
        resetData();
    });

    //scheduler which will kill itself after init is done
    updateChecksData();
    var initializer = setInterval(function() {
        if(checkData != undefined) {
            populateMpsDropdown(Object.keys(checkData));
            resetData();
            clearTimeout(initializer);
        }
    },50);


    //data refresher which will run for lifetime of app
    setInterval(function() {
        updateChecksData();
        if(GLOBALS.autoRefresh) {
            resetData();
        }
    }, 10000);


    function resetData() {
        var mps = $('#lcp-check-current-mps').val();
        console.log("selectedMps=" + mps);
        $("#lcp-checks-heading1").html(mps.toUpperCase().replace(/\//g,' / '));

        var showTableId = "lcp-checks-table-default";
        var hideTableId = "lcp-checks-table-all";
        if(mps != "default") {
            //swap show and hide table
            hideTableId = [showTableId, showTableId = hideTableId][0];
        }

        $('#' + hideTableId).closest('.parent-l1').hide();
        $('#' + showTableId).closest('.parent-l1').show();
        $('#' + showTableId).bootstrapTable("destroy");
        $('#' + showTableId).bootstrapTable({
            data: checkData[mps]
        });

        $('.lcp-check-edit').unbind('click');
        $('.lcp-check-edit').on('click', function(event){
            var elem = $(event.target);
            console.log(elem.attr('mps') + " "  + elem.attr('id')  + "  " + elem.attr('cid'))
        });

    }


    function updateChecksData() {

        $.when(ajax_getLcpChecksData("all")).then(function(response) {

            checkData = {};

            if(response != undefined) {
                var defaultChecks  = response['default'];
                var custChecks = response['cust'];
                if(defaultChecks != undefined && custChecks != undefined) {
                    respToRows(defaultChecks.concat(custChecks));
                }
            }

            function respToRows(resp) {
                $.each(resp,function(index, value) {
                    var mps = value['mps'];

                    if(! checkData.hasOwnProperty(mps)) {
                        checkData[mps] = []
                    }
                    var row = {
                        "id":value['id'],
                        "cid": value['cid'],
                        "name": value['desc'],
                        "interval": value['interval'],
                        "critical_threshold": value['critical_threshold'],
                        "warning_threshold":value['warning_threshold'],
                        "wait_duration": value['wait_duration'],
                        "threshold_unit":value['threshold_unit'],
                        "status":value['status'],
                        "actions":"<a href='#' mps='" + mps  +"' id='" + value['id'] + "' cid='" + value['cid'] + "' " +
                                    " class='lcp-check-edit'>Edit</a>"
                    };

                    checkData[mps].push(row);

                });

            }
        })

    }

    function populateMpsDropdown(mpsList) {
        var htmlStr = "";
        var currentMps = $('#lcp-check-current-mps').val();
        if(mpsList.indexOf(currentMps) != 1) {
            htmlStr += "<option value='" + currentMps + "' selected='selected'>" + currentMps +"</option>"
        }

        $.each(mpsList, function(index, mps) {
            if(mps != currentMps) {
                htmlStr += "<option value='" + mps + "'>" + mps + "</option>"
            }
        });

        $('#lcp-check-current-mps').html(htmlStr)
    }

    function ajax_getLcpChecksData(mode, idList) {

        var params = {};
        if(idList != undefined && idList != "") {
            params['id'] = idList
        }
        return ($.ajax({
            type: "GET",
            url: "v1/api/checks/info/" + mode,
            data: params
        }))
    }

};

