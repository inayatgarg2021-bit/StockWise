
/* =========================================
   SMARTSTOCK AI - PAGE AUTHENTICATION GUARD
========================================= */

(function () {

    const isLoggedIn =
        sessionStorage.getItem("smartstock_logged_in") === "true";

    const currentPath = window.location.pathname;

    // Allow login page without authentication
    if (currentPath === "/login.html") {
        return;
    }

    // Redirect unauthenticated users
    if (!isLoggedIn) {
        window.location.replace("/login.html");
    }

})();
