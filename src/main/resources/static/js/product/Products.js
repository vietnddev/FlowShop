$(document).ready(function () {
    createListener();
});

function createListener() {
    $('#createProduct').on('click', function () {
        loadCategory();
    });

    $('#btnAddVariant').on('click', function () {
        addVariant();
    });

    $('#btnAddAttribute').on('click', function () {
        addAttribute();
    });

    $(document).on('click', 'button[name="btnRemoveVariant"]', function () {
        let $row = $(this).closest('tr');

        let colorId      = $row.find('td[colorId]').attr('colorId');
        let sizeId       = $row.find('td[sizeId]').attr('sizeId');
        let fabricTypeId = $row.find('td[fabricTypeId]').attr('fabricTypeId');
        let key = colorId + "-" + sizeId + "-" + fabricTypeId;

        let index = mvVariantAddedList.findIndex(item => item.key === key);
        if (index !== -1) {
            mvVariantAddedList.splice(index, 1);
        }

        $row.remove();
    });

    $(document).on('click', 'button[name="btnRemoveAttribute"]', function () {
        let $row = $(this).closest('tr');

        let attributeName = $row.find('.attribute-name').text();
        let key = attributeName;

        let index = mvAttributeAddedList.findIndex(item => item.key === key);
        if (index !== -1) {
            mvAttributeAddedList.splice(index, 1);
        }

        $row.remove();
    });

    $(document).on('change', 'input[name="txtVariantRetailPrice"]', function () {
        let inputValue = $(this).val().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
        $(this).val(inputValue);
    });

    $(document).on('change', 'input[name="txtVariantCostPrice"]', function () {
        let inputValue = $(this).val().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
        $(this).val(inputValue);
    });

    $('#createProductSubmit').on('click', function () {
        createProduct();
    });

    $(document).on('click', 'button[name="btnViewDetail"]', function () {
        let lvProductInfo = mvProductList[$(this).attr("productId")];
        showProductDetailOnPopup(lvProductInfo);
    });

    $(document).on('click', 'button[name="btnUpdatePrice"]', function () {
        let productVariantId = $(this).attr("productVariantId");
        let rowInfo = $(this).closest('tr');
        let variantCode = rowInfo.find('td[name="colVariantCode"]').text().trim();
        let costPrice = rowInfo.find('td[name="colCostPrice"]').text().trim();
        let retailPrice = rowInfo.find('td[name="colRetailPrice"]').text().trim();

        $("#pUP_VariantCode").text(variantCode);
        $("#pUP_CostPrice").val(costPrice);
        $("#pUP_RetailPrice").val(retailPrice);
        $("#pUP_Confirm").attr("variantId", productVariantId);
        $("#popUpdatePrice").modal("show");
    });

    $(document).on('click', '#pUP_Confirm', function () {
        updatePrice();
    });

    // $(document).on('click', '[data-widget="expandable-table"]', function (e) {
    //     e.stopPropagation();
    //     var $icon = $(this);
    //     var $mainRow = $icon.closest('tr');
    //     var $expandRow = $mainRow.next('.expandable-body');
    //     var $subHeaderRow = $expandRow.find('thead tr');
    //
    //     // Small delay to wait Bootstrap toggle done
    //     setTimeout(function() {
    //         var isExpanded = !$expandRow.hasClass('d-none');
    //
    //         // Remove highlight class from all
    //         $('tr').removeClass('table-primary');
    //
    //         if (isExpanded) {
    //             $mainRow.addClass('table-primary');
    //             $expandRow.addClass('table-primary');
    //             $subHeaderRow.addClass('table-primary'); // Add for sub-header
    //         }
    //     }, 20);
    // });



    $("#pUP_RetailPrice").on("change", function () {
        let inputValue = formatUSCurrency($(this).val());
        $(this).val(inputValue);
    })

    $("#pUP_CostPrice").on("change", function () {
        let inputValue = formatUSCurrency($(this).val());
        $(this).val(inputValue);
    });
}

