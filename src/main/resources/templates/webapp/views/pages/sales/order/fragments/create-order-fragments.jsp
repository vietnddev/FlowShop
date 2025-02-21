<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
    <div th:fragment="modalUpdateItem">
        <div class="modal fade" th:id="'modalUpdateItems_' + ${item.id}">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form th:action="@{/sls/order/ban-hang/cart/item/update/{itemId}(itemId=${item.id})}"
                          th:object="${items}" method="POST">
                        <div class="modal-header">
                            <strong class="modal-title">Cập nhật sản phẩm</strong>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="cartId" th:value="${cart.id}">
                            <input type="hidden" name="id"     th:value="${item.id}">
                            <input type="hidden" name="productDetail" th:value="${item.productDetail.id}">
                            <div class="form-group row" style="display: flex; align-items: center; margin: 0 0 15px 0">
                                <label class="col-sm-4 text-left">Số lượng</label>
                                <input class="col-sm-8 form-control"
                                       type="number" name="quantity"
                                       min="1" th:max="${item.productDetail.availableSalesQty}"
                                       th:value="${item.quantity}"/>
                            </div>
                            <div class="form-group row" style="display: flex; align-items: center; margin: 0 0 15px 0">
                                <label class="col-sm-4 text-left">Giảm thêm</label>
                                <input class="col-sm-8 form-control" name="extraDiscount" th:value="${item.extraDiscount}"/>
                            </div>
                            <div class="form-group row" style="display: flex; align-items: center; margin: 0 0 15px 0">
                                <label class="col-sm-4 text-left">Loại giá</label>
                                <select th:if="${item.priceType == 'L'}" class="col-8 custom-select" name="priceType" required>
                                    <option value="L">Giá lẻ</option>
                                    <option value="S">Giá sỉ</option>
                                </select>
                                <select th:if="${item.priceType == 'S'}" class="col-8 custom-select" name="priceType" required>
                                    <option value="S">Giá sỉ</option>
                                    <option value="L">Giá lẻ</option>
                                </select>
                            </div>
                            <div class="form-group row" style="display: flex; align-items: center; margin: 0">
                                <label class="col-sm-4 text-left">Ghi chú</label>
                                <textarea class="col-sm-8 form-control" id="note" name="note" th:text="${item.note}"></textarea>
                            </div>
                        </div>
                        <div class="modal-footer justify-content-end">
                            <button type="button" class="btn btn-sm btn-default" data-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-sm btn-primary">Đồng ý</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <div th:fragment="modalDeleteItem">
        <div class="modal fade" th:id="'modalDeleteItems_' + ${item.id}">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form th:action="@{/sls/order/ban-hang/cart/item/delete/{itemId}(itemId=${item.id})}" method="POST">
                        <div class="modal-header">
                            <strong class="modal-title">Cập nhật giỏ hàng</strong>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="cartId" th:value="${cart.id}">
                            <input type="hidden" name="itemId" th:value="${item.id}">
                            Xác nhận xóa sản phẩm
                            <span class="badge badge-info" th:text="${item.productDetail.variantName}"></span>
                            khỏi giỏ hàng!
                        </div>
                        <div class="modal-footer justify-content-end">
                            <button type="button" class="btn btn-sm btn-default" data-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-sm btn-primary">Đồng ý</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <div th:fragment="modalClearCart">
        <div class="modal fade" th:id="'modalDeleteCart_' + ${cart.id}">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form th:action="@{/sls/order/ban-hang/cart/{id}/reset(id=${cart.id})}"
                          method="POST">
                        <div class="modal-header">
                            <strong class="modal-title">Thông báo xác nhận</strong>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" name="id" th:value="${cart.id}">Xác nhận reset giỏ hàng
                        </div>
                        <div class="modal-footer justify-content-end">
                            <button type="button" class="btn btn-sm btn-default" data-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-sm btn-primary">Đồng ý</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</body>
</html>