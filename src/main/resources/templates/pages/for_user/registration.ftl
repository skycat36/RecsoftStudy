<#import "../../parts/common.ftl" as c>

<@c.page>

    <div class="form-group row">
        <h1><label class="col-ml-2 col-form-label">${Registration_message}</label></h1>
    </div>

    <div class="row justify-content-center">
        <#if fileError??>
            <div class="alert alert-danger" role="alert">
                ${fileError}
            </div>
        </#if>
    </div>

    <img id="output" class="img-fluid mb-4"
            <#if photo??>
                src="/img/${photo.name}"
                alt="${photo.name}"
            </#if>
         width="25%" height="25%"/>

    <form action="/registration" method="post" enctype="multipart/form-data">

        <div class="form-group">
            <div class="custom-file col-4 col-mb-4 col-mt-4">
                <input type="file" accept="image/jpeg,image/png" name="file" id="customFile" onchange="loadFile(event)" multiple >
                <label class="custom-file-label" for="customFile">${Selected_file_message}</label>
                <#if fileError??>
                    <div class="invalid-feedback">
                        ${fileError}
                    </div>
                </#if>
            </div>
        </div>

        <div class="form-group row">
            <label class="col-sm-2 col-form-label">${Login_message} : </label>
            <div class="col-sm-3">
                <input type="text" name="login" value="<#if userTemp??><#if userTemp.login??>${userTemp.login}</#if></#if>"
                       class="form-control small ${(loginError??)?string('is-invalid', '')}"
                       placeholder="${Login_message}"/>
                <#if nameError??>
                    <div class="invalid-feedback">
                        ${nameError}
                    </div>
                </#if>
            </div>
        </div>

        <div class="form-group row">
            <label class="col-sm-2 col-form-label">${Password_message} : </label>
            <div class="col-sm-3">
                <input type="password" name="password"
                       class="form-control small ${(passwordError??)?string('is-invalid', '')}"
                       placeholder="${Password_message}"/>
                <#if passwordError??>
                    <div class="invalid-feedback">
                        ${passwordError}
                    </div>
                </#if>
            </div>
        </div>

        <div class="form-group row">
            <label class="col-sm-2 col-form-label">${Repeat_password_message} : </label>
            <div class="col-sm-3">
                <input type="password" name="password2"
                       class="form-control small ${(password2Error??)?string('is-invalid', '')}"
                       placeholder="${Repeat_password_message}"/>
                <#if password2Error??>
                    <div class="invalid-feedback">
                        ${password2Error}
                    </div>
                </#if>
            </div>
        </div>

        <div class="form-group row">
            <label class="col-sm-2 col-form-label">${Name_message} : </label>
            <div class="col-sm-3">
                <input type="text" name="name" value="<#if userTemp??><#if userTemp.name??>${userTemp.name}</#if></#if>"
                       class="form-control small ${(nameError??)?string('is-invalid', '')}"
                       placeholder="${Name_message}"/>
                <#if nameError??>
                    <div class="invalid-feedback">
                        ${nameError}
                    </div>
                </#if>
            </div>
        </div>

        <div class="form-group row">
            <label class="col-sm-2 col-form-label">${Surname_message} : </label>
            <div class="col-sm-3">
                <input type="text" name="fam" value="<#if userTemp??><#if userTemp.fam??>${userTemp.fam}</#if></#if>"
                       class="form-control small ${(famError??)?string('is-invalid', '')}"
                       placeholder="${Surname_message}"/>
                <#if famError??>
                    <div class="invalid-feedback">
                        ${famError}
                    </div>
                </#if>
            </div>
        </div>

        <div class="form-group row">
            <label class="col-sm-2 col-form-label">${Patronymic_message} : </label>
            <div class="col-sm-3">
                <input type="text" name="secName" value="<#if userTemp??><#if userTemp.secName??>${userTemp.secName}</#if></#if>"
                       class="form-control small ${(secNameError??)?string('is-invalid', '')}"
                       placeholder="${Patronymic_message}"/>
                <#if secNameError??>
                    <div class="invalid-feedback">
                        ${secNameError}
                    </div>
                </#if>
            </div>
        </div>

        <div class="form-group row">
            <label class="col-sm-2 col-form-label">${Email_message} : </label>
            <div class="col-sm-3">
                <input type="email" name="email" value="<#if userTemp??><#if userTemp.email??>${userTemp.email}</#if></#if>"
                       class="form-control small ${(emailError??)?string('is-invalid', '')}"
                       placeholder="${Email_message}"/>
                <#if emailError??>
                    <div class="invalid-feedback">
                        ${emailError}
                    </div>
                </#if>
            </div>
        </div>


        <div class="form-group row">
            <div class="col-sm-1 mr-4"><button type="submit" class="btn btn-primary ml-0">${Edit_message}</button></div>
            <input type="hidden" value="${_csrf.token}" name="_csrf">
        </div>
    </form>
    <script>


        var loadFile = function(event) {
            var img = document.getElementById('output');
            img.src = URL.createObjectURL(event.target.files[0]);
        };
    </script>
</@c.page>