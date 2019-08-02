<#import "../../parts/common.ftl" as c>

<@c.page>

    <div class="form-group row">
        <h1 class="mb-3">${Add_to_cart_message}</h1>
    </div>

    <div class="row justify-content-center">
        <#if priceError??>
            <div class="alert alert-danger" role="alert">
                ${priceError}
            </div>
        </#if>
    </div>

    <div class="form-group row">
        <label class="col-form-label">${Name_message} : <#if product??>${product.name}</#if></label>
    </div>

    <div class="form-group row">
        <label class="col-form-label">${Price_message} : <#if product??>${product.price}</#if></label>
    </div>

    <div class="form-group row">
        <label class="col-form-label">Имеющиеся размеры и кол. товаров </br>
            <#list productNotZero as prodSize>
                ${prodSize.sizeUser.nameSize} : ${prodSize.count}
            </#list>
        </label>
    </div>

    <#if productNotZero?has_content>
        <form action="/order/add_product_in_cart/${product.id}" method="post">

            <div class="form-group row">
                <label class="col-form-label">Выберите размер : </label>
                    <select class="custom-select col-sm-4" name="sizeProd" required>
                        <#list productNotZero as prodSize>
                            <option value="${prodSize.sizeUser.id}"
                                    <#if selProd??><#if selProd.id == prodSize.sizeUser.id>selected</#if></#if>>
                                ${prodSize.sizeUser.nameSize}
                            </option>
                        </#list>
                    </select>
            </div>

            <div class="form-group row" id="countProduct">

                <label class="col-form-label">${Number_of_selected_items_message} : </label>
                <div class="col-sm-3">
                    <input type="number" name="count" min="1" step="1" value="<#if count??>${count}</#if>"
                           class="form-control small ${(countError??)?string('is-invalid', '')}"
                           placeholder="${Amount_of_goods}"/>
                    <#if countError??>
                        <div class="invalid-feedback">
                            ${countError}
                        </div>
                    </#if>
                </div>
            </div>

            <div class="col-sm-4"><button type="submit" class="btn btn-outline-primary">${Throw_to_cart_message}</button></div>
            <input type="hidden" name="_csrf" value="${_csrf.token}" />
        </form>
    <#else>
        <div class="form-group row">
            <label class="col-form-label">Товара в наличии нет.</label>
        </div>
    </#if>

</@c.page>