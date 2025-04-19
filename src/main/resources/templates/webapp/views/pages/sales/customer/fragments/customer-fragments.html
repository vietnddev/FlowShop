<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
    <div th:fragment="modalAddNewContact">
        <div class="modal fade" id="modelAddNewContact">
            <div class="modal-dialog">
                <div class="modal-content text-left">
                    <div class="modal-header">
                        <strong class="modal-title">Thêm mới thông tin liên hệ</strong>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <label>Loại</label>
                            <select class="custom-select" name="code" id="contactTypeField" required>
                                <option value="P">Số điện thoại</option>
                                <option value="E">Email</option>
                                <option value="A">Địa chỉ</option>
                            </select>
                        </div>
                        <div class="form-group" id="contactPhoneAndEmailBlock">
                            <label>Giá trị</label>
                            <input type="text" class="form-control" placeholder="Nội dung" name="value"/>
                        </div>
                        <div class="form-group row"
                             id="contactAddressBlock">
                            <div class="col-4">
                                <label>Tỉnh</label>
                                <select class="custom-select" name="code" id="contactAddressProvinceField" required></select>
                            </div>
                            <div class="col-4">
                                <label>Huyện</label>
                                <select class="custom-select" name="code" id="contactAddressDistrictField" required></select>
                            </div>
                            <div class="col-4">
                                <label>Xã</label>
                                <select class="custom-select" name="code" id="contactAddressCommuneField" required></select>
                            </div>
                            <div class="col mt-3">
                                <label>Số nhà, tên đường</label>
                                <input type="text" class="form-control" name="value" placeholder="Số nhà, tên đường"/>
                            </div>
                        </div>
                        <div class="form-group">
                            <label>Ghi chú</label>
                            <input type="text" class="form-control" placeholder="Ghi chú" name="note"/>
                        </div>
                        <div class="form-group text-left">
                            <label>Sử dụng mặc định</label>
                            <input type="checkbox" class="form-control" name="isDefault"/>
                        </div>
                        <div class="form-group">
                            <label>Trạng thái</label>
                            <input type="text" class="form-control" placeholder="Ghi chú" name="note"/>
                        </div>
                    </div>
                    <div class="modal-footer justify-content-end">
                        <input type="hidden" name="customer" th:value="${customerDetail.id}">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-primary">Đồng ý</button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div th:fragment="modalUpdateContact">
        <div class="modal fade" th:id="'update-' + ${c.id}">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form th:action="@{/customer/contact/update/{id}(id=${c.id})}"
                          th:object="${customerContact}"
                          method="post">
                        <div class="modal-header">
                            <strong class="modal-title">Cập nhật thông tin liên hệ</strong>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body">
                            <div class="form-group">
                                <label>Loại</label>
                                <input type="text"
                                       class="form-control"
                                       placeholder="Loại"
                                       name="code"
                                       th:value="${c.code}"
                                       readonly/>
                            </div>
                            <div class="form-group">
                                <label>Nội dung</label>
                                <input type="text"
                                       class="form-control"
                                       placeholder="Nội dung"
                                       name="value"
                                       th:value="${c.value}"/>
                            </div>
                            <div class="form-group">
                                <label>Ghi chú</label>
                                <input type="text"
                                       class="form-control"
                                       placeholder="Ghi chú"
                                       name="note"
                                       th:value="${c.note}"/>
                            </div>
                            <div class="form-group text-left">
                                <label>Sử dụng mặc định</label>
                                <input type="checkbox"
                                       class="form-control"
                                       name="isDefault"
                                       th:value="${c.isDefault}"/>
                            </div>
                            <div class="form-group">
                                <label>Trạng thái</label>
                                <input type="text"
                                       class="form-control"
                                       placeholder="Ghi chú"
                                       name="note"
                                       th:value="${c.note}"/>
                            </div>
                        </div>
                        <div class="modal-footer justify-content-end">
                            <input th:type="hidden" name="customer" th:value="${c.customer.id}">
                            <button type="button" class="btn btn-default" data-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-primary">Đồng ý</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
    <div th:fragment="modalDeleteContact">
        <div class="modal fade" th:id="'delete-' + ${c.id}">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form th:action="@{/customer/contact/delete/{id}(id=${c.id})}"
                          method="post">
                        <div class="modal-header">
                            <strong class="modal-title">Xác nhận
                                xóa thông tin liên hệ</strong>
                            <button type="button" class="close"
                                    data-dismiss="modal"
                                    aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body">
                            Liên hệ <strong
                                class="badge text-bg-info"
                                th:text="${c.value}"
                                style="font-size: 16px;"></strong>
                            sẽ bị xóa vĩnh viễn!
                        </div>
                        <div class="modal-footer justify-content-end">
                            <button type="button"
                                    class="btn btn-default"
                                    data-dismiss="modal">Hủy
                            </button>
                            <button type="submit"
                                    class="btn btn-primary">
                                Đồng ý
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</body>
</html>