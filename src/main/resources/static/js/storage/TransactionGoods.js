let mvTitle = $("#titleField");

$(document).ready(function () {
    init();
    setupListeners();
    submitProductOnSearchModal("goodsImport");
});

function init() {
    loadItems();
    loadUploadedImages();
}

function setupListeners() {
    $("#btnAddItems").on("click", function () {
        $("#searchProductModal").modal();
        setupSearchModalInCreateOrderPage();
    });

    $("#btnGenerateItems").on("click", function () {
        let lvOrderCode = mvOrderCodeField.val().trim();
        if (lvOrderCode === "") {
            alert("Please input order code!");
            return;
        }

        $(this).attr("entity", "transactionGoodsItem");
        $(this).attr("actionType", "generate");
        showConfirmModal($(this), "Thông báo hệ thống!", `Do you want to generate items by this order code, ${lvOrderCode}?`);
    });

    $("#btnUpdateTransactionGoods").on("click", function () {
        $(this).attr("entity", "transactionGoods");
        $(this).attr("entityId", mvTransactionGoodsId);
        $(this).attr("actionType", "update");
        showConfirmModal($(this), "Thông báo hệ thống!", "Do you want to update?");
    });

    $("#btnDeleteTransactionGoods").on("click", function () {
        $(this).attr("entity", "transactionGoods");
        $(this).attr("entityId", mvTransactionGoodsId);
        $(this).attr("actionType", "delete");
        showConfirmModal($(this), "Thông báo hệ thống!", "Do you want to delete?");
    });

    $(document).on("click", ".btn-delete-image", function () {
        $(this).attr("entity", "image");
        $(this).attr("entityId", $(this).attr("imageId"));
        $(this).attr("actionType", "delete");
        showConfirmModal($(this), null, "Bạn có chắc muốn xóa " + $(this).attr("imageName"));
    });

    $('#yesButton').on("click", function () {
        let entity = $(this).attr("entity")
        let entityId = $(this).attr("entityId")
        let actionType = $(this).attr("actionType")

        if (entity === "image") {
            if (actionType === "delete") {
                callApiDelete(mvHostURLCallApi + "/file/delete/" + entityId);
            }
        } else if (entity === "transactionGoods") {
            if (actionType === "update") {
                updateTransactionGoods();
            } else if (actionType === "delete") {
                deleteTransactionGoods();
            }
        } else if (entity === "transactionGoodsItem") {
            if (actionType === "generate") {
                generateItems();
            }
        }
    });
}

function addTransactionGoodsItems(pItems) {
    let apiURL = mvHostURLCallApi + `/stg/transaction-goods/${mvTransactionGoodsId}/item/add`;
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

function generateItems() {
    let apiURL = mvHostURLCallApi + `/stg/transaction-goods/${mvTransactionGoodsId}/item/generate`;
    let body = {
        order: {
            code: mvOrderCodeField.val()
        }
    };
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

function loadItems() {
    let apiURL = mvHostURLCallApi + `/stg/transaction-goods/${mvTransactionGoodsId}/item`;
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

function appendItems(pItems) {
    let currentRowCount = $('#tblItems').children('tr').length;
    $.each(pItems, function (index, d) {
        $("#tblItems").append(`
            <tr class="row-item" itemId="${d.id}">
                <td>${currentRowCount + index + 1}</td>
                <td>[${d.itemType}] ${d.itemName}</td>
                <td>${d.unitCost}</td>
                <td>${d.quantity}</td>
                <td>${d.amount}</td>
                <td>${d.note}</td>
                <td>
                    <button type="button" class="btn btn-sm btn-primary btnUpdateItem" item="${d}">
                        <i class="fa-solid fa-pencil"></i>
                    </button>
                    <button type="button" class="btn btn-sm btn-danger btnDeleteItem" item="${d}">
                        <i class="fa-solid fa-trash"></i>
                    </button>
                </td>
            </tr>
        `);
    });
}

function updateTransactionGoods() {
    let lvTranType = "N/A";
    if (mvTransactionType === "IMPORT") {
        lvTranType = "import";
    } else if (mvTransactionType === "EXPORT") {
        lvTranType = "export";
    }
    let apiURL = `${mvHostURLCallApi}/stg/transaction-goods/${lvTranType}/update/${mvTransactionGoodsId}`;
    let body = {
        title: mvTitle.val(),
        description: mvDescription.val(),
        transactionStatus: mvStatus.val()
    };
    $.ajax({
        url: apiURL,
        type: "PUT",
        contentType: "application/json",
        data: JSON.stringify(body),
        success: function (response, textStatus, jqXHR) {
            if (response.status === "OK") {
                alert("Updated successfully!");
                window.location.reload();
            }
        },
        error: function (xhr) {
            alert("Error: " + $.parseJSON(xhr.responseText).message);
        }
    });
}

function deleteTransactionGoods() {
    let lvTranType = "N/A";
    if (mvTransactionType === "IMPORT") {
        lvTranType = "import";
    } else if (mvTransactionType === "EXPORT") {
        lvTranType = "export";
    }
    let apiURL = `${mvHostURLCallApi}/stg/transaction-goods/${lvTranType}/delete/${mvTransactionGoodsId}`;
    let body = {};
    $.ajax({
        url: apiURL,
        type: "DELETE",
        contentType: "application/json",
        data: JSON.stringify(body),
        success: function (response, textStatus, jqXHR) {
            if (response.status === "OK") {
                alert("Delete successfully!");
                window.location =  mvHostURL + '/stg/transaction-goods/' + lvTranType;
            }
        },
        error: function (xhr) {
            alert("Error: " + $.parseJSON(xhr.responseText).message);
        }
    });
}

function loadUploadedImages() {
    let apiURL = mvHostURLCallApi + `/stg/transaction-goods/${mvTransactionGoodsId}/image`;
    $.get(apiURL, function (response) {
        if (response.status === "OK") {
            let data = response.data;
            let gridUploadedImages = $("#gridUploadedImages");
            $.each(data, function (index, d) {
                let imageSrc = `/${d.directoryPath}/${d.storageName}`;
                let imageStyle = `border-radius: 5px`;

                gridUploadedImages.append(`
                    <div class="col-2 mb-2">
                        <div class="card border" style="height: 186px">
                            <div class="card-body product-image-thumb" style="margin: auto; border: none">
                                <a href="${imageSrc}" data-toggle="lightbox" data-title="${d.storageName}" data-gallery="gallery">
                                    <img src="${imageSrc}" class="img-fluid sub-image" alt="Imported image" imageId="${d.id}" style="${imageStyle}">
                                </a>
                            </div>
                            <div class="card-footer row">
                                <i style="cursor: pointer" imageId="${d.id}" imageName="${d.customizeName}" class="fa-solid fa-trash text-danger col btn-delete-image"></i>
                            </div>
                        </div>
                    </div>
                `);
            });
        }
    }).fail(function () {
        showErrorModal("Could not connect to the server");
    });
}