var GLOBALS = {
    'userName': "",
    'userDisplayName': "",
    'userEmail': "",
    'autoRefresh':false
};

var USERS = undefined;

$(document).ready(function() {

    $.when(ajax_updateUserInfo()).then(function(response) {

        USERS = response;
        USERS['none'] = {
            "name":"None",
            "external":"0"
        };

        var currentUserEmail = currentUser();
        var userData = USERS[currentUserEmail];
        if(userData != undefined) {
            var name = userData['name'];
            $('#header-username').html(name)

            var autoRefresh = userData['autoRefresh'] != "0";
            $('#auto-refresh-checkbox').bootstrapSwitch('state', autoRefresh);

            $('#auto-refresh-checkbox').on('switchChange.bootstrapSwitch', function(event, state) {
                GLOBALS.autoRefresh = state
            });
        }

        $('#header-signout').click(function() {
            logout()
        });


        $.feedback({
            ajaxURL: '/v1/feedback/input',
            html2canvasURL: 'assets/javascripts/lib/feedback/stable/2.0/html2canvas.js'
        });
    });

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

function currentUser() {
    return Cookies.get("username");
}

function ajax_updateUserInfo() {
    console.log("Inside updateUserInfo")
    return ($.ajax({
        type: "GET",
        url: "v1/api/user/info/all"
    }));
}


function logout() {
    document.cookie = "username=";
    document.cookie = "password=";
    window.location = "/"
}


function isGlassbeamEmail(email) {
    var validEmailRegex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
    var glassbeamEmailRegex = /(@glassbeam.com)$/;

    return (validEmailRegex.test(email) && glassbeamEmailRegex.test(email) )

}