CREATE OR REPLACE VIEW vw_storage_items AS
WITH product_item AS (
    SELECT
        'Y' AS IS_PRODUCT,
        pd.ID,
        pd.VARIANT_NAME AS NAME,
        cpt.NAME AS ITEM_TYPE,
        cbr.NAME AS BRAND_NAME,
        pd.QUANTITY_STG - pd.QUANTITY_DEFECTIVE AS AVAILABLE_QTY,
        pd.QUANTITY_STG AS STORAGE_QTY,
        (SELECT CREATED_AT FROM product_detail_temp WHERE PRODUCT_VARIANT_ID = pd.ID ORDER BY CREATED_AT ASC limit 1) AS FIRST_IMPORT_TIME,
        (SELECT CREATED_AT FROM product_detail_temp WHERE PRODUCT_VARIANT_ID = pd.ID ORDER BY CREATED_AT DESC limit 1) AS LAST_IMPORT_TIME,
        tg.warehouse_id as STORAGE_ID
    FROM
        product_detail pd
    INNER JOIN product p ON
        p.ID = pd.PRODUCT_ID
    LEFT JOIN category cpt ON
        cpt.ID = p.PRODUCT_TYPE_ID
    LEFT JOIN category cbr ON
        cbr.ID = p.BRAND_ID
    LEFT JOIN transaction_goods_item ti ON
        pd.ID = ti.product_variant_id
    LEFT JOIN transaction_goods tg ON
     	ti.transaction_goods_id = tg.id
),
material_item AS (
    SELECT
        'N' AS IS_PRODUCT,
        m.ID,
        m.NAME,
        '' AS ITEM_TYPE,
        cbr.NAME AS BRAND_NAME,
        0 AS AVAILABLE_QTY,
        m.QUANTITY AS STORAGE_QTY,
        (SELECT CREATED_AT FROM material_temp WHERE MATERIAL_ID = m.ID ORDER BY CREATED_AT ASC limit 1) AS FIRST_IMPORT_TIME,
        (SELECT CREATED_AT FROM material_temp WHERE MATERIAL_ID = m.ID ORDER BY CREATED_AT DESC limit 1) AS LAST_IMPORT_TIME,
        tg.warehouse_id as STORAGE_ID
    FROM
        material m
    LEFT JOIN category cbr ON
        cbr.ID = m.BRAND_ID
    LEFT JOIN transaction_goods_item ti ON
        m.ID = ti.material_id
    LEFT JOIN transaction_goods tg ON
       ti.transaction_goods_id = tg.id
),
storage_item AS (
    SELECT * FROM product_item
    UNION ALL
    SELECT * FROM material_item
)
SELECT DISTINCT
    IS_PRODUCT,
    ID,
    '\ ' AS ITEM_IMAGE_SRC,
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
ORDER BY
    IS_PRODUCT DESC;

-------- KEEP THIS LINE AS LAST LINE, DO NOT ADD NEW CHANGE AFTER THIS LINE --------