<#include "security.ftl">
<#import "login.ftl" as l>

<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <a class="navbar-brand" href="/">Motolom</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav mr-auto">
            <#if user??>
                <#if user.id??>
                        <li class="nav-item">
                            <a class="nav-link" href="/product/product_list">${List_of_goods_message}</a>
                        </li>

                        <#if (user.role.name != 'user')>
                            <li class="nav-item">
                                <a class="nav-link" href="/product/add_product">${Add_product_message}</a>
                            </li>

                        </#if>
                        <li class="nav-item">
                            <a class="nav-link" href="/order/cart">
                                <#if (user.role.name == 'user')>${Shopping_cart_message}</#if>
                                <#if (user.role.name == 'seller')>${Shopping_baskets_message}</#if>
                            </a>
                        </li>
                        <#if (user.role.name = 'user')>
                            <li class="nav-item">
                                <a class="nav-link" href="/order/orders_user">${List_of_orders_message}</a>
                            </li>
                        </#if>

        </ul>
                    <a class="nav-link" href="/change_profile">
                        <div class="navbar-text mr-3">${name}</div>
                    </a>
                        <#if languageList??>
                            <select class="custom-select col-sm-1 mr-2" name="language" id="languageId" onchange="ajaxPostChangeLanguage()">
                                <#list languageList as langu>
                                    <option value="${langu}"
                                            <#if language??><#if language == langu>selected</#if></#if>>
                                        ${langu}
                                    </option>
                                </#list>
                            </select>
                            <input type="hidden" id="csrf" name="_csrf" value="${_csrf.token}" />
                        </#if>
                </#if>
            </#if>
    </div>
    <#if known?if_exists>
        <@l.logout/>
    <#else>
        <@l.log_in/>
    </#if>
</nav>

<script>
    function ajaxPostChangeLanguage() {
        var formData = document.getElementById("languageId").value;
        var token = document.getElementById("csrf").value;
        console.log(token);
        console.log(formData);
        console.log("<#if user??><#if user.id??>${user.language.readbleName}</#if></#if>");

        // DO POST
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/change_language",
            data: formData,
            headers: {
                "Accept": "application/json",
                "Content-Type": "application/json",
                'X-Csrf-Token': token
            },
            success: function () {
                console.log("OK: god job:)");
                location.reload();
            },
            error: function (e) {
                console.log("ERROR: ", e);
            }
        });
    }
</script>