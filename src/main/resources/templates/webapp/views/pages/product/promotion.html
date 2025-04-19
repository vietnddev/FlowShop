<!DOCTYPE html>
<html lang="en" xmlns:th="https://www.thymeleaf.org">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Promotion</title>
    <th:block th:replace="header :: stylesheets"></th:block>
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
                            <div class="card">
                                <div class="card-header">
                                    <div class="row justify-content-between">
                                        <div class="col-4" style="display: flex; align-items: center">
                                            <h3 class="card-title"><strong>CHƯƠNG TRÌNH KHUYẾN MÃI</strong></h3>
                                        </div>
                                        <div class="col-6 text-right">
                                            <button type="button" class="btn btn-success btn-insert"><i class="fa-solid fa-circle-plus mr-2"></i>Thêm mới</button>
                                        </div>
                                    </div>
                                </div>
                                <div class="card-body p-0">
                                    <table class="table table-bordered table-striped">
                                        <thead>
                                            <tr>
                                                <th>STT</th>
                                                <th>Tiêu đề</th>
                                                <th>Mô tả</th>
                                                <th>Sản phẩm áp dụng</th>
                                                <th>Phần trăm giảm</th>
                                                <th>Số tiền giảm</th>
                                                <th>Tối đa giảm</th>
                                                <th>Thời gian bắt đầu</th>
                                                <th>Thời gian kết thúc</th>
                                                <th>Trạng thái</th>
                                                <th></th>
                                            </tr>
                                        </thead>
                                        <tbody id="contentTable"></tbody>
                                        <tfoot>
                                            <tr>
                                                <th>STT</th>
                                                <th>Tiêu đề</th>
                                                <th>Mô tả</th>
                                                <th>Sản phẩm áp dụng</th>
                                                <th>Phần trăm giảm</th>
                                                <th>Số tiền giảm</th>
                                                <th>Tối đa giảm</th>
                                                <th>Thời gian bắt đầu</th>
                                                <th>Thời gian kết thúc</th>
                                                <th>Trạng thái</th>
                                                <th></th>
                                            </tr>
                                        </tfoot>
                                    </table>
                                </div>
                                <div class="card-footer">
                                    <div th:replace="fragments :: pagination"></div>
                                </div>

                                <!--Modal insert/update-->
                                <div class="modal fade" id="modal_insert_update">
                                    <div class="modal-dialog modal-lg">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <strong class="modal-title" id="modal_insert_update_title"></strong>
                                                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                                            </div>
                                            <div class="modal-body">
                                                <div class="row">
                                                    <div class="form-group col-12">
                                                        <label>Title</label>
                                                        <input type="text" class="form-control" id="titleField" required/>
                                                    </div>
                                                    <div class="form-group col-12">
                                                        <label>Sản phẩm áp dụng</label>
                                                        <select class="form-control select2" multiple="multiple" style="width: 100%;" id="applicableProductsField"></select>
                                                    </div>
                                                    <div class="form-group col-12">
                                                        <label>Phần trăm giảm</label>
                                                        <input type="text" class="form-control" id="discountPercentField"/>
                                                    </div>
                                                    <div class="form-group col-12">
                                                        <label>Số tiền giảm</label>
                                                        <input type="text" class="form-control" id="discountPriceField"/>
                                                    </div>
                                                    <div class="form-group col-12">
                                                        <label>Số tiền giảm tối đa</label>
                                                        <input type="text" class="form-control" id="discountPriceMaxField"/>
                                                    </div>
                                                    <div class="form-group col-6">
                                                        <label>Thời gian bắt đầu</label>
                                                        <input class="form-control" type="date" id="startTimeField" required>
                                                    </div>
                                                    <div class="form-group col-6">
                                                        <label>Thời gian kết thúc</label>
                                                        <input class="form-control" type="date" id="endTimeField" required>
                                                    </div>
                                                    <div class="form-group col-12">
                                                        <label>Ghi chú</label>
                                                        <textarea class="form-control" rows="5" id="descriptionField"></textarea>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="modal-footer justify-content-end">
                                                <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                                                <button type="button" class="btn btn-primary" id="btn-insert-update-submit-">Lưu</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <!--Modal insert/update-->
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
        let mvPromotions = [];
        let mvId = 0;
        let mvTitle = $("#titleField");
        let mvDiscountPercent = $("#discountPercentField");
        let mvDiscountPrice = $("#discountPriceField");
        let mvDiscountPriceMax = $("#discountPriceMaxField");
        let mvStartTime = $("#startTimeField");
        let mvEndTime = $("#endTimeField");
        let mvDescription = $("#descriptionField");
        let mvApplicableProductIds = $('#applicableProductsField');

        $(document).ready(function() {
            loadPromotions(mvPageSizeDefault, 1);
            updateTableContentWhenOnClickPagination(loadPromotions);

            //loadProducts();
            //preCreateStorage();
            //preUpdateStorage();
            //submitInsertOrUpdate();
            //deleteStorage();
        });

        /*function loadProducts() {
            let apiURL = mvHostURLCallApi + '/product/all';
            let params = {fullInfo: false}
            $.get(apiURL, params, function (response) {
                if (response.status === "OK") {
                    let products = response.data;
                    mvApplicableProductIds.empty();
                    $.each(products, function (index, d) {
                        mvApplicableProductIds.append(`<option value="${d.id}">${d.productName}</option>`);
                    });
                }
            }).fail(function () {
                showErrorModal("Could not connect to the server");
            });
        }

        function preCreateStorage() {
            $(document).on("click", ".btn-insert", function () {
                $("#modal_insert_update_title").text("Add new Promotion");
                mvId = 0;
                mvTitle.val("");
                mvDiscountPercent.val("");
                mvDiscountPrice.val("");
                mvDiscountPriceMax.val("");
                mvStartTime.val("");
                mvEndTime.val("");
                mvDescription.val("");
                $("#btn-insert-update-submit").attr("actionType", "insert");
                $("#modal_insert_update").modal();
            });
        }

        function preUpdateStorage() {
            $(document).on("click", ".btn-update", function () {
                let promotion = mvPromotions[$(this).attr("id")];
                $("#modal_insert_update_title").text("Update Promotion");
                mvId = promotion.id;
                mvTitle.val(promotion.title);
                mvDiscountPercent.val(promotion.discountPercent);
                mvDiscountPrice.val(promotion.discountPrice);
                mvDiscountPriceMax.val(promotion.discountPriceMax);
                mvStartTime.val(promotion.startTime);
                mvEndTime.val(promotion.endTime);
                mvDescription.val(promotion.description);
                $("#btn-insert-update-submit").attr("actionType", "update");
                $("#modal_insert_update").modal();
            });
        }

        function submitInsertOrUpdate() {
            $("#btn-insert-update-submit").on("click", function () {
                let actionType = $(this).attr("actionType");
                let applicableProducts = [];
                $.each(mvApplicableProductIds.val(), function (index, productId) {
                    applicableProducts.push(productId);
                })
                let promotion = {
                    title : mvTitle.val(),
                    discountPercent : parseInt(mvDiscountPercent.val()),
                    discountPrice : parseFloat(mvDiscountPrice.val()),
                    discountPriceMax : parseFloat(mvDiscountPriceMax.val()),
                    startTime : mvStartTime.val(),
                    endTime : mvEndTime.val(),
                    description : mvDescription.val(),
                    applicableProducts : applicableProducts
                }
                if (actionType === "insert") {
                    $.ajax({
                        url: mvHostURLCallApi + "/promotion/create",
                        type: "POST",
                        contentType: "application/json",
                        data: JSON.stringify(promotion),
                        success: function(response, a, b) {
                            if (response.status === "OK") {
                                alert("Create successfully!");
                                window.location.reload();
                            }
                        },
                        error: function (xhr) {
                            alert("Error: " + $.parseJSON(xhr.responseText).message);
                        }
                    });
                }
                if (actionType === "update") {
                    $.ajax({
                        url: mvHostURLCallApi + "/promotion/update/" + mvId,
                        type: "PUT",
                        contentType: "application/json",
                        data: JSON.stringify(promotion),
                        success: function(response) {
                            if (response.status === "OK") {
                                alert("Update successfully!");
                                window.location.reload();
                            }
                        },
                        error: function (xhr) {
                            alert("Error: " + $.parseJSON(xhr.responseText).message);
                        }
                    });
                }
            });
        }

        function deleteStorage() {
            $(document).on("click", ".btn-delete", function () {
                let promotion = mvPromotions[$(this).attr("id")];
                mvId = promotion.id;
                $(this).attr("actionType", "delete");
                $(this).attr("entityName", promotion.title);
                showConfirmModal($(this), "Delete Promotion", "Are you sure to delete: " + promotion.title);
            });

            $('#yesButton').on("click", function () {
                let apiURL = mvHostURLCallApi + "/promotion/delete/" + mvId;
                callApiDelete(apiURL);
            });
        }*/

        function loadPromotions(pageSize, pageNum) {
            let apiURL = mvHostURLCallApi + '/promotion/all';
            let params = {pageSize: pageSize, pageNum: pageNum}
            $.get(apiURL, params, function (response) {
                if (response.status === "OK") {
                    let data = response.data;
                    let pagination = response.pagination;

                    updatePaginationUI(pagination.pageNum, pagination.pageSize, pagination.totalPage, pagination.totalElements);

                    let contentTable = $('#contentTable');
                    contentTable.empty();
                    $.each(data, function (index, d) {
                        mvPromotions[d.id] = d;
                        let productApplyBlock = '';
                        $.each(d.applicableProducts, function (applyIndex, applyInfo) {
                            productApplyBlock += `<span class="mr-2">${applyIndex + 1}</span><a href="/san-pham/${applyInfo.productId}"><span>${applyInfo.productName}</span></a><br>`;
                        });
                        contentTable.append(`
                            <tr>
                                <td>${(((pageNum - 1) * pageSize + 1) + index)}</td>
                                <td>${d.title}</td>
                                <td>${d.description}</td>
                                <td>${productApplyBlock}</td>
                                <td>${d.discountPercent}</td>
                                <td>${d.discountPrice}</td>
                                <td>${d.discountPriceMax}</td>
                                <td>${d.startTime}</td>
                                <td>${d.endTime}</td>
                                <td>${d.status}</td>
                                <td>
                                    <button class="btn btn-info    btn-sm btn-update mr-1"  id="${d.id}"><i class="fa-solid fa-pencil"></i></button>
                                    <button class="btn btn-danger  btn-sm btn-delete"       id="${d.id}"><i class="fa-solid fa-trash"></i></button>
                                </td>
                            </tr>
                        `);
                    });
                }
            }).fail(function () {
                showErrorModal("Could not connect to the server");
            });
        }
    </script>

    <script>
        $(function () {
            //Initialize Select2 Elements
            $('.select2').select2()
            //Initialize Select2 Elements
            $('.select2bs4').select2({
                theme: 'bootstrap4'
            })
            //Bootstrap Duallistbox
            $('.duallistbox').bootstrapDualListbox()
            $("input[data-bootstrap-switch]").each(function () {
                $(this).bootstrapSwitch('state', $(this).prop('checked'));
            })
            //Date and time picker
            $('#reservationdatetime').datetimepicker({icons: {time: 'far fa-clock'}});
            //Timepicker
            $('#timepicker').datetimepicker({
                format: 'LT'
            })
            //Date range picker
            $('#reservation').daterangepicker()
        })
    </script>
</body>
</html>