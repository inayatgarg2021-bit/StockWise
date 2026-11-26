let availableProducts = [];

let cart = [];

// =========================================
// INITIALIZE
// =========================================

document.addEventListener(
    "DOMContentLoaded",
    initializeSales
);

async function initializeSales() {

    await loadSaleProducts();

    await loadSalesHistory();

    renderCart();
}

// =========================================
// LOAD PRODUCTS
// =========================================

async function loadSaleProducts() {

    try {

        availableProducts =
            await ProductAPI.getAll();

        const dropdown =
            document.getElementById(
                "saleProduct"
            );

        dropdown.innerHTML = `
            <option value="">
                Select product
            </option>
        `;

        availableProducts
            .filter(product =>
                product.currentStock > 0
            )
            .forEach(product => {

                dropdown.innerHTML += `
                    <option value="${product.productId}">
                        ${escapeHTML(product.name)}
                        (${product.currentStock} available)
                        - ${formatCurrency(product.sellingPrice)}
                    </option>
                `;
            });

    } catch (error) {

        showToast(
            "Unable to load products.",
            "error"
        );
    }
}

// =========================================
// ADD TO CART
// =========================================

function addToCart() {

    const productId =
        Number(
            document.getElementById(
                "saleProduct"
            ).value
        );

    const quantity =
        Number(
            document.getElementById(
                "saleQuantity"
            ).value
        );

    if (!productId) {

        showToast(
            "Please select a product.",
            "error"
        );

        return;
    }

    if (quantity <= 0) {

        showToast(
            "Quantity must be greater than zero.",
            "error"
        );

        return;
    }

    const product =
        availableProducts.find(
            p => p.productId === productId
        );

    if (!product) {
        return;
    }

    if (quantity > product.currentStock) {

        showToast(
            "Insufficient stock.",
            "error"
        );

        return;
    }

    const existing =
        cart.find(
            item => item.productId === productId
        );

    if (existing) {

        const newQuantity =
            existing.quantity + quantity;

        if (newQuantity >
            product.currentStock) {

            showToast(
                "Quantity exceeds available stock.",
                "error"
            );

            return;
        }

        existing.quantity =
            newQuantity;

    } else {

        cart.push({

            productId:
            product.productId,

            productName:
            product.name,

            sku:
            product.sku,

            price:
            product.sellingPrice,

            quantity:
            quantity

        });
    }

    renderCart();

    document.getElementById(
        "saleQuantity"
    ).value = 1;

    showToast(
        "Product added to cart."
    );
}

// =========================================
// RENDER CART
// =========================================

function renderCart() {

    const table =
        document.getElementById(
            "cartTable"
        );

    if (!cart.length) {

        table.innerHTML = `
            <tr>
                <td colspan="5"
                    style="text-align:center;padding:30px;color:#64748b;">

                    Your cart is empty.

                </td>
            </tr>
        `;

        document.getElementById(
            "cartTotal"
        ).textContent =
            formatCurrency(0);

        return;
    }

    table.innerHTML = "";

    let total = 0;

    cart.forEach((item, index) => {

        const subtotal =
            item.price * item.quantity;

        total += subtotal;

        const row =
            document.createElement("tr");

        row.innerHTML = `

            <td>
                ${escapeHTML(item.productName)}
            </td>

            <td>
                ${formatCurrency(item.price)}
            </td>

            <td>
                ${item.quantity}
            </td>

            <td>
                ${formatCurrency(subtotal)}
            </td>

            <td>

                <button
                    class="btn-delete"
                    onclick="removeFromCart(${index})">

                    🗑️

                </button>

            </td>

        `;

        table.appendChild(row);
    });

    document.getElementById(
        "cartTotal"
    ).textContent =
        formatCurrency(total);
}

// =========================================
// REMOVE CART ITEM
// =========================================

function removeFromCart(index) {

    cart.splice(index, 1);

    renderCart();
}

// =========================================
// COMPLETE SALE
// =========================================

async function completeSale() {

    if (!cart.length) {

        showToast(
            "Your cart is empty.",
            "error"
        );

        return;
    }

    const confirmed =
        confirm(
            "Complete this sale?"
        );

    if (!confirmed) {
        return;
    }

    const sale = {

        paymentMethod:
        document.getElementById(
            "paymentMethod"
        ).value,

        items:
            cart.map(item => ({

                productId:
                item.productId,

                quantity:
                item.quantity

            }))
    };

    try {

        await SalesAPI.create(sale);

        showToast(
            "Sale completed successfully!"
        );

        cart = [];

        renderCart();

        await loadSaleProducts();

        await loadSalesHistory();

    } catch (error) {

        showToast(
            error.message,
            "error"
        );
    }
}

// =========================================
// SALES HISTORY
// =========================================

async function loadSalesHistory() {

    const table =
        document.getElementById(
            "salesTable"
        );

    showLoading(table);

    try {

        const sales =
            await SalesAPI.getAll();

        document.getElementById(
            "salesCount"
        ).textContent =
            sales.length;

        if (!sales.length) {

            showEmptyState(
                table,
                "No sales recorded."
            );

            return;
        }

        table.innerHTML = "";

        sales.forEach(sale => {

            const itemCount =
                sale.items
                    ? sale.items.reduce(
                        (total, item) =>
                            total + item.quantity,
                        0
                    )
                    : 0;

            const row =
                document.createElement("tr");

            row.innerHTML = `

                <td>
                    #${sale.saleId}
                </td>

                <td>
                    ${formatDate(sale.saleDate)}
                </td>

                <td>
                    ${itemCount}
                </td>

                <td>
                    <strong>
                        ${formatCurrency(sale.totalAmount)}
                    </strong>
                </td>

                <td>
                    ${escapeHTML(sale.paymentMethod)}
                </td>

            `;

            table.appendChild(row);
        });

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