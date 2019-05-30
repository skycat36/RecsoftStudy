<#import "../../parts/common.ftl" as c>
<#import "../../parts/actionWithProduct.ftl" as aww>

<@c.page>
    <div class="form-group row">
        <h1><label class="col-ml-2 col-form-label"><#if product??>${product.name}</#if></label></h1>
    </div>

<#--<div class="row justify-content-center">-->
    <#--<#if fileError??>-->
        <#--<div class="alert alert-danger" role="alert">-->
            <#--${fileError}-->
        <#--</div>-->
    <#--</#if>-->
<#--</div>-->

    <div id="carouselExampleControls" class="carousel slide" data-ride="carousel">
        <div class="carousel-inner" >
        <#--<div class="carousel-item active">-->
        <#--<img class="d-block w-100" id="img_0" alt="First slide">-->
        <#--</div>-->
        <#--<div class="carousel-item">-->
        <#--<img class="d-block w-100" id="img_1" alt="Second slide">-->
        <#--</div>-->
        <#--<div class="carousel-item">-->
        <#--<img class="d-block w-100" id="img_2" alt="Third slide">-->
        <#--</div>-->
        <#--<div class="carousel-item">-->
        <#--<img class="d-block w-100" id="img_3" alt="Forth slide">-->
        <#--</div>-->
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
                    <option value="${category.id}">${category.name}</option>
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
</@c.page>