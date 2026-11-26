
/* =========================================
   DASHBOARD
========================================= */

document.addEventListener(
    "DOMContentLoaded",
    loadDashboard
);


/* =========================================
   LOAD DASHBOARD
========================================= */

async function loadDashboard() {

    try {

        const stats =
            await DashboardAPI.getStats();


        /* =====================================
           MAIN KPI CARDS
        ===================================== */

        document.getElementById(
            "totalProducts"
        ).textContent =
            stats.totalProducts;

        document.getElementById(
            "totalSuppliers"
        ).textContent =
            stats.totalSuppliers;

        document.getElementById(
            "lowStock"
        ).textContent =
            stats.lowStockProducts;

        document.getElementById(
            "outOfStock"
        ).textContent =
            stats.outOfStockProducts;

        document.getElementById(
            "totalUnits"
        ).textContent =
            stats.totalUnitsInStock;

        document.getElementById(
            "inventoryValue"
        ).textContent =
            formatCurrency(
                stats.totalInventoryValue
            );

        document.getElementById(
            "salesToday"
        ).textContent =
            formatCurrency(
                stats.totalSalesToday
            );

        document.getElementById(
            "salesMonth"
        ).textContent =
            formatCurrency(
                stats.totalSalesThisMonth
            );


        /* =====================================
           INVENTORY HEALTH
        ===================================== */

        const products =
            await ProductAPI.getAll();


        // Healthy products

        const healthyProducts =
            products.filter(product => {

                return product.currentStock >
                    product.safetyStock;

            }).length;


        // Update Healthy Stock

        document.getElementById(
            "healthyStock"
        ).textContent =
            healthyProducts;


        // Update Low Stock

        document.getElementById(
            "lowStockHealth"
        ).textContent =
            stats.lowStockProducts;


        // Update Critical Stock

        const criticalStockElement =
            document.getElementById(
                "criticalStockHealth"
            );

        if (criticalStockElement) {

            criticalStockElement.textContent =
                stats.criticalStockProducts;
        }


        // Update Out of Stock

        document.getElementById(
            "outStockHealth"
        ).textContent =
            stats.outOfStockProducts;


        /* =====================================
           DATE
        ===================================== */

        document.getElementById(
            "currentDate"
        ).textContent =
            new Date().toLocaleDateString(
                "en-IN",
                {
                    weekday: "long",
                    day: "numeric",
                    month: "long",
                    year: "numeric"
                }
            );

    } catch (error) {

        console.error(
            "Dashboard loading failed:",
            error
        );

        showToast(
            "Unable to load dashboard data.",
            "error"
        );
    }
}
