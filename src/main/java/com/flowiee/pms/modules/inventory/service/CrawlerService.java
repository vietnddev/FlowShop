package com.flowiee.pms.modules.inventory.service;

import com.flowiee.pms.modules.inventory.entity.*;
import com.flowiee.pms.modules.inventory.repository.*;
import com.flowiee.pms.modules.system.entity.Category;
import com.flowiee.pms.modules.system.entity.SystemLog;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.modules.system.repository.CategoryRepository;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.utils.FileUtils;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.common.security.UserSession;
import com.flowiee.pms.modules.system.service.SystemLogService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class CrawlerService {
    private final ProductCrawlerRepository productCrawlerRepository;
    private final ImageCrawlerRepository imageCrawlerRepository;
    private final ProductRepository productRepository;
    private final ProductDetailRepository productVariantRepository;
    private final CategoryRepository categoryRepository;
    private final ProductPriceRepository productPriceRepository;
    private final ProductPriceService mvProductPriceService;
    private final ProductImageService productImageService;
    private final UserSession userSession;
    private final SystemLogService systemLogService;

    private String baseURL = "https://juno.vn";
    private final String PRODUCT_CODE = "Mã sản phẩm:";
    private final String PRODUCT_TYPE = "Kiểu dáng:";
    private final String FABRIC = "Chất liệu:";
    private final String COLOR = "Màu sắc:";
    private final String SIZE = "Kích cỡ:";
    private final String ORIGINAL_COUNTRY = "Xuất xứ:";

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Transactional
    public void merge() throws AppException {
        logger.info("Merge temp data: begin");

        List<ProductCrawled> productCrawledList = productCrawlerRepository.findAll();
        if (productCrawledList == null) {
            return;
        }
        logger.info("Merge temp data: " + productCrawledList.size() + " record(s)");

        List<Category> fullColorList = categoryRepository.findSubCategory(List.of(CATEGORY.COLOR.name()));
        List<Category> fullSizeList = categoryRepository.findSubCategory(List.of(CATEGORY.SIZE.name()));
        Category brand = categoryRepository.findByTypeAndCode(CATEGORY.BRAND.name(), "FW");
        Category unit = categoryRepository.findByTypeAndCode(CATEGORY.UNIT.name(), "1");

        for (ProductCrawled p : productCrawledList) {
            Category productType = categoryRepository.findByTypeAndName(CATEGORY.PRODUCT_TYPE.name(), CoreUtils.trim(p.getProductType()));
            if (productType == null) {
                productType = getRandomProductType();
            }
            Category fabric = categoryRepository.findByTypeAndName(CATEGORY.FABRIC_TYPE.name(), p.getFabric());
            if (fabric == null) {
                fabric = getRandomFabric();
            }
            BigDecimal originalPrice = p.getOriginalPrice();
            BigDecimal discountPrice = p.getDiscountPrice();

            Product productSaved = productRepository.save(Product.builder()
                    .productCategory(PID.CLOTHES.getId())
                    .productType(productType)
                    .brand(brand)
                    .unit(unit)
                    .productName(CoreUtils.trim(p.getProductName()))
                    .isSaleOff(discountPrice.doubleValue() < originalPrice.doubleValue())
                    //.status(ProductStatus.ACT)
                    .build());

            String[] sizeList = CoreUtils.trim(p.getSize()).split("-");
            List<String> sizeNameSaved = new ArrayList<>();
            int skuIndex = 1;
            for (String size : sizeList) {
                Category sizeMdl = categoryRepository.findByTypeAndCode(CATEGORY.SIZE.name(), size);
                if (sizeMdl == null) {
                    sizeMdl = getRandomSize(fullSizeList, sizeNameSaved);
                }
                String lvSku = p.getProductCode().equals(p.getSku()) ? p.getSku() + skuIndex : p.getSku();

                String[] colorList = CoreUtils.trim(p.getColor()).split("-");
                List<String> colorNameSaved = new ArrayList<>();
                for (String color : colorList) {
                    Category colorMdl = categoryRepository.findByTypeAndName(CATEGORY.COLOR.name(), color);
                    if (colorMdl == null) {
                        colorMdl = getRandomColor(fullColorList, colorNameSaved);
                    }

                    ProductDetail productVariantSaved = productVariantRepository.save(ProductDetail.builder()
                            .product(productSaved)
                            .variantCode(p.getProductCode() + "-" + UUID.randomUUID())
                            .sku(lvSku  + "-" + UUID.randomUUID())
                            .variantName(p.getProductName() + " - " + colorMdl.getName() +  " - " + sizeMdl.getName())
                            .size(sizeMdl)
                            .color(colorMdl)
                            .fabricType(fabric)
                            .storageQty(100)
                            .soldQty(0)
                            .defectiveQty(0)
                            .status(ProductStatus.ACT)
                            .build());

//                    productPriceRepository.save(ProductPrice.builder()
//                            .productVariant(productVariantSaved)
//                            .priceValue(originalPrice)
//                            .appliedValue(discountPrice)
//                            .wholesalePrice(originalPrice)
//                            .wholesalePriceDiscount(discountPrice)
//                            .state(ProductPrice.STATE_ACTIVE)
//                            .build());

                    colorNameSaved.add(colorMdl.getName());
                    skuIndex ++;
                }
                colorNameSaved.add(sizeMdl.getName());
            }

            List<ImageCrawled> imageList = imageCrawlerRepository.findByProductId(p.getId());
            if (CollectionUtils.isNotEmpty(imageList)) {
                for (ImageCrawled img : imageList) {
                    boolean lvIsMainImage = Boolean.TRUE.equals(img.isMainImage());
                    File imgFile = new File(img.getPath());
                    try {
                        MultipartFile imgMultipartFile = FileUtils.convertFileToMultipartFile(imgFile);
                        productImageService.saveImageProduct(imgMultipartFile, productSaved.getId(), lvIsMainImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        systemLogService.writeLogCreate(MODULE.SYSTEM, ACTION.SYS_DATA_MERGE, MasterObject.Master, "Merge data temp into system", SystemLog.EMPTY);
        logger.info("Merge temp data: finish");
    }

    private Category getRandomColor(List<Category> colorList, List<String> colorNameUsed) {
        for (Category c : colorList) {
            if (!colorNameUsed.contains(c)) {
                return c;
            }
        }
        return null;
    }

    private Category getRandomSize(List<Category> sizeList, List<String> sizeNameUsed) {
        for (Category c : sizeList) {
            if (!sizeNameUsed.contains(c)) {
                return c;
            }
        }
        return null;
    }

    private Category getRandomFabric() {
        List<Category> fabricList = categoryRepository.findSubCategory(List.of(CATEGORY.FABRIC_TYPE.name()));
        if (fabricList == null) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(fabricList.size());
        return fabricList.get(randomIndex);
    }

    private Category getRandomProductType() {
        List<Category> productTypeList = categoryRepository.findSubCategory(List.of(CATEGORY.PRODUCT_TYPE.name()));
        if (productTypeList == null) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(productTypeList.size());
        return productTypeList.get(randomIndex);
    }

    @Transactional
    public List<ProductCrawled> crawl() {
        productCrawlerRepository.deleteAll();
        imageCrawlerRepository.deleteAll();

        List<ProductCrawled> productMdlList = new ArrayList<>();
        int pageNum = 1;
        while (true) {
            String url = "https://juno.vn/collections/quan-ao?view=filter&page=" + pageNum + "&sort_by=manual";
            List<ProductCrawled> productMdlSubList = crawl(url);
            if (productMdlSubList.isEmpty()) {
                break;
            }
            productMdlList.addAll(crawl(url));
            pageNum++;
        }
        System.out.println("Total product crawled: " + productMdlList.size());

        for (ProductCrawled p : productMdlList) {
            p.setCreatedTime(LocalDateTime.now());
            p.setCreatedBy(userSession.getUserPrincipal().getUsername());
            ProductCrawled productCrawledSaved = productCrawlerRepository.save(p);

            for (ImageCrawled img : p.getImageCrawledList()) {
                img.setProductId(productCrawledSaved.getId());
            }
            imageCrawlerRepository.saveAll(p.getImageCrawledList());
        }

        systemLogService.writeLogCreate(MODULE.SYSTEM, ACTION.SYS_DATA_CRAWLER, MasterObject.Master, "Crawl data temp", SystemLog.EMPTY);

        return productMdlList;
    }

    private List<ProductCrawled> crawl(String url) {
        List<ProductCrawled> productMdlList = new ArrayList<>();
        try {
            System.out.println("Page URL source: " + url);
            Document doc = Jsoup.connect(url).get();

            Elements elements = doc.select("[id^=product-info-]");
            if (elements == null) {
                return productMdlList;
            }

            for (Element element : elements) {
                System.out.println("Found product: " + element.id());

                String detailURL = baseURL + element.text();
                System.out.println("Product URL" + detailURL);
                Document docDetail = Jsoup.connect(detailURL).get();

                productMdlList.add(getDetail(docDetail));
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return productMdlList;
    }

    private ProductCrawled getDetail(Document docDetail) {
        Elements productTitleNode = docDetail.getElementsByClass("product-title hidden-xs");
        String lvProductName = productTitleNode.get(0).getElementsByTag("h1").text();

        Elements skuNode = productTitleNode.get(0).getElementsByClass("skuCode");
        String lvSku = skuNode.get(0).getElementsByTag("b").text();

        Element productPriceNode = docDetail.getElementById("price-preview");
        String lvOriginalPrice = productPriceNode.getElementsByTag("del").text();
        String lvDiscountPrice = productPriceNode.getElementsByTag("span").text();
        if (CoreUtils.isNullStr(lvOriginalPrice)) {//case have not discount price
            lvOriginalPrice = lvDiscountPrice;
        }

        Element descriptionNode = docDetail.getElementById("1b");//tab-pane
        Element detailInfoNode = docDetail.getElementById("2b");//tab-pane active

        Elements ulDetailTab = detailInfoNode.getElementsByTag("ul");

        Map<String, String> fieldMap = new HashedMap();
        for (Element e : ulDetailTab.get(0).getElementsByTag("li")) {
            if (e.getElementsByClass("infobe") == null) {
                continue;
            }
            String label = e.getElementsByClass("infobe").text();
            String value = e.getElementsByClass("infoaf").text();
            fieldMap.put(label, value);
        }

        String lvProductCode = fieldMap.get(PRODUCT_CODE);
        String lvProductType = fieldMap.get(PRODUCT_TYPE);
        String lvFabric = fieldMap.get(FABRIC);
        String lvColor = fieldMap.get(COLOR);
        String lvSize = fieldMap.get(SIZE);
        String lvOriginalCountry = fieldMap.get(ORIGINAL_COUNTRY);


        List<ImageCrawled> imageCrawledList = new ArrayList<>();
        Elements imageNode = docDetail.getElementsByClass("removeImgMobile");
        if (imageNode != null) {
            for (int i = 0; i < imageNode.size(); i++) {
                Element imageTag = imageNode.get(i);
                if (imageTag == null) {
                    break;
                }
                String imageSrc = imageTag.attr("src");
                if (imageSrc == null) {
                    break;
                }
                try {
                    ImageCrawled imageCrawled = downloadImage("https:" + imageSrc, FileUtils.getImageTempPath());
                    imageCrawled.setMainImage(i == 0 ? true : false);
                    imageCrawledList.add(imageCrawled);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return ProductCrawled.builder()
                .sku(lvSku)
                .productCode(lvProductCode)
                .productName(lvProductName)
                .productType(lvProductType)
                .fabric(lvFabric)
                .color(lvColor)
                .size(lvSize)
                .originCountry(lvOriginalCountry)
                .originalPrice(parseBigDecimal(lvOriginalPrice))
                .discountPrice(parseBigDecimal(lvDiscountPrice))
                .imageCrawledList(imageCrawledList)
                .build();
    }

    private ImageCrawled downloadImage(String imageUrl, String outputDir) throws IOException {
        Path outputDirPath = Paths.get(outputDir);
        if (!Files.exists(outputDirPath)) {
            Files.createDirectories(outputDirPath);
        }
        System.out.println("Found image " + imageUrl);

        // Lấy tên file từ URL
        String fileName = UUID.randomUUID() + "_" + imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

        // Đường dẫn lưu file
        Path outputPath = Paths.get(outputDir, fileName);

        // Đọc dữ liệu từ URL và lưu vào file
        try (InputStream in = new URL(imageUrl).openStream()) {
            Files.copy(in, outputPath);
        }

        String lvPath = outputPath.toAbsolutePath().toString();

        return ImageCrawled.builder()
                .path(lvPath)
                .build();
    }

    private BigDecimal parseBigDecimal(String currencyString) {
        if (currencyString == null || currencyString.isEmpty()) {
            throw new IllegalArgumentException("Currency string cannot be null or empty");
        }
        // Loại bỏ ký tự không phải số và dấu phân cách
        String cleanedString = currencyString.replaceAll("[^\\d.]", "");
        // Chuyển đổi sang BigDecimal
        return new BigDecimal(cleanedString);
    }
}