<#macro action nameAction isCart>


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
            <#if isCart>
                <th scope="col">Адресс</th>
            </#if>
            <th scope="col">Цена за шт.</th>
            <th scope="col">Количество</th>
            <#if isCart>
                <th scope="col">Статус заказа</th>
            </#if>
            <th scope="col"></th>
        </tr>
        </thead>
        <tbody>

        <#list orderList as order>
        <tr>
            <td>
                <a href="/product/show_product/${order.product.id}">${order.product.name}</a>
            </td>
            <#if isCart>
                <td>${order.adress}</td>
            </#if>
            <td>${order.product.price}</td>
            <td>
                <#if isCart>
                    ${order.count}
                <#else>
                    <div>
                        <#--class="form-control small ${(discountError??)?string('is-invalid', '')}"-->
                        <input  class="col-sm-10 form-control small"
                                type="number" id="countProduct${order.id}" onchange="ajaxPost(${order.id})" step="0" min="0"
                                max="${order.product.count + order.count}" name="count_p[]" value="${order.count}">
                    </div>

                </#if>
            </td>
            <#if isCart>
                <td>${listReadbleStatus[order_index]}</td>
            </#if>
            <td>
                <div class="col-sm-4">
                    <#if isCart><#else></#if>
                    <button type="submit" formmethod="post"
                        <#if isCart>
                                formaction="/order/cart/delete/${order.id}"
                        <#else>
                                formaction="/order/cart/delete_not_pay/${order.id}"
                        </#if>
                             class="btn btn-outline-primary">Отменить заказ</button>
                </div>
            </td>
        </tr>
        </#list>

        </tbody>
    </table>

    <#if priceUser != 0 && !isCart >
        <label class="col-form-label">Адресс получателя : </label>
        <div class="col-sm-3">
            <input type="text" name="adress" class="form-control small ${(adressError??)?string('is-invalid', '')}"
                   placeholder="Адресс получателя"/>
                <#if adressError??>
                    <div class="invalid-feedback">
                        ${adressError}
                    </div>
                </#if>
        </div>

        <div class="row justify-content-center mt-4">
            <button type="submit" formmethod="post" formaction="/order/cart/create_list_order" class=" col-sm-4 btn btn-outline-primary">Оформить заказ</button>
            <button type="submit" formmethod="post" formaction="/order/cart/delete_all" class="col-sm-4 ml-4 btn btn-outline-danger">Очистить корзину</button>
        </div>
    </#if>

    <input type="hidden" id="csrf" name="_csrf" value="${_csrf.token}" />
</form>
    <div class="form-group row">
        <label class="col-form-label"><#if priceUser != 0>Общая сумма заказа : ${priceUser}<#else>Заказов нет</#if></label>
    </div>

<script>

        function ajaxPost(order) {

            //event.preventDefault();
            // PREPARE FORM DATA
            var formData = document.getElementById("countProduct" + order).value;
            var token = document.getElementById("csrf").value;
            console.log(token);
            console.log(formData);

            // DO POST
            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: window.location + "/change_count_prod/" + order,
                data: formData,
                headers: {
                    "Accept": "application/json",
                    "Content-Type": "application/json",
                    'X-Csrf-Token': token
                },
                dataType: 'json',
                success: function (result) {
                        var item = document.getElementById("countProduct" + order);
                        item.classList.remove('is-invalid');
                        item.value = result;
                        console.log("r2d2");
                },
                error: function (e) {
                    var item = document.getElementById("countProduct" + order);
                    item.classList.add('is-invalid');
                    //alert(e);
                    console.log("ERROR: ", e);
                }
            });

        }
</script>
</#macro>