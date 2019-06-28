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
            <li class="nav-item">
                <a class="nav-link" href="/product/product_list">Список товаров</a>
            </li>

            <#if (user.role.name != 'user')>
                <li class="nav-item">
                    <a class="nav-link" href="/product/add_product">Добавить товар</a>
                </li>

            </#if>
            <li class="nav-item">
                <a class="nav-link" href="/order/cart">
                    <#if (user.role.name == 'user')>Корзина товаров</#if>
                    <#if (user.role.name == 'seller')>Корзины покупателей</#if>
                </a>
            </li>
            <#if (user.role.name = 'user')>
                <li class="nav-item">
                    <a class="nav-link" href="/order/orders_user">Список заказов</a>
                </li>
            </#if>

        </ul>
        <div class="navbar-text mr-3">${name}</div>
        </#if>
    <#if known?if_exists>
        <@l.logout/>
    <#else>
        <@l.log_in/>
    </#if>
    </div>
</nav>