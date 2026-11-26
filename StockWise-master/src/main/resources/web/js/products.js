
let products = [];

let suppliers = [];

let editingProductId = null;

// =========================================
// INITIALIZATION
// =========================================

document.addEventListener(
    "DOMContentLoaded",
    initializeProducts
);

async function initializeProducts() {

    await loadSuppliers();

    await loadProducts();

    document.getElementById(
        "searchProducts"
    ).addEventListener(
        "input",
        filterProducts
    );

    document.getElementById(
        "category"
    ).addEventListener(
        "change",
        updateCategoryFields
    );

    document.getElementById(
        "productForm"
    ).addEventListener(
        "submit",
        saveProduct
    );
}

// =========================================
// LOAD PRODUCTS
// =========================================

async function loadProducts() {

    const table =
        document.getElementById("productsTable");

    showLoading(table);

    try {

        products =
            await ProductAPI.getAll();

        renderProducts(products);

    } catch (error) {

        showToast(
            error.message,
            "error"
        );

        showEmptyState(
            table,
            "Unable to load products."
        );
    }
}

// =========================================
// LOAD SUPPLIERS
// =========================================

async function loadSuppliers() {

    try {

        suppliers =
            await SupplierAPI.getAll();

        const dropdown =
            document.getElementById("supplierId");

        dropdown.innerHTML = `
            <option value="">
                Select Supplier
            </option>
        `;

        suppliers.forEach(supplier => {

            dropdown.innerHTML += `
                <option value="${supplier.supplierId}">
                    ${escapeHTML(supplier.supplierName)}
                </option>
            `;
        });

    } catch (error) {

        console.error(
            "Supplier loading failed:",
            error
        );
    }
}

// =========================================
// RENDER PRODUCTS
// =========================================

function renderProducts(data) {

    const table =
        document.getElementById("productsTable");

    document.getElementById(
        "productCount"
    ).textContent =
        data.length;

    if (!data.length) {

        showEmptyState(
            table,
            "No products found."
        );

        return;
    }

    table.innerHTML = "";

    data.forEach(product => {

        // =====================================
        // STOCK STATUS
        // =====================================

        let status = "Healthy";

        let statusClass = "badge-healthy";

        if (product.currentStock === 0) {

            status = "Out of Stock";

            statusClass = "badge-critical";

        } else if (
            product.currentStock <=
            product.safetyStock * 0.5
        ) {

            status = "Critical";

            statusClass = "badge-critical";

        } else if (
            product.currentStock <=
            product.safetyStock
        ) {

            status = "Low Stock";

            statusClass = "badge-low";
        }

        // =====================================
        // SUPPLIER
        // =====================================

        const supplier =
            suppliers.find(
                s => s.supplierId ===
                    product.supplierId
            );

        const supplierName =
            supplier
                ? supplier.supplierName
                : "Unknown";

        // =====================================
        // TABLE ROW
        // =====================================

        const row =
            document.createElement("tr");

        row.innerHTML = `

            <td>
                <strong>
                    ${escapeHTML(product.sku)}
                </strong>
            </td>

            <td>
                ${escapeHTML(product.name)}
            </td>

            <td>
                ${escapeHTML(product.categoryType)}
            </td>

            <td>
                ${product.currentStock}
            </td>

            <td>
                ${formatCurrency(product.purchasePrice)}
            </td>

            <td>
                ${formatCurrency(product.sellingPrice)}
            </td>

            <td>
                ${escapeHTML(supplierName)}
            </td>

            <td>
                <span class="badge ${statusClass}">
                    ${status}
                </span>
            </td>

            <td>

                <div class="table-actions">

                    <button
                        class="btn-edit"
                        onclick="editProduct(${product.productId})">

                        ✏️

                    </button>

                    <button
                        class="btn-delete"
                        onclick="deleteProduct(${product.productId})">

                        🗑️

                    </button>

                </div>

            </td>
        `;

        table.appendChild(row);
    });
}

// =========================================
// SEARCH
// =========================================

function filterProducts() {

    const term =
        document.getElementById(
            "searchProducts"
        ).value.toLowerCase();

    const filtered =
        products.filter(product => {

            return product.name
                    .toLowerCase()
                    .includes(term)

                || product.sku
                    .toLowerCase()
                    .includes(term)

                || product.categoryType
                    .toLowerCase()
                    .includes(term);
        });

    renderProducts(filtered);
}

// =========================================
// OPEN MODAL
// =========================================

