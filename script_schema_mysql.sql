CREATE OR REPLACE VIEW vw_storage_items AS
WITH product_item AS (
    SELECT
        'Y' AS IS_PRODUCT,
        pd.ID,
        CONCAT(CONCAT(fs.DIRECTORY_PATH, '/'), fs.SAVED_NAME) AS ITEM_IMAGE_SRC,
        pd.VARIANT_NAME AS NAME,
        cpt.NAME AS ITEM_TYPE,
        cbr.NAME AS BRAND_NAME,
        pd.QUANTITY_STG - pd.QUANTITY_DEFECTIVE AS AVAILABLE_QTY,
        pd.QUANTITY_STG AS STORAGE_QTY,
        (SELECT CREATED_AT FROM product_detail_temp WHERE PRODUCT_VARIANT_ID = pd.ID ORDER BY CREATED_AT ASC limit 1) AS FIRST_IMPORT_TIME,
        (SELECT CREATED_AT FROM product_detail_temp WHERE PRODUCT_VARIANT_ID = pd.ID ORDER BY CREATED_AT DESC limit 1) AS LAST_IMPORT_TIME,
        ti.STORAGE_ID
    FROM
        product_detail pd
    LEFT JOIN file_storage fs ON
        fs.PRODUCT_VARIANT_ID = pd.ID
        AND fs.IS_ACTIVE = 1
    INNER JOIN product p ON
        p.ID = pd.PRODUCT_ID
    LEFT JOIN category cpt ON
        cpt.ID = p.PRODUCT_TYPE_ID
    LEFT JOIN category cbr ON
        cbr.ID = p.BRAND_ID
    INNER JOIN product_detail_temp pt ON
        pt.PRODUCT_VARIANT_ID = pd.ID
    INNER JOIN ticket_import_goods ti ON
        ti.ID = pt.GOODS_IMPORT_ID
),
material_item AS (
    SELECT
        'N' AS IS_PRODUCT,
        m.ID,
        CONCAT(CONCAT(fs.DIRECTORY_PATH, '/'), fs.SAVED_NAME) AS ITEM_IMAGE_SRC,
        m.NAME,
        '' AS ITEM_TYPE,
        cbr.NAME AS BRAND_NAME,
        0 AS AVAILABLE_QTY,
        m.QUANTITY AS STORAGE_QTY,
        (SELECT CREATED_AT FROM material_temp WHERE MATERIAL_ID = m.ID ORDER BY CREATED_AT ASC limit 1) AS FIRST_IMPORT_TIME,
        (SELECT CREATED_AT FROM material_temp WHERE MATERIAL_ID = m.ID ORDER BY CREATED_AT DESC limit 1) AS LAST_IMPORT_TIME,
        ti.STORAGE_ID
    FROM
        material m
    LEFT JOIN file_storage fs ON
        fs.MATERIAL_ID = m.ID
        AND fs.IS_ACTIVE = 1
    LEFT JOIN category cbr ON
        cbr.ID = m.BRAND_ID
    INNER JOIN material_temp mt ON
        mt.MATERIAL_ID = m.ID
    INNER JOIN ticket_import_goods ti ON
        ti.ID = mt.GOODS_IMPORT_ID
),
storage_item AS (
    SELECT * FROM product_item
    UNION ALL
    SELECT * FROM material_item
)
SELECT DISTINCT
    IS_PRODUCT,
    ID,
    CASE
        WHEN ITEM_IMAGE_SRC = '/' THEN ''
        ELSE ITEM_IMAGE_SRC
        END AS ITEM_IMAGE_SRC,
    NAME,
    ITEM_TYPE,
    BRAND_NAME,
    AVAILABLE_QTY,
    STORAGE_QTY,
    FIRST_IMPORT_TIME,
    LAST_IMPORT_TIME,
    STORAGE_ID
FROM
    storage_item
WHERE
        STORAGE_QTY > 0
ORDER BY
    IS_PRODUCT DESC;

-------- KEEP THIS LINE AS LAST LINE, DO NOT ADD NEW CHANGE AFTER THIS LINE --------