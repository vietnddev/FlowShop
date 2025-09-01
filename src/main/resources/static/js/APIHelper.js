const api = {
    getOrder: (orderId) => `${mvHostURLCallApi}/sls/order/${orderId}`,
    getOrders: () => `${mvHostURLCallApi}/sls/order/all`,
    returnOrder: (orderId) => `${mvHostURLCallApi}/sls/order/${orderId}/returns`
};

function callApiDelete(apiURL, redirectTo) {
    $.ajax({
        url: apiURL,
        type: 'DELETE',
        success: function(result) {
            // Xử lý kết quả nếu cần thiết
            alert(result.message)
            if (redirectTo != null) {
                window.location = mvHostURL + redirectTo;
            } else {
                window.location.reload();
            }
        },
        error: function(xhr, status, error) {
            // Xử lý lỗi nếu có
            alert(status + ': ' + JSON.stringify(xhr.responseJSON.message));
        }
    });
}

function downloadSelectionCategory(element, optionDefault, type) {
    let endpoint = mvHostURLCallApi + "/category/" + type;

    element.empty();
    if (optionDefault != null) {
        element.append(`<option value="">${optionDefault}</option>`);
    }

    $.get(endpoint, function (response) {
        if (response.status === "OK") {
            $.each(response.data, function (index, d) {
                element.append(`<option value="${d.id}">${d.name}</option>`);
            });
        }
    }).fail(function () {
        showErrorModal("Could not connect to the server");
    });
}

function downloadCategoryForSelection(element, endpoint) {
    $.get(endpoint, function (response) {
        if (response.status === "OK") {
            $.each(response.data, function (index, d) {
                element.append(`<option value="${d.id}">${d.name}</option>`);
            });
        }
    }).fail(function () {
        showErrorModal("Could not connect to the server");
    });
}