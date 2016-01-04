$(document).ready(function(){
   new Auth();
});

var Auth = function() {

    registerSignin();
    registerNewUser();

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

    function registerNewUser() {
        $('#auth-btn-signup').click(function() {
            $('#auth-singup-success-div').hide();

            var email = ($('#auth-signup-email').val()).trim();
            var name  = ($('#auth-signup-name').val()).trim();
            var pass1 = ($('#auth-signup-pass1').val()).trim();
            var pass2 = ($('#auth-signup-pass2').val()).trim();

  /*          $("#auth-btn-signup").button('loading');
            $('#auth-singup-success-div').show();
*/

            if(! isGlassbeamEmail(email)) {
                alertify.error("Invalid email. Please provide a valid Glassbeam email id.");
                return;
            }

            $.when(ajax_updateUserInfo()).then(function(response){

                USERS = response;
                USERS['none'] = {
                    "name":"None",
                    "external":"0"
                };

                if(USERS.hasOwnProperty(email)) {
                    alertify.error("Account already exists for <strong>" + email + "</strong>.");
                    return;
                }

                if(name.length == 0) {
                    alertify.error("Name field is empty");
                    return;
                }

                if(pass1.length == 0 || pass2.length == 0) {
                    alertify.error("Password field is empty");
                    return;
                }

                if(pass1 != pass2) {
                    alertify.error("Passwords do not match.");
                    return;
                }

                $("#auth-btn-signup").button('loading');
                var requestData = {
                    email: email,
                    name: name,
                    password: ($.md5(pass1))
                };

                $.when(ajax_registerUser(requestData)).then(function(response){
                    $("#auth-btn-signup").button('reset');
                    $('#auth-singup-success-div').show()
                });

            });



        });
    }

    function ajax_registerUser(data) {

        return ($.ajax({
            type: "GET",
            url: "v1/auth/add",
            data: data
        }))
    }

};

