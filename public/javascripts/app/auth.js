$(document).ready(function(){
   new Auth();
});

var Auth = function() {

    registerSignin();

    function registerSignin() {
        $('#auth-btn-signin').click(function() {
            var username = $('#auth-login-username').val();
            var password = $('#auth-login-password').val();

            console.log(password);
            document.cookie = "username=" + username;
            document.cookie = "password=" + $.md5(password);

            window.location = "/"
        })
    }

    function registerForgotPassword() {

    }

};