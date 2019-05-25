<#import "../../parts/common.ftl" as c>
<@c.page>
<div class="card-columns">
    <#list productList as product>
        <div class="card my-3">
        <#if product.filename??>
        <img src="" class="card-img-top">
        </#if>
            <div class="m-2">
                <span>${product.name}</span>
                <i>${product.price}</i>
            </div>
            <div class="card-footer text-muted">
                ${product.count}
            </div>
        </div>
    <#else>
    No message
    </#list>
</div>
</@c.page>