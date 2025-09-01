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
        let deliveryMethodId = $('#deliveryTypeField').val();
        let note = $('#noteFieldCart').val();
        let customerNote = $('#customerNoteFieldCart').val();
        let cartId = mvCurrentCartId;
        let receiveName = $('#receiveNameField').val();
        let receivePhoneNumber = $('#receivePhoneNumberField').val();
        let receiveEmail = $('#receiveEmailField').val();
        let receiveAddress = $('#receiveAddressField').val();
        let accumulateBonusPoints = $('#ckxAccumulatePoints').is(':checked');

        let apiURL = mvHostURLCallApi + '/sls/order/insert';
        let body = {
            customerId: customerId,
            salesAssistantId : accountId,
            salesChannelId: salesChannelId,
            paymentMethodId: paymentMethodId,
            orderStatus : orderStatusId,
            deliveryMethodId: deliveryMethodId,
            note : note, //internal note
            customerNote : customerNote, //customer note
            orderTime : orderTime,
            cartId : cartId,
            recipientName : receiveName,
            recipientPhone : receivePhoneNumber,
            recipientEmail : receiveEmail,
            shippingAddress : receiveAddress,
            couponCode : mvVoucherCode,
            amountDiscount : mvAmountDiscount,
            accumulateBonusPoints: accumulateBonusPoints
        }
        $.ajax({
            url: apiURL,
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(body),
            success: function (response) {
                if (response.status === "OK") {
                    let orderCreated = response.data;
                    alert('Your order has been created!')
                    window.location =  mvHostURL + '/sls/order/' + orderCreated.id;
                }
            },
            error: function (xhr) {
                alert("Error: " + $.parseJSON(xhr.responseText).message);
            }
        });
    });
}