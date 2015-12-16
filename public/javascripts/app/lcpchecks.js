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
        if(GLOBALS.autoRefresh) {
            updateChecksData(true);

        }
    }, 10000);


    function resetData() {
        console.log("reset data called");
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
            var data = {
                "mps":elem.attr('mps'),
                "id":elem.attr('id'),
                "cid":elem.attr('cid')
            };
            console.log("Editing checks" + data)
            showEditCheckPopUp(data);
        });

    }

    function writeNewCheckData(mode) {
        var newData = getNewCheckValues();
        if(mode.toUpperCase() == "ALL") {
            newData.mps = mode
        }

        $.when(ajax_writeNewCheckData(newData)).then(function(resp){
            console.log(resp);
            $('#lcp-checks-edit-popup').modal('hide');
            updateChecksData(true);

            if(mode.toUpperCase() == "ALL")
                alertify.success("Check ID '" + newData.id + "' updated successfully for <b>ALL MPS</b>");
            else
                alertify.success("Check ID '" + newData.id + "' updated successfully for <b>" + newData.mps + "</b>")

        })
    }

    function ajax_writeNewCheckData(params) {
        console.log("Sending ajax_writeNewCheckData");
        console.log(params);

        var id = params.id;

        return ($.ajax({
            type: "GET",
            url: "v1/api/checks/update/" +  id,
            data: params
        }))
    }

    function showEditCheckPopUp(data) {
        var mode = data.mps == "default" ? "default" : "cust";
        var id = data.mps == "default" ? data.cid : data.id;
        $.when(ajax_getLcpChecksData(mode, id)).then(function (resp) {
            if(resp.hasOwnProperty(mode)) {
                var row = resp[mode][0];
                populatePopUpData(row);
            }
        });

        function populatePopUpData(row) {
            var mps = row.mps;
            var id = mps == "default" ? row.cid : row.id;
            var name = row.desc;
            var interval = row.interval;
            var criticalThreshold = row.critical_threshold;
            var warningThreshold = row.warning_threshold;
            var waitDuration = row.wait_duration;
            var status = row.status;
            var emailExternal = row.emailExternal;

            $('#lcp-checks-edit-popup').find(".check-mps").html(mps);
            $('#lcp-checks-edit-popup').find(".check-id").val(id);
            $('#lcp-checks-edit-popup').find(".check-name").val(name);
            $('#lcp-checks-edit-popup').find(".check-interval").val(interval);
            $('#lcp-checks-edit-popup').find(".check-critical-threshold").val(criticalThreshold);
            $('#lcp-checks-edit-popup').find(".check-warning-threshold").val(warningThreshold);
            $('#lcp-checks-edit-popup').find(".check-wait-duration").val(waitDuration);
            $('#lcp-checks-edit-popup').find('input[name="check-status"][value="' + status + '"]').prop('checked',true);
            $('#lcp-checks-edit-popup').find('input[name="check-external-email"][value="' + emailExternal + '"]').prop('checked',true);


            $('#lcp-checks-edit-popup').modal('show');

            if(row.mps == "default") {
                $('#lcp-checks-edit-popup').find('.check-btn-change-all-mps').show();
                $('#lcp-checks-edit-popup').find('.check-btn-change-default').show();
                $('#lcp-checks-edit-popup').find('.check-btn-change-mps').hide();
            } else {
                $('#lcp-checks-edit-popup').find('.check-btn-change-all-mps').hide();
                $('#lcp-checks-edit-popup').find('.check-btn-change-default').hide();
                $('#lcp-checks-edit-popup').find('.check-btn-change-mps').show();
            }

            $('#lcp-checks-edit-popup').find('.check-btn-change-all-mps').unbind('click');
            $('#lcp-checks-edit-popup').find('.check-btn-change-all-mps').on('click',function(){
                writeNewCheckData("all");
            });

            $('#lcp-checks-edit-popup').find('.check-btn-change-default').unbind('click');
            $('#lcp-checks-edit-popup').find('.check-btn-change-default').on('click',function(){
                writeNewCheckData("default");
            });

            $('#lcp-checks-edit-popup').find('.check-btn-change-mps').unbind('click');
            $('#lcp-checks-edit-popup').find('.check-btn-change-mps').on('click',function(){
                writeNewCheckData("mps");
            });
        }
    }


    function getNewCheckValues() {

        var id = $('#lcp-checks-edit-popup').find(".check-id").val();
        var mps = $('#lcp-checks-edit-popup').find(".check-mps").html();
        var name = $('#lcp-checks-edit-popup').find(".check-name").val();
        var interval = $('#lcp-checks-edit-popup').find(".check-interval").val();
        var criticalThreshold = $('#lcp-checks-edit-popup').find(".check-critical-threshold").val();
        var warningThreshold = $('#lcp-checks-edit-popup').find(".check-warning-threshold").val();
        var waitDuration = $('#lcp-checks-edit-popup').find(".check-wait-duration").val();
        var status =  $('#lcp-checks-edit-popup').find('input[name="check-status"]:checked').val();
        var emailExternal =  $('#lcp-checks-edit-popup').find('input[name="check-external-email"]:checked').val();


        return {
            "id":id,
            "mps":mps,
            "name":name,
            "interval":interval,
            "criticalThreshold":criticalThreshold,
            "warningThreshold": warningThreshold,
            "waitDuration":waitDuration,
            "status":status,
            "emailExternal":emailExternal
        };

    }

    function updateChecksData(reset) {

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
                        "emailExternal":value['emailExternal'],
                        "status":value['status'],
                        "actions":"<a href='#' mps='" + mps  +"' id='" + value['id'] + "' cid='" + value['cid'] + "' " +
                                    " class='lcp-check-edit'>Edit</a>"
                    };

                    checkData[mps].push(row);

                });

            }

            if(reset) resetData()
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

