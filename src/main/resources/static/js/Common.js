//Host
const mvHostURL = window.location.origin;
const mvHostURLCallApi = mvHostURL + '/api/v1';

//Language to use
let mvLang = "vi";

//User login info
const mvCurrentAccountId = '';
const mvCurrentAccountUsername = '';

//Config product status
const mvProductStatus = {};
mvProductStatus["ACT"] = "Đang kinh doanh";
mvProductStatus["INA"] = "Ngừng kinh doanh";
mvProductStatus["DIS"] = "Ngừng kinh doanh";
mvProductStatus["OOS"] = "Hết hàng";

//Config ticket import status
const mvTicketImportStatus = {};
mvTicketImportStatus["DRAFT"] = "Nháp";
mvTicketImportStatus["COMPLETED"] = "Hoàn thành";
mvTicketImportStatus["CANCEL"] = "Hủy";

//Config ticket export status
const mvTicketExportStatus = {};
mvTicketExportStatus["DRAFT"] = "Nháp";
mvTicketExportStatus["COMPLETED"] = "Hoàn thành";
mvTicketExportStatus["CANCEL"] = "Hủy";

const mvStorageStatusInit = {};
mvStorageStatusInit["Y"] = "Đang sử dụng";
mvStorageStatusInit["N"] = "Không sử dụng";

//Pagination
const mvPageSizeDefault = 10;
let getPageSize = () => getPageParams().pageSize;
let getPageNum = () => getPageParams().pageNum;

function getPageParams() {
    let params = new URLSearchParams(window.location.search);
    return {
        pageNum: params.get("page") ? parseInt(params.get("page")) : 1,
        pageSize: params.get("size") ? parseInt(params.get("size")) : mvPageSizeDefault
    };
}

function updatePaginationUI(pageNum, pageSize, totalPage, totalElements) {
    $('#paginationInfo').attr("pageNum", pageNum);
    $('#paginationInfo').attr("pageSize", pageSize);
    $('#paginationInfo').attr("totalPage", totalPage);
    $('#paginationInfo').attr("totalElements", totalElements);
    $('#pageNum').text(pageNum);

    let startCount = (pageNum - 1) * pageSize + 1;
    let endCount = startCount + pageSize - 1;
    if (endCount > totalElements) {
        endCount = totalElements;
    }
    $('#paginationInfo').text("Showing " + startCount + " to " + endCount + " of " + totalElements + " entries");

    $('#totalPages').text("Total pages " + totalPage);

    if(pageNum === 1) {
        $('#firstPage').attr("disable", "");
        $('#previousPage').attr("disable", "");
    }
}

function updatePaginationUI2(pageNum, pageSize, totalPage, totalElements) {
    $('#paginationInfo2').attr("pageNum", pageNum);
    $('#paginationInfo2').attr("pageSize", pageSize);
    $('#paginationInfo2').attr("totalPage", totalPage);
    $('#paginationInfo2').attr("totalElements", totalElements);
    $('#pageNum2').text(pageNum);

    let startCount = (pageNum - 1) * pageSize + 1;
    let endCount = startCount + pageSize - 1;
    if (endCount > totalElements) {
        endCount = totalElements;
    }
    $('#paginationInfo2').text("Showing " + startCount + " to " + endCount + " of " + totalElements + " entries");

    $('#totalPages2').text("Total pages " + totalPage);

    if(pageNum === 1) {
        $('#firstPage2').attr("disable", "");
        $('#previousPage2').attr("disable", "");
    }
}

function updatePaginationUI3(pageNum, pageSize, totalPage, totalElements) {
    $('#paginationInfo3').attr("pageNum", pageNum);
    $('#paginationInfo3').attr("pageSize", pageSize);
    $('#paginationInfo3').attr("totalPage", totalPage);
    $('#paginationInfo3').attr("totalElements", totalElements);
    $('#pageNum3').text(pageNum);

    let startCount = (pageNum - 1) * pageSize + 1;
    let endCount = startCount + pageSize - 1;
    if (endCount > totalElements) {
        endCount = totalElements;
    }
    $('#paginationInfo3').text("Showing " + startCount + " to " + endCount + " of " + totalElements + " entries");

    $('#totalPages3').text("Total pages " + totalPage);

    if(pageNum === 1) {
        $('#firstPage3').attr("disable", "");
        $('#previousPage3').attr("disable", "");
    }
}

