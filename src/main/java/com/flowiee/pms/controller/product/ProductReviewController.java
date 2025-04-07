package com.flowiee.pms.controller.product;

import com.flowiee.pms.base.BaseController;
import com.flowiee.pms.exception.ResourceNotFoundException;
import com.flowiee.pms.model.AppResponse;
import com.flowiee.pms.model.dto.ProductReviewDTO;
import com.flowiee.pms.service.product.ProductReviewService;
import com.flowiee.pms.common.enumeration.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/product/review")
@Tag(name = "Product review API", description = "Quản lý đánh giá sản phẩm")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductReviewController extends BaseController {
    ProductReviewService mvProductReviewService;

    @Operation(summary = "Find all product reviews")
    @GetMapping
    public AppResponse<List<ProductReviewDTO>> findByProduct(@PathVariable("productId") Long productId) {
        Page<ProductReviewDTO> productReview = mvProductReviewService.findByProduct(productId);
        return success(productReview.getContent(), 1, -1, productReview.getTotalPages(), productReview.getTotalElements());
    }

    @Operation(summary = "Create product review")
    @PostMapping("/create")
    public AppResponse<ProductReviewDTO> createProductReview(@RequestBody ProductReviewDTO productReview) {
        return success(mvProductReviewService.save(productReview));
    }

    @Operation(summary = "Update product review")
    @PutMapping("/update/{reviewId}")
    @PreAuthorize("@vldModuleProduct.updateReview(true)")
    public AppResponse<ProductReviewDTO> updateProductReview(@RequestBody ProductReviewDTO productReview, @PathVariable("reviewId") Long reviewId) {
        if (mvProductReviewService.findById(reviewId, true) == null) {
            throw new ResourceNotFoundException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "productReview"));
        }
        return success(mvProductReviewService.update(productReview, reviewId));
    }

    @Operation(summary = "Delete product review")
    @DeleteMapping("/delete/{reviewId}")
    @PreAuthorize("@vldModuleProduct.deleteReview(true)")
    public AppResponse<String> deleteProductReview(@PathVariable("reviewId") Long reviewId) {
        return success(mvProductReviewService.delete(reviewId));
    }
}