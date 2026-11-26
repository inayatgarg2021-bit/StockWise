const API_BASE = "/api";

async function apiRequest(endpoint, options = {}) {

    const config = {
        method: options.method || "GET",
        headers: {
            "Content-Type": "application/json"
        },
        ...options
    };

    try {

        const response = await fetch(
            `${API_BASE}${endpoint}`,
            config
        );

        const contentType =
            response.headers.get("content-type");

        let data;

        if (contentType &&
            contentType.includes("application/json")) {

            data = await response.json();

        } else {

            data = await response.text();
        }

        if (!response.ok) {

            const message =
                data?.error ||
                data?.message ||
                "Something went wrong.";

            throw new Error(message);
        }

        return data;

    } catch (error) {

        console.error("API Error:", error);

        throw error;
    }
}

// =========================================
// PRODUCTS
// =========================================

const ProductAPI = {

    getAll() {
        return apiRequest("/products");
    },

    getById(id) {
        return apiRequest(`/products?id=${id}`);
    },

    getLowStock() {
        return apiRequest("/products/low-stock");
    },

    create(product) {
        return apiRequest("/products", {
            method: "POST",
            body: JSON.stringify(product)
        });
    },

    update(id, product) {
        return apiRequest(`/products?id=${id}`, {
            method: "PUT",
            body: JSON.stringify(product)
        });
    },

    delete(id) {
        return apiRequest(`/products?id=${id}`, {
            method: "DELETE"
        });
    }
};

// =========================================
// SALES
// =========================================

const SalesAPI = {

    getAll() {
        return apiRequest("/sales");
    },

    getById(id) {
        return apiRequest(`/sales?id=${id}`);
    },

    create(sale) {
        return apiRequest("/sales", {
            method: "POST",
            body: JSON.stringify(sale)
        });
    }
};

// =========================================
// SUPPLIERS
// =========================================

const SupplierAPI = {

    getAll() {
        return apiRequest("/suppliers");
    },

    getById(id) {
        return apiRequest(`/suppliers?id=${id}`);
    },

    create(supplier) {
        return apiRequest("/suppliers", {
            method: "POST",
            body: JSON.stringify(supplier)
        });
    },

    update(id, supplier) {
        return apiRequest(`/suppliers?id=${id}`, {
            method: "PUT",
            body: JSON.stringify(supplier)
        });
    },

    delete(id) {
        return apiRequest(`/suppliers?id=${id}`, {
            method: "DELETE"
        });
    }
};

// =========================================
// FORECAST
// =========================================

const ForecastAPI = {

    get(multiplier = 1) {

        return apiRequest(
            `/forecast?multiplier=${multiplier}`
        );
    }
};

// =========================================
// DASHBOARD
// =========================================

const DashboardAPI = {

    getStats() {
        return apiRequest("/dashboard");
    }
};