function updateTableContentWhenOnClickPagination(loadNewDataMethod) {
    let lvPageSize = $('#selectPageSize').val();
    let lvPageNum = 1;

    $('#selectPageSize').on('click', function() {
        if (lvPageSize === $(this).val()) {
            return;
        }
        lvPageSize = $(this).val();
        lvPageNum = 1;
        loadNewDataMethod($(this).val(), lvPageNum);
        updateURLParameter(lvPageNum , lvPageSize);
    });

    $('#firstPage').on('click', function() {
        if (parseInt($('#paginationInfo').attr("pageNum")) === 1) {
            return;
        }
        lvPageNum = 1;
        loadNewDataMethod(lvPageSize, lvPageNum);
        changePage();
        updateURLParameter(lvPageNum , lvPageSize);
    });

    $('#previousPage').on('click', function() {
        if (parseInt($('#paginationInfo').attr("pageNum")) === 1) {
            return;
        }
        lvPageNum = parseInt($('#paginationInfo').attr("pageNum")) - 1;
        loadNewDataMethod(lvPageSize, lvPageNum);
        changePage();
        updateURLParameter(lvPageNum , lvPageSize);
    });

    $('#nextPage').on('click', function() {
        if ($('#paginationInfo').attr("pageNum") === $('#paginationInfo').attr("totalPage")) {
            return;
        }
        lvPageNum = parseInt($('#paginationInfo').attr("pageNum")) + 1;
        loadNewDataMethod(lvPageSize, lvPageNum);
        changePage();
        updateURLParameter(lvPageNum, lvPageSize);
    });

    $('#lastPage').on('click', function() {
        if ($('#paginationInfo').attr("pageNum") === $('#paginationInfo').attr("totalPage")) {
            return;
        }
        lvPageNum = $('#paginationInfo').attr("totalPage");
        loadNewDataMethod(lvPageSize, lvPageNum);
        changePage();
        updateURLParameter(lvPageNum, lvPageSize);
    });
}

function updateTableContentWhenOnClickPagination2(loadNewDataMethod) {
    let lvPageSize = $('#selectPageSize2').val();
    $('#selectPageSize2').on('click', function() {
        console.log($(this).val())
        if (lvPageSize === $(this).val()) {
            return;
        }
        lvPageSize = $(this).val();
        loadNewDataMethod($(this).val(), 1);
    });

    $('#firstPage2').on('click', function() {
        if (parseInt($('#paginationInfo2').attr("pageNum")) === 1) {
            return;
        }
        loadNewDataMethod(lvPageSize, 1);
        changePage();
    });

    $('#previousPage2').on('click', function() {
        if (parseInt($('#paginationInfo2').attr("pageNum")) === 1) {
            return;
        }
        loadNewDataMethod(lvPageSize, $('#paginationInfo2').attr("pageNum") - 1);
        changePage();
    });

    $('#nextPage2').on('click', function() {
        if ($('#paginationInfo2').attr("pageNum") === $('#paginationInfo2').attr("totalPage")) {
            return;
        }
        loadNewDataMethod(lvPageSize, parseInt($('#paginationInfo2').attr("pageNum")) + 1);
        changePage();
    });

    $('#lastPage2').on('click', function() {
        if ($('#paginationInfo2').attr("pageNum") === $('#paginationInfo2').attr("totalPage")) {
            return;
        }
        loadNewDataMethod(lvPageSize, $('#paginationInfo2').attr("totalPage"));
        changePage();
    });
}

