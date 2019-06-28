<#import "../../parts/common.ftl" as c>
<@c.page>
<form action="/product/product_list/filter" method="get">
    <div class="form-row align-items-center">
        <div class="col-auto my-1">
            <label class="mr-sm-2" for="inlineFormCustomSelect">Выбрать категорию</label>
            <select class="custom-select mr-sm-2" name="selectCategory" id="inlineFormCustomSelect">
                <option value="" <#if selectCategory??>selected</#if>>Все товары</option>
                <#list listCategory as category>
                    <option value="${category.id}" <#if selectCategory??><#if (category.id == selectCategory)>selected</#if></#if>>${category.name}</option>
                </#list>
            </select>
        </div>
        <div class="col-auto my-1">
            <button type="submit" name="filterCategory" class="btn btn-primary">Выбрать категорию</button>
        </div>
    </div>
</form>
<div class="card-columns">
    <#list productList as product>
        <div class="card my-3" style="width: 18rem;">
        <#if (product.photoProducts?size > 0)>
        <div id="carouselExampleIndicators" class="carousel slide d-inline-block" data-ride="carousel">
            <ol class="carousel-indicators">
                <#list product.photoProducts as photoProduct>
                    <#if photoProduct_index == 0>
                        <li data-target="#carouselExampleIndicators" data-slide-to="${photoProduct_index}" class="active"></li>
                    <#else>
                        <li data-target="#carouselExampleIndicators" data-slide-to="${photoProduct_index}"></li>
                    </#if>
                </#list>
            </ol>
            <div class="carousel-inner">
                <#list product.photoProducts as photoProduct>
                    <#if photoProduct_index == 0>
                        <div class="carousel-item active">
                            <img class="d-block w-100 img-fluid" src="/img/${photoProduct.name}" alt="${photoProduct_index} slide">
                        </div>
                    <#else>
                        <div class="carousel-item">
                            <img class="d-block w-100 img-fluid" src="/img/${photoProduct.name}" alt="${photoProduct_index} slide">
                        </div>
                    </#if>
                </#list>
            </div>
            <a class="carousel-control-prev" href="#carouselExampleIndicators" role="button" data-slide="prev">
                <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                <span class="sr-only">Previous</span>
            </a>
            <a class="carousel-control-next" href="#carouselExampleIndicators" role="button" data-slide="next">
                <span class="carousel-control-next-icon" aria-hidden="true"></span>
                <span class="sr-only">Next</span>
            </a>
        </div>
        </#if>
            <div class="card-body">
                <p class="card-text">${product.name}</p>
            </div>
            <ul class="list-group list-group-flush">
                <li class="list-group-item">Цена: ${product.price} руб</li>
            </ul>
            <div class="card-body">
                <a href="/product/show_product/${product.id}" class="card-link">Посмотреть</a>
                <#if (user.role.name == 'user')>
                    <a href="/order/create_order/${product.id}" class="card-link">Оформить заказ</a>
                </#if>
            </div>
        </div>
    <#else>
    No message
    </#list>
</div>
</@c.page>