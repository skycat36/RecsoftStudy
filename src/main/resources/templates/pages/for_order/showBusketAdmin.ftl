<#import "../../parts/common.ftl" as c>

<@c.page>

    <div class="form-group row">
        <h1><label class="col-ml-2 col-form-label">Список клиентов : </label></h1>
    </div>


    <table class="table">
        <thead>
        <tr>
            <th scope="col">Логин</th>
            <th scope="col">Роль</th>
            <th scope="col">Кошелек</th>
        </tr>
        </thead>
        <tbody>

        <#list userList as user>
        <tr>
            <td><a href="/order/basket/select_user/${user.id}" class="card-link">${user.login}</a></td>
            <td>${user.rating}</td>
            <td>${user.cash}</td>
        </tr>
        </#list>
        </tbody>
    </table>


</@c.page>