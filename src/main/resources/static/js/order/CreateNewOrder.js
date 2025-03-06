function createOrder() {
    $(".link-confirm").on("click", function(e) {
        e.preventDefault();
        let title = 'Tạo mới đơn hàng'
        let text = 'Bạn có muốn tạo đơn hàng này?'
        showConfirmModal($(this), title, text);
    });

    $('#yesButton').on("click", async function () {
        let customerId = $('#customerField').val();
        let orderTime = $('#orderTimeField').val();
        let accountId = $('#accountField').val();
        let salesChannelId = $('#salesChannelField').val();
        let paymentMethodId = $('#paymentMethodField').val();
        let orderStatusId = $('#orderStatusField').val();
        let note = $('#noteFieldCart').val();
        let cartId = mvCartId;
        let receiveName = $('#receiveNameField').val();
        let receivePhoneNumber = $('#receivePhoneNumberField').val();
        let receiveEmail = $('#receiveEmailField').val();
        let receiveAddress = $('#receiveAddressField').val();

        let apiURL = mvHostURLCallApi + '/sls/order/insert';
        let body = {
            customerId: customerId,
            salesAssistantId : accountId,
            salesChannelId: salesChannelId,
            paymentMethodId: paymentMethodId,
            orderStatus : orderStatusId,
            note : note,
            orderTime : orderTime,
            cartId : cartId,
            recipientName : receiveName,
            recipientPhone : receivePhoneNumber,
            recipientEmail : receiveEmail,
            shippingAddress : receiveAddress,
            couponCode : mvVoucherCode,
            amountDiscount : mvAmountDiscount
        }
        $.ajax({
            url: apiURL,
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(body),
            success: function (response) {
                if (response.status === "OK") {
                    alert('Create new order success!')
                    window.location =  mvHostURL + '/sls/order';
                }
            },
            error: function (xhr) {
                alert("Error: " + $.parseJSON(xhr.responseText).message);
            }
        });
    });
}