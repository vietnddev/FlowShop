$(document).ready(function () {
    init();
    setupListeners();
    submitProductOnSearchModal("goodsImport");
});

function init() {
    loadItems();
    loadPaymentMethods();
    loadPaymentStatuses();
}

function setupListeners() {
    $("#btnAddItems").on("click", function () {
        $("#searchProductModal").modal();
        setupSearchModalInCreateOrderPage();
    });
}

function loadItems() {
    let apiURL = mvHostURLCallApi + `/stg/transaction-goods/import/${mvTransactionGoodsImportId}/item`;
    let params = {};
    $.get(apiURL, params, function (response) {
        if (response.status === "OK") {
            let data = response.data;
            $("#tblItems").empty();
            appendItems(data);
        }
    }).fail(function (xhr) {
        alert("Error: " + $.parseJSON(xhr.responseText).message);
    });
}

function addGoodsImportItems(pItems) {
    let apiURL = mvHostURLCallApi + `/stg/transaction-goods/import/${mvTransactionGoodsImportId}/item/add`;
    let body = {
        reqItems: pItems
    }
    $.ajax({
        url: apiURL,
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(body),
        success: function (response) {
            if (response.status === "OK") {
                alert(response.message);
                loadItems();
                $("#searchProductModal").modal("hide");
            }
        },
        error: function (xhr) {
            alert("Error: " + $.parseJSON(xhr.responseText).message);
        }
    });
}

function appendItems(pItems) {
    let currentRowCount = $('#tblItems').children('tr').length;
    $.each(pItems, function (index, d) {
        $("#tblItems").append(`
            <tr class="row-item" itemId="${d.id}">
                <td>${currentRowCount + index + 1}</td>         
                <td>${d.itemType}</td>
                <td>${d.itemName}</td>
                <td>${d.unitCost}</td>
                <td>${d.quantity}</td>
                <td>${d.amount}</td>
                <td>${d.note}</td>
                <td></td>
            </tr>
        `);
    });
}