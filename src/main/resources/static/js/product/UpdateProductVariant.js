function updateProductVariant(productVariantId) {
    let apiURL = mvHostURLCallApi + "/product/variant/update/" + productVariantId;
    let body = {
        variantName: mvVariantNameF2.val(),
        //originalPrice: $("#originalPriceField").val().replaceAll(',', ''),
        //discountPrice: $("#promotionPriceField").val().replaceAll(',', ''),
        price: {
            //purchasePrice: mvPurchasePriceF2.val().replaceAll(',', ''),
            costPrice: mvCostPriceF2.val().replaceAll(',', ''),
            retailPrice: mvRetailPriceF2.val().replaceAll(',', ''),
            //retailPriceDiscount: mvRetailPriceDiscountF2.val().replaceAll(',', ''),
            wholesalePrice: mvWholesalePriceF2.val().replaceAll(',', '')
            //wholesalePriceDiscount: mvWholesalePriceDiscountF2.val().replaceAll(',', '')
        },
        defectiveQty: mvDefectiveQtyF2.val(),
        weight: mvWeightF2.val(),
        note: mvVariantNoteF2.val()
    };
    $.ajax({
        url: apiURL,
        type: "PUT",
        contentType: "application/json",
        data: JSON.stringify(body),
        success: function (response) {
            if (response.status === "OK") {
                alert("Update product variant successfully!");
                window.location.reload();
            }
        },
        error: function (xhr) {
            alert("Error: " + $.parseJSON(xhr.responseText).message);
        }
    });
}