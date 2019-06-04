<#import "../../parts/common.ftl" as c>
<#import "../../parts/actionWithProduct.ftl" as aww>

<@c.page>
    <@aww.action "/product/edit_product/${product.id}" "Редактировать продукт" true/>
</@c.page>