<#import "../../parts/common.ftl" as c>

<@c.page>

    <div class="form-group row">
        <h1><label class="col-ml-2 col-form-label">Изменить профиль</label></h1>
    </div>

    <div class="row justify-content-center">
        <#if fileError??>
            <div class="alert alert-danger" role="alert">
                ${fileError}
            </div>
        </#if>
    </div>

    <img id="output" class="img-fluid mb-4"
            <#if user??>
                <#if user.photoUser??>
                    src="/img/${user.photoUser.name}"
                    alt="${user.photoUser.name}"
                </#if>
            </#if>
         width="25%" height="25%"/>

    <form action="/change_profile" method="post" enctype="multipart/form-data">

        <div class="form-group">
            <div class="custom-file col-4 col-mb-4 col-mt-4">
                <input type="file" accept="image/jpeg,image/png" name="file" id="customFile" onchange="loadFile(event)" multiple >
                <label class="custom-file-label" for="customFile">Выберите файл</label>
                <#if fileError??>
                    <div class="invalid-feedback">
                        ${fileError}
                    </div>
                </#if>
            </div>
        </div>

        <div class="form-group row">
            <label class="col-sm-2 col-form-label">Login :</label>
            <div class="col-sm-3">
                <input type="text" name="login" value="<#if user??>${user.login}</#if>"
                       class="form-control small ${(loginError??)?string('is-invalid', '')}"
                       placeholder="Логин"/>
                <#if nameError??>
                    <div class="invalid-feedback">
                        ${nameError}
                    </div>
                </#if>
            </div>
        </div>

        <div class="form-group row">
            <label class="col-sm-2 col-form-label">Пароль :</label>
            <div class="col-sm-3">
                <input type="password" name="password"
                       class="form-control small ${(passwordError??)?string('is-invalid', '')}"
                       placeholder="Пароль"/>
                <#if passwordError??>
                    <div class="invalid-feedback">
                        ${passwordError}
                    </div>
                </#if>
            </div>
        </div>

        <div class="form-group row">
            <label class="col-sm-2 col-form-label">Повторить пароль :</label>
            <div class="col-sm-3">
                <input type="password" name="password2"
                       class="form-control small ${(password2Error??)?string('is-invalid', '')}"
                       placeholder="Повторить пароль"/>
                <#if password2Error??>
                    <div class="invalid-feedback">
                        ${password2Error}
                    </div>
                </#if>
            </div>
        </div>

        <div class="form-group row">
            <label class="col-sm-2 col-form-label">Имя :</label>
            <div class="col-sm-3">
                <input type="text" name="name" value="<#if user??>${user.name}</#if>"
                       class="form-control small ${(nameError??)?string('is-invalid', '')}"
                       placeholder="Имя"/>
                <#if nameError??>
                    <div class="invalid-feedback">
                        ${nameError}
                    </div>
                </#if>
            </div>
        </div>

        <div class="form-group row">
            <label class="col-sm-2 col-form-label">Фамилия :</label>
            <div class="col-sm-3">
                <input type="text" name="fam" value="<#if user??>${user.fam}</#if>"
                       class="form-control small ${(famError??)?string('is-invalid', '')}"
                       placeholder="Фамилия"/>
                <#if famError??>
                    <div class="invalid-feedback">
                        ${famError}
                    </div>
                </#if>
            </div>
        </div>

        <div class="form-group row">
            <label class="col-sm-2 col-form-label">Отчество :</label>
            <div class="col-sm-3">
                <input type="text" name="secName" value="<#if user??>${user.secName}</#if>"
                       class="form-control small ${(secNameError??)?string('is-invalid', '')}"
                       placeholder="Отчество"/>
                <#if secNameError??>
                    <div class="invalid-feedback">
                        ${secNameError}
                    </div>
                </#if>
            </div>
        </div>

        <div class="form-group row">
            <label class="col-sm-2 col-form-label">Email :</label>
            <div class="col-sm-3">
                <input type="email" name="email" value="<#if user??>${user.email}</#if>"
                       class="form-control small ${(emailError??)?string('is-invalid', '')}"
                       placeholder="Email"/>
                <#if emailError??>
                    <div class="invalid-feedback">
                        ${emailError}
                    </div>
                </#if>
            </div>
        </div>

        <div class="form-group row">
            <div class="col-sm-1 mr-4"><button type="submit" class="btn btn-primary ml-0">Изменить</button></div>
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