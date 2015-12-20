var GLOBALS = {
    'userName': "saumitra",
    'userDisplayName': "Saumitra",
    'userEmail': "saumitra.srivastav@glassbeam.com",
    'autoRefresh':false
};

$(document).ready(function() {
    $('#auto-refresh-checkbox').bootstrapSwitch();
    $('#auto-refresh-checkbox').on('switchChange.bootstrapSwitch', function(event, state) {
        GLOBALS.autoRefresh = state
    });

    $('#header-username').html("Saumitra")

});

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

var EVENT_COMPONENT_LABELS = {
    "platform": "Platform",
    "solution": "Solutions",
    "ops": "DevOps",
    "thirdparty": "Third Party",
    "unidentified": "Not Yet Identified"
};
