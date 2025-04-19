<!DOCTYPE html>
<html lang="en" xmlns:th="https://www.thymeleaf.org">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Quản lý phiếu nhập hàng</title>
    <th:block th:replace="header :: stylesheets"></th:block>
    <style>
        .table td.vertical-center {
            vertical-align: middle;
        }
    </style>
</head>

<body class="hold-transition sidebar-mini layout-fixed">
<div class="wrapper">
    <div th:replace="header :: header"></div>

    <div th:replace="sidebar :: sidebar"></div>

    <div class="content-wrapper" style="padding-top: 10px; padding-bottom: 1px;">
        <section class="content">
            <div class="container-fluid vertical-center">
                <div class="row">
                    <div class="col-12">
                        <!--Search tool-->
                        <div th:replace="fragments :: searchTool(${configSearchTool})" id="searchTool"></div>

                        <div class="card">
                            <div class="card-header">
                                <div class="row justify-content-between">
                                    <div class="col-4" style="display: flex; align-items: center">
                                        <h3 class="card-title"><strong>NHẬP HÀNG HÓA</strong></h3>
                                    </div>
                                    <div class="col-4 text-right"></div>
                                </div>
                            </div>
                            <div class="card-body p-0">
                                <table class="table table-bordered table-striped">
                                    <thead>
                                        <tr>
                                            <th>STT</th>
                                            <th>Kho</th>
                                            <th>Tiêu đề</th>
                                            <th>Số lượng</th>
                                            <th>Giá trị</th>
                                            <th>Người nhập</th>
                                            <th>Thời gian nhập</th>
                                            <th>Ghi chú</th>
                                            <th>Trạng thái</th>
                                        </tr>
                                    </thead>
                                    <tbody id="contentTable"></tbody>
                                    <tfoot></tfoot>
                                </table>
                            </div>
                            <div class="card-footer">
                                <div th:replace="fragments :: pagination"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>

    <div th:replace="footer :: footer"></div>

    <aside class="control-sidebar control-sidebar-dark"></aside>

    <div th:replace="header :: scripts"></div>

</div>
<script>
    $(document).ready(function () {
        init();
        loadTickImports(mvPageSizeDefault, 1);
        updateTableContentWhenOnClickPagination(loadTickImports);
    });

    function init() {}

    function loadTickImports(pageSize, pageNum) {
        let apiURL = mvHostURLCallApi + "/stg/ticket-import/all";
        let params = {pageSize: pageSize, pageNum: pageNum}
        $.get(apiURL, params, function (response) {
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
                            <td>${d.storageName}</td>
                            <td><a href="/stg/ticket-import/${d.id}">${d.title}</td>
                            <td>${d.totalItems}</td>
                            <td>${formatCurrency(d.totalValue)}</td>
                            <td>${d.importer}</td>
                            <td>${d.importTime}</td>
                            <td>${d.note}</td>
                            <td>${mvTicketImportStatus[d.status]}</td>
                        </tr>
                    `);
                });
            }
        }).fail(function () {
            showErrorModal("Could not connect to the server");
        });
    }
</script>
</body>

</html>
