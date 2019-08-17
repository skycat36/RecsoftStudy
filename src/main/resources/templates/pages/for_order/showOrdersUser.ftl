<#import "../../parts/common.ftl" as c>

<@c.page>
    <div class="form-group row">
        <h1><label class="col-ml-2 col-form-label">Заказы пользователя</label></h1>
    </div>

    <div class="row justify-content-center">
        <#if priceError??>
            <div class="alert alert-danger" role="alert">
                ${priceError}
            </div>
        </#if>
    </div>

    <div class="form-group row">
        <label class="col-form-label mr-1" >${Wallet_message} : </label>
        <label class="col-form-label mr-1" id="cashUser"><#if user??>${user.cash}</#if></label>
        <label class="col-form-label">${RUB_message}</label>
    </div>
        <table class="table">
            <thead>
            <tr>
                <th scope="col">№</th>
                <th scope="col">${Address_message}</th>
                <th scope="col">${Order_status_message}</th>
                <th scope="col"></th>
            </tr>
            </thead>
            <tbody>
            <#if orderList??>
                <#list orderList as order>
                    <tr id="order#{order.id}">
                        <td>
                            <form id="form#{order.id}" method="post" action="/order/orders_user/show_product_in_order/#{order.id}">
                                <a href="#" class="text-primary" onclick="document.getElementById('form#{order.id}').submit();">#{order.id}</a>
                                <input type="hidden" id="csrf" name="_csrf" value="${_csrf.token}" />
                            </form>
                        </td>

                        <td>${order.adress}</td>
                        <td>${listReadbleStatus[order_index]}</td>
                        <td>
                            <div class="col-sm-2 ml-1">
                                <button type="button" formmethod="post"
                                        onclick="ajaxPostDeletePayOrder(#{order.id})"
                                        class="btn btn-outline-primary">${Cancel_the_order_message}</button>
                            </div>
                        </td>
                    </tr>
                </#list>
            </#if>

            </tbody>
        </table>
        <input type="hidden" id="csrf" name="_csrf" value="${_csrf.token}" />
    <div class="form-group row">
        <#if priceUser??>
            <label class="col-form-label mr-1" id="textForCashOrder">Общая сумма заказов : </label>
            <label class="col-form-label mr-1" id="priseOrd"><#if priceUser??>${priceUser}</#if></label>
            <label class="col-form-label" id="money"> ${RUB_message}</label>
        <#else>
            <label class="col-form-label">${No_orders_message}</label>
        </#if>
    </div>

    <script>

        function ajaxPostDeletePayOrder(idOrder) {
            var token = document.getElementById("csrf").value;
            console.log(token);

            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: "/order/orders_user/delete/" + idOrder,
                headers: {
                    "Accept": "application/json",
                    "Content-Type": "application/json",
                    'X-Csrf-Token': token
                },
                success: function (result) {
                    var item = document.getElementById("order" + idOrder);
                    var data = JSON.parse(result);
                    var priseOrd = document.getElementById("priseOrd");
                    if (data.priseOrders == 0) {
                        priseOrd.remove();
                        document.getElementById("textForCashOrder").innerHTML = "${No_orders_message}";
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