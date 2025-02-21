function loadOrders(pageSize, pageNum) {
    let apiURL = mvHostURLCallApi + '/sls/order/all';
    let params = {
        pageSize: pageSize,
        pageNum: pageNum,
        txtSearch: $('#txtFilter').val(),
        groupCustomerId: $('#groupCustomerFilter').val(),
        orderStatusId: $('#orderStatusFilter').val(),
        paymentMethodId: $('#paymentMethodFilter').val(),
        branchId: $('#branchFilter').val(),
        salesChannelId: $('#salesChannelFilter').val(),
        dateFilter: $("#dateFilter").val()
    }
    $.get(apiURL, params, function (response) {//dùng Ajax JQuery để gọi xuống controller
        if (response.status === "OK") {
            let data = response.data;
            let pagination = response.pagination;

            updatePaginationUI(pagination.pageNum, pagination.pageSize, pagination.totalPage, pagination.totalElements);

            let contentTable = $('#contentTable');
            contentTable.empty();
            $.each(data, function (index, d) {
                contentTable.append(`
                               <tr>
                                    <td>${(((pageNum - 1) * pageSize + 1) + index)}</td>
                                    <td><a href="/sls/order/${d.id}">${d.code}</a></td>
                                    <td>${d.orderTime}</td>
                                    <td>${d.receiverAddress}</td>
                                    <td>${d.receiverName}</td>
                                    <td>${d.receiverPhone}</td>
                                    <td>${formatCurrency(d.totalAmountDiscount)}</td>
                                    <td>${d.salesChannelName}</td>
                                    <td>${d.paymentStatus == true ? "<span class=\"badge bg-success\">Đã thanh toán</span>" : "Chưa thanh toán"}</td>
                                    <td>${d.orderStatusName}</td>
                                    <td><a class="btn btn-sm btn-info btn-print-invoice" href="/sls/order/print-invoice/${d.id}" orderId="${d.id}"><i class="fa-solid fa-print"></i></a></td>
                                </tr>
                            `);
            });
        }
    }).fail(function () {
        showErrorModal("Could not connect to the server");//nếu ko gọi xuống được controller thì báo lỗi
    });
}