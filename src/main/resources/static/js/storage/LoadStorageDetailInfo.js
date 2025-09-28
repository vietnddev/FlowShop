function loadStorageItems(pageSize, pageNum) {
    let apiURL = mvHostURLCallApi + '/storage/' + mvStorageId + '/items';
    let params = {
        pageSize: pageSize,
        pageNum: pageNum,
        searchText: $("#txtFilter").val()
    }
    $.get(apiURL, params, function (response) {
        if (response.status === "OK") {
            let data = response.data;
            let pagination = response.pagination;
            updatePaginationUI(pagination.pageNum, pagination.pageSize, pagination.totalPage, pagination.totalElements);

            let itemsTable = $('#storageItemsTbl');
            itemsTable.empty();
            $.each(data, function (index, d) {
                let itemImageSrc = `<img class="text-center" src="${d.itemImageSrc}" style="width: 60px; height: 60px; border-radius: 5px">`;
                if (d.itemImageSrc == null) {
                    itemImageSrc = '';
                }
                itemsTable.append(`
                    <tr>
                        <td>${(((pageNum - 1) * pageSize + 1) + index)}</td>
                        <td>${itemImageSrc}</td>
                        <td>${d.itemName}</td>
                        <td>${d.itemType}</td>
                        <td>${d.itemBrand}</td>
                        <td>${d.itemSalesAvailableQty}</td>
                        <td>${d.itemStorageQty}</td>
                        <td>${d.lastImportTime}</td>
                        <td>${d.firstImportTime}</td>
                    </tr>
                `);
            });
        }
    }).fail(function () {
        showErrorModal("Could not connect to the server");
    });
}

function loadStorageDetailInfo() {
    let apiURL = mvHostURLCallApi + '/storage/' + mvStorageId;
    let params = {}
    $.get(apiURL, params, function (response) {
        if (response.status === "OK") {
            let data = response.data;
            mvStorageCode.val(data.code);
            mvStorageName.val(data.name);
            mvStorageLocation.val(data.location);
            mvStorageDescription.val(data.description);
            mvStorageTotalItems.val(data.totalItems);
            mvStorageTotalValue.val(data.totalInventoryValue);
            if (data.isDefault) {
                mvStorageIsDefault.append(`<option value="true" selected>Có</option>`);
                mvStorageIsDefault.append(`<option value="false">Không</option>`);
            } else {
                mvStorageIsDefault.append(`<option value="false" selected>Không</option>`);
                mvStorageIsDefault.append(`<option value="true">Có</option>`);
            }
            if (data.status === "Y") {
                mvStorageStatus.append(`<option value="Y" selected>${mvStorageStatusInit["Y"]}</option>`);
                mvStorageStatus.append(`<option value="N">${mvStorageStatusInit["N"]}</option>`);
            } else {
                mvStorageStatus.append(`<option value="N" selected>${mvStorageStatusInit["N"]}</option>`);
                mvStorageStatus.append(`<option value="Y">${mvStorageStatusInit["Y"]}</option>`);
            }
        }
    }).fail(function () {
        showErrorModal("Could not connect to the server");
    });
}

function loadStorageImportHistory(pageSize, pageNum) {
    let apiURL = mvHostURLCallApi + "/stg/transaction-goods/import";
    let params = {pageSize: pageSize, pageNum: pageNum, storageId: mvStorageId}
    $.get(apiURL, params, function (response) {
        if (response.status === "OK") {
            let data = response.data;
            let pagination = response.pagination;
            updatePaginationUI2(pagination.pageNum, pagination.pageSize, pagination.totalPage, pagination.totalElements);

            let contentTable = $('#storageImportHistoryTbl');
            contentTable.empty();
            $.each(data, function (index, d) {
                contentTable.append(`
                    <tr>
                        <td>${(((pageNum - 1) * pageSize + 1) + index)}</td>
                        <td><a href="/stg/transaction-goods/import/${d.id}">${d.title}</td>
                        <td>${d.totalItems}</td>
                        <td>${d.createdBy}</td>
                        <td>${d.createdAt}</td>
                        <td>${d.description}</td>
                        <td>${d.transactionStatus}</td>
                    </tr>
                `);
            });
        }
    }).fail(function () {
        showErrorModal("Could not connect to the server");
    });
}

function loadStorageExportHistory(pageSize, pageNum) {
    let apiURL = mvHostURLCallApi + "/stg/transaction-goods/export";
    let params = {pageSize: pageSize, pageNum: pageNum, storageId: mvStorageId}
    $.get(apiURL, params, function (response) {
        if (response.status === "OK") {
            let data = response.data;
            let pagination = response.pagination;
            updatePaginationUI3(pagination.pageNum, pagination.pageSize, pagination.totalPage, pagination.totalElements);

            let contentTable = $('#storageExportHistoryTbl');
            contentTable.empty();
            $.each(data, function (index, d) {
                contentTable.append(`
                    <tr>
                        <td>${(((pageNum - 1) * pageSize + 1) + index)}</td>
                        <td><a href="/stg/transaction-goods/export/${d.id}">${d.title}</td>
                        <td>${d.totalItems}</td>
                        <td>${d.createdBy}</td>
                        <td>${d.createdAt}</td>
                        <td>${d.description}</td>
                        <td>${d.transactionStatus}</td>
                    </tr>
                `);
            });
        }
    }).fail(function () {
        showErrorModal("Could not connect to the server");
    });
}