<#import "../../parts/common.ftl" as c>

<@c.page>
<form action="/order/cart/select_user/${user.id}" method="post">
    <div class="form-group row">
        <label class="col-form-label">Имя клиента : <#if user??>${user.login}</#if></label>
    </div>

    <div class="form-group row">
        <label class="col-sm-2 col-form-label">Кошелек : <#if user??>${user.cash}</#if></label>
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
        <#--<input type="hidden" name="orderList" value="<#if orderList??>${orderList}</#if>"/>-->
        <#list orderList as order>
        <tr>
            <td>${order.product.name}</td>
            <td>${order.adress}</td>
            <td>${order.product.price}</td>
            <td>${order.count}</td>
            <td>
                    <select class="custom-select col-sm-8" name="statusOrd[]" id="inputGroupSelect01">
                    <#list listStatus as status>
                        <option value="${status}" <#if (status == listReadbleStatus[order_index])>selected</#if>>${status}</option>
                    </#list>
                    </select>
            </td>

            <td>
                    <div class="col-sm-4"><button type="submit" value="${order.id}" name="bDelete" class="btn btn-outline-primary">Отменить заказ</div>
            </td>
        </tr>
        </#list>

        </tbody>
    </table>

    <div class="form-group row">
        <label class="col-form-label"><#if priceUser != 0>Общая сумма заказа : ${priceUser}<#else>Заказов в корзине нет</#if></label>
    </div>
    <div class="col-sm-4"><button type="submit" value="update" name="bUpdate" class="btn btn-outline-primary">Обновить данные</div>
    <input type="hidden" name="_csrf" value="${_csrf.token}"/>
</form>
</@c.page>