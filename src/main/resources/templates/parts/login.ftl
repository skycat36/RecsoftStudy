<#macro login path>
<form action="${path}" method="post">
    <div class="form-group row">
        <label class="col-sm-2 col-form-label">${User_name_message} : </label>
        <div class="col-sm-4">
            <input type="text" name="username" value="<#if user??>${user.username}</#if>"
                   class="form-control ${(usernameError??)?string('is-invalid', '')}"
                   placeholder="${User_name_message}"/>
            <#if usernameError??>
                <div class="invalid-feedback">
                    ${usernameError}
                </div>
            </#if>
        </div>
    </div>
    <div class="form-group row">
        <label class="col-sm-2 col-form-label">${Password_message} : </label>
        <div class="col-sm-4">
            <input type="password" name="password"
                   class="form-control ${(passwordError??)?string('is-invalid', '')}"
                   placeholder="${Password_message}"/>
            <#if passwordError??>
                <div class="invalid-feedback">
                    ${passwordError}
                </div>
            </#if>
        </div>
    </div>

    <input type="hidden" name="_csrf" value="${_csrf.token}" />
    <div class="form-group">
        <button type="submit" class="btn btn-primary">${Enter_message}</button>
        <a class="ml-4" href="/registration"><button type="button"  class="btn btn-primary">${Registration_message}</button></a>
    </div>
</form>
</#macro>

<#macro logout>
<form action="/logout" method="post">
    <input type="hidden" name="_csrf" value="${_csrf.token}" />
    <button type="submit" class="btn btn-primary">${Exit_message}</button>
</form>
</#macro>

<#macro log_in>
<form action="/product/product_list" method="get">
    <button type="submit" class="btn btn-primary text-right">${Enter_message}</button>
</form>
</#macro>