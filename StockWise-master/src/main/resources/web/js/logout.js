document.addEventListener("DOMContentLoaded", function () {

    const logoutButton = document.getElementById("logoutButton");

    if (logoutButton) {
        logoutButton.addEventListener("click", function () {

            sessionStorage.removeItem("smartstock_logged_in");
            sessionStorage.removeItem("smartstock_user_email");

            window.location.replace("/login.html");

        });
    }

});