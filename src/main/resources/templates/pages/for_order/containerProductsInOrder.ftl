<#import "../../parts/common.ftl" as c>

<@c.page>
    <div class="form-group row">
        <h1><label class="col-ml-2 col-form-label">Заказ № ${order.id}</label></h1>
    </div>

    <div class="row justify-content-center">
        <#if priceError??>
            <div class="alert alert-danger" role="alert">
                ${priceError}
            </div>
        </#if>
    </div>

    <table class="table">
        <thead>
        <tr>
            <th scope="col">Название</th>
            <th scope="col">Размер</th>
            <th scope="col">Цена</th>
            <th scope="col">Количество</th>
        </tr>
        </thead>
        <tbody>
        <#if productInCartList??>
            <#list productInCartList as productInCart>
                <tr id="order#{productInCart.id}">
                    <td>
                        <a  href="/product/show_product/#{productInCart.product.id}">${productInCart.product.name}</a>
                    </td>
                    <td>${productInCart.sizeUser.nameSize}</td>
                    <td>${productInCart.product.price}</td>
                    <td>${productInCart.count}</td>
                </tr>
            </#list>
        </#if>
        </tbody>
    </table>

    <div class="form-group row">
        <label class="col-form-label mr-1" >Общая цена : </label>
        <label class="col-form-label mr-1" id="cashUser">${priceOrder}</label>
        <label class="col-form-label">руб</label>
    </div>
</@c.page>