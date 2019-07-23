<#macro action path isSave>
<style>
    img {
        object-fit: cover;
    }
</style>

<div class="form-group row">
    <h1><label class="col-ml-2 col-form-label">${nameAction_message}</label></h1>
</div>

    <div class="row justify-content-center">
        <#if messageError??>
            <div class="alert alert-danger" role="alert">
                ${messageError}
            </div>
        </#if>
    </div>

    <div id="carouselExampleControls" class="carousel slide" data-ride="carousel">
        <div class="carousel-inner">
            <#if product??>
                <#if product.photoProducts??>
                    <#list product.photoProducts as photoProduct>
                        <#if photoProduct_index == 0>
                                <div class="carousel-item active">
                                    <img class="d-block w-100 img-fluid" src="/img/${photoProduct.name}" alt="${photoProduct_index} slide">
                                </div>
                        <#else>
                                <div class="carousel-item">
                                    <img class="d-block w-100 img-fluid" src="/img/${photoProduct.name}" alt="${photoProduct_index} slide">
                                </div>
                        </#if>
                    </#list>
                </#if>
            </#if>
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
            <label class="custom-file-label" for="customFile">${Selected_file_message}</label>
        </div>
    </div>

    <div class="form-group row">
        <label class="col-sm-2 col-form-label">${Name_message} : </label>
        <div class="col-sm-3">
            <input type="text" name="name" value="<#if product??><#if product.name??>${product.name}</#if></#if>"
                   class="form-control small ${(nameError??)?string('is-invalid', '')}"
                   placeholder="${Name_message}"/>
            <#if nameError??>
                <div class="invalid-feedback">
                    ${nameError}
                </div>
            </#if>
        </div>
    </div>

    <div class="form-group row">
        <label class="col-sm-2 col-form-label">${Price_message} : </label>
        <div class="col-sm-3">
            <input type="number" step="any" min="0"  name="price" value="<#if price??>${price}<#else>0</#if>"
                   class="form-control small ${(priceError??)?string('is-invalid', '')}"
                   placeholder="${Price_message}"/>
            <#if priceError??>
                <div class="invalid-feedback">
                    ${priceError}
                </div>
            </#if>
        </div>
    </div>

    <div class="form-group row">
        <label class="col-sm-2 col-form-label">${Number_message} : </label>
        <div class="col-sm-3">
            <input type="number" step="0" min="0" name="count" value="<#if product??><#if product.count??>${product.count}</#if></#if>"
                   class="form-control small ${(countError??)?string('is-invalid', '')}"
                   placeholder="${Number_message}"/>
            <#if countError??>
                <div class="invalid-feedback">
                    ${countError}
                </div>
            </#if>
        </div>
    </div>

    <div class="form-group row">
        <label class="col-sm-2 col-form-label">${Discount_message} %: </label>
        <div class="col-sm-3">
            <input type="number" step="0" min="0" name="discount" value="<#if product??><#if product.discount??>${product.discount}</#if></#if>"
                   class="form-control small ${(discountError??)?string('is-invalid', '')}"
                   placeholder="${Discount_message}"/>
            <#if discountError??>
                <div class="invalid-feedback">
                    ${discountError}
                </div>
            </#if>
        </div>
    </div>

    <div class="form-group row">
        <label class="col-sm-2 col-form-label">${Product_category_message} : </label>
        <div class="col-sm-3">
            <select class="custom-select" name="categoryProd" id="inputGroupSelect01" required>
                <#list listCategory as category>
                    <option value="${category.id}"
                        <#if categoryProd??><#if category.id == categoryProd>selected</#if></#if>>
                            ${category.name}
                    </option>
                </#list>
            </select>
            <#if categoryProdError??>
                <div class="invalid-feedback">
                    ${categoryProdError}
                </div>
            </#if>
        </div>
    </div>

    <div class="form-group row">
        <label for="exampleFormControlSelect2 mr-2">${Choose_available_sizes_message} : </label>
        <select multiple class="form-control col-4" name="sizeUsersProd" id="exampleFormControlSelect2" required>
            <#list listSizeUser as sUser>
                <option value="${sUser.id}"
                <#if sizeUsersProd??><#list sizeUsersProd as sizeNum><#if sizeNum == sUser.id>selected</#if></#list></#if>>
                ${sUser.nameSize}
            </#list>
        </select>

    </div>
    <#if sizeUsersProdError??>
        <div class="invalid-feedback">
            ${sizeUsersProdError}
        </div>
    </#if>

    <div class="form-group row">
        <label class="col-sm-2 col-form-label">${Description_message} : </label>
        <div class="col-sm-3">
            <textarea name="description" class="form-control small ${(descriptionError??)?string('is-invalid', '')}"
                      placeholder="${Description_message}"><#if product??><#if product.description??>${product.description}</#if></#if></textarea>
            <#if descriptionError??>
                <div class="invalid-feedback">
                    ${descriptionError}
                </div>
            </#if>
        </div>
    </div>

    <div class="form-group row">
        <#if isSave>
            <div class="col-sm-1 mr-4"><button type="submit" class="btn btn-primary ml-0">${Save_message}</button></div>
        <#else>
            <div class="col-sm-1"><button type="submit" class="btn btn-primary ml-0">${nameAction_message}</button></div>
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

