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

    $("#btnUpdateTransactionGoods").on("click", function () {
        $(this).attr("actionType", "update");
        showConfirmModal($(this), "Thông báo hệ thống!", "Bạn muốn cập nhật thông tin phiếu nhập hàng hóa?")
    });

    $("#yesButton").on("click", function () {
        let actionType = $(this).attr("actiontype");
        if (actionType === "update") {
            updateTransactionGoods();
        }
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
                callApiDelete(mvHostURLCallApi + "/file/delete/" + entityId)
            }
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

function addTransactionGoodsItems(pItems) {
    if (mvTransactionType === "IMPORT") {
        let apiURL = mvHostURLCallApi + `/stg/transaction-goods/import/${mvTransactionGoodsId}/item/add`;
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
    } else {}
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
    if (mvTransactionType === "IMPORT") {
        let apiURL = mvHostURLCallApi + `/stg/transaction-goods/import/update/${mvTransactionGoodsId}`;
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
    } else {}
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