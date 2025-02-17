<div th:fragment="sidebar">
    <aside class="main-sidebar sidebar-dark-primary elevation-4">
        <a href="" class="brand-link">
            <img th:src="@{/dist/img/FlowieeLogo.png}" alt="Flowiee Logo" class="brand-image img-circle elevation-3" style="opacity: .8">
            <span class="brand-text font-weight-bold">FLOWIEE</span>
        </a>

        <div class="sidebar">
            <div class="form-inline mt-3">
                <div class="input-group" data-widget="sidebar-search">
                    <input class="form-control form-control-sidebar" type="search" placeholder="Search"
                           aria-label="Search">
                    <div class="input-group-append">
                        <button class="btn btn-sidebar"><i class="fas fa-search fa-fw"></i></button>
                    </div>
                </div>
            </div>

            <nav class="mt-2">
                <ul class="nav nav-pills nav-sidebar flex-column" data-widget="treeview" role="menu"
                    data-accordion="false">
                    <li class="nav-header">
                        <strong>QUẢN LÝ BÁN HÀNG</strong>
                    </li>
                    <li class="nav-item">
                        <a href="#" class="nav-link">
                            <i class="fa-solid fa-shirt mr-2"></i>
                            <p>Sản phẩm<i class="fas fa-angle-left right"></i></p>
                        </a>
                        <ul class="nav nav-treeview">
                            <li class="nav-item">
                                <a th:href="@{${URL_PRODUCT}}" class="nav-link"><i class="nav-icon mr-3"></i>
                                    <p>Quần áo</p></a>
                            </li>
                            <li class="nav-item">
                                <a th:href="@{${URL_PRODUCT_COMBO}}" class="nav-link"><i class="nav-icon mr-3"></i>
                                    <p>Combo sản phẩm</p></a>
                            </li>
                            <li class="nav-item">
                                <a th:href="@{${URL_STG_MATERIAL}}" class="nav-link"><i class="nav-icon mr-3"></i>
                                    <p>Nguyên vật liệu</p></a>
                            </li>
                            <li class="nav-item">
                                <a th:href="@{${URL_PRODUCT_PROMOTION}}" class="nav-link"><i
                                        class="nav-icon mr-3"></i></i><p>Khuyến mãi</p></a>
                            </li>
                            <li class="nav-item">
                                <a th:href="@{${URL_PRODUCT_VOUCHER}}" class="nav-link"><i class="nav-icon mr-3"></i>
                                    <p>Voucher</p></a>
                            </li>
                        </ul>
                    </li>
                    <li class="nav-item">
                        <a href="#" class="nav-link">
                            <i class="fa-solid fa-cart-shopping mr-2"></i>
                            <p>Đơn hàng<i class="fas fa-angle-left right"></i></p>
                        </a>
                        <ul class="nav nav-treeview">
                            <li class="nav-item">
                                <a th:href="@{${URL_PRODUCT_ORDER}}" class="nav-link"><i class="nav-icon mr-3"></i>
                                    <p>Danh sách đơn hàng</p></a>
                            </li>
                            <li class="nav-item">
                                <a th:href="@{${URL_PRODUCT_CREATE_ORDER}}" class="nav-link"><i
                                        class="nav-icon mr-3"></i>
                                    <p>Tạo đơn</p></a>
                            </li>
                        </ul>
                    </li>
                    <li class="nav-item">
                        <a th:href="@{${URL_PRODUCT_CUSTOMER}}" class="nav-link"><i
                                class="fa-solid fa-user-tag mr-2"></i>
                            <p>Khách hàng</p></a>
                    </li>
                    <li class="nav-item">
                        <a th:href="@{${URL_PRODUCT_SUPPLIER}}" class="nav-link"><i
                                class="fa-solid fa-user-tag mr-2"></i>
                            <p>Nhà cung cấp</p></a>
                    </li>
                    <li class="nav-item">
                        <a href="#" class="nav-link">
                            <i class="fa-solid fa-cart-shopping mr-2"></i>
                            <p>Sổ quỹ<i class="fas fa-angle-left right"></i></p>
                        </a>
                        <ul class="nav nav-treeview">
                            <li class="nav-item">
                                <a th:href="@{${URL_SALES_LEDGER_RECEIPT}}" class="nav-link"><i class="nav-icon mr-3"></i>
                                    <p>Phiếu thu</p></a>
                            </li>
                            <li class="nav-item">
                                <a th:href="@{${URL_SALES_LEDGER_PAYMENT}}" class="nav-link"><i class="nav-icon mr-3"></i>
                                    <p>Phiếu chi</p></a>
                            </li>
                            <li class="nav-item">
                                <a th:href="@{${URL_SALES_LEDGER}}" class="nav-link"><i class="nav-icon mr-3"></i>
                                    <p>Sổ quỹ</p></a>
                            </li>
                        </ul>
                    </li>
                    <li class="nav-item">
                        <a th:href="@{/loyalty-programs}" class="nav-link"><i
                                class="fa-solid fa-user-tag mr-2"></i>
                            <p>Tích điểm - đổi thưởng</p></a>
                    </li>

                    <li class="nav-header">
                        <hr class="mt-0 mb-3" style="border-color: darkgrey">
                        <strong>KHO</strong>
                    </li>
                    <li class="nav-item">
                        <a th:href="@{${URL_STG_TICKET_IMPORT}}" class="nav-link"><i
                                class="fa-solid fa-cloud-arrow-up fa-rotate-90 mr-2"></i>
                            <p>Nhập hàng</p></a>
                    </li>
                    <li class="nav-item">
                        <a th:href="@{${URL_STG_TICKET_EXPORT}}" class="nav-link"><i
                                class="fa-solid fa-cloud-arrow-up fa-rotate-270 mr-2"></i>
                            <p>Xuất hàng</p></a>
                    </li>
                    <li class="nav-item">
                        <a th:href="@{${URL_STG_STORAGE}}" class="nav-link"><i
                                class="fa-solid fa-warehouse nav-icon mr-2"></i>
                            <p>Kho</p></a>
                    </li>

                    <li class="nav-header">
                        <hr class="mt-0 mb-3" style="border-color: darkgrey">
                        <strong>QUẢN TRỊ HỆ THỐNG</strong>
                    </li>
                    <li class="nav-item">
                        <a th:href="@{${URL_SYS_CONFIG}}" class="nav-link"><i
                                class="fa-solid fa-gear nav-icon mr-2"></i>
                            <p>Cấu hình</p></a>
                    </li>
                    <li class="nav-item">
                        <a th:href="@{${URL_SYS_LOG}}" class="nav-link"><i class="fa-solid fa-clock nav-icon mr-2"></i>
                            <p>Nhật ký hệ thống</p></a>
                    </li>
                    <li class="nav-item">
                        <a th:href="@{${URL_SYS_BRANCH}}" class="nav-link"><i class="fa-solid fa-code-branch nav-icon mr-2"></i>
                            <p>Chi nhánh cửa hàng</p></a>
                    </li>
                    <li class="nav-item">
                        <a href="#" class="nav-link">
                            <i class="fa-solid fa-users nav-icon mr-2"></i>
                            <p>Tài khoản hệ thống<i class="fas fa-angle-left right"></i></p>
                        </a>
                        <ul class="nav nav-treeview">
                            <li class="nav-item">
                                <a th:href="@{${URL_SYS_ACCOUNT}}" class="nav-link"><i class="nav-icon mr-3"></i>
                                    <p>Danh sách người dùng</p></a>
                            </li>
                            <li class="nav-item">
                                <a th:href="@{${URL_SYS_GR_ACCOUNT}}" class="nav-link"><i class="nav-icon mr-3"></i>
                                    <p>Nhóm người dùng</p></a>
                            </li>
                        </ul>
                    </li>
                </ul>
            </nav>
        </div>
    </aside>
</div>
