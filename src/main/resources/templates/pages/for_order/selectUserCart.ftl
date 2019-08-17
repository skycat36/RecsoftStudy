<#import "../../parts/common.ftl" as c>

<@c.page>
    <div class="form-group row">
        <label class="col-form-label">${Customer_name_message} : <#if user??>${user.login}</#if></label>
    </div>

    <div class="form-group row">
        <label class="col-form-label mr-1">${Purse_message} :</label>
        <label class="col-form-label mr-1" id="cashUser"><#if user??>${user.cash}</#if></label>
        <label class="col-form-label">${RUB_message}</label>
    </div>

    <table class="table">
        <thead>
        <tr>
            <th scope="col">№ заказа</th>
            <th scope="col">${Address_message}</th>
            <th scope="col">${Order_status_message}</th>
            <th scope="col"></th>
        </tr>
        </thead>
        <tbody>
        <#list orderList as order>
            <tr id="order#{order.id}">
                <#--!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!-->
                <td>
                    <form id="form#{order.id}" method="post" action="/order/orders_user/show_product_in_order/#{order.id}">
                        <a href="#" class="text-primary" onclick="document.getElementById('form#{order.id}').submit();">#{order.id}</a>
                        <input type="hidden" id="csrf" name="_csrf" value="${_csrf.token}" />
                    </form>
                </td>
                <td>${order.adress}</td>
                <td>
                    <select class="custom-select col-sm-8" name="statusOrd[]" id="changeStatus#{order.id}" onchange="ajaxPostChangeStatusProductUser(#{order.id})">
                        <#list listStatus as status>
                            <option value="${status}" <#if (status == listReadbleStatus[order_index])>selected</#if>>${status}</option>
                        </#list>
                    </select>
                </td>
                <td>
                    <div class="col-sm-4"><button type="button" value="#{order.id}" onclick="ajaxPostDeletePayOrderUser(#{order.id})" class="btn btn-outline-primary">${Cancel_the_order_message}</div>
                </td>
            </tr>
        </#list>

        </tbody>
    </table>

    <div class="form-group row">
        <#if priceUser != 0>
                <label class="col-form-label mr-1" id="forCashOrder">${The_total_amount_of_the_order_message} : </label>
                <label class="col-form-label mr-1" id="priseOrd">${priceUser}</label>
                <label class="col-form-label" id="money"> ${RUB_message}</label>
        <#else>
            <label class="col-form-label">${No_orders_in_cart_message}</label>
        </#if>
    </div>
    <input type="hidden" id="csrf" name="_csrf" value="${_csrf.token}"/>



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
                url: "/order/cart/select_user/" + #{user.id} + "/change_status",
                data: JSON.stringify({'newStatusOrder' : newStatusOrder, 'idOrder' : idOrder}),
                headers: {
                    "Accept": "application/json",
                    "Content-Type": "application/json",
                    'X-Csrf-Token': token
                },
                //dataType: 'json',
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
                url: "/order/cart/select_user/" + #{user.id} + "/delete/" + idOrder,
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
                        priseOrd.remove();
                        document.getElementById("forCashOrder").innerHTML = "${No_orders_in_cart_message}";
                        document.getElementById("money").remove();
                    } else{
                        priseOrd.innerHTML = data.priseOrders;
                        console.log(" look good");
                    }
                    document.getElementById("cashUser").innerHTML = data.cashUser;
                    item.remove();
                },
                error: function (e) {
                    console.log("ERROR: ", e);
                }
            });
        }
    </script>
</@c.page>