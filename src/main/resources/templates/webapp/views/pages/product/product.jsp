<!DOCTYPE html>
<html lang="en" xmlns:th="https://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Danh sách sản phẩm</title>
    <div th:replace="header :: stylesheets">
        <!--Nhúng các file css, icon,...-->
    </div>
    <style rel="stylesheet">
        .table td,
        th {
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
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-12">
                            <!--Search tool-->
                            <div th:replace="fragments :: searchTool('Y','Y','Y','Y','Y','Y','Y')" id="searchTool"></div>

                            <div class="card">
                                <div class="card-header">
                                    <div class="row justify-content-between">
                                        <div class="col-4" style="display: flex; align-items: center">
                                            <h3 class="card-title"><strong th:text="#{pro.product.list}" class="text-uppercase"></strong></h3>
                                        </div>
                                        <div class="col-6 text-right">
                                            <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#import"><i class="fa-solid fa-cloud-arrow-up mr-2"></i>Import</button>
                                            <a th:href="@{${url_export}}" class="btn btn-info"><i class="fa-solid fa-cloud-arrow-down mr-2"></i>Export</a>
                                            <button type="button" class="btn btn-success" data-toggle="modal" data-target="#insert" id="createProduct"><i class="fa-solid fa-circle-plus mr-2"></i>Thêm mới</button>
                                        </div>
                                    </div>
                                    <!-- modal-content (Thêm mới sản phẩm)-->
                                </div>
                                <!-- /.card-header -->
                                <div class="card-body align-items-center p-0">
                                    <table class="table table-bordered table-striped align-items-center">
                                        <thead class="align-self-center">
                                            <tr class="align-self-center">
                                                <th>STT</th>
                                                <th></th>
                                                <th>Tên sản phẩm</th>
                                                <th>Loại</th>
                                                <th>Màu sắc</th>
                                                <th>Số lượng</th>
                                                <th>Đơn vị tính</th>
                                                <th>Khuyến mãi</th>
                                                <th>Trạng thái</th>
                                            </tr>
                                        </thead>
                                        <tbody id="contentTable"></tbody>
                                    </table>
                                </div>
                                <!-- /.card-body -->
                                <div class="card-footer">
                                    <div th:replace="fragments :: pagination"></div>
                                </div>


                                <!-- modal import -->
                                <div class="modal fade" id="import">
                                    <div class="modal-dialog">
                                        <div class="modal-content">
                                            <form th:action="@{${url_import}}" method="POST">
                                                <div class="modal-header">
                                                    <strong class="modal-title">Import data</strong>
                                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
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
                                                                <a th:href="@{${url_template}}" class="form-control link"><i class="fa-solid fa-cloud-arrow-down"></i>[[${templateImportName}]]</a>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="modal-footer justify-content-end">
                                                    <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                                                    <button type="submit" class="btn btn-primary">Lưu</button>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                                <!-- modal import -->

                                <div class="modal fade" id="insert">
                                    <div class="modal-dialog">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <strong class="modal-title">Thêm mới sản phẩm</strong>
                                                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                                            </div>
                                            <div class="modal-body">
                                                <div class="row">
                                                    <div class="col-12">
                                                        <div class="form-group">
                                                            <label>Tên sản phẩm</label>
                                                            <input type="text" class="form-control" placeholder="Tên sản phẩm" id="productNameField">
                                                        </div>
                                                        <div class="form-group">
                                                            <label>Loại sản phẩm</label>
                                                            <select class="custom-select" id="productTypeField"></select>
                                                        </div>
                                                        <div class="form-group">
                                                            <label>Đơn vị tính</label>
                                                            <select class="custom-select" id="unitField"></select>
                                                        </div>
                                                        <div class="form-group">
                                                            <label>Nhãn hiệu</label>
                                                            <select class="custom-select" id="brandField"></select>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="modal-footer justify-content-end">
                                                <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                                                <button type="button" class="btn btn-primary" id="createProductSubmit">Lưu</button>
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

        <div th:replace="modal_fragments :: confirm_modal"></div>

        <div th:replace="footer :: footer"></div>

        <aside class="control-sidebar control-sidebar-dark"></aside>

        <div th:replace="header :: scripts"></div>
    </div>

    <script type="text/javascript">
        let mvSearchTool = ["BRAND", "PRODUCT_TYPE", "COLOR", "SIZE", "UNIT", "DISCOUNT", "PRODUCT_STATUS"];

        $(document).ready(function () {
            $('#createProduct').on('click', function () {
                loadCategory();
            });

            $('#createProductSubmit').on('click', function () {
                createProduct();
            });

            loadProducts(mvPageSizeDefault, 1);
            updateTableContentWhenOnClickPagination(loadProducts);

            setupSearchTool(mvSearchTool);
            $("#btnSearch").on("click", function () {
                let brandFilter = $('#brandFilter').val();
                let unitFilter = $('#unitFilter').val();
                let discountFilter = $('#discountFilter').val();
                let productStatusFilter = $('#productStatusFilter').val();
                loadProducts($('#paginationInfo').attr("pageSize"), 1);
            })
        });

        function loadProducts(pageSize, pageNum) {
            let apiURL = mvHostURLCallApi + '/product/all';
            let params = {
                pageSize: pageSize,
                pageNum: pageNum,
                txtSearch : $('#txtFilter').val()
            }
            $.get(apiURL, params, function (response) {//dùng Ajax JQuery để gọi xuống controller
                if (response.status === "OK") {
                    let data = response.data;
                    let pagination = response.pagination;

                    updatePaginationUI(pagination.pageNum, pagination.pageSize, pagination.totalPage, pagination.totalElements);

                    let contentTable = $('#contentTable');
                    contentTable.empty();
                    $.each(data, function (index, p) {
                        let variantBlock = '';
                        $.each(p.productVariantInfo, function (color, quantity) {
                            variantBlock += `<span class="span">${color} : ${quantity}</span><br>`;
                        });

                        let voucherBlock = '';
                        $.each(p.listVoucherInfoApply, function (voucherIndex, voucherInfo) {
                            voucherBlock += `<span>${voucherIndex + 1} </span><a href="/san-pham/voucher/detail/${voucherInfo.id}"><span>${voucherInfo.title}</span></a><br>`;
                        });

                        contentTable.append(`
                            <tr>
                                <td>${(((pageNum - 1) * pageSize + 1) + index)}</td>
                                <td class="text-center"><img src="${p.imageActive}" style="width: 60px; height: 60px; border-radius: 5px"></td>
                                <td><a href="/san-pham/${p.id}">${p.productName}</a></td>
                                <td>${p.productTypeName}</td>
                                <td>${variantBlock}</td>
                                <td>
                                <div className="span">Hiện có: ${p.totalQtyStorage}</div>
                                <div className="span">Đã bán: ${p.totalQtySell}</div>
                                </td>
                                <td>${p.unitName}</td>
                                <td>${voucherBlock}</td>
                                <td>${mvProductStatus[p.status]}</td>
                            </tr>
                        `);
                    });
                }
            }).fail(function () {
                showErrorModal("Could not connect to the server");//nếu ko gọi xuống được controller thì báo lỗi
            });
        }

        function loadCategory() {
            let productTypeSelect = $('#productTypeField');
            let unitSelect = $('#unitField');
            let brandSelect = $('#brandField');

            productTypeSelect.empty();
            unitSelect.empty();
            brandSelect.empty();

            //Load product type
            $.get(mvHostURLCallApi + '/category/product-type', function (response) {
                productTypeSelect.append('<option>Chọn loại sản phẩm</option>');
                if (response.status === "OK") {
                    $.each(response.data, function (index, d) {
                        productTypeSelect.append('<option value=' + d.id + '>' + d.name + '</option>');
                    });
                }
            }).fail(function () {
                showErrorModal("Could not connect to the server");
            });

            //Load unit
            $.get(mvHostURLCallApi + '/category/unit', function (response) {
                unitSelect.append('<option>Chọn đơn vị tính</option>');
                if (response.status === "OK") {
                    $.each(response.data, function (index, d) {
                        unitSelect.append('<option value=' + d.id + '>' + d.name + '</option>');
                    });
                }
            }).fail(function () {
                showErrorModal("Could not connect to the server");
            });

            //Load brand
            $.get(mvHostURLCallApi + '/category/brand', function (response) {
                brandSelect.append('<option>Chọn nhãn hiệu</option>');
                if (response.status === "OK") {
                    $.each(response.data, function (index, d) {
                        brandSelect.append('<option value=' + d.id + '>' + d.name + '</option>');
                    });
                }
            }).fail(function () {
                showErrorModal("Could not connect to the server");
            });
        }

        function createProduct() {
            let apiURL = mvHostURLCallApi + "/product/create";
            let productTypeId = $("#productTypeField").val();
            let brandId = $("#brandField").val();
            let productName = $("#productNameField").val();
            let unitId = $("#unitField").val();
            let body = {productTypeId: productTypeId, brandId: brandId, productName: productName, unitId: unitId};
            $.ajax({
                url: apiURL,
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify(body),
                success: function (response, textStatus, jqXHR) {
                    if (response.status === "OK") {
                        alert("Create successfully")
                        window.location.reload();
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    showErrorModal("Could not connect to the server");
                }
            });
        }
    </script>
</body>
</html>