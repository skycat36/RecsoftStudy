<#import "../../parts/common.ftl" as c>

<@c.page>
    <#if error??>
    <div class="row justify-content-center">
        <div class="alert alert-danger" role="alert">
            ${error}
        </div>
    </div>
    </#if>
    <div class="row justify-content-center">
    <img src="/static/motocross_art.jpg" style="
        height: 100%;
        width: auto;"
         alt="Welcome">
    </div>
</@c.page>