function updateTableContentWhenOnClickPagination3(loadNewDataMethod) {
    let lvPageSize = $('#selectPageSize3').val();
    $('#selectPageSize3').on('click', function() {
        console.log($(this).val())
        if (lvPageSize === $(this).val()) {
            return;
        }
        lvPageSize = $(this).val();
        loadNewDataMethod($(this).val(), 1);
    });

    $('#firstPage3').on('click', function() {
        if (parseInt($('#paginationInfo3').attr("pageNum")) === 1) {
            return;
        }
        loadNewDataMethod(lvPageSize, 1);
        changePage();
    });

    $('#previousPage3').on('click', function() {
        if (parseInt($('#paginationInfo3').attr("pageNum")) === 1) {
            return;
        }
        loadNewDataMethod(lvPageSize, $('#paginationInfo3').attr("pageNum") - 1);
        changePage();
    });

    $('#nextPage3').on('click', function() {
        if ($('#paginationInfo3').attr("pageNum") === $('#paginationInfo3').attr("totalPage")) {
            return;
        }
        loadNewDataMethod(lvPageSize, parseInt($('#paginationInfo3').attr("pageNum")) + 1);
        changePage();
    });

    $('#lastPage3').on('click', function() {
        if ($('#paginationInfo3').attr("pageNum") === $('#paginationInfo3').attr("totalPage")) {
            return;
        }
        loadNewDataMethod(lvPageSize, $('#paginationInfo3').attr("totalPage"));
        changePage();
    });
}

function changePage() {
    mvProductSearchModalListSelected = [];
    $('#cbxChooseAllProduct').prop("checked", false);
}

function updateURLParameter(pageNum, pageSize) {
    let url = new URL(window.location.href);
    let params = new URLSearchParams(url.search);

    params.set("page", pageNum);
    params.set("size", pageSize);

    window.history.pushState({}, "", `${url.pathname}?${params.toString()}`);
}

