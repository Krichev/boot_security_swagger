$(function () {
    var $loggedIn = $("#loggedIn").hide();

    $("#loginForm").submit(function (event) {
        event.preventDefault();

        var $form = $(this);
        var role = $form.find('input[type="selection"]').val();
        if (role == "client") {
            role == "ROLE_CLIENT";
        } else role == "ROLE_AGENT"
        var formData = {
                username: $form.find('input[type=text]').val(),
                password: $form.find('input[type="password"]').val(),
                role: new Array(role)
            }
        ;
        alert(formData);
        doLogin(formData);
    });

    function setJwtToken(token) {
        localStorage.setItem(TOKEN_KEY, token);
    }

    function doLogin(loginData) {
        $.ajax({
            url: "/touch/register",
            type: "POST",
            data: JSON.stringify(loginData),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (data, textStatus, jqXHR) {
                setJwtToken(data.token);
                var origin = window.location.origin;
                window.location.href = origin + "/login";
                // $login.hide();
                // $notLoggedIn.hide();
                // showTokenInformation()
                // showUserInformation();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                if (jqXHR.status === 401) {
                    /* $('#loginErrorModal')
                         .modal("show")
                         .find(".modal-body")
                         .empty()
                         .html("<p>Spring exception:<br>" + jqXHR.responseJSON.exception + "</p>");*/
                } else {
                    throw new Error("an unexpected error occured: " + errorThrown);
                }
            }
        });
    }
});