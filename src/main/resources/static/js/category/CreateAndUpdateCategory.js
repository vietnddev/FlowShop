function preCreateCategory() {
    $(document).on("click", ".btn-insert", function () {
        $("#modal_insert_update_title").text("Thêm mới danh mục hệ thống");
        mvCode.val("");
        mvName.val("");
        mvNote.val("");
        mvSort.val("");
        mvStatus.append('<option value="true">Sử dụng</option><option value="false">Không sử dụng</option>');
        $("#btn-insert-update-submit").attr("actionType", "insert");
        $("#modal_insert_update").modal();
    });
}

function preUpdateCategory() {
    $(document).on("click", ".btn-update", function () {
        let category = mvCategories[$(this).attr("id")];
        $("#modal_insert_update_title").text("Cập nhật danh mục hệ thống");
        mvId = category.id;
        mvCode.val(category.code);
        mvName.val(category.name);
        mvNote.val(category.note);
        mvSort.val(category.sort);
        if (category.status === true) {
            mvStatus.append('<option value="true">Sử dụng</option><option value="false">Không sử dụng</option>');
        } else {
            mvStatus.append('<option value="false">Không sử dụng</option><option value="true">Sử dụng</option>');
        }
        $("#btn-insert-update-submit").attr("actionType", "update");
        $("#modal_insert_update").modal();
    });
}

function submitInsertOrUpdate() {
    $("#btn-insert-update-submit").on("click", function () {
        let actionType = $(this).attr("actionType");
        let category = {
            id : mvId,
            type : mvType,
            code : mvCode.val(),
            name : mvName.val(),
            sort : mvSort.val(),
            isDefault : 0,
            note : mvNote.val(),
            status: mvStatus.val()
        };
        console.log(category)
        if (actionType === "insert") {
            $.ajax({
                url: mvHostURLCallApi + "/category/create",
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify(category),
                success: function(response, a, b) {
                    if (response.status === "OK") {
                        alert("Create successfully!");
                        window.location.reload();
                    }
                },
                error: function (xhr) {
                    alert("Error: " + $.parseJSON(xhr.responseText).message);
                }
            });
        }
        if (actionType === "update") {
            $.ajax({
                url: mvHostURLCallApi + "/category/update/" + mvId,
                type: "PUT",
                contentType: "application/json",
                data: JSON.stringify(category),
                success: function(response) {
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
    });
}