async function openProductModal() {

    editingProductId = null;

    document.getElementById(
        "modalTitle"
    ).textContent =
        "Add Product";

    document.getElementById(
        "productForm"
    ).reset();

    document.getElementById(
        "productId"
    ).value = "";

    document.getElementById(
        "leadTime"
    ).value = 7;

    document.getElementById(
        "warrantyMonths"
    ).value = 12;

    updateCategoryFields();

    document.getElementById(
        "productModal"
    ).classList.add("show");
}

// =========================================
// CLOSE MODAL
// =========================================

function closeProductModal() {

    document.getElementById(
        "productModal"
    ).classList.remove("show");
}

// =========================================
// CATEGORY FIELDS
// =========================================

function updateCategoryFields() {

    const category =
        document.getElementById(
            "category"
        ).value;

    document.getElementById(
        "expiryGroup"
    ).style.display =
        category === "FOOD"
            ? "block"
            : "none";

    document.getElementById(
        "warrantyGroup"
    ).style.display =
        category === "ELECTRONICS"
            ? "block"
            : "none";
}

// =========================================
// EDIT PRODUCT
// =========================================

async function editProduct(id) {

    const product =
        products.find(
            p => p.productId === id
        );

    if (!product) {
        return;
    }

    editingProductId = id;

    document.getElementById(
        "modalTitle"
    ).textContent =
        "Edit Product";

    document.getElementById(
        "productId"
    ).value =
        product.productId;

    document.getElementById(
        "sku"
    ).value =
        product.sku;

    document.getElementById(
        "productName"
    ).value =
        product.name;

    document.getElementById(
        "category"
    ).value =
        product.categoryType;

    document.getElementById(
        "supplierId"
    ).value =
        product.supplierId;

    document.getElementById(
        "purchasePrice"
    ).value =
        product.purchasePrice;

    document.getElementById(
        "sellingPrice"
    ).value =
        product.sellingPrice;

    document.getElementById(
        "currentStock"
    ).value =
        product.currentStock;

    document.getElementById(
        "safetyStock"
    ).value =
        product.safetyStock;

    document.getElementById(
        "coverageDays"
    ).value =
        product.targetCoverageDays;

    document.getElementById(
        "leadTime"
    ).value =
        product.leadTimeDays;

    document.getElementById(
        "expiryDate"
    ).value =
        product.expiryDate || "";

    document.getElementById(
        "warrantyMonths"
    ).value =
        product.warrantyMonths || 12;

    updateCategoryFields();

    document.getElementById(
        "productModal"
    ).classList.add("show");
}

// =========================================
// SAVE PRODUCT
// =========================================

async function saveProduct(event) {

    event.preventDefault();

    const product = {

        sku:
            document.getElementById(
                "sku"
            ).value.trim(),

        name:
            document.getElementById(
                "productName"
            ).value.trim(),

        categoryType:
        document.getElementById(
            "category"
        ).value,

        supplierId:
            Number(
                document.getElementById(
                    "supplierId"
                ).value
            ),

        leadTimeDays:
            Number(
                document.getElementById(
                    "leadTime"
                ).value
            ),

        purchasePrice:
            Number(
                document.getElementById(
                    "purchasePrice"
                ).value
            ),

        sellingPrice:
            Number(
                document.getElementById(
                    "sellingPrice"
                ).value
            ),

        currentStock:
            Number(
                document.getElementById(
                    "currentStock"
                ).value
            ),

        safetyStock:
            Number(
                document.getElementById(
                    "safetyStock"
                ).value
            ),

        targetCoverageDays:
            Number(
                document.getElementById(
                    "coverageDays"
                ).value
            ),

        expiryDate:
            document.getElementById(
                "expiryDate"
            ).value || null,

        warrantyMonths:
            Number(
                document.getElementById(
                    "warrantyMonths"
                ).value
            )
    };

    try {

        if (editingProductId) {

            await ProductAPI.update(
                editingProductId,
                product
            );

            showToast(
                "Product updated successfully!"
            );

        } else {

            await ProductAPI.create(
                product
            );

            showToast(
                "Product added successfully!"
            );
        }

        closeProductModal();

        await loadProducts();

    } catch (error) {

        showToast(
            error.message,
            "error"
        );
    }
}

// =========================================
// DELETE PRODUCT
// =========================================

async function deleteProduct(id) {

    const confirmed =
        confirm(
            "Are you sure you want to delete this product?"
        );

    if (!confirmed) {
        return;
    }

    try {

        await ProductAPI.delete(id);

        showToast(
            "Product deleted successfully!"
        );

        await loadProducts();

    } catch (error) {

        showToast(
            error.message,
            "error"
        );
    }
}

// =========================================
// HTML ESCAPE
// =========================================

function escapeHTML(value) {

    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}
