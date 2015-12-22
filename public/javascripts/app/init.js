var GLOBALS = {
    'userName': "saumitra",
    'userDisplayName': "Saumitra",
    'userEmail': "saumitra.srivastav@glassbeam.com",
    'autoRefresh':false
};

var USERS = undefined;

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


Array.prototype.merge = function(b){
    var a = this.concat();                // clone current object
    if(!b.push || !b.length) return a;    // if b is not an array, or empty, then return a unchanged
    if(!a.length) return b.concat();      // if original is empty, return b

    // go through all the elements of b
    for(var i = 0; i < b.length; i++){
        // if b's value is not in a, then add it
        if(a.indexOf(b[i]) == -1) a.push(b[i]);
    }
    return a;
};


setTimeout(function() {
    updateUserInfo();
}, 1000);

function updateUserInfo() {
    $.ajax({
        type: "GET",
        url: "v1/api/user/info/all",
        'success': function (data) {
            console.log(data);
            USERS = data;
            USERS['none'] = {
                "name":"None",
                "external":"0"
            }
        }
    });
}