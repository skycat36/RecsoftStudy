<#import "../../parts/common.ftl" as c>
<#--<style>-->
<#--img {-->
<#--height: 100vh;-->
<#--background: url(http://localhost:8080/static/motocross_art.jpg);-->
<#--background-repeat: no-repeat;-->
<#--background-size: cover;-->
<#--}-->
<#--</style>-->
<@c.page>
    <#if error??>
    <div class="row justify-content-center">
        <div class="alert alert-danger" role="alert">
            ${error}
        </div>
    </div>
    </#if>
    <img src="static/motocross_art.jpg" class="img-fluid" alt="Welcome">
</@c.page>