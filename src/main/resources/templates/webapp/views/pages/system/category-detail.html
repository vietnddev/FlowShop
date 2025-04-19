<!DOCTYPE html>
<html lang="en" xmlns:th="https://www.thymeleaf.org">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Flowiee | System category</title>
    <div th:replace="header :: stylesheets"></div>
    <style rel="stylesheet">
        .table td, th {
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
                                            <h3 class="card-title text-uppercase font-weight-bold" th:text="${ctgRootName}"></h3>
                                        </div>
                                        <div class="col-6 text-right">
                                            <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#import"><i class="fa-solid fa-cloud-arrow-up"></i>Import</button>
                                            <a th:href="@{${url_export}}" class="btn btn-info"><i class="fa-solid fa-cloud-arrow-down"></i>Export</a>
                                            <button type="button" class="btn btn-success btn-insert"><i class="fa-solid fa-circle-plus"></i>Thêm mới</button>
                                        </div>
                                    </div>
                                </div>
                                <!-- /.card-header -->
                                <div class="card-body align-items-center p-0">
                                    <table class="table table-bordered table-striped align-items-center">
                                        <thead class="align-self-center">
                                            <tr class="align-self-center">
                                                <th>#</th>
                                                <th>Code</th>
                                                <th>Name</th>
                                                <th>Color</th>
                                                <th>Note</th>
                                                <th>In use</th>
                                                <th>Status</th>
                                                <th>Default</th>
                                                <th>Sort</th>
                                                <th></th>
                                            </tr>
                                        </thead>
                                        <tbody id="contentTable"></tbody>
                                        <tfoot>
                                            <tr class="align-self-center">
                                                <th>#</th>
                                                <th>Code</th>
                                                <th>Name</th>
                                                <th>Color</th>
                                                <th>Note</th>
                                                <th>In use</th>
                                                <th>Status</th>
                                                <th>Default</th>
                                                <th>Sort</th>
                                                <th></th>
                                            </tr>
                                        </tfoot>
                                    </table>
                                </div>
                                <!-- /.card-body -->
                                <div class="card-footer">
                                    <div th:replace="fragments :: pagination"></div>
                                </div>
                            </div>
                        </div>
                        <!-- Modal create and update category -->
                        <div th:replace="pages/system/fragments/category-fragments :: modalInsertAndUpdate"></div>
                        <!-- Modal import category -->
                        <div th:replace="pages/system/fragments/category-fragments :: modalImport"></div>
                    </div>
                </div>
            </section>
        </div>

        <div th:replace="modal_fragments :: confirm_modal"></div>

        <div th:replace="footer :: footer"></div>

        <aside class="control-sidebar control-sidebar-dark"></aside>

        <div th:replace="header :: scripts"></div>

        <script th:src="@{/js/category/LoadCategories.js}"></script>
        <script th:src="@{/js/category/CreateAndUpdateCategory.js}"></script>
    </div>
    <script>
        let mvCategories = {};
        let mvId = 0;
        let mvType = "[[${categoryType}]]";
        let mvCode = $("#codeField");
        let mvName = $("#nameField");
        let mvNote = $("#noteField");
        let mvSort = $("#sortField");
        let mvStatus = $("#statusField");

        $(document).ready(function () {
            loadCategories(mvPageSizeDefault, 1);
            updateTableContentWhenOnClickPagination(loadCategories);

            preCreateCategory();
            preUpdateCategory();
            submitInsertOrUpdate();
            deleteCategory();
        });

        function deleteCategory() {
            $(document).on("click", ".btn-delete", function () {
                let category = mvCategories[$(this).attr("id")];
                mvId = category.id;
                $(this).attr("actionType", "delete");
                $(this).attr("entityName", category.name);
                showConfirmModal($(this), "Xóa danh mục hệ thống", "Bạn chắc chắn muốn xóa danh mục: " + category.name);
            });

            $('#yesButton').on("click", function () {
                let apiURL = mvHostURLCallApi + "/category/delete/" + mvId;
                callApiDelete(apiURL);
            });
        }
    </script>
</body>
</html>