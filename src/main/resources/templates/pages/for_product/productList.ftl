<#import "../../parts/common.ftl" as c>
<@c.page>
<div class="card-columns">
    <#list productList as product>
        <div class="card my-3" style="width: 18rem;">
        <#if (product.photos?size > 0)>
        <div id="carouselExampleIndicators" class="carousel slide" data-ride="carousel">
            <ol class="carousel-indicators">
                <#list product.photos as photo>
                    <#if photo_index == 0>
                        <li data-target="/static/data/${photo.name}" data-slide-to="${photo_index}" class="active"></li>
                    <#else>
                        <li data-target="/static/data/${photo.name}" data-slide-to="${photo_index}"></li>
                    </#if>
                </#list>
            </ol>
            <div class="carousel-inner">

                <#list product.photos as photo>
                    <#if photo_index == 0>
                        <div class="carousel-item active">
                            <img class="d-block w-100" src="/static/data/${photo.name}" alt="${photo_index} slide">
                        </div>
                    <#else>
                        <div class="carousel-item">
                            <img class="d-block w-100" src="/static/data/${photo.name}" alt="${photo_index} slide">
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
                <#--<h5 class="card-title">Card title</h5>-->
                <p class="card-text">${product.name}</p>
            </div>
            <ul class="list-group list-group-flush">
                <li class="list-group-item">Цена: ${product.price} руб</li>
                <#--<li class="list-group-item">Dapibus ac facilisis in</li>-->
                <#--<li class="list-group-item">Vestibulum at eros</li>-->
            </ul>
            <div class="card-body">
                <a href="#" class="card-link">Посмотреть</a>
                <a href="#" class="card-link">Оформить заказ</a>
            </div>
        </div>
    <#else>
    No message
    </#list>
</div>
</@c.page>