function addVariant() {
    let lvTableVariants = $("#tblInsertVariants");
    let lvColorSelect = $('#colorField');
    let lvSizeSelect = $('#sizeField');
    let lvFabricTypeSelect = $('#fabricTypeField');

    if (lvColorSelect.val() === "" || lvSizeSelect.val() === "" || lvFabricTypeSelect.val() === "") {
        alert("Invalid information!");
        return;
    }

    let lvColorId = lvColorSelect.val();
    let lvColorName = lvColorSelect.find('option:selected').text();
    let lvSizeId = lvSizeSelect.val();
    let lvSizeName = lvSizeSelect.find('option:selected').text();
    let lvFabricTypeId = lvFabricTypeSelect.val();
    let lvFabricTypeName = lvFabricTypeSelect.find('option:selected').text();
    let lvRetailPrice = 0;
    let lvCostPrice = 0;
    let lvSoldQty = 0;
    let lvStorageQty = 0;
    let key = lvColorId + "-" + lvSizeId + "-" + lvFabricTypeId;

    let variant = mvVariantAddedList.find(item => item.key === key);
    if (variant) {
        alert("This product already exists!");
        return;
    }

    lvTableVariants.append(`
        <tr>
            <td colorId="${lvColorId}">${lvColorName}</td>
            <td sizeId="${lvSizeId}">${lvSizeName}</td>
            <td fabricTypeId="${lvFabricTypeId}">${lvFabricTypeName}</td>
            <td><input type="text"   class="form-control" style="width: 120px" name="txtVariantRetailPrice" value="${lvRetailPrice}"></td>
            <td><input type="text"   class="form-control" style="width: 120px" name="txtVariantCostPrice"   value="${lvCostPrice}"></td>
            <td><input type="number" class="form-control" style="width: 60px"  name="txtSoldQty"            value="${lvSoldQty}"></td>
            <td><input type="number" class="form-control" style="width: 60px"  name="txtStorageQty"         value="${lvStorageQty}"></td>
            <td><button type="button" class="btn btn-sm btn-secondary" name="btnRemoveVariant">X</button></td>
        </tr>
    `);

    let lvVariant = {
        key: key,
        color: {
            id: lvColorId
        },
        size: {
            id: lvSizeId
        },
        fabricType: {
            id: lvFabricTypeId
        },
        price: {
            retailPrice: lvRetailPrice,
            wholesalePrice: lvRetailPrice,
            costPrice: lvCostPrice
        },
        soldQty: lvSoldQty,
        storageQty: lvStorageQty
    }
    mvVariantAddedList.push(lvVariant);
}

function addAttribute() {
    let lvTableAttributes = $("#tblInsertAttributes");
    let lvNameInput = $('#attributeNameField').val();
    let lvValueInput = $('#attributeValueField').val();
    let lvSortInput = $('#sortField').val();

    if (lvNameInput === "") {
        alert("Name is required!");
        return;
    }

    let key = lvNameInput;

    let attribute = mvAttributeAddedList.find(item => item.key === key);
    if (attribute) {
        alert("This attribute already exists!");
        return;
    }

    lvTableAttributes.append(`
        <tr>
            <td class="attribut-name">${lvNameInput}</td>
            <td class="attribut-value">${lvValueInput}</td>
            <td class="attribut-sort">${lvSortInput}</td>                    
            <td><button type="button" class="btn btn-sm btn-secondary" name="btnRemoveAttribute">X</button></td>
        </tr>
    `);

    let lvAttribute = {
        key: key,
        attributeName: lvNameInput,
        attributeValue: lvValueInput,
        sort: lvSortInput
    }
    mvAttributeAddedList.push(lvAttribute);
}

function showProductDetailOnPopup(pProductInfo) {
    let pfProductDetailTitle = $("#pfProductDetailTitle");
    let pfProductName = $("#pfProductName");
    let pfProductType = $("#pfProductType");
    let pfUnit = $("#pfUnit");
    let pfBrand = $("#pfBrand");

    pfProductDetailTitle.text(pProductInfo.productName);
    pfProductName.val(pProductInfo.productName);

    downloadSelectionCategory(pfProductType, null, "product-type");
    downloadSelectionCategory(pfUnit, null, "unit");
    downloadSelectionCategory(pfBrand, null, "brand");
}

function updatePrice() {
    let variantId = $("#pUP_Confirm").attr("variantId");
    let costPrice = parseNumbericPrice($("#pUP_CostPrice").val());
    let retailPrice = parseNumbericPrice($("#pUP_RetailPrice").val());
    let apiURL = mvHostURLCallApi + `/product/variant/${variantId}/price/update`;
    let body = {costPrice: costPrice, retailPrice: retailPrice};

    $.ajax({
        url: apiURL,
        type: "PUT",
        contentType: "application/json",
        data: JSON.stringify(body),
        success: function (response, textStatus, jqXHR) {
            if (response.status === "OK") {
                alert("Update successfully")
                postUpdatePrice(variantId, response.data);
                $("#popUpdatePrice").modal("hide");
            }
        },
        error: function (xhr, textStatus, errorThrown) {
            //showErrorModal("Could not connect to the server");
            showErrorModal($.parseJSON(xhr.responseText).message);
        }
    });
}

function postUpdatePrice(pVariantId, pPrice) {
    var rowInfo = $(`tr[variantId="${pVariantId}"]`);
    rowInfo.find('td[name="colCostPrice"]').text(formatUSCurrency(pPrice.costPrice));
    rowInfo.find('td[name="colRetailPrice"]').text(formatUSCurrency(pPrice.retailPrice));

    $("#pUP_VariantCode").text();
    $("#pUP_CostPrice").text();
    $("#pUP_RetailPrice").text();
    $("#pUP_Confirm").attr("variantId", "0");
}