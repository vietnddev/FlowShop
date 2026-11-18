
let productImages = [];
let selectedImageIndex = -1;

// Reuse image URLs instead of creating new URLs on every render
const imageUrlMap = new WeakMap();

$(document).ready(function () {

    // Add images
    $('#btnAddProductImage').on('click', function () {
        $('#productImageInput')
            .removeData('change-index')
            .click();
    });

    // Change selected image
    $('#btnChangeProductImage').on('click', function () {
        if (selectedImageIndex >= 0) {
            $('#productImageInput')
                .data('change-index', selectedImageIndex)
                .click();
        }
    });

    // Handle selected files
    $('#productImageInput').on('change', function (e) {

        const files = Array.from(e.target.files)
            .filter(file => file.type.startsWith('image/'));

        const changeIndex = $(this).data('change-index');

        if (changeIndex !== undefined) {

            // Replace existing image
            if (files.length > 0) {
                const oldFile = productImages[changeIndex];
                revokeImageUrl(oldFile);

                productImages[changeIndex] = files[0];
                selectedImageIndex = changeIndex;
            }

        } else {

            // Add new images
            productImages.push(...files);

            if (selectedImageIndex === -1 && productImages.length > 0) {
                selectedImageIndex = 0;
            }
        }

        $(this).removeData('change-index');
        $(this).val('');

        renderImages();
    });

    // Select thumbnail
    $('#productImageGallery').on(
        'click',
        '.image-thumbnail-box',
        function (e) {

            if ($(e.target).closest('.btn-remove-image').length) {
                return;
            }

            selectedImageIndex = Number($(this).data('index'));
            showPreview();
        }
    );

    // Remove image
    $('#productImageGallery').on(
        'click',
        '.btn-remove-image',
        function (e) {

            e.preventDefault();
            e.stopPropagation();

            const index = Number(
                $(this).closest('.image-thumbnail-box').data('index')
            );

            const removedFile = productImages[index];
            revokeImageUrl(removedFile);

            productImages.splice(index, 1);

            if (productImages.length === 0) {
                selectedImageIndex = -1;
            } else if (selectedImageIndex === index) {
                selectedImageIndex = Math.min(
                    index,
                    productImages.length - 1
                );
            } else if (selectedImageIndex > index) {
                selectedImageIndex--;
            }

            renderImages();
        }
    );

    renderImages();
});


function getImageUrl(file) {

    if (!imageUrlMap.has(file)) {
        imageUrlMap.set(file, URL.createObjectURL(file));
    }

    return imageUrlMap.get(file);
}


function revokeImageUrl(file) {

    if (file && imageUrlMap.has(file)) {
        URL.revokeObjectURL(imageUrlMap.get(file));
        imageUrlMap.delete(file);
    }
}


function renderImages() {

    const gallery = $('#productImageGallery');

    gallery.find('.image-thumbnail-box').remove();

    $.each(productImages, function (index, file) {

        const imageUrl = getImageUrl(file);

        const box = $(`
            <div class="image-thumbnail-box"
                 data-index="${index}">

                <img src="${imageUrl}" alt="Product image">

                <button type="button"
                        class="btn btn-danger btn-sm btn-remove-image"
                        title="Remove image">
                    <i class="fas fa-times"></i>
                </button>

            </div>
        `);

        if (index === selectedImageIndex) {
            box.addClass('active');
        }

        // Insert before the permanent Add button
        $('#btnAddProductImage').before(box);
    });

    showPreview();
}


function showPreview() {

    if (selectedImageIndex < 0 ||
        selectedImageIndex >= productImages.length) {

        $('#productImagePreview')
            .attr('src', '')
            .addClass('d-none');

        $('#imagePreviewEmpty').removeClass('d-none');
        $('#btnChangeProductImage').addClass('d-none');

        return;
    }

    const file = productImages[selectedImageIndex];

    $('#imagePreviewEmpty').addClass('d-none');

    $('#productImagePreview')
        .attr('src', getImageUrl(file))
        .removeClass('d-none');

    $('#btnChangeProductImage').removeClass('d-none');

    $('.image-thumbnail-box').removeClass('active');

    $('.image-thumbnail-box[data-index="' + selectedImageIndex + '"]')
        .addClass('active');
}


/**
 * Return selected image files when submitting product.
 */
function getProductImages() {
    return productImages;
}
