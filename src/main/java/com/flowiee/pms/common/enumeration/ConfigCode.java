package com.flowiee.pms.common.enumeration;

import lombok.Getter;

@Getter
public enum ConfigCode {//props.put("mail.smtp.auth", mailConfig.getAuth());props.put("mail.smtp.starttls.enable", mailConfig.getStarttls());
    adminEmailRecipientExceptionNotification(MODULE.SYSTEM, "Email nhận thông báo khi có sự số hệ thống", "vietnd.stg008@gmail.com"),
    allowSellPriceLessThanCostPrice(MODULE.SALES, "Cho phép giá bán ra thấp hơn giá nhập", "Y"),
    allowDuplicateCustomerPhoneNumber(MODULE.SALES, "Cho phép trùng số điện thoại của khách hàng", "N"),
    daySendNotifyBeforeProductExpiry(MODULE.PRODUCT, "Thời gian gửi thông báo trước khi sản phẩm hết hạn sử dụng", "10"),
    dayDeleteSystemLog(MODULE.SYSTEM, "Thời gian xóa nhật ký hệ thống, các nhật ký có thời gian tạo từ >= ? ngày sẽ được xóa tự động", "100"),
    dayDeleteBackupFile(MODULE.SYSTEM, "Thời gian xóa uploaded file backup, các nhật ký có thời gian tạo từ >= ? ngày sẽ được xóa tự động", "30"),
    deleteSystemLog(MODULE.SYSTEM, "Xóa nhật ký hệ thống tự động", "N"),
    deleteBackupFile(MODULE.SYSTEM, "Xóa uploaed file backup tự động", "Y"),
    emailHost(MODULE.SYSTEM, "Email host", "smtp.gmail.com"),
    emailPort(MODULE.SYSTEM, "Email port", "587"),
    emailUser(MODULE.SYSTEM, "Email username", "vietnd.stg008@gmail.com"),
    emailPass(MODULE.SYSTEM, "Email password", "khpsawxcfkntzfbe"),
    emailSmtpAuth(MODULE.SYSTEM, "Email smtp auth", "true"),
    emailSmtpStarttlsEnable(MODULE.SYSTEM, "Email smtp starttls enable ", "true"),
    extensionAllowedFileUpload(MODULE.SYSTEM, "Định dạng file được phép upload", null),
    failLogonCount(MODULE.SYSTEM, "Khóa tài khoản khi đăng nhập sai số lần", "5"),
    forceApplyAccountRightsNoNeedReLogin(MODULE.SYSTEM, "Áp dụng phân quyền mới không cần đăng nhập lại", "N"),
    initData(MODULE.SYSTEM, "Initialize initial data for the system", "Y"),
    lowStockAlert(MODULE.PRODUCT, "Thông báo cảnh báo hàng tồn kho thấp", "N"),
    maxSizeFileUpload(MODULE.SYSTEM, "Dung lượng file tối đa cho phép upload (MB)", "2"),
    resourceVolume(MODULE.SYSTEM, "Resource volume (MB)", "400"),
    resourceUploadPath(MODULE.SYSTEM, "Thư mực chứa tệp upload", null),
    resourceBackupPath(MODULE.SYSTEM, "Thư mực chứa tệp backup", null),
    resourceRestorePath(MODULE.SYSTEM, "Thư mực chứa tệp tải lên để restore", null),
    resourceArchivePath(MODULE.SYSTEM, "Thư mực lưu trữ tệp cũ sau khi restore tệp mới xong", null),
    returnPeriodDays(MODULE.SALES, "Thời gian cho phép đổi trả hàng", "7"),
    sendEmailReportDaily(MODULE.SALES, "Gửi mail báo cáo hoạt động kinh doanh hàng ngày", "N"),
    sendNotifyCustomerOnOrderConfirmation(MODULE.SALES, "Gửi email thông báo đến khách hàng khi đơn hàng đã được xác nhận", "N"),
    sendNotifyAdminExceptionRuntime(MODULE.SYSTEM, "Gửi email thông báo đến Admin khi có sự cố hệ thống", "N"),
    shopAddress(MODULE.SYSTEM, "Địa chỉ", "Phường 7, Quận 8, Thành phố Hồ Chí Minh"),
    shopEmail(MODULE.SYSTEM, "Email", "nguyenducviet0684@gmail.com"),
    shopLogoUrl(MODULE.SYSTEM, "Logo", "src/main/resources/static/dist/img/FlowieeLogo.png"),
    shopName(MODULE.SYSTEM, "Tên cửa hàng", "Flowiee"),
    shopPhoneNumber(MODULE.SYSTEM, "Số điện thoại", "(+84) 706 820 684"),
    sysTimeOut(MODULE.SYSTEM, "Thời gian timeout", "3600"),
    tokenResetPasswordValidityPeriod(MODULE.SYSTEM, "Thông gian hiệu lực của token đổi mật khẩu (phút)", "60"),
    trackRequestLocation(MODULE.SYSTEM, "Track request location", "Y"),
    generateNewPasswordDefault(MODULE.SYSTEM, "Mật khẩu mặc định", "123456"),
    passwordLength(MODULE.SYSTEM, "Độ dài mật khẩu", "8"),
    passwordValidityPeriod(MODULE.SYSTEM, "Thời gian hiệu lực của mật khẩu (ngày)", "30");

    private final MODULE module;
    private final String description;
    private final String defaultValue;

    ConfigCode(MODULE module, String description, String defaultValue) {
        this.module = module;
        this.description = description;
        this.defaultValue = defaultValue;
    }

    public static ConfigCode get(String pConfigCode) {
        try {
            return valueOf(pConfigCode);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}