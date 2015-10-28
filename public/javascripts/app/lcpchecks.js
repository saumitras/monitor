$(document).ready(function() {

   populateBundleLevelChecks();

   $('.popup1').click(function() {
       showPopup1();
   });

});

function showPopup1() {
    $('#').modal("show");
}
function showPopup2() {
    $('#clients-popup1').modal("show");
}


function populateBundleLevelChecks() {

    var data = [];
    for(var i=1;i<200;i++) {
        var obj = {
            "id": "lcp-chk-01",
            "name": "Bundle stuck in parsing",
            "frequency": "120",
            "critical_threshold": "1800",
            "warning_threshold":"600",
            "wait_time": "0",
            "threshold_unit":"time",
            "status":"active",
            "details":"<a href='#' class='popup1'>Details</a>",
            "actions":"<a href='#' class='popup1'>Edit</a>"

        };

        data.push(obj);
    }

    $('#lcp-checks-bundletable').bootstrapTable({
        data: data
    });
}
