// =========================================
// SIDEBAR NAVIGATION
// =========================================

document.addEventListener("DOMContentLoaded", () => {

    const currentPath =
        window.location.pathname;

    document
        .querySelectorAll(".nav-link")
        .forEach(link => {

            const href =
                link.getAttribute("href");

            if (href === currentPath) {
                link.classList.add("active");
            }
        });

});

// =========================================
// TOAST NOTIFICATION
// =========================================

function showToast(
    message,
    type = "success"
) {

    let toast =
        document.getElementById("toast");

    if (!toast) {

        toast =
            document.createElement("div");

        toast.id = "toast";

        document.body.appendChild(toast);
    }

    toast.textContent = message;

    toast.className =
        `toast toast-${type}`;

    toast.classList.add("show");

    setTimeout(() => {

        toast.classList.remove("show");

    }, 3000);
}

// =========================================
// FORMAT CURRENCY
// =========================================

function formatCurrency(value) {

    return new Intl.NumberFormat(
        "en-IN",
        {
            style: "currency",
            currency: "INR",
            maximumFractionDigits: 2
        }
    ).format(value || 0);
}

// =========================================
// FORMAT DATE
// =========================================

function formatDate(date) {

    if (!date) {
        return "-";
    }

    return new Date(date)
        .toLocaleDateString("en-IN");
}

// =========================================
// LOADING
// =========================================

function showLoading(element) {

    if (element) {

        element.innerHTML = `
            <div class="loading">
                <div class="spinner"></div>
                <p>Loading...</p>
            </div>
        `;
    }
}

// =========================================
// EMPTY STATE
// =========================================

function showEmptyState(
    element,
    message = "No data available"
) {

    if (element) {

        element.innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">📦</div>
                <p>${message}</p>
            </div>
        `;
    }
}