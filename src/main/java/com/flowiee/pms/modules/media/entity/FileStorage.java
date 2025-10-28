package com.flowiee.pms.modules.media.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;

import com.flowiee.pms.modules.inventory.entity.*;
import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.modules.staff.entity.Account;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.utils.FileUtils;
import lombok.*;

import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

@Builder
@Entity
@Table(name = "file_storage",
       indexes = {@Index(name = "idx_FileStorage_productId", columnList = "product_id"),
                  @Index(name = "idx_FileStorage_productVariantId", columnList = "product_variant_id")})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileStorage extends BaseEntity implements Serializable {
    @Serial
	static final long serialVersionUID = 1L;

    public static final String QRCODE = "Q";
    public static final String BARCODE = "B";
    public static final String DOCUMENT = "D";
    public static final String IMAGE = "I";

	@Column(name = "customize_name")
    String customizeName;

    @Column(name = "saved_name", nullable = false)
    String storageName;

    @Column(name = "original_name", nullable = false)
    String originalName;

    @Column(name = "note")
    String note;

    @Column(name = "extension", length = 10)
    String extension;

    @Column(name = "content_type", length = 100)
    String contentType;

    @Column(name = "file_size")
    long fileSize;

    @Column(name = "content")
    byte[] content;

    @Column(name = "content_base_64")
    String contentBase64;

    @Column(name = "directory_path", length = 500)
    String directoryPath;

    @Column(name = "sort")
    Integer sort;

    @Column(name = "status", nullable = false)
    boolean status;

    @Column(name = "module", nullable = false)
    String module;

    @Column(name = "file_type")
    String fileType;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_variant_id")
    ProductDetail productDetail;

    @ManyToOne
    @JoinColumn(name = "material_id")
    Material material;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "order_id")
    Order order;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "upload_by", nullable = false)
    Account uploadBy;

    @ManyToOne
    @JoinColumn(name = "transaction_goods_import_id")
    TransactionGoods transactionGoodsImport;

    @ManyToOne
    @JoinColumn(name = "transaction_goods_export_id")
    TransactionGoods transactionGoodsExport;

    @ManyToOne
    @JoinColumn(name = "product_combo_id")
    ProductCombo productCombo;

    @ManyToOne
    @JoinColumn(name = "product_damaged_id")
    ProductDamaged productDamaged;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    Account account;

    @Column(name = "is_active", nullable = false)
    boolean isActive;

    @Transient
    String src;

    @JsonIgnore
    @Transient
    MultipartFile fileAttach;

    public FileStorage(MultipartFile file, String pModule, Long productId) {
        try {
            this.module = pModule;
            this.extension = FileUtils.getFileExtension(file.getOriginalFilename());
            this.originalName = file.getOriginalFilename();
            this.customizeName = file.getOriginalFilename();
            this.storageName = FileUtils.genRandomFileName() + "." + this.extension;
            this.fileSize = file.getSize();
            this.contentType = file.getContentType();
            this.directoryPath = CommonUtils.getPathDirectory(pModule).substring(CommonUtils.getPathDirectory(pModule).indexOf("uploads"));
            this.uploadBy = new Account(CommonUtils.getUserPrincipal().getId());
            if (productId != null) {
                this.product = new Product(productId);
            }
            this.isActive = false;
            this.fileAttach = file;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Override
	public String toString() {
		return "FileStorage [id=" + super.id + ", customizeName=" + customizeName + ", storageName=" + storageName
				+ ", originalName=" + originalName + ", ghiChu=" + note + ", extension=" + extension + ", contentType="
				+ contentType + ", fileSize=" + fileSize + ", content=" + Arrays.toString(content)
				+ ", directoryPath=" + directoryPath + ", sort=" + sort + ", status=" + status + ", module=" + module
				//+ ", product=" + product + ", productVariant=" + productDetail + ", account=" + account
                + ", isActive=" + isActive + "]";
	}        
}