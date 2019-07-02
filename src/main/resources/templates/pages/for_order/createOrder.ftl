<#import "../../parts/common.ftl" as c>

<@c.page>

    <div class="form-group row">
        <h1 class="mb-3">Положить в корзину</h1>
    </div>

    <div class="row justify-content-center">
        <#if priceError??>
            <div class="alert alert-danger" role="alert">
                ${priceError}
            </div>
        </#if>
    </div>

    <div class="form-group row">
        <label class="col-form-label">Название : <#if product??>${product.name}</#if></label>
    </div>

    <div class="form-group row">
        <label class="col-form-label">Цена : <#if product??>${product.price}</#if></label>
    </div>

    <div class="form-group row">
        <label class="col-form-label">Количество товара : <#if product??>${product.count}</#if></label>
    </div>

<form action="/order/create_order/${product.id}" method="post">
    <div class="form-group row">
        <#--<label class="col-form-label">Адресс получателя : </label>-->
        <#--<div class="col-sm-3">-->
            <#--<input type="text" name="adress" value="<#if order??>${order.adress}</#if>"-->
                   <#--class="form-control small ${(adressError??)?string('is-invalid', '')}"-->
                   <#--placeholder="Адресс получателя"/>-->
            <#--<#if adressError??>-->
                <#--<div class="invalid-feedback">-->
                    <#--${adressError}-->
                <#--</div>-->
            <#--</#if>-->
        <#--</div>-->

        <label class="col-form-label">Количество выбранных товаров : </label>
        <div class="col-sm-3">
            <input type="number" name="count" min="1" step="1" value="<#if count??>${count}</#if>"
                   class="form-control small ${(countError??)?string('is-invalid', '')}"
                   placeholder="Количество товаров"/>
            <#if countError??>
                <div class="invalid-feedback">
                    ${countError}
                </div>
            </#if>
        </div>
    </div>

    <div class="col-sm-4"><button type="submit" class="btn btn-outline-primary">Добавить в корзину</button></div>
    <input type="hidden" name="_csrf" value="${_csrf.token}" />
</form>


</@c.page>