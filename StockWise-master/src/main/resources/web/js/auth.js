
/* =========================================
   SMARTSTOCK AI - AUTHENTICATION
========================================= */

document.addEventListener("DOMContentLoaded", () => {

    const loginForm = document.getElementById("loginForm");
    const otpForm = document.getElementById("otpForm");

    const emailInput = document.getElementById("email");
    const otpInput = document.getElementById("otp");

    const userEmail = document.getElementById("userEmail");

    const formTitle = document.getElementById("formTitle");
    const formSubtitle = document.getElementById("formSubtitle");

    const message = document.getElementById("message");
    const backButton = document.getElementById("backButton");

    let generatedOTP = null;
    let otpExpiry = null;
    let attempts = 0;

    // =====================================
    // CHECK EXISTING LOGIN
    // =====================================

    if (sessionStorage.getItem("smartstock_logged_in") === "true") {
        window.location.href = "/index.html";
        return;
    }

    // =====================================
    // SHOW MESSAGE
    // =====================================

    function showMessage(text, type = "") {

        message.textContent = text;
        message.className = "message " + type;

    }

    // =====================================
    // GENERATE OTP
    // =====================================

    function generateOTP() {

        return Math.floor(100000 + Math.random() * 900000)
            .toString();

    }

    // =====================================
    // LOGIN FORM
    // =====================================

    loginForm.addEventListener("submit", (event) => {

        event.preventDefault();

        const email = emailInput.value.trim();

        if (!email) {
            showMessage("Please enter your email address.", "error");
            return;
        }

        generatedOTP = generateOTP();

        otpExpiry = Date.now() + (5 * 60 * 1000);

        attempts = 0;

        sessionStorage.setItem("smartstock_demo_otp", generatedOTP);
        sessionStorage.setItem("smartstock_otp_expiry", otpExpiry);
        sessionStorage.setItem("smartstock_user_email", email);

        userEmail.textContent = email;

        loginForm.classList.add("hidden");
        otpForm.classList.remove("hidden");

        formTitle.textContent = "Verify Your Identity";
        formSubtitle.textContent =
            "Enter the verification code to continue.";

        showMessage("");

        // DEMO OTP
        console.log("SmartStock AI Demo OTP:", generatedOTP);

        alert(
            "DEMO MODE\n\nYour OTP is: " + generatedOTP +
            "\n\nThis OTP is for demonstration purposes."
        );

        otpInput.value = "";
        otpInput.focus();

    });

    // =====================================
    // OTP VERIFICATION
    // =====================================

    otpForm.addEventListener("submit", (event) => {

        event.preventDefault();

        const enteredOTP = otpInput.value.trim();

        if (!/^\d{6}$/.test(enteredOTP)) {

            showMessage(
                "Please enter a valid 6-digit OTP.",
                "error"
            );

            return;
        }

        if (Date.now() > otpExpiry) {

            showMessage(
                "OTP expired. Please go back and generate a new one.",
                "error"
            );

            return;
        }

        attempts++;

        if (attempts > 5) {

            showMessage(
                "Too many incorrect attempts. Please generate a new OTP.",
                "error"
            );

            otpForm.reset();
            return;
        }

        if (enteredOTP !== generatedOTP) {

            showMessage(
                "Incorrect OTP. Attempts remaining: " +
                (5 - attempts),
                "error"
            );

            otpInput.value = "";
            otpInput.focus();

            return;
        }

        // =================================
        // SUCCESSFUL LOGIN
        // =================================

        sessionStorage.setItem("smartstock_logged_in", "true");

        sessionStorage.setItem(
            "smartstock_user_email",
            emailInput.value.trim()
        );

        sessionStorage.removeItem("smartstock_demo_otp");
        sessionStorage.removeItem("smartstock_otp_expiry");

        showMessage("Verification successful!", "success");

        setTimeout(() => {

            window.location.href = "/index.html";

        }, 700);

    });

    // =====================================
    // BACK TO EMAIL
    // =====================================

    backButton.addEventListener("click", () => {

        otpForm.classList.add("hidden");
        loginForm.classList.remove("hidden");

        formTitle.textContent = "Welcome Back";

        formSubtitle.textContent =
            "Sign in to access your inventory dashboard.";

        generatedOTP = null;

        showMessage("");

        otpInput.value = "";

    });

});
