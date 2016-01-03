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

            $("#auth-btn-signup").button('reset');

            $('#auth-singup-success-div').show()

        });
    }

    function ajax_registerUser(data) {

        return ($.ajax({
            type: "GET",
            url: "v1/api/event/lcp/close/" + id,
            data: data
        }))
    }

};


function showSignUpSuccess() {
    var str = "<div class='alert alert-success collapse' id='auth-singup-success-div'>" +
        "<a href='#' class='close' data-dismiss='alert' aria-label='close'>&times;</a>" +
        "<strong>Congrats!</strong> We have successfully created your account. <br><br>" +
        " Email confirmation is required before you can login to your account." +
        " Please check your email for further instructions. " + "" +
        "If you don't receive confirmation email within 5 minutes, let us know at <strong>gbmonitor@@glassbeam.com</strong>" +
        "</div>";

    $('#auth-singup-success-div').html(str);
    $('#auth-singup-success-div').show();

}

function hideSignUpSuccess() {
    $('#auth-singup-success-div').hide();
}