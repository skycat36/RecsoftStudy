<#macro action path nameAction isSave>
<div class="form-group row">
    <h1><label class="col-ml-2 col-form-label">${nameAction}</label></h1>
</div>
<form action="${path}" method="post" enctype="multipart/form-data">

    <div class="form-group">
        <div class="custom-file">
            <input type="file" name="file" id="customFile">
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
                    <option value="${category.id}" <#if product??><#if product.category.name == category.name>selected</#if></#if>>${category.name}</option>
                </#list>
            </select>
        </div>
    </div>


    <div class="form-group row">
        <label for="exampleFormControlSelect2">Выберите имеющиеся размеры</label>
        <select multiple class="form-control" name="sizeUsersProd" id="exampleFormControlSelect2" required>
            <#list listSizeUser as sizeUser>
                <option value="${sizeUser.id}" <#if product??><#if product.sizeUsers.nameSize == sizeUser.nameSize>selected</#if></#if>>${sizeUser.nameSize}</option>
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
</#macro>