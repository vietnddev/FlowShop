function loadCategories(pageSize, pageNum) {
    let apiURL = mvHostURLCallApi + "/category/" + mvType;
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

            let contentTable = $('#contentTable');
            contentTable.empty();
            $.each(data, function (index, d) {
                mvCategories[d.id] = d;
                let iconConfig = "";
                if (d.type === "DOCUMENT_TYPE") {
                    iconConfig = `<a class="btn btn-secondary btn-sm" href="/stg/doc/doc-type/${d.id}"><i class="fa-solid fa-gear"></i></a>`;
                }
                contentTable.append(`
                    <tr>
                        <td>${(((pageNum - 1) * pageSize + 1) + index)}</td>
                        <td>${d.code}</td>
                        <td>${d.name}</td>
                        <td style="background-color: ${d.color}"></td>
                        <td>${d.note}</td>
                        <td>${d.inUse}</td>
                        <td>${d.statusName}</td>
                        <td>${d.isDefault}</td>
                        <td>${d.sort}</td>
                        <td>
                            <button class="btn btn-info   btn-sm btn-update mr-1" id="${d.id}"><i class="fa-solid fa-pencil"></i></button>
                            <button class="btn btn-danger btn-sm btn-delete"      id="${d.id}"><i class="fa-solid fa-trash"></i></button>
                            ${iconConfig}
                        </td>
                    </tr>
                `);
            });
        }
    }).fail(function () {
        showErrorModal("Could not connect to the server");//nếu ko gọi xuống được controller thì báo lỗi
    });
}