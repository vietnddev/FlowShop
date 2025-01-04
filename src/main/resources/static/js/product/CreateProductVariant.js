function createProductVariant() {
    $("#btnCreateProductVariantSubmit").on("click", function () {
        if ($("#originalPriceField").val() === "") {
            alert("Can't insert null origial price!")
            return;
        }
        let paramsCheckExists = {
            productId : mvProductId,
            colorId : mvColorField.val(),
            sizeId : mvSizeField.val(),
            fabricTypeId : mvFabricTypeField.val(),
            originalPrice : $("#originalPriceField").val(),
            discountPrice : $("#promotionPriceField").val()
        }
        $.get(mvHostURLCallApi + "/product/variant/exists", paramsCheckExists, function (response) {
            if (response.data === true) {
                alert("This product variant already exists!");
            } else {
                let apiURL = mvHostURLCallApi + "/product/variant/create";
                let body = {
                    productId : mvProductId,
                    variantName : mvProductVariantNameField.val(),
                    variantCode : mvProductVariantCodeField.val(),
                    fabricTypeId : parseInt($("#fabricTypeField").val()),
                    colorId : parseInt($("#colorField").val()),
                    sizeId : parseInt($("#sizeField").val()),
                    originalPrice : parseFloat($("#originalPriceField").val().replaceAll(',', '')),
                    discountPrice : parseFloat($("#promotionPriceField").val().replaceAll(',', '')),
                    price : {
                        purchasePrice : parseFloat(mvPurchasePriceField.val().replaceAll(',', '')),
                        costPrice : parseFloat(mvCostPriceField.val().replaceAll(',', '')),
                        retailPrice : parseFloat(mvRetailPriceField.val().replaceAll(',', '')),
                        retailPriceDiscount : parseFloat(mvRetailPriceDiscountField.val().replaceAll(',', '')),
                        wholesalePrice : parseFloat(mvWholesalePriceField.val().replaceAll(',', '')),
                        wholesalePriceDiscount : parseFloat(mvWholesalePriceDiscountField.val().replaceAll(',', ''))
                    },
                    storageQty : parseInt(mvStorageQtyField.val()),
                    soldQty : parseInt(mvSoldQtyField.val()),
                    defectiveQty : parseInt(mvDefectiveQtyField.val()),
                    weight : mvWeightField.val(),
                    note : mvVariantNoteField.val(),
                    storageIdInitStorageQty : parseInt(mvKhoTonKhoInit.val()),
                    storageIdInitSoldQty : parseInt(mvKhoDaBanInit.val())
                };
                $.ajax({
                    url: apiURL,
                    type: "POST",
                    contentType: "application/json",
                    data: JSON.stringify(body),
                    success: function (response) {
                        if (response.status === "OK") {
                            alert("Create new product variant successfully!");
                            window.location.reload();
                        }
                    },
                    error: function (xhr) {
                        alert("Error: " + $.parseJSON(xhr.responseText).message);
                    }
                });
            }
        });
    });
}