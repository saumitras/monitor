$(document).ready(function(){
    new GlobalConfig();
    $('#username').editable({
        type: 'select',
        mode: 'inline',
        pk: 1,
        url: '/v1/api/config/global/update',
        title: 'Enter username',
        params: {"key":"123"},
        source: [
            {value: 1, text: 'status 1'},
            {value: 2, text: 'status 2'},
            {value: 3, text: 'status 3'}
        ]
    });
    new CustConfig();
});

var GlobalConfig = function() {
    $('#monitor-config-global').bootstrapTable()
};
var CustConfig = function() {

    var custConfig = undefined;
    var custConfigTable = [];


    //scheduler which will kill itself after init is done
    updateConfigData();
    var initializer = setInterval(function() {
        if(custConfig != undefined) {
            console.log(custConfig);
            //populateMpsDropdown(Object.keys(custConfig));
            resetData();
            clearTimeout(initializer);
        }
    },50);

    //data refresher which will run for lifetime of app
    setInterval(function() {
        if(GLOBALS.autoRefresh) {
            updateConfigData(true);
        }
    }, 5000);


    function bindApplyChange() {
        $('#lcp-custconfig-edit-popup').find('.custconfig-btn-apply-change').unbind("click");
        $('#lcp-custconfig-edit-popup').find('.custconfig-btn-apply-change').on("click", function() {
            var mps = $('#lcp-custconfig-edit-popup').find('.custconfig-mps').html();
            var emailMandatory = $('#lcp-custconfig-edit-popup').find('.email-mandatory').val();
            var emailInternal = $('#lcp-custconfig-edit-popup').find('.email-internal').val();
            var emailExternal =  $('#lcp-custconfig-edit-popup').find('.email-external').val();
            var skipEmailRules =  $('#lcp-custconfig-edit-popup').find('.skip-email-rules').val();

            var data = {
                mps:mps,
                emailMandatory:emailMandatory,
                emailInternal:emailInternal,
                emailExternal:emailExternal,
                skipEmailRules:skipEmailRules
            };

            console.log("Updating data...");
            console.log(data);

            $.when(ajax_updateCustConfig(data)).then(function(resp) {
                updateConfigData(true);
                $('#lcp-custconfig-edit-popup').modal("hide");
                alertify.success("Config for <b>" + mps +"</b> updated successfully")
            })

        });

    };


    function resetData() {

        $('#lcp-custconfig-config-table').bootstrapTable("destroy");
        $('#lcp-custconfig-config-table').bootstrapTable({
            data: custConfigTable
        });

        $('#lcp-custconfig-config-table').find('.cust-config-edit').unbind("click");
        $('#lcp-custconfig-config-table').find('.cust-config-edit').on("click", function() {
            var mps = $(this).attr('mps');
            populatePopUpData(mps)
        });


    }


    function populatePopUpData(mps) {

        $.when(ajax_getCustConfig()).then(function(resp) {
            var data = resp[mps];
            var emailMandatory = data.emailMandatory;
            var emailInternal = data.emailInternal;
            var emailExternal = data.emailExternal;
            var skipEmailRules = data.skipEmailRules;

            $('#lcp-custconfig-edit-popup').find('.custconfig-mps').html(mps);
            $('#lcp-custconfig-edit-popup').find('.email-mandatory').val(emailMandatory);
            $('#lcp-custconfig-edit-popup').find('.email-internal').val(emailInternal);
            $('#lcp-custconfig-edit-popup').find('.email-external').val(emailExternal);
            $('#lcp-custconfig-edit-popup').find('.skip-email-rules').val(skipEmailRules);

            $('#lcp-custconfig-edit-popup').modal("show");
            bindApplyChange();
        })
    }


    function updateConfigData(reset) {
        $.when(ajax_getCustConfig()).then(function(resp) {
            console.log(resp);
            custConfig = resp;

            respToRows(resp);
            console.log("custConfigTable");
            console.log(custConfigTable);
            function respToRows(resp) {
                custConfigTable = [];
                $.each(resp, function(mps, values){
                    var row = {
                        mps:mps,
                        emailMandatory:values.emailMandatory,
                        emailInternal: values.emailInternal,
                        emailExternal: values.emailExternal,
                        skipEmailRules: values.skipEmailRules,
                        action:"<a href='#' mps='" + mps  + "' class='cust-config-edit'>Edit</a>"
                    };
                    custConfigTable.push(row)
                });

            }
            if(reset) resetData();
        });
    }


    function ajax_getCustConfig() {

        var params = {};

        return ($.ajax({
            type: "GET",
            url: "v1/api/config/info",
            data: params
        }))
    }

    function ajax_updateCustConfig(data) {

        return ($.ajax({
            type: "GET",
            url: "v1/api/config/update",
            data: data
        }))
    }





    /*function populateMpsDropdown(mpsList) {
        var htmlStr = "";
        var currentMps = $('#lcp-custconfig-current-mps').val();
        if(mpsList.indexOf(currentMps) != 1) {
            htmlStr += "<option value='" + currentMps + "' selected='selected'>" + currentMps +"</option>"
        }

        $.each(mpsList, function(index, mps) {
            if(mps != currentMps) {
                htmlStr += "<option value='" + mps + "'>" + mps + "</option>"
            }
        });

        $('#lcp-custconfig-current-mps').html(htmlStr)
    }*/
};