<!-- Проверяем существует ли обьект и позволяет пользоваться им через сессию
     ?? приведение к булеву типу-->
<#assign
known = Session.SPRING_SECURITY_CONTEXT??
>

<#if known>
<!-- Выдергивает пользователя из бд если он существует и авторизировался и предоставляет методы для работы с ним -->
    <#assign
    user = Session.SPRING_SECURITY_CONTEXT.authentication.principal
    name = user.getUsername()
    >
<#else>
    <#assign
    name = "unknown"
    >
</#if>