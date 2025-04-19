<!DOCTYPE html>
<html lang="en" xmlns:th="https://www.thymeleaf.org">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Quản lý phiếu xuất hàng</title>
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
                        <div class="col-6">
                            <div class="card">
                                <div class="card-body row">
                                    <div class="text-center col-12">
                                        <span class="w-100 font-weight-bold text-uppercase mb-3">Thông tin chi tiết phiếu xuất hàng</span>
                                    </div>
                                    <div class="form-group col-12">
                                        <label for="titleField">Tên phiếu</label>
                                        <input class="form-control" type="text" id="titleField" th:value="${ticketExportDetail.title}" required>
                                    </div>
                                    <div class="form-group col-6">
                                        <label for="exporterField">Người xuất</label>
                                        <input class="form-control" type="text" id="exporterField" th:value="${ticketExportDetail.exporter}" required disabled>
                                    </div>
                                    <div class="form-group col-6">
                                        <label for="exportTimeField">Thời gian xuất</label>
                                        <input class="form-control" type="text" id="exportTimeField" th:value="${ticketExportDetail.exportTimeStr}" required disabled>
                                    </div>
                                    <div class="form-group col-12">
                                        <label for="noteField">Tổng giá trị</label>
                                        <input class="form-control" type="text" id="totalValueField" th:value="${#numbers.formatDecimal(ticketExportDetail.totalValue, 0, 'COMMA', 0, 'NONE')}" disabled>
                                    </div>
                                    <div class="form-group col-12">
                                        <label for="noteField">Ghi chú</label>
                                        <input class="form-control" type="text" id="noteField" th:value="${ticketExportDetail.note}">
                                    </div>
                                    <div class="form-group col-12">
                                        <label for="statusField">Trạng thái</label>
                                        <select class="custom-select" id="statusField" required>
                                            <option th:each="s : ${ticketExportStatus}" th:value="${s.key}" th:text="${s.value}">
                                        </select>
                                    </div>
                                    <hr class="w-50">
                                    <div class="justify-content-center col-12 row">
                                        <button class="btn btn-sm btn-info col-2 mr-1 link-confirm" type="button" id="btnSave" th:actionType="update">Cập nhật</button>
                                        <button class="btn btn-sm btn-secondary col-2 mr-1 link-confirm" type="button" id="btnPrint">In</button>
                                        <button class="btn btn-sm btn-danger col-2 mr-1 link-confirm" type="button" id="btnDelete" th:actionType="delete">Xóa</button>
                                        <button class="btn btn-sm btn-success col-2" type="button" data-toggle="modal" data-target="#modalUploadFile"><i class="fa-solid fa-upload"></i></button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="row">
                                <div class="col-3 mb-2" th:each="list : ${ticketExportDetail.listImages}">
                                    <div class="card border" style="height: 186px">
                                        <div class="card-body product-image-thumb" style="margin: auto; border: none">
                                            <img th:src="@{'/' + ${list.directoryPath} + '/' + ${list.storageName}}"
                                                 th:imageId="${list.id}" style="border-radius: 5px" alt="Product Image" class="sub-image" >
                                        </div>
                                        <div class="card-footer row">
                                            <i style="cursor: pointer" th:imageId="${list.id}" class="fa-solid fa-trash text-danger col btn-delete-image"></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-12">
                            <div class="card">
                                <div class="card-header font-weight-bold">Danh sách sản phẩm theo đơn hàng</div>
                                <div class="card-body p-0">
                                    <table class="table table-bordered">
                                        <thead>
                                            <tr>
                                                <th>STT</th>
                                                <th>Loại sản phẩm</th>
                                                <th>Tên sản phẩm</th>
                                                <th>Giá lúc xuất</th>
                                                <th>Số lượng</th>
                                                <th>Thành tiền</th>
                                                <th>Ghi chú</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr th:each="list, index : ${ticketExportDetail.listProductVariantTemp}">
                                                <td th:text="${index.index + 1}"></td>
                                                <td th:text="${list.productVariant.product.productType.name}"></td>
                                                <td th:text="${list.productVariant.variantName}">/td>
                                                <td th:text="${#numbers.formatDecimal(list.sellPrice, 0, 'COMMA', 0, 'NONE')}"></td>
                                                <td th:text="${list.quantity}"></td>
                                                <td th:text="${list.sellPrice != null} ? ${#numbers.formatDecimal(list.sellPrice * list.quantity, 0, 'COMMA', 0, 'NONE')} : ''"></td>
                                                <td th:text="${list.note}"></td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </div>

        <div th:replace="modal_fragments :: confirm_modal"></div>
        <div th:replace="fragments :: uploadFileModal"></div>

        <div th:replace="footer :: footer"></div>

        <aside class="control-sidebar control-sidebar-dark"></aside>

        <div th:replace="header :: scripts"></div>
    </div>

    <script type="text/javascript">
        let mvId = [[${ticketExportId}]];
        let mvTitle = $("#titleField");
        let mvExporter = $("#exporterField");
        let mvExportTime = $("#exportTimeField");
        let mvNote = $("#noteField");
        let mvStatus = $("#statusField");

        $(document).ready(function() {
            updateOrDelete();
        });

        function updateOrDelete() {
            $(".link-confirm").on("click", function (e) {
                console.log("actionType: " + $(this).attr("actionType"))
                e.preventDefault();
                if ($(this).attr("actionType") === "update") {
                    showConfirmModal($(this), "Cập nhật phiếu xuất hàng", "Bạn muốn cập nhật phiếu xuất: " + $("#titleField").val());
                } else if ($(this).attr("actionType") === "delete") {
                    showConfirmModal($(this), "Xóa phiếu xuất hàng", "Bạn muốn xóa phiếu xuất: " + $("#titleField").val());
                }
            });
            $("#yesButton").on("click", function (e) {
                e.preventDefault();
                if ($(this).attr("actionType") === "update") {
                    let apiURL = mvHostURLCallApi + "/stg/ticket-export/update/" + mvId;
                    let lvExportTime = mvExportTime.val();
                    let body = {title : mvTitle.val(), exporter : mvExporter.val(), exportTime : lvExportTime, note : mvNote.val(), status : mvStatus.val()}
                    $.ajax({
                        url: apiURL,
                        type: 'PUT',
                        contentType: "application/json",
                        data: JSON.stringify(body),
                        success: function (response) {
                            if (response.status === "OK") {
                                alert("Update successfully!");
                                window.location.reload();
                            }
                        },
                        error: function (xhr) {
                            alert("Error: " + $.parseJSON(xhr.responseText).message);
                        }
                    });
                } else if ($(this).attr("actionType") === "delete") {
                    let apiURL = mvHostURLCallApi + "/stg/ticket-export/delete/" + mvId;
                    $.ajax({
                        url: apiURL,
                        type: 'DELETE',
                        success: function (response) {
                            if (response.status === "OK") {
                                alert("Delete successfully!");
                                window.location = mvHostURL + "/stg/ticket-export";
                            }
                        },
                        error: function (xhr) {
                            alert("Error: " + $.parseJSON(xhr.responseText).message);
                        }
                    });
                }
            });
        }
    </script>

    <script> // Upload file
    // DropzoneJS Demo Code Start
    Dropzone.autoDiscover = true

    // Get the template HTML and remove it from the doumenthe template HTML and remove it from the doument
    var previewNode = document.querySelector("#template")
    previewNode.id = ""
    var previewTemplate = previewNode.parentNode.innerHTML
    previewNode.parentNode.removeChild(previewNode)

    var myDropzone = new Dropzone(document.body, { // Make the whole body a dropzone
        url: "/uploads/ticket-export/[[${ticketExportId}]]", // Gọi tới API trong spring để xử lý file
        thumbnailWidth: 80,
        thumbnailHeight: 80,
        parallelUploads: 20,
        previewTemplate: previewTemplate,
        autoQueue: false, // Make sure the files aren't queued until manually added
        previewsContainer: "#previews", // Define the container to display the previews
        clickable: ".fileinput-button", // Define the element that should be used as click trigger to select files.
    })

    myDropzone.on("addedfile", function (file) {
        // Hookup the start button
        file.previewElement.querySelector(".start").onclick = function () {
            myDropzone.enqueueFile(file)
        }
    })

    // Update the total progress bar
    myDropzone.on("totaluploadprogress", function (progress) {
        document.querySelector("#total-progress .progress-bar").style.width = progress + "%"
    })

    myDropzone.on("sending", function (file) {
        // Show the total progress bar when upload starts
        document.querySelector("#total-progress").style.opacity = "1"
        // And disable the start button
        file.previewElement.querySelector(".start").setAttribute("disabled", "disabled")
    })

    // Hide the total progress bar when nothing's uploading anymore
    myDropzone.on("queuecomplete", function (progress) {
        document.querySelector("#total-progress").style.opacity = "0"
    })

    // Setup the buttons for all transfers
    // The "add files" button doesn't need to be setup because the config
    // `clickable` has already been specified.
    document.querySelector("#actions .start").onclick = function () {
        myDropzone.enqueueFiles(myDropzone.getFilesWithStatus(Dropzone.ADDED))
    }
    document.querySelector("#actions .cancel").onclick = function () {
        myDropzone.removeAllFiles(true)
    }
    // DropzoneJS Demo Code End

    </script>
</body>
</html>