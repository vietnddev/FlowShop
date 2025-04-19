<!DOCTYPE html>
<html lang="en" xmlns:th="https://www.thymeleaf.org">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Flowiee | Quản lý nguyên vật liệu</title>
    <div th:replace="header :: stylesheets"></div>
    <style rel="stylesheet">
        .table td, th {
            vertical-align: middle;
        }
    </style>
</head>

<body class="hold-transition sidebar-mini layout-fixed">
    <div class="wrapper">
        <!-- Navbar (header) -->
        <div th:replace="header :: header"></div>
        <!-- /.navbar (header)-->

        <!-- Main Sidebar Container -->
        <div th:replace="sidebar :: sidebar"></div>

        <!-- Content Wrapper. Contains page content -->
        <div class="content-wrapper" style="padding-top: 10px; padding-bottom: 1px;">
            <!-- Main content -->
            <section class="content">
                <div class="container-fluid">
                    <!-- Small boxes (Stat box) -->
                    <div class="row">
                        <div class="col-12">
                            <!--Search tool-->
                            <div th:replace="fragments :: searchTool(${configSearchTool})" id="searchTool"></div>

                            <div class="card">
                                <div class="card-header">
                                    <div class="row justify-content-between">
                                        <div class="col-4" style="display: flex; align-items: center">
                                            <h3 class="card-title"><strong>DANH SÁCH NGUYÊN VẬT LIỆU</strong></h3>
                                        </div>
                                        <div class="col-6 text-right">
                                            <button type="button" class="btn btn-sm btn-primary" data-toggle="modal" data-target="#import"><i class="fa-solid fa-cloud-arrow-up mr-2"></i>Import</button>
                                            <a th:href="@{${url_export}}" class="btn btn-sm btn-info"><i class="fa-solid fa-cloud-arrow-down mr-2"></i>Export</a>
                                            <button type="button" class="btn btn-sm btn-success" data-toggle="modal" data-target="#insert" id="btnInsertForm">
                                                <i class="fa-solid fa-circle-plus mr-2"></i>Thêm mới
                                            </button>
                                        </div>
                                    </div>
                                </div>
                                <!-- /.card-header -->
                                <div class="card-body align-items-center p-0">
                                    <table class="table table-bordered table-striped align-items-center">
                                        <thead class="align-self-center">
                                            <tr class="align-self-center">
                                                <th>STT</th>
                                                <th>Loại</th>
                                                <th>Tên</th>
                                                <th>Đơn vị tính</th>
                                                <th>Số lượng</th>
                                                <th>Vị trí</th>
                                                <th>Ghi chú</th>
                                                <th>Thao tác</th>
                                            </tr>
                                        </thead>
                                        <tbody id="contentTable"></tbody>
                                    </table>
                                </div>
                                <!-- /.card-body -->

                                <div class="card-footer">
                                    <div th:replace="fragments :: pagination"></div>
                                </div>

                                <!-- modal delete
                                <div class="modal fade" th:id="'delete-' + ${list.id}">
                                    <div class="modal-dialog">
                                        <div class="modal-content">
                                            <form th:action="@{/storage/material/delete/{id}(id=${list.id})}"
                                                  th:object="${material}" method="post">
                                                <div class="modal-header">
                                                    <strong class="modal-title">Xác nhận xóa đơn vị
                                                        tính</strong>
                                                    <button type="button" class="close"
                                                            data-dismiss="modal" aria-label="Close">
                                                        <span aria-hidden="true">&times;</span>
                                                    </button>
                                                </div>
                                                <div class="modal-body">
                                                    Danh mục <strong class="badge text-bg-info"
                                                                     th:text="${list.name}"
                                                                     style="font-size: 16px;"></strong>
                                                    sẽ bị xóa vĩnh viễn!
                                                </div>
                                                <div class="modal-footer justify-content-end">
                                                    <button type="button" class="btn btn-default"
                                                            data-dismiss="modal">Hủy
                                                    </button>
                                                    <button type="submit" class="btn btn-primary">
                                                        Đồng ý
                                                    </button>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </div>-->

                                <!-- Modal update
                                <div class="modal fade" th:id="'update-' + ${list.id}">
                                    <div class="modal-dialog modal-lg">
                                        <div class="modal-content">
                                            <form th:action="@{/storage/material/update/{id}(id=${list.id})}"
                                                  th:object="${material}" method="post">
                                                <div class="modal-header">
                                                    <strong class="modal-title">Cập nhật đơn vị
                                                        tính</strong>
                                                    <button type="button" class="close"
                                                            data-dismiss="modal" aria-label="Close">
                                                        <span aria-hidden="true">&times;</span>
                                                    </button>
                                                </div>
                                                <div class="modal-body">
                                                    <div class="row">
                                                        <div class="col-12">
                                                            <input type="hidden" name="id"
                                                                   th:value="${list.id}"/>
                                                            <div class="form-group">
                                                                <label>Mã loại</label>
                                                                <input type="text" class="form-control"
                                                                       placeholder="Mã loại" required
                                                                       name="maLoai"
                                                                       th:value="${list.code}"/>
                                                            </div>
                                                            <div class="form-group">
                                                                <label>Tên loại</label>
                                                                <input type="text" class="form-control"
                                                                       placeholder="Tên loại" required
                                                                       name="name"
                                                                       th:value="${list.name}"/>
                                                            </div>
                                                            <div class="form-group">
                                                                <label>Ghi chú</label>
                                                                <textarea class="form-control" rows="5"
                                                                          placeholder="Ghi chú"
                                                                          name="ghiChu"
                                                                          th:text="${list.note}"></textarea>
                                                            </div>
                                                            <div class="form-group"
                                                                 th:if="${list.status}">
                                                                <label>Trạng thái</label>
                                                                <select class="custom-select"
                                                                        name="status">
                                                                    <option value="true" selected>Sử
                                                                        dụng
                                                                    </option>
                                                                    <option value="false">Không sử
                                                                        dụng
                                                                    </option>
                                                                </select>
                                                            </div>
                                                            <div class="form-group"
                                                                 th:if="not ${list.status}">
                                                                <label>Trạng thái</label>
                                                                <select class="custom-select"
                                                                        name="status">
                                                                    <option value="true">Sử dụng
                                                                    </option>
                                                                    <option value="false" selected>Không
                                                                        sử dụng
                                                                    </option>
                                                                </select>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="modal-footer justify-content-end"
                                                         style="margin-bottom: -15px;">
                                                        <button type="button" class="btn btn-default"
                                                                data-dismiss="modal">Hủy
                                                        </button>
                                                        <button type="submit" class="btn btn-primary">
                                                            Lưu
                                                        </button>
                                                    </div>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </div>-->

                                <!-- modal import -->
                                <div class="modal fade" id="import">
                                    <div class="modal-dialog">
                                        <div class="modal-content">
                                            <form th:action="@{${url_import}}" enctype="multipart/form-data" method="POST">
                                                <div class="modal-header">
                                                    <strong class="modal-title">Import data</strong>
                                                    <button type="button" class="close" data-dismiss="modal"
                                                            aria-label="Close">
                                                        <span aria-hidden="true">&times;</span>
                                                    </button>
                                                </div>
                                                <div class="modal-body">
                                                    <div class="row">
                                                        <div class="col-12">
                                                            <div class="form-group">
                                                                <label>Chọn file import</label>
                                                                <input type="file" class="form-control" name="file">
                                                            </div>
                                                            <div class="form-group">
                                                                <label>Template</label>
                                                                <a th:href="@{${url_template}}" class="form-control link">
                                                                    <i class="fa-solid fa-cloud-arrow-down"></i>
                                                                    [[${templateImportName}]]
                                                                </a>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="modal-footer justify-content-end" style="margin-bottom: -15px;">
                                                        <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                                                        <button type="submit" class="btn btn-primary">Lưu</button>
                                                    </div>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                                <!-- modal import -->

                                <!-- modal insert -->
                                <div class="modal fade" id="insert">
                                    <div class="modal-dialog">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <strong class="modal-title">Thêm mới nguyên vật liệu</strong>
                                                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                                            </div>
                                            <div class="modal-body">
                                                <div class="row">
                                                    <div class="col-12">
                                                        <div class="form-group">
                                                            <label>Loại</label>
                                                            <select class="custom-select col-sm" name="code" data-placeholder="Loại">
                                                                <option value="MAIN">Nguyên liệu sản xuất</option>
                                                                <option value="SUB">Phụ liệu sản xuất</option>
                                                                <option value="OTHER">Khác</option>
                                                            </select>
                                                        </div>
                                                        <div class="form-group">
                                                            <label for="nameField">Tên nguyên vật liệu</label>
                                                            <input type="text" class="form-control" id="nameField" required/>
                                                        </div>
                                                        <div class="form-group">
                                                            <label for="quantityField">Số lượng</label>
                                                            <input type="number" class="form-control" value="1" id="quantityField" required/>
                                                        </div>
                                                        <div class="form-group">
                                                            <label for="unitField">Đơn vị tính</label>
                                                            <select class="custom-select col-sm" name="unit" id="unitField"></select>
                                                        </div>
                                                        <div class="form-group">
                                                            <label for="noteField">Ghi chú</label>
                                                            <textarea class="form-control" rows="5" placeholder="Ghi chú" id="noteField"></textarea>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="modal-footer justify-content-end" style="margin-bottom: -15px">
                                                <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                                                <button type="button" class="btn btn-primary" id="btnInsertSubmit">Lưu</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </div>

        <div th:replace="footer :: footer"></div>

        <!-- Control Sidebar -->
        <aside class="control-sidebar control-sidebar-dark"></aside>

        <div th:replace="header :: scripts"></div>
    </div>
    <script>
        let mvMaterials = {};

        $(document).ready(function () {
            loadMaterials(mvPageSizeDefault, 1);
            updateTableContentWhenOnClickPagination(loadMaterials);
            insert();
        });

        function loadMaterials(pageSize, pageNum) {
            let apiURL = mvHostURLCallApi + '/storage/material/all';
            let params = {pageSize: pageSize, pageNum: pageNum}
            $.get(apiURL, params, function (response) {
                if (response.status === "OK") {
                    let mvMaterials = response.data;
                    let pagination = response.pagination;

                    updatePaginationUI(pagination.pageNum, pagination.pageSize, pagination.totalPage, pagination.totalElements);

                    let contentTable = $('#contentTable');
                    contentTable.empty();
                    $.each(mvMaterials, function (index, d) {
                        let materialType = d.code;
                        if (materialType === "MAIN") {
                            materialType = "Nguyên liệu sản xuất";
                        } else if (materialType === "SUB") {
                            materialType = "Phụ liệu sản xuất";
                        } else {
                            materialType = "Khác";
                        }

                        contentTable.append(`
                            <tr>
                                <td>${(((pageNum - 1) * pageSize + 1) + index)}</td>
                                <td>${materialType}</td>
                                <td>${d.name}</td>
                                <td>${d.unitName}</td>
                                <td>${d.quantity}</td>
                                <td>${d.location}</td>
                                <td>${d.note}</td>
                                <td>
                                    <button class="btn btn-info btn-sm" data-toggle="modal" data-target="'#update-' + ${d.id}">
                                        <i class="fa-solid fa-pencil"></i>
                                    </button>
                                    <button class="btn btn-danger btn-sm" data-toggle="modal" data-target="'#delete-' + ${d.id}">
                                         <i class="fa-solid fa-trash"></i>
                                    </button>
                                </td>
                            </tr>
                        `);
                    });
                }
            }).fail(function () {
                showErrorModal("Could not connect to the server");
            });
        }
        
        function insert() {
            $("#btnInsertForm").on("click", function () {
                $.get(mvHostURLCallApi + "/category/unit", function (response) {
                    if (response.status === "OK") {
                        $.each(response.data, function (index, d) {
                            $("#unitField").append('<option value=' + d.id + '>' + d.name + '</option>');
                        });
                    }
                });
            })

            $("#btnInsertSubmit").on("click", function () {
                let apiURL = mvHostURLCallApi + "/storage/material/create";
                let body = {
                    code : $("#codeField").val(),
                    name : $("#nameField").val(),
                    unitId : $("#unitField").val(),
                    quantity : $("#quantityField").val(),
                    note : $("#noteField").val()
                };
                $.ajax({
                    url: apiURL,
                    type: "POST",
                    contentType: "application/json",
                    data: JSON.stringify(body),
                    success: function (response) {
                        if (response.status === "OK") {
                            alert("Create new material successfully!");
                            window.location.reload();
                        }
                    },
                    error: function (xhr) {
                        alert("Error: " + $.parseJSON(xhr.responseText).message);
                    }
                });
            })
        }
    </script>
</body>
</html>