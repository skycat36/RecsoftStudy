<#import "../../parts/common.ftl" as c>

<@c.page>
<form action="/order/cart/select_user/${user.id}" method="post">
    <div class="form-group row">
        <label class="col-form-label">Имя клиента : <#if user??>${user.login}</#if></label>
    </div>

    <div class="form-group row">
        <label class="col-sm-2 col-form-label" id="cashUser">Кошелек : <#if user??>${user.cash}</#if></label>
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
        <tr id="order${order.id}">
            <td>
                <a href="/product/show_product/${order.product.id}">${order.product.name}</a>
            </td>
            <td>${order.adress}</td>
            <td>${order.product.price}</td>
            <td>${order.count}</td>
            <td>
                    <select class="custom-select col-sm-8" name="statusOrd[]" id="changeStatus${order.id}" onchange="ajaxPostChangeStatusProductUser(${order.id})">
                    <#list listStatus as status>
                        <option value="${status}" <#if (status == listReadbleStatus[order_index])>selected</#if>>${status}</option>
                    </#list>
                    </select>
            </td>

            <td>
                    <div class="col-sm-4"><button type="button" value="${order.id}" onclick="ajaxPostDeletePayOrderUser(${order.id})" class="btn btn-outline-primary">Отменить заказ</div>
            </td>
        </tr>
        </#list>

        </tbody>
    </table>

    <div class="form-group row">
        <label class="col-form-label" id="priseOrd"><#if priceUser != 0>Общая сумма заказа : ${priceUser}<#else>Заказов в корзине нет</#if></label>
    </div>
    <#--<div class="col-sm-4"><button type="submit" value="update" name="bUpdate" class="btn btn-outline-primary">Обновить данные</div>-->
    <input type="hidden" id="csrf" name="_csrf" value="${_csrf.token}"/>
</form>

    <script>
        function ajaxPostChangeStatusProductUser(idOrder) {
            var newStatusOrder = document.getElementById("changeStatus" + idOrder).value;
            var token = document.getElementById("csrf").value;
            console.log(token);
            console.log(newStatusOrder);

            // DO POST
            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: window.location + "/change_status",
                data: JSON.stringify({'newStatusOrder' : newStatusOrder, 'idOrder' : idOrder}),
                headers: {
                    "Accept": "application/json",
                    "Content-Type": "application/json",
                    'X-Csrf-Token': token
                },
                dataType: 'json',
                success: function () {
                    console.log("ok");
                },
                error: function (e) {
                    console.log("ERROR: ", e);
                }
            });
        }

        function ajaxPostDeletePayOrderUser(idOrder) {
            var token = document.getElementById("csrf").value;
            console.log(token);

            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: window.location + "/delete/" + idOrder,
                headers: {
                    "Accept": "application/json",
                    "Content-Type": "application/json",
                    'X-Csrf-Token': token
                },
                success: function (result) {
                    var item = document.getElementById("order" + idOrder);
                    var data = JSON.parse(result);
                    var priseOrd = document.getElementById("priseOrd");
                    console.log(" look good");
                    if (data.priseOrders == 0) {
                        priseOrd.innerHTML = "Заказов нет";
                    } else{
                        priseOrd.innerHTML = "Общая сумма заказа : " + data.priseOrders;
                        console.log(" look good");
                    }
                    document.getElementById("cashUser").innerHTML = "Кошелек : " + data.cashUser + " руб";
                    item.remove();
                },
                error: function (e) {
                    console.log("ERROR: ", e);
                }
            });
        }
    </script>
</@c.page>