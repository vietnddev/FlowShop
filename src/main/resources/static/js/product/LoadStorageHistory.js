function getStorageHistory(productVariantId) {
    let apiURL = mvHostURLCallApi + "/product/variant/" + productVariantId + "/storage-history";
    mvStorageHistoryTbl.empty();
    $.get(apiURL, function (response) {
        if (response.status === "OK") {
            $.each(response.data, function (index, d) {
                mvStorageHistoryTbl.append(`
                    <tr>
                        <td>${index + 1}</td>
                        <td>${d.createdAt}</td>
                        <td>${d.staff}</td>
                        <td>${d.action}</td>
                        <td>${d.variantCode}</td>
                        <td>${d.changeQty}</td>
                        <td>${d.storageQty}</td>
                        <td></td>
                        <td></td>
                    </tr>
                `);
            });
        }
    });
}