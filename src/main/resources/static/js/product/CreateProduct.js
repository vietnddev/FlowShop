function createProduct() {
    let creationType = $('#createProductSubmit').attr('creationType');

    let apiURL = mvHostURLCallApi + "/product/create";
    let productTypeId = parseInt($("#productTypeField").val());
    let brandId = parseInt($("#brandField").val());
    let productName = $("#productNameField").val();
    let productCode = $("#productCodeField").val().trim();
    let unitId = parseInt($("#unitField").val());
    let productStatus = $("#statusField").val();

    //Validate
    if (creationType === 'newProduct') {
        if (!productCode) {
            alert("Please enter product code!");
            return;
        }
        if (!productTypeId) {
            alert("Please enter product type!");
            return;
        }
        if (!unitId) {
            alert("Please enter product unit!");
            return;
        }
        if (!brandId) {
            alert("Please enter product brand!");
            return;
        }
        if (!productStatus) {
            alert("Please enter product status!");
            return;
        }
    }

    //Update variant's actual value
    $('#tblInsertVariants tr').each(function () {
        let $row = $(this);

        let colorId       = $row.find('td[colorId]').attr('colorId');
        let sizeId        = $row.find('td[sizeId]').attr('sizeId');
        let fabricTypeId  = $row.find('td[fabricTypeId]').attr('fabricTypeId');

        let retailPrice = parseFloat($row.find('input[name="txtVariantRetailPrice"]').val().replaceAll(',', '')) || 0;
        let wholesalePrice = retailPrice;
        let costPrice   = parseFloat($row.find('input[name="txtVariantCostPrice"]').val().replaceAll(',', ''))   || 0;
        let soldQty     = parseInt($row.find('input[name="txtSoldQty"]').val())    || 0;
        let storageQty  = parseInt($row.find('input[name="txtStorageQty"]').val()) || 0;

        let key = colorId + "-" + sizeId + "-" + fabricTypeId;

        let variant = mvVariantAddedList.find(item => item.key === key);
        if (variant) {
            variant.retailPrice = retailPrice;
            variant.costPrice   = costPrice;
            variant.soldQty     = soldQty;
            variant.storageQty  = storageQty;
            variant.price = {
                retailPrice: retailPrice,
                wholesalePrice: wholesalePrice,
                costPrice: costPrice
            };
        }
    });

    //Update attribute's actual value
    $('#tblInsertAttributes tr').each(function () {
        let $row = $(this);

        let attributeName = $row.find('.attribute-name').text();
        let attributeValue = $row.find('.attribute-value').text();
        let attributeSort = $row.find('.attribute-sort').text();

        let key = attributeName;

        let attribute = mvAttributeAddedList.find(item => item.key === key);
        if (attribute) {
            attribute.attributeName = attributeName;
            attribute.attributeValue = attributeValue;
            attribute.sort = attributeSort;
        }
    });

    let body = {
        productType: {
            id: productTypeId
        },
        brand: {
            id: brandId
        },
        unit: {
            id: unitId
        },
        productName: productName,
        productCode: productCode,
        status: productStatus,
        variants: mvVariantAddedList,
        attributes: mvAttributeAddedList
    };

    if (creationType === 'variantOnly') {
        let productId = Number($('#createProductSubmit').attr('productId'));
        apiURL = `${mvHostURLCallApi}/product/${productId}/variant/create`;
        body = {
            productId: productId,
            variants: mvVariantAddedList
        };
    } else if (creationType === 'attributeOnly') {
        let productId = Number($('#createProductSubmit').attr('productId'));
        apiURL = `${mvHostURLCallApi}/product/${productId}/attribute/create`;
        body = {
            productId: productId,
            attributes: mvAttributeAddedList
        };
    }

    $.ajax({
        url: apiURL,
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(body),
        success: function (response, textStatus, jqXHR) {
            if (response.status === "OK") {
                alert("Create successfully")
                window.location.reload();
            }
        },
        error: function (xhr, textStatus, errorThrown) {
            //showErrorModal("Could not connect to the server");
            showErrorModal($.parseJSON(xhr.responseText).message);
        }
    });
}