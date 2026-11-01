/* =================================================================
   small progressive enhancements.
   Every page works without it, it is this only to make it nicer.
   ================================================================= */
(function () {
    "use strict";

    // ------------------------------------------------ flash messages
    // They slide away on their own after a few seconds, or when closed.
    document.querySelectorAll(".toast").forEach(function (toast) {
        var dismiss = function () {
            toast.classList.add("leaving");
            setTimeout(function () { toast.remove(); }, 400);
        };
        var close = toast.querySelector("button");
        if (close) close.addEventListener("click", dismiss);
        setTimeout(dismiss, toast.classList.contains("toast-error") ? 9000 : 6000);
    });

    // ------------------------------------------------ menus on small screens
    document.querySelectorAll("[data-toggle-open]").forEach(function (button) {
        button.addEventListener("click", function () {
            document.querySelector(button.dataset.toggleOpen).classList.toggle("open");
        });
    });

    // ------------------------------------------------ "are you sure?" before destructive actions
    document.querySelectorAll("form[data-confirm]").forEach(function (form) {
        form.addEventListener("submit", function (event) {
            if (!window.confirm(form.dataset.confirm)) event.preventDefault();
        });
    });

    document.querySelectorAll("[data-print]").forEach(function (button) {
        button.addEventListener("click", function () { window.print(); });
    });

    // ------------------------------------------------ the donate form
    var form = document.getElementById("donate-form");
    if (!form) return;

    var feeRate = parseFloat(form.dataset.feeRate);
    var matchMultiplier = parseFloat(form.dataset.matchMultiplier);
    var amountInput = form.querySelector("[name=amount]");

    var valueOf = function (name) {
        var field = form.querySelector("[name=" + name + "]");
        return field && !field.disabled ? field.value.trim() : "";
    };
    var isTicked = function (code) {
        var box = form.querySelector("input[name=addOns][value=" + code + "]");
        return box && !box.disabled && box.checked;
    };
    var money = function (value) {
        return value.toLocaleString("en-US", { style: "currency", currency: "USD" });
    };
    var round2 = function (value) { return Math.round(value * 100) / 100; };
    var setText = function (key, text) {
        form.querySelectorAll("[data-summary=" + key + "]").forEach(function (el) { el.textContent = text; });
    };
    var showLine = function (key, visible) {
        form.querySelectorAll("[data-line=" + key + "]").forEach(function (el) { el.hidden = !visible; });
    };

    // Fields that do not apply are hidden AND disabled, so the browser does not send them.
    var toggleSection = function (element, visible) {
        element.hidden = !visible;
        element.querySelectorAll("input, select, textarea").forEach(function (input) { input.disabled = !visible; });
    };

    var update = function () {
        var methodInput = form.querySelector("input[name=method]:checked");
        var method = methodInput ? methodInput.value : "";
        form.querySelectorAll("[data-for]").forEach(function (section) {
            toggleSection(section, section.dataset.for.split(" ").indexOf(method) >= 0);
        });

        var campaign = form.querySelector("input[name=campaignId]:checked");
        var general = !campaign || campaign.value === "";
        form.querySelectorAll("[data-when-general]").forEach(function (section) { toggleSection(section, general); });
        var category = form.querySelector("[name=categoryId]");
        setText("cause", general
            ? (category && category.selectedIndex > 0 ? category.options[category.selectedIndex].text : "Where it's needed most")
            : campaign.dataset.title);
        setText("method", methodInput ? methodInput.dataset.label : "-");

        // The same wrapping order as AddOnFactory: fees first, then the match, then the dedication.
        var inKind = method === "IN_KIND";
        var base = parseFloat(inKind ? valueOf("estimatedValue") : valueOf("amount")) || 0;
        var total = base, deductible = base, fee = 0, match = 0;
        var description = inKind ? "In-kind gift: " + (valueOf("itemDescription") || "...") : "Monetary gift";

        // The fee goes to the card processor, so it adds to the receipt but not to the total impact.
        if (!inKind && isTicked("COVER_FEES")) {
            fee = round2(total * feeRate);
            deductible += fee;
            description += ", processing fees covered";
        }
        if (!inKind && isTicked("EMPLOYER_MATCH")) {
            match = total * (matchMultiplier - 1);
            total += match;
            description += ", matched by " + (valueOf("employerName") || "your employer");
        }
        if (!inKind && isTicked("DEDICATION")) {
            description += ", in honour of " + (valueOf("honoreeName") || "...");
        }

        setText("gift", money(base));
        setText("fee", "+ " + money(fee));
        setText("match", "+ " + money(match));
        setText("total", money(total));
        setText("deductible", money(deductible));
        setText("description", description);
        showLine("fee", fee > 0);
        showLine("match", match > 0);

        form.querySelectorAll(".preset").forEach(function (preset) {
            preset.classList.toggle("active", parseFloat(preset.dataset.amount) === parseFloat(amountInput.value));
        });
    };

    form.querySelectorAll(".preset").forEach(function (preset) {
        preset.addEventListener("click", function () {
            amountInput.value = preset.dataset.amount;
            update();
        });
    });
    form.addEventListener("input", update);
    form.addEventListener("change", update);
    update();
})();
