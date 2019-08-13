<#macro action isCart>
    <div class="form-group row">
        <h1><label class="col-ml-2 col-form-label">${nameAction_message}</label></h1>
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
    <form>
        <table class="table">
            <thead>
            <tr>
                <th scope="col">${Name_message}</th>
                <#if isCart>
                    <th scope="col">${Address_message}</th>
                <#else>
                    <th scope="col">Размер</th>
                </#if>
                <th scope="col">${Price_per_piece_message}</th>
                <th scope="col">${Number_message}</th>
                <#if isCart>
                    <th scope="col">${Order_status_message}</th>
                </#if>
                <th scope="col"></th>
            </tr>
            </thead>
            <tbody>
            <#if productInCartList??>
                <#list productInCartList as productInCart>
                    <tr id="order${productInCart.id}">
                        <td>
                            <a href="/product/show_product/${productInCart.product.id}">${productInCart.product.name}</a>
                        </td>
                        <#if isCart>
                            <td>${productInCart.order.adress}</td>
                        <#else>
                            <td>${productInCart.sizeUser.nameSize}</td>
                        </#if>
                        <td>${productInCart.product.price}</td>
                        <td>
                            <#if isCart>
                                ${productInCart.count}
                            <#else>
                                <div>
                                    <input  class="col-sm-3 form-control small"
                                            type="number" id="countProduct${productInCart.id}" onchange="ajaxPostChangeCountProduct(${productInCart.id})" step="0" min="0"
                                            name="count_p[]" value="${productInCart.count}">
                                </div>

                            </#if>
                        </td>
                        <#if isCart>
                            <td>${listReadbleStatus[productInCart_index]}</td>
                        </#if>
                        <td>
                            <div class="col-sm-2 ml-1">
                                <#if isCart><#else></#if>
                                <button type="button" formmethod="post"
                                        <#if isCart>
                                            onclick="ajaxPostDeletePayOrder(${productInCart.id})"
                                        <#else>
                                            onclick="ajaxPostDeleteNotPayOrder(${productInCart.id})"
                                        </#if>
                                        class="btn btn-outline-primary">${Cancel_the_order_message}</button>
                            </div>
                        </td>
                    </tr>
                </#list>
            </#if>

            </tbody>
        </table>
        <#if priceUser??>
            <div id="notOrder">

                <#if priceUser != 0 && !isCart >
                    <label class="col-form-label mr-2">${Recipient_address_message} :</label>
                    <div class="col-sm-3">
                        <input type="text" name="adress" class="form-control small ${(adressError??)?string('is-invalid', '')}"
                               placeholder="${Recipient_address_message}"/>
                        <#if adressError??>
                            <div class="invalid-feedback">
                                ${adressError}
                            </div>
                        </#if>
                    </div>

                    <div class="row justify-content-center mt-4">
                        <button type="submit" formmethod="post" formaction="/order/cart/create_list_order" class=" col-sm-4 btn btn-outline-primary">${Place_your_order}</button>
                        <button type="submit" formmethod="post" formaction="/order/cart/delete_all" class="col-sm-4 ml-4 btn btn-outline-danger">${Empty_recycle_bin}</button>
                    </div>
                </#if>
            </div>
        </#if>
        <input type="hidden" id="csrf" name="_csrf" value="${_csrf.token}" />
    </form>
    <div class="form-group row">
        <#if priceUser??>
            <label class="col-form-label mr-1" id="textForCashOrder">${Total_order_amount_message} : </label>
            <label class="col-form-label mr-1" id="priseOrd"><#if priceUser??>${priceUser}</#if></label>
            <label class="col-form-label" id="money"> ${RUB_message}</label>
        <#else>
            <label class="col-form-label">${No_orders_message}</label>
        </#if>
    </div>

    <script>
        function ajaxPostChangeCountProduct(idOrder) {
            var formData = document.getElementById("countProduct" + idOrder).value;
            var token = document.getElementById("csrf").value;
            console.log(token);
            console.log(formData);

            // DO POST
            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: window.location + "/change_count_prod/" + idOrder,
                data: formData,
                headers: {
                    "Accept": "application/json",
                    "Content-Type": "application/json",
                    'X-Csrf-Token': token
                },
                dataType: 'json',
                success: function (result) {
                    var item = document.getElementById("countProduct" + idOrder);
                    item.classList.remove('is-invalid');
                    var data = JSON.parse(result);
                    item.value = data.newCountData;
                    var priseOrd = document.getElementById("priseOrd");
                    priseOrd.innerHTML = data.priseOrders;
                    console.log(data.newCountData + " look");
                },
                error: function (e) {
                    var item = document.getElementById("countProduct" + idOrder);
                    item.classList.add('is-invalid');
                    console.log("ERROR: ", e);
                }
            });
        }

        function ajaxPostDeleteNotPayOrder(idOrder) {
            var token = document.getElementById("csrf").value;
            console.log(token);

            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: window.location + "/delete_not_pay/" + idOrder,
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
                        document.getElementById("notOrder").remove();
                        document.getElementById("money").remove();
                    } else{
                        priseOrd.innerHTML = data.priseOrders;
                        console.log(" look good");
                    }
                    item.remove();
                },
                error: function (e) {
                    console.log("ERROR: ", e);
                }
            });
        }

        function ajaxPostDeletePayOrder(idOrder) {
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
                    if (data.priseOrders == 0) {
                        priseOrd.remove();
                        document.getElementById("textForCashOrder").innerHTML = "${No_orders_message}";
                        document.getElementById("notOrder").remove();
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
</#macro>