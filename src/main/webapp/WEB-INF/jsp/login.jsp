<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="ISO-8859-1">
    <title>Insert title here</title>
    <script>


    </script>
</head>
<body>
<div align="center" id="all">
    <h1>Chat Login Form</h1>
    <form id="loginForm">
        <table style="with: 80%">
            <tr>
                <td>UserName</td>
                <td><input type="text" name="username" minlength="4" placeholder="Only letters, more then 3" required
                           pattern="[A-Za-z]*"/></td>
            </tr>
            <tr>
                <td>Password</td>
                <td><input type="password" name="password" placeholder="More then 3 symbols  " minlength="4" required
                           pattern="[^\s]*" width=""/></td>
            </tr>

        </table>
        <p></p>
        <p></p>
        <input type="submit" value="Submit"/>
    </form>


    <div id="error"></div>
</div>


<script src="https://code.jquery.com/jquery-2.2.2.js" integrity="sha256-4/zUCqiq0kqxhZIyp4G0Gk+AOtCJsY1TA00k5ClsZYE="
        crossorigin="anonymous"></script>
<!-- Latest compiled and minified JavaScript -->

<script> $(function () {

    function setJwtToken(token) {
        localStorage.setItem("token", token);
    }

    function getJwtToken() {
        return localStorage.getItem("token");
    }

    function getName() {
        return localStorage.getItem("name");
    }

    function setRoleSelect(role) {
        localStorage.setItem("role", role);
    }

    function setUsername(name) {
        localStorage.setItem("name", name);
    }

    $('#loginForm').submit(function (event) {
        event.preventDefault();

        let $form = $(this);


        let name = $form.find('input[type=text]').val();
        setUsername(name);
        let formData = {
                username: $form.find('input[type=text]').val(),
                password: $form.find('input[type="password"]').val(),
                roles: null
            }
        ;
        doLoginUser(formData);

    });


    function createAuthorizationTokenHeader() {
        let token = getJwtToken();
        if (token) {
            return {"Authorization": "Bearer " + token};
        } else {
            return {};
        }
    }


    function doLoginUser(loginData) {
        $.ajax({
            url: "/web/login",
            type: "POST",
            data: JSON.stringify(loginData),
            contentType: "application/json; charset=utf-8",
            dataType: "json",

            success: function (data, textStatus, jqXHR) {
                alert(data.token)
                setJwtToken(data.token);
                setRoleSelect(data.role)
                if (confirm("Would you like to chat ?")) {
                    window.location.href = origin + "/chat";

                } else {
                    $('#all').html(" <input type=\"submit\"  value=\"logOut\" id=\"out\" onclick=\"logOut()\"/>");
                }


            },
            error: function (jqXHR, textStatus, errorThrown) {
                $("input[type=text], textarea").val("");
                $("input[type=password], textarea").val("");
                $('#error').html("<p>Exception:<br>" + jqXHR.responseJSON.exception + "</p>");
                $('#error').html("<p>Exception:<br>" + errorThrown + "</p>");
            }
        });
    }

});
</script>
</body>
</html>
