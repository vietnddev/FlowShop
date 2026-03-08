function updateProductDescription() {
    if (confirm("Do you want to update the description?")) {
        let apiURL = mvHostURLCallApi + "/product/description/update/" + mvProductId;
        let body = {
            description : $("#txtProductDescriptionContent").val()
        };
        $.ajax({
            url: apiURL,
            type: "PUT",
            contentType: "application/json",
            data: JSON.stringify(body),
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
    }
}