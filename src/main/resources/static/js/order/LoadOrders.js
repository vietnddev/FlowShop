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

                let itemsBlock = '';
                $.each(d.items, function (itemIndex, itemInfo) {
                    itemsBlock +=
                        `<tr>
                            <td>${itemIndex + 1}</td>
                            <td>${itemInfo.productVariantDTO.variantName}</td>
                            <td>${itemInfo.productVariantDTO.unitName}</td>
                            <td class="text-right">${formatCurrency(itemInfo.price)}</td>
                            <td class="text-right">${itemInfo.quantity}</td>
                            <td class="text-right">${formatCurrency(itemInfo.price * itemInfo.quantity)}</td>                           
                            <td>${itemInfo.note}</td>
                        </tr>`;
                });

                contentTable.append(`
                    <tr aria-expanded="false">                        
                        <td><a href="/sls/order/${d.id}">${(((pageNum - 1) * pageSize + 1) + index)}. ${d.code}</a></td>
                        <td>${d.orderTime}</td>               
                        <td>${d.receiverName}</td>
                        <td>${d.receiverPhone}</td>
                        <td class="text-right">${formatCurrency(d.totalAmount)}</td>
                        <td class="text-right">${d.items.length} <i class="fa-solid fa-caret-down ml-1" data-widget="expandable-table"></i></td>
                        <td>${d.salesChannelName}</td>                        
                        <td>-Paument status-</td>
                        <td>-Delivery type-</td>
                        <td>${d.orderStatusName}</td>
                        <td><a class="btn btn-sm btn-info btn-print-invoice" href="/sls/order/print-invoice/${d.id}" orderId="${d.id}"><i class="fa-solid fa-print"></i></a></td>
                    </tr>
                    <tr class="expandable-body d-none">
                        <td colspan="10">
                            <table class="table table-bordered" style="display: none; margin: 6px auto">
                                <thead>
                                    <tr>
                                        <th>No.</th><th>Item name</th><th>Unit</th><th>Unit price</th><th>Quantity</th><th>Total price</th><th>Note</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    ${itemsBlock}
                                </tbody>
                            </table>
                        </td>
                    </tr>
                `);
            });
        }
    }).fail(function () {
        showErrorModal("Could not connect to the server");//nếu ko gọi xuống được controller thì báo lỗi
    });
}