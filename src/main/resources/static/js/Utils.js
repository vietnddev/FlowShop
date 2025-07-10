let mvFormatCurrency = (currencyInput) => {
    return currencyInput.replace(/,/g, '');
}

let formatCurrency = (number) =>  {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(number);
}

function parseNumbericPrice(raw) {
    if (!raw) return 0;
    return parseFloat(raw.replaceAll(',', '').trim()) || 0;
}

function formatNumberWithCommas(number) {
    if (isNaN(number) || number === null) return '0';
    return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

function formatUSCurrency(value) {
    if (!value) return '0.00';

    // Remove all non-digit and non-dot characters
    let cleanValue = String(value).replace(/[^0-9.]/g, '');

    // Parse to float and fix to 2 decimal places
    let number = parseFloat(cleanValue);
    if (isNaN(number)) number = 0;

    return number.toLocaleString('en-US', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

//Change language
$("#langOptionVi").click(function () {
    mvLang = "vi";
    window.location.replace("?lang=vi");
})

$("#langOptionEn").click(function () {
    mvLang = "en";
    window.location.replace("?lang=en");
})