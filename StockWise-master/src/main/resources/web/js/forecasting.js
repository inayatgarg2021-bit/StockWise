let forecasts = [];

let currentMultiplier = 1;

// =========================================
// INITIALIZATION
// =========================================

document.addEventListener(
    "DOMContentLoaded",
    initializeForecast
);

async function initializeForecast() {

    await loadForecasts();

    document.getElementById(
        "demandSlider"
    ).addEventListener(
        "input",
        handleDemandSlider
    );

    document.getElementById(
        "forecastSearch"
    ).addEventListener(
        "input",
        filterForecasts
    );
}

// =========================================
// LOAD FORECAST
// =========================================

async function loadForecasts(
    multiplier = 1
) {

    const table =
        document.getElementById(
            "forecastTable"
        );

    showLoading(table);

    try {

        forecasts =
            await ForecastAPI.get(multiplier);

        renderForecasts(forecasts);

        updateForecastKPIs(forecasts);

    } catch (error) {

        showToast(
            error.message,
            "error"
        );
    }
}

// =========================================
// DEMAND SLIDER
// =========================================

let sliderTimeout;

function handleDemandSlider(event) {

    currentMultiplier =
        Number(event.target.value);

    document.getElementById(
        "multiplierValue"
    ).textContent =
        `${currentMultiplier.toFixed(1)}x`;

    updateMultiplierDescription(
        currentMultiplier
    );

    clearTimeout(sliderTimeout);

    sliderTimeout =
        setTimeout(() => {

            loadForecasts(
                currentMultiplier
            );

        }, 300);
}

// =========================================
// DESCRIPTION
// =========================================

function updateMultiplierDescription(
    multiplier
) {

    const description =
        document.getElementById(
            "multiplierDescription"
        );

    if (multiplier < 1) {

        description.textContent =
            "Reduced demand";

    } else if (multiplier === 1) {

        description.textContent =
            "Normal demand";

    } else if (multiplier <= 1.5) {

        description.textContent =
            "Increased demand";

    } else {

        description.textContent =
            "Demand surge";
    }
}

// =========================================
// RENDER FORECASTS
// =========================================

function renderForecasts(data) {

    const table =
        document.getElementById(
            "forecastTable"
        );

    if (!data.length) {

        showEmptyState(
            table,
            "No forecast data available."
        );

        return;
    }

    table.innerHTML = "";

    data.forEach(item => {

        const daysText =
            item.estimatedDaysRemaining >= 900
                ? "∞"
                : `${item.estimatedDaysRemaining} days`;

        let statusClass =
            "badge-healthy";

        if (
            item.status === "OUT OF STOCK"
            || item.status === "CRITICAL"
        ) {

            statusClass =
                "badge-critical";

        } else if (
            item.status === "LOW STOCK"
        ) {

            statusClass =
                "badge-low";
        }

        const row =
            document.createElement("tr");

        row.innerHTML = `

            <td>
                <strong>
                    ${escapeHTML(item.sku)}
                </strong>
            </td>

            <td>

                <strong>
                    ${escapeHTML(item.name)}
                </strong>

                <small class="forecast-subtext">

                    ${escapeHTML(item.category)}
                    •
                    ${escapeHTML(item.attributeInfo || "")}

                </small>

            </td>

            <td>
                ${item.currentStock}
            </td>

            <td>
                ${item.avgDailySales}
            </td>

            <td>
                ${daysText}
            </td>

            <td>
                ${item.reorderPoint}
            </td>

            <td>

                <strong>
                    +${item.suggestedReorderQty}
                </strong>

            </td>

            <td>

                <div class="priority-container">

                    <strong>
                        ${item.priorityScore}
                    </strong>

                    <div class="priority-bar">

                        <div
                            class="priority-fill"
                            style="width:${item.priorityScore}%">

                        </div>

                    </div>

                </div>

            </td>

            <td>

                <span class="badge ${statusClass}">

                    ${escapeHTML(item.status)}

                </span>

            </td>

        `;

        table.appendChild(row);
    });
}

// =========================================
// KPI CARDS
// =========================================

function updateForecastKPIs(data) {

    document.getElementById(
        "forecastTotal"
    ).textContent =
        data.length;

    const critical =
        data.filter(item =>
            item.status === "CRITICAL"
            || item.status === "OUT OF STOCK"
        ).length;

    const low =
        data.filter(item =>
            item.status === "LOW STOCK"
        ).length;

    const healthy =
        data.filter(item =>
            item.status === "HEALTHY"
        ).length;

    document.getElementById(
        "forecastCritical"
    ).textContent =
        critical;

    document.getElementById(
        "forecastLow"
    ).textContent =
        low;

    document.getElementById(
        "forecastHealthy"
    ).textContent =
        healthy;
}

// =========================================
// SEARCH
// =========================================

function filterForecasts() {

    const term =
        document.getElementById(
            "forecastSearch"
        ).value.toLowerCase();

    const filtered =
        forecasts.filter(item => {

            return item.name
                    .toLowerCase()
                    .includes(term)

                || item.sku
                    .toLowerCase()
                    .includes(term)

                || item.category
                    .toLowerCase()
                    .includes(term);
        });

    renderForecasts(filtered);
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