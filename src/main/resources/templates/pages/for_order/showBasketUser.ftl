<#import "../../parts/common.ftl" as c>

<@c.page>

    <div class="row justify-content-center">
            <#if priceError??>
                <div class="alert alert-danger" role="alert">
                    ${priceError}
                </div>
            </#if>
    </div>

    <div class="form-group row">
        <label class="col-form-label">Кошелек : <#if user??>${user.cash}</#if> руб</label>
    </div>
<form>
    <table class="table">
        <thead>
        <tr>
            <th scope="col">Название</th>
            <th scope="col">Адресс</th>
            <th scope="col">Цена за шт.</th>
            <th scope="col">Количество</th>
            <th scope="col">Статус заказа</th>
            <th scope="col"></th>
        </tr>
        </thead>
        <tbody>

        <#list orderList as order>
            <tr>
                <td>
                    <a href="/product/show_product/${order.product.id}">${order.product.name}</a></td>
                <td>${order.adress}</td>
                <td>${order.product.price}</td>
                <td><div class="col-sm-4"><input type="number" step="0" min="0"  max="${order.product.count + order.count}" name="count_p[]" value="${order.count}"></div></td>
                <td>${listReadbleStatus[order_index]}</td>
                <td>
                    <div class="col-sm-4"><button type="submit" formmethod="post" formaction="/order/basket/delete/${order.id}" class="btn btn-outline-primary">Отменить заказ</div>

                </td>
            </tr>
        </#list>

        </tbody>
    </table>

    <#if priceUser != 0 >
        <div class="row justify-content-center">
            <button type="submit" formmethod="post" formaction="/order/basket/update" class=" col-sm-4 btn btn-outline-primary">Обновить данные</button>
            <button type="submit" formmethod="post" formaction="/order/basket/delete_all" class="col-sm-4 ml-4 btn btn-outline-danger">Очистить корзину</button>
        </div>
    </#if>

    <input type="hidden" name="_csrf" value="${_csrf.token}" />
</form>
    <div class="form-group row">
        <label class="col-form-label"><#if priceUser != 0>Общая сумма заказа : ${priceUser}<#else>Заказов в корзине нет</#if></label>
    </div>

</@c.page>