<#import "../../parts/common.ftl" as c>

<@c.page>

    <div class="form-group row">
        <h1><label class="col-ml-2 col-form-label">${Customer_list_message} :</label></h1>
    </div>


    <table class="table">
        <thead>
        <tr>
            <th scope="col">${Login_message}</th>
            <th scope="col">${Rating_message}</th>
            <th scope="col">${Purse_message}</th>
        </tr>
        </thead>
        <tbody>

        <#list userList as user>
        <tr>
            <td><a href="/order/cart/select_user/${user.id}" class="card-link">${user.login}</a></td>
            <td>${user.rating}</td>
            <td>${user.cash}</td>
        </tr>
        </#list>
        </tbody>
    </table>


</@c.page>