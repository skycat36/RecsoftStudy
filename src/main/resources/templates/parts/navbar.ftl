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

            <li class="nav-item">
                <a class="nav-link" href="/product/add_product">Добавить товар</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/user/${user.id}/show_shopping_cart">Корзина товаров</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/user/${user.id}/edit_profile_user">Корзина товаров</a>
            </li>
        </#if>
        </ul>
        <div class="navbar-text mr-3">${name}</div>
    <#if known?if_exists>
        <@l.logout/>
    <#else>
        <@l.log_in/>
    </#if>
    </div>
</nav>