<#macro action path nameAction isSave>
<style>
    img {
        object-fit: cover;
    }
</style>

<div class="form-group row">
    <h1><label class="col-ml-2 col-form-label">${nameAction}</label></h1>
</div>

<div class="row justify-content-center">
    <#if fileError??>
        <div class="alert alert-danger" role="alert">
            ${fileError}
        </div>
    </#if>
</div>

    <div id="carouselExampleControls" class="carousel slide" data-ride="carousel">
        <div class="carousel-inner">
            <#list product.photos as photo>
                <#if photo_index == 0>
                        <div class="carousel-item active">
                            <img class="d-block w-100 img-fluid" src="/img/${photo.name}" alt="${photo_index} slide">
                        </div>
                <#else>
                        <div class="carousel-item">
                            <img class="d-block w-100 img-fluid" src="/img/${photo.name}" alt="${photo_index} slide">
                        </div>
                </#if>
            </#list>
        </div>
        <a class="carousel-control-prev" href="#carouselExampleControls" role="button" data-slide="prev">
            <span class="carousel-control-prev-icon" aria-hidden="true"></span>
            <span class="sr-only">Previous</span>
        </a>
        <a class="carousel-control-next" href="#carouselExampleControls" role="button" data-slide="next">
            <span class="carousel-control-next-icon" aria-hidden="true"></span>
            <span class="sr-only">Next</span>
        </a>
    </div>
<form action="${path}" method="post" enctype="multipart/form-data">
    <img id="output" style="height: 25%; width: 25%"/>
    <div class="form-group">
        <div class="custom-file col-4 col-mb-4 col-mt-4">
            <input type="file" accept="image/jpeg,image/png" name="file" id="customFile" onchange="loadFile(event)" multiple >
            <label class="custom-file-label" for="customFile">Выберите файл</label>
            <#if fileError??>
                <div class="invalid-feedback">
                    ${fileError}
                </div>
            </#if>
        </div>
    </div>

    <div class="form-group row">
        <label class="col-sm-2 col-form-label">Название :</label>
        <div class="col-sm-3">
            <input type="text" name="name" value="<#if product??>${product.name}</#if>"
                   class="form-control small ${(nameError??)?string('is-invalid', '')}"
                   placeholder="Название"/>
            <#if nameError??>
                <div class="invalid-feedback">
                    ${nameError}
                </div>
            </#if>
        </div>
    </div>

    <div class="form-group row">
        <label class="col-sm-2 col-form-label">Цена :</label>
        <div class="col-sm-3">
            <input type="number" step="any" min="0" name="price" value="<#if product??>${product.price}</#if>"
                   class="form-control small ${(priceError??)?string('is-invalid', '')}"
                   placeholder="Цена"/>
            <#if priceError??>
                <div class="invalid-feedback">
                    ${priceError}
                </div>
            </#if>
        </div>
    </div>

    <div class="form-group row">
        <label class="col-sm-2 col-form-label">Количество :</label>
        <div class="col-sm-3">
            <input type="number" step="0" min="0" name="count" value="<#if product??>${product.count}</#if>"
                   class="form-control small ${(countError??)?string('is-invalid', '')}"
                   placeholder="Количество"/>
            <#if countError??>
                <div class="invalid-feedback">
                    ${countError}
                </div>
            </#if>
        </div>
    </div>

    <div class="form-group row">
        <label class="col-sm-2 col-form-label">Скидка %:</label>
        <div class="col-sm-3">
            <input type="number" step="0" min="0" name="discount" value="<#if product??>${product.discount}</#if>"
                   class="form-control small ${(discountError??)?string('is-invalid', '')}"
                   placeholder="Скидка"/>
            <#if discountError??>
                <div class="invalid-feedback">
                    ${discountError}
                </div>
            </#if>
        </div>
    </div>

    <div class="form-group row">
        <label class="col-sm-2 col-form-label">Категория товара :</label>
        <div class="col-sm-3">
            <select class="custom-select" name="categoryProd" id="inputGroupSelect01" required>
                <#list listCategory as category>
                    <option value="${category.id}" <#if (category.name == product.category.name)>selected</#if>>${category.name}</option>
                </#list>
            </select>
        </div>
    </div>


    <div class="form-group row">
        <label for="exampleFormControlSelect2">Выберите имеющиеся размеры : </label>
        <select multiple class="form-control col-4" name="sizeUsersProd" id="exampleFormControlSelect2" required>
            <#list listSizeUser as sizeUser>
                <option value="${sizeUser.id}">${sizeUser.nameSize}</option>
            </#list>
        </select>
    </div>


    <div class="form-group row">
        <label class="col-sm-2 col-form-label">Описание :</label>
        <div class="col-sm-3">
            <textarea name="description" class="form-control small ${(descriptionError??)?string('is-invalid', '')}"
                      placeholder="Описание"><#if product??>${product.description}</#if></textarea>
            <#if descriptionError??>
                <div class="invalid-feedback">
                    ${descriptionError}
                </div>
            </#if>
        </div>
    </div>

    <div class="form-group row">
        <#if isSave>
            <div class="col-sm-1 mr-4"><button type="submit" class="btn btn-primary ml-0">Сохранить</button></div>
        <#else>
            <div class="col-sm-1"><button type="submit" class="btn btn-primary ml-0">Создать продукт</button></div>
        </#if>
        <input type="hidden" value="${_csrf.token}" name="_csrf">
    </div>
</form>
<script>
    var container = document.querySelector('.carousel-inner');
    console.log(container, 'containger');

    var loadFile = function(event) {

        container.innerHTML = '';

        for (i = 0; i < event.target.files.length; i++) {
            var item = document.createElement('div');
            item.classList.add('carousel-item', 'w-100');
            var img = document.createElement('img');
            img.classList.add('d-block');
            img.src = URL.createObjectURL(event.target.files[i]);
            item.appendChild(img);
            container.appendChild(item);
        }

        var imgActive = document.querySelectorAll('.carousel-item')[0];
        imgActive.classList.add('active');
        console.log(imgActive, 'imgActive')
    };
</script>
</#macro>

