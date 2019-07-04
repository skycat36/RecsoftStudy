<#import "../../parts/common.ftl" as c>

<@c.page>
    <#if error??>
    <div class="row justify-content-center">
        <div class="alert alert-danger" role="alert">
            ${error}
        </div>
    </div>
    </#if>
    <img src="/static/motocross_art.jpg" class="img-fluid" alt="Welcome">
</@c.page>