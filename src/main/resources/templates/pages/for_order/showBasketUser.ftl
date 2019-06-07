<#import "../../parts/common.ftl" as c>

<@c.page>

    <div class="form-group row">
        <label class="col-form-label">Кошелек : <#if cash??>${cash}<#else>0</#if> руб</label>
    </div>

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
                <td>${order.product.name}</td>
                <td>${order.adress}</td>
                <td>${order.product.price}</td>
                <td>${order.count}</td>
                <td>${listReadbleStatus[order_index]}</td>
                <td>
                    <form action="/order/basket/delete/${order.id}" method="post">
                        <div class="col-sm-4"><button type="submit" class="btn btn-outline-primary">Отменить заказ</div>
                        <input type="hidden" name="_csrf" value="${_csrf.token}" />
                    </form>
                </td>
            </tr>
        </#list>

        </tbody>
    </table>

    <div class="form-group row">
        <label class="col-form-label"><#if priceUser != 0>Общая сумма заказа : ${priceUser}<#else>Заказов в корзине нет</#if></label>
    </div>

</@c.page>