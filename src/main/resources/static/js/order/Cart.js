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

    $("#btnSearchCustomer").on("click", function () {
        $("#modalSearchCustomer").modal("show");
    });

    //Load customer by filters from database
    $("#mdlSearchCustomer_search").on("click", function () {
        let lvCustomerName = $("#mdlSearchCustomer_customerName").val();
        let lvPhoneNumber = $("#mdlSearchCustomer_phoneNumber").val();
        $("#mdlSearchCustomer_tblCustomers").empty();
        searchCustomer(lvCustomerName, lvPhoneNumber);
    });

    //Submit pick customer
    $("#mdlSearchCustomer_submit").on("click", function () {
        let customerSelectedCkx = $('input[name="mdlSearchCustomer_ckx"]:checked');
        if (customerSelectedCkx.length === 0) {
            alert("Please select a customer before proceeding.");
            return;
        }
        let rowInfo = customerSelectedCkx.closest("tr");
        let customerId = customerSelectedCkx.attr("customerId");
        let lvCustomer = {
            customerName: rowInfo.find('.col-customer-name').text(),
            phoneDefault: rowInfo.find('.col-phone').text(),
            emailDefault: rowInfo.find('.col-email').text(),
            addressDefault: rowInfo.find('.col-address').text()
        };
        fillCustomerInfoOntoForm(lvCustomer);
        $('#customerField').val(customerId).trigger('change');
        $("#modalSearchCustomer").modal("hide");
    });

    $('#customerField').on('click', function () {
        let lvCustomer = mvCustomers[$(this).val()];
        fillCustomerInfoOntoForm(lvCustomer);
    });

    //Only allow one customer's check box is checked
    $(document).on('change', 'input[name="mdlSearchCustomer_ckx"]', function () {
        if ($(this).is(':checked')) {
            $('input[name="mdlSearchCustomer_ckx"]').not(this).prop('checked', false);
        }
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
            $("#amountReceivedField").val(formatUSCurrency(cartValue));
            $("#amountReturnField").val(formatUSCurrency(cartValue - cartValue));
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

function searchCustomer(pCustomerName, pPhoneNumber) {
    let apiURL = mvHostURLCallApi + '/customer/all';
    let params = {
        name: pCustomerName,
        phone: pPhoneNumber
    };
    $.get(apiURL, params, function (response) {
        if (response.status === "OK") {
            $.each(response.data, function (index, d) {
                $("#mdlSearchCustomer_tblCustomers").append(`
                    <tr>
                        <td><input type="checkbox" class="form-check" name="mdlSearchCustomer_ckx" customerId="${d.id}"></td>                        
                        <td class="col-customer-name">${d.customerName}</td>
                        <td class="col-phone">${d.phoneDefault}</td>
                        <td class="col-email">${d.emailDefault}</td>
                        <td class="col-address">${d.addressDefault}</td>
                    </tr>
                `);
            });
        }
    }).fail(function (xhr) {
        alert("Error: " + $.parseJSON(xhr.responseText).message);
    });
    //do something more
}

function fillCustomerInfoOntoForm(pCustomer) {
    $('#receiveNameField').val(pCustomer.customerName);
    $('#receivePhoneNumberField').val(pCustomer.phoneDefault);
    $('#receiveEmailField').val(pCustomer.emailDefault);
    $('#receiveAddressField').val(pCustomer.addressDefault);
}

function addCartItems(pItems) {
    let apiURL = mvHostURLCallApi + '/sls/cart/add-items';
    let body = {
        cartId: mvCurrentCartId,
        items: pItems
    }
    $.ajax({
        url: apiURL,
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(body),
        success: function (response) {
            if (response.status === "OK") {
                alert(response.message)
                viewCartInfo(mvCurrentCartId);
                $("#searchProductModal").modal("hide");
            }
        },
        error: function (xhr) {
            alert("Error: " + $.parseJSON(xhr.responseText).message);
        }
    });
}