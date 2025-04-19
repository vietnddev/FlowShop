<!DOCTYPE html>
<html lang="en" xmlns:th="https://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Danh sách combo sản phẩm</title>
    <th:block th:replace="header :: stylesheets"></th:block>
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
                            <div th:replace="fragments :: searchTool(${configSearchTool})" id="searchTool"></div>

                            <div class="card">
                                <div class="card-header">
                                    <div class="row justify-content-between">
                                        <div class="col-4" style="display: flex; align-items: center">
                                            <h3 class="card-title"><strong class="text-uppercase">Danh sách combo sản phẩm</strong></h3>
                                        </div>
                                        <div class="col-6 text-right">
                                            <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#import"><i class="fa-solid fa-cloud-arrow-up mr-2"></i>Import</button>
                                            <a th:href="@{/san-pham/export}" class="btn btn-info"><i class="fa-solid fa-cloud-arrow-down mr-2"></i>Export</a>
                                            <button type="button" class="btn btn-success" data-toggle="modal" data-target="#insert" id="btnCreateCombo"><i class="fa-solid fa-circle-plus mr-2"></i>Thêm mới</button>
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
                                                <th>Tên combo</th>
                                                <th>Sản phẩm</th>
                                                <th>Số lượng</th>
                                                <th>Giá</th>
                                                <th>Ghi chú</th>
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
                                                <strong class="modal-title">Thêm mới combo sản phẩm</strong>
                                                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                                            </div>
                                            <div class="modal-body">
                                                <div class="row">
                                                    <div class="col-12">
                                                        <div class="form-group">
                                                            <label>Tên combo</label>
                                                            <input type="text" class="form-control" placeholder="Tên combo" id="comboNameField">
                                                        </div>
                                                        <div class="form-group">
                                                            <label>Chọn sản phẩm</label>
                                                            <select class="form-control select2" multiple="multiple" data-placeholder="Chọn sản phẩm" style="width: 100%;" id="applicableProductsField"></select>
                                                        </div>
                                                        <div class="form-group">
                                                            <label>Ghi chú</label>
                                                            <textarea class="form-control" placeholder="Ghi chú" id="noteField"></textarea>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="modal-footer justify-content-end">
                                                <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                                                <button type="button" class="btn btn-primary" id="btnSubmitCreateCombo">Lưu</button>
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
        setupSelectMultiple();

        let mvNameField = $("#comboNameField");
        let mvNoteField = $("#noteField");
        let mvApplicableProductsField = $("#applicableProductsField");

        $(document).ready(function () {
            $('#btnCreateCombo').on('click', function () {
                loadProductVariants();
            });

            $('#btnSubmitCreateCombo').on('click', function () {
                createCombo();
            });

            loadProductCombos(mvPageSizeDefault, 1);
            updateTableContentWhenOnClickPagination(loadProductCombos);

            setupSearchTool();
            $("#btnSearch").on("click", function () {
                let brandFilter = $('#brandFilter').val();
                let unitFilter = $('#unitFilter').val();
                let discountFilter = $('#discountFilter').val();
                let productStatusFilter = $('#productStatusFilter').val();
                loadProductCombos($('#paginationInfo').attr("pageSize"), 1);
            })
        });

        function loadProductCombos(pageSize, pageNum) {
            let apiURL = mvHostURLCallApi + '/product/combo/all';
            let params = {
                pageSize: pageSize,
                pageNum: pageNum
            }
            $.get(apiURL, params, function (response) {//dùng Ajax JQuery để gọi xuống controller
                if (response.status === "OK") {
                    let data = response.data;
                    let pagination = response.pagination;

                    updatePaginationUI(pagination.pageNum, pagination.pageSize, pagination.totalPage, pagination.totalElements);

                    let contentTable = $('#contentTable');
                    contentTable.empty();
                    $.each(data, function (index, d) {
                        let productIncludesBlock = "";
                        $.each(d.applicableProducts, function (index, p) {
                            productIncludesBlock += `${index + 1}. ${p.variantName} <br>`;
                        });

                        contentTable.append(`
                            <tr>
                                <td>${(((pageNum - 1) * pageSize + 1) + index)}</td>
                                <td class="text-center"><img src="" style="width: 60px; height: 60px; border-radius: 5px"></td>
                                <td><a href="/product/combo/${d.id}">${d.comboName}</a></td>
                                <td>${productIncludesBlock}</td>
                                <td>${d.quantity}</td>
                                <td>${d.totalValue}</td>
                                <td>${d.note}</td>
                                <td>${d.status}</td>
                            </tr>
                        `);
                    });
                }
            }).fail(function () {
                showErrorModal("Could not connect to the server");//nếu ko gọi xuống được controller thì báo lỗi
            });
        }

        function createCombo() {
            let apiURL = mvHostURLCallApi + "/product/combo/create";
            let lvApplicableProducts = [];
            $.each(mvApplicableProductsField.val(), function (index, d) {
                let productVariant = {
                    id: d
                };
                lvApplicableProducts.push(productVariant);
            });
            console.log("id ", mvApplicableProductsField.val())
            let body = {
                comboName: mvNameField.val(),
                note: mvNoteField.val(),
                applicableProducts: lvApplicableProducts
            };
            $.ajax({
                url: apiURL,
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify(body),
                success: function (response, textStatus, jqXHR) {
                    if (response.status === "OK") {
                        alert("Create successfully!")
                        window.location.reload();
                    }
                },
                error: function (xhr, textStatus, errorThrown) {
                    //showErrorModal("Could not connect to the server");
                    showErrorModal($.parseJSON(xhr.responseText).message);
                }
            });
        }

        function loadProductVariants() {
            let productVariantSelect = $('#applicableProductsField');
            productVariantSelect.empty();
            $.get(mvHostURLCallApi + '/product/variant/all', function (response) {
                if (response.status === "OK") {
                    $.each(response.data, function (index, d) {
                        productVariantSelect.append(`<option value="${d.id}">${d.variantName}</option>`);
                    });
                }
            }).fail(function () {
                showErrorModal("Could not connect to the server");
            });
        }
    </script>
</body>
</html>