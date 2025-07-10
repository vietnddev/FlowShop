$(document).ready(function () {
    //Click btn decrease item quantity
    $(document).on('click', '.btn-minus', function () {
        const lvQuantityInput = $(this).closest('.item-quantity-input').find('.itemQuantity');
        let itemId = lvQuantityInput.attr("itemId");
        let value = parseInt(lvQuantityInput.val()) || 1;
        if (value > 1) {
            let newValue = value - 1;

            updateItemQuantity(itemId, newValue, function(success) {
                if (success) {
                    lvQuantityInput.val(newValue);
                }
            });
        }
    });

    $(document).on('change', '.itemQuantity', function () {
        let itemId = $(this).attr("itemId");
        let value = parseInt($(this).val()) || 1;

        updateItemQuantity(itemId, value, function(success) {
            if (success) {
                $(this).val(value);
            }
        });
    });

    //Click btn increase item quantity
    $(document).on('click', '.btn-plus', function () {
        const lvQuantityInput = $(this).closest('.item-quantity-input').find('.itemQuantity');
        let itemId = lvQuantityInput.attr("itemId");
        let value = parseInt(lvQuantityInput.val()) || 1;
        let newValue = value + 1;

        updateItemQuantity(itemId, newValue, function(success) {
            if (success) {
                lvQuantityInput.val(newValue);
            }
        });
    });
});

function updateItemQuantity(itemId, quantity, callback) {
    let apiURL = mvHostURLCallApi + `/sls/cart/${mvCurrentCartId}/item/${itemId}/update-quantity`;
    let body = {quantity: quantity};
    $.ajax({
        url: apiURL,
        type: 'PUT',
        data: JSON.stringify(body),
        contentType: 'application/json',
        success: function(result) {
            let item = result.data;
            postUpdateItemQuantity(item);
            if (callback) callback(true);
        },
        error: function(xhr, status, error) {
            alert(status + ': ' + JSON.stringify(xhr.responseJSON.message));
            if (callback) callback(false);
        }
    });
}

function postUpdateItemQuantity(itemDTO) {
    //SubTotal for line item
    let colItemSubTotal = $(`.row-item[itemId="${itemDTO.itemId}"]`).find(`.col-item-subTotal`);
    colItemSubTotal.empty();
    colItemSubTotal.text(formatCurrency(itemDTO.subTotal));

    reCalCartValue(mvCurrentCartId, function(cartValue) {
        if (cartValue !== null) {
            $("#mTotalAmountPreDiscount").text(formatUSCurrency(cartValue));
            $("#totalAmountDiscountField").text(formatUSCurrency(cartValue));
        }
    });

    //Update total amount for whole cart
    //...
}

function reCalCartValue(pCartId, callback) {
    let apiURL = mvHostURLCallApi + `/sls/cart/${pCartId}/value`;
    $.get(apiURL, function (response) {
        if (response.status === "OK") {
            let cartValue = response.data;
            if (callback) callback(cartValue); // Trả về giá trị cartValue qua callback
        }
    }).fail(function (xhr) {
        alert("Error: " + $.parseJSON(xhr.responseText).message);
        if (callback) callback(0.00); // Trả về null nếu lỗi
    });
}