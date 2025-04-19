function changeImage() {
    $(document).on("click", ".btn-change-image", function () {
        let image = mvImagesOfProduct[$(this).attr("imageId")];
        $("#btnChangeImageSubmit").attr("imageId", image.id);
        $("#modalChangeImage").modal();
    })

    $("#btnChangeImageSubmit").on("click", function () {
        let apiURL = mvHostURLCallApi + "/product/" + mvProductId + "/change-image/" + $(this).attr("imageId");
        let file = $("#imageToChange")[0].files[0];
        let formData = new FormData();
        formData.append("file", file);
        $.ajax({
            url: apiURL,
            type: "PUT",
            data: formData,
            processData: false,  // Không xử lý dữ liệu
            contentType: false,  // Không đặt kiểu dữ liệu
            success: function(response) {
                if (response.status === "OK") {
                    alert("Change successfully!");
                    window.location.reload();
                }
            },
            error: function (xhr) {
                alert("Error: " + $.parseJSON(xhr.responseText).message);
            }
        });
    })
}

function activeImage() {
    $(document).on("click", ".btn-active-image", function () {
        let image = mvImagesOfProduct[$(this).attr("imageId")];
        $("#btnActiveImageSubmit").attr("imageId", image.id);
        $("#modalActiveImage").modal();
    })

    $("#btnActiveImageSubmit").on("click", function () {
        let apiURL = mvHostURLCallApi + "/product/" + mvProductId + "/active-image/" + $(this).attr("imageId");
        $.ajax({
            url: apiURL,
            type: "PUT",
            success: function (response) {
                if (response.status === "OK") {
                    alert("Update successfully!");
                    window.location.reload();
                }
            },
            error: function (xhr) {
                alert("Error: " + $.parseJSON(xhr.responseText).message);
            }
        });
    })
}

function loadImageInfoOnForm(subImage) {
    $(document).on("click", ".sub-image", function () {
        subImage = mvImagesOfProduct[$(this).attr("imageId")];
        $("#imageNameField").val(subImage.name);
        $("#imageSizeField").val(subImage.size);
        $("#imageOriginalNameField").val(subImage.originalName);
        $("#imageUploadByField").val(subImage.uploadByStr);
        $("#imageUploadAtField").val(subImage.uploadAt);
        $("#imageStatusField").val(subImage.isActive);
    })
    if (subImage != null) {
        $("#imageNameField").val(subImage.name);
        $("#imageSizeField").val(subImage.size);
        $("#imageOriginalNameField").val(subImage.originalName);
        $("#imageUploadByField").val(subImage.uploadByStr);
        $("#imageUploadAtField").val(subImage.uploadAt);
        $("#imageStatusField").val(subImage.isActive);
    }
}

function loadImagesOfProduct() {
    $("#custom-tabs-three-images-tab").on("click", function () {
        let apiURL = mvHostURLCallApi + '/product/' + mvProductId + '/images';
        $.get(apiURL, function (response) {
            if (response.status === "OK") {
                let data = response.data;
                let gridSubImages = $("#gridSubImages");
                mvImagesOfProduct = [];
                $.each(data, function (index, d) {
                    mvImagesOfProduct[d.id] = d;
                    let classCard;
                    let styleCard;
                    if (d.isActive) {
                        mvImageActive = d;
                        loadImageInfoOnForm(mvImageActive);
                        $("#mainImage").append(`<img class="product-image" src="/${mvImageActive.src}" alt="Product Image" style="width: 100%; border-radius: 5px; margin: auto" id="imageActive">`);
                        classCard = "card border border-primary";
                        styleCard = "height: 186px; background-color:aliceblue";
                    } else {
                        classCard = "card border";
                        styleCard = "height: 186px"
                    }
                    gridSubImages.append(`
                                <div class="col-2">
                                    <div class="${classCard}" style="${styleCard}">
                                        <div class="card-body product-image-thumb" style="margin: auto">
                                            <img src="/${d.src}" alt="Product Image" class="sub-image" imageId="${d.id}">
                                        </div>
                                        <div class="card-footer row">
                                            <i style="cursor: pointer" imageId="${d.id}" class="fa-solid fa-arrows-rotate text-info col btn-change-image"></i>
                                            <i style="cursor: pointer" imageId="${d.id}" class="fa-regular fa-circle-check col btn-active-image"></i>
                                            <i style="cursor: pointer" imageId="${d.id}" class="fa-solid fa-trash text-danger col btn-delete-image"></i>
                                        </div>
                                    </div>
                                </div>
                            `);
                })
            }
        }).fail(function () {
            showErrorModal("Could not connect to the server");
        });
    })
}