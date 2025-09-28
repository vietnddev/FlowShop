$(document).ready(function () {
    $("#btnAddItems").on("click", function () {
        $("#searchProductModal").modal();
        setupSearchModalInCreateOrderPage();
    });
});

function addGoodsImportItems(pItems) {
    let apiURL = mvHostURLCallApi + '/stg/transaction-goods/import/create';
    let body = {
        transactionType: null,
        transactionStatus: null,
        description: null,
        transactionTime: null,
        confirmedBy: null,
        confirmedTime: null,
        requestNote: null,
        purpose: null,
        sourceType: null,
        order: null,
        warehouse: null,
        items: pItems
    }
    $.ajax({
        url: apiURL,
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(body),
        success: function (response) {
            if (response.status === "OK") {
                alert(response.message);
                window.location.reload();
            }
        },
        error: function (xhr) {
            alert("Error: " + $.parseJSON(xhr.responseText).message);
        }
    });
}