//Search tool
function setupSearchTool() {
    let brandFilter = $('#brandFilter');
    let productTypeFilter = $('#productTypeFilter');
    let colorFilter = $('#colorFilter');
    let sizeFilter = $('#sizeFilter');
    let unitFilter = $('#unitFilter');
    let discountFilter = $('#discountFilter');
    let productStatusFilter = $('#productStatusFilter');
    let branchFilter = $('#branchFilter');
    let paymentMethodFilter = $('#paymentMethodFilter');
    let salesChannelFilter = $('#salesChannelFilter');
    let orderStatusFilter = $('#orderStatusFilter');
    let paymentStatusFilter = $('#paymentStatusFilter');
    let orderTypeFilter = $('#orderTypeFilter');
    let shipMethodFilter = $('#shipMethodFilter');
    let groupCustomerFilter = $('#groupCustomerFilter');
    let dateFilter = $('#dateFilter');

    $("#btnOpenSearchAdvance").on("click", function () {
        clearSearchSelection(sizeFilter, 'Chọn kích cỡ');
        clearSearchSelection(unitFilter, 'Chọn đơn vị tính');
        clearSearchSelection(brandFilter, 'Chọn nhãn hiệu');
        clearSearchSelection(branchFilter, 'Chọn chi nhánh');
        clearSearchSelection(colorFilter, 'Chọn màu sắc');
        clearSearchSelection(discountFilter, 'Chọn khuyến mãi');
        clearSearchSelection(orderTypeFilter, 'Chọn loại đơn hàng');
        clearSearchSelection(shipMethodFilter, 'Chọn hình thức giao hàng');
        clearSearchSelection(orderStatusFilter, 'Chọn trạng thái đơn hàng');
        clearSearchSelection(productTypeFilter, 'Chọn loại sản phẩm');
        clearSearchSelection(salesChannelFilter, 'Chọn kênh bán hàng');
        clearSearchSelection(productStatusFilter, 'Chọn trạng thái sản phẩm');
        clearSearchSelection(paymentMethodFilter, 'Chọn hình thức thanh toán');
        clearSearchSelection(paymentStatusFilter, 'Chọn trạng thái thanh toán');
        clearSearchSelection(groupCustomerFilter, 'Chọn nhóm khách hàng');
        clearSearchSelection(dateFilter, 'Lọc theo thời gian');

        let keySearch = [];
        $.each($('.search-selection'), function (index, d) {
            keySearch.push($(d).attr('entity'));
        })

        $.each(keySearch, function (index, key) {
            switch (key) {
                case "BRAND":
                    downloadCategoryForSelection(brandFilter, mvHostURLCallApi + '/category/brand');
                    break;
                case "PRODUCT_TYPE":
                    downloadCategoryForSelection(productTypeFilter, mvHostURLCallApi + '/category/product-type');
                    break;
                case "COLOR":
                    downloadCategoryForSelection(colorFilter, mvHostURLCallApi + '/category/color');
                    break;
                case "SIZE":
                    downloadCategoryForSelection(sizeFilter, mvHostURLCallApi + '/category/size');
                    break;
                case "UNIT":
                    downloadCategoryForSelection(unitFilter, mvHostURLCallApi + '/category/unit');
                    break;
                case "BRANCH":
                    $.get((mvHostURLCallApi + '/system/branch/all'), function (response) {
                        if (response.status === "OK") {
                            $.each(response.data, function (index, d) {
                                branchFilter.append('<option value=' + d.id + '>' + d.branchName + '</option>');
                            });
                        }
                    }).fail(function () {
                        showErrorModal("Could not connect to the server");
                    });
                    break;
                case "PAYMENT_METHOD":
                    downloadCategoryForSelection(paymentMethodFilter, mvHostURLCallApi + '/category/payment-method');
                    break;
                case "SALES_CHANNEL":
                    downloadCategoryForSelection(salesChannelFilter, mvHostURLCallApi + '/category/sales-channel');
                    break;
                case "ORDER_STATUS":
                    downloadCategoryForSelection(orderStatusFilter, mvHostURLCallApi + '/category/order-status');
                    break;
                case "PAYMENT_STATUS":
                    downloadCategoryForSelection(paymentStatusFilter, mvHostURLCallApi + '/category/payment-status');
                    break;
                case "ORDER_TYPE":
                    downloadCategoryForSelection(orderTypeFilter, mvHostURLCallApi + '/category/order-type');
                    break;
                case "SHIP_METHOD":
                    downloadCategoryForSelection(shipMethodFilter, mvHostURLCallApi + '/category/ship-method');
                    break;
                case "GROUP_CUSTOMER":
                    downloadCategoryForSelection(groupCustomerFilter, mvHostURLCallApi + '/category/group-customer');
                    break;
                case "DISCOUNT":
                    discountFilter.append(`<option value="Y">Đang áp dụng</option>
                                           <option value="N">Không áp dụng</option>`);
                    break;
                case "PRODUCT_STATUS":
                    productStatusFilter.append(`<option value="ACTIVE">Đang kinh doanh</option>
                                                <option value="INACTIVE">Không kinh doanh</option>`);
                    break;
                case "DATE_FILTER":
                        dateFilter.append(`<option value="T0">Hôm nay</option>
                                           <option value="T-1">Hôm qua</option>
                                           <option value="T-7">7 ngày trước</option>
                                           <option value="M0">Tháng này</option>
                                           <option value="M-1">Tháng trước</option>`);
            }
        })
    })
}

function clearSearchSelection(element, defaultOption) {
    element.empty();
    if (defaultOption != null) {
        element.append(`<option value="">${defaultOption}</option>`);
    }
}

let convertDateT1 = (dateInput) => {
    let dateObject = new Date(dateInput);
    let year = dateObject.getFullYear();
    let month = dateObject.getMonth() + 1; // Tháng (đánh số từ 0)
    let day = dateObject.getDate();
    // Format lại chuỗi ngày tháng thành 'dd/MM/yyyy'
    return formattedDate = (day < 10 ? '0' : '') + day + '/' + (month < 10 ? '0' : '') + month + '/' + year;
}

function setupSelectMultiple() {
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
}

function setupSearchSelector(element, defaultOption, endPoint) {
    clearSearchSelection(element, defaultOption);
    downloadCategoryForSelection(element, endPoint);
}