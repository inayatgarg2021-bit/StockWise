let supplierList = [];

let editingSupplierId = null;

// =========================================
// INITIALIZE
// =========================================

document.addEventListener(
    "DOMContentLoaded",
    initializeSuppliers
);

async function initializeSuppliers() {

    await loadSuppliersPage();

    document.getElementById(
        "searchSuppliers"
    ).addEventListener(
        "input",
        filterSuppliers
    );

    document.getElementById(
        "supplierForm"
    ).addEventListener(
        "submit",
        saveSupplier
    );
}

// =========================================
// LOAD SUPPLIERS
// =========================================

async function loadSuppliersPage() {

    const table =
        document.getElementById(
            "suppliersTable"
        );

    showLoading(table);

    try {

        supplierList =
            await SupplierAPI.getAll();

        renderSuppliers(
            supplierList
        );

    } catch (error) {

        showToast(
            error.message,
            "error"
        );
    }
}

// =========================================
// RENDER
// =========================================

function renderSuppliers(data) {

    const table =
        document.getElementById(
            "suppliersTable"
        );

    document.getElementById(
        "supplierCount"
    ).textContent =
        data.length;

    if (!data.length) {

        showEmptyState(
            table,
            "No suppliers found."
        );

        return;
    }

    table.innerHTML = "";

    data.forEach(supplier => {

        const row =
            document.createElement("tr");

        row.innerHTML = `

            <td>
                <strong>
                    ${escapeHTML(supplier.supplierName)}
                </strong>
            </td>

            <td>
                ${escapeHTML(supplier.contactPerson || "-")}
            </td>

            <td>
                ${escapeHTML(supplier.phone || "-")}
            </td>

            <td>
                ${escapeHTML(supplier.email || "-")}
            </td>

            <td>
                ${supplier.leadTimeDays} days
            </td>

            <td>

                <div class="table-actions">

                    <button
                        class="btn-edit"
                        onclick="editSupplier(${supplier.supplierId})">

                        ✏️

                    </button>

                    <button
                        class="btn-delete"
                        onclick="deleteSupplier(${supplier.supplierId})">

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

function filterSuppliers() {

    const term =
        document.getElementById(
            "searchSuppliers"
        ).value.toLowerCase();

    const filtered =
        supplierList.filter(supplier => {

            return supplier.supplierName
                    .toLowerCase()
                    .includes(term)

                || (supplier.contactPerson || "")
                    .toLowerCase()
                    .includes(term)

                || (supplier.phone || "")
                    .toLowerCase()
                    .includes(term)

                || (supplier.email || "")
                    .toLowerCase()
                    .includes(term);
        });

    renderSuppliers(filtered);
}

// =========================================
// OPEN MODAL
// =========================================

function openSupplierModal() {

    editingSupplierId = null;

    document.getElementById(
        "supplierModalTitle"
    ).textContent =
        "Add Supplier";

    document.getElementById(
        "supplierForm"
    ).reset();

    document.getElementById(
        "supplierId"
    ).value = "";

    document.getElementById(
        "supplierLeadTime"
    ).value = 7;

    document.getElementById(
        "supplierModal"
    ).classList.add("show");
}

// =========================================
// CLOSE MODAL
// =========================================

function closeSupplierModal() {

    document.getElementById(
        "supplierModal"
    ).classList.remove("show");
}

// =========================================
// EDIT
// =========================================

function editSupplier(id) {

    const supplier =
        supplierList.find(
            s => s.supplierId === id
        );

    if (!supplier) {
        return;
    }

    editingSupplierId = id;

    document.getElementById(
        "supplierModalTitle"
    ).textContent =
        "Edit Supplier";

    document.getElementById(
        "supplierId"
    ).value =
        supplier.supplierId;

    document.getElementById(
        "supplierName"
    ).value =
        supplier.supplierName;

    document.getElementById(
        "contactPerson"
    ).value =
        supplier.contactPerson || "";

    document.getElementById(
        "supplierPhone"
    ).value =
        supplier.phone || "";

    document.getElementById(
        "supplierEmail"
    ).value =
        supplier.email || "";

    document.getElementById(
        "supplierLeadTime"
    ).value =
        supplier.leadTimeDays;

    document.getElementById(
        "supplierAddress"
    ).value =
        supplier.address || "";

    document.getElementById(
        "supplierModal"
    ).classList.add("show");
}

// =========================================
// SAVE
// =========================================

async function saveSupplier(event) {

    event.preventDefault();

    const supplier = {

        supplierName:
            document.getElementById(
                "supplierName"
            ).value.trim(),

        contactPerson:
            document.getElementById(
                "contactPerson"
            ).value.trim(),

        phone:
            document.getElementById(
                "supplierPhone"
            ).value.trim(),

        email:
            document.getElementById(
                "supplierEmail"
            ).value.trim(),

        leadTimeDays:
            Number(
                document.getElementById(
                    "supplierLeadTime"
                ).value
            ),

        address:
            document.getElementById(
                "supplierAddress"
            ).value.trim()
    };

    try {

        if (editingSupplierId) {

            await SupplierAPI.update(
                editingSupplierId,
                supplier
            );

            showToast(
                "Supplier updated successfully!"
            );

        } else {

            await SupplierAPI.create(
                supplier
            );

            showToast(
                "Supplier added successfully!"
            );
        }

        closeSupplierModal();

        await loadSuppliersPage();

    } catch (error) {

        showToast(
            error.message,
            "error"
        );
    }
}

// =========================================
// DELETE
// =========================================

async function deleteSupplier(id) {

    const confirmed =
        confirm(
            "Delete this supplier?"
        );

    if (!confirmed) {
        return;
    }

    try {

        await SupplierAPI.delete(id);

        showToast(
            "Supplier deleted successfully!"
        );

        await loadSuppliersPage();

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