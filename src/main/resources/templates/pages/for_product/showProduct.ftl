<#import "../../parts/common.ftl" as c>
<#import "../../parts/actionWithProduct.ftl" as aww>

<@c.page>
        <div class="form-group row">
            <h1><label class="col-ml-2 col-form-label"><#if product??>${product.name}</#if></label></h1>
        </div>

        <#if (product.photoProducts?size > 0)>
            <div id="carouselExampleIndicators" class="carousel slide d-inline-block" data-ride="carousel">
                <ol class="carousel-indicators">
                    <#list product.photoProducts as photoProduct>
                        <#if photoProduct_index == 0>
                            <li data-target="#carouselExampleIndicators" data-slide-to="${photoProduct_index}" class="active"></li>
                        <#else>
                            <li data-target="#carouselExampleIndicators" data-slide-to="${photoProduct_index}"></li>
                        </#if>
                    </#list>
                </ol>
                <div class="carousel-inner">

                    <#list product.photoProducts as photoProduct>
                        <#if photoProduct_index == 0>
                            <div class="carousel-item active">
                                <img class="d-block w-100 img-fluid" src="/img/${photoProduct.name}" alt="${photoProduct_index} slide">
                            </div>
                        <#else>
                            <div class="carousel-item">
                                <img class="d-block w-100 img-fluid" src="/img/${photoProduct.name}" alt="${photoProduct_index} slide">
                            </div>
                        </#if>
                    </#list>
                </div>
                <a class="carousel-control-prev" href="#carouselExampleIndicators" role="button" data-slide="prev">
                    <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                    <span class="sr-only">Previous</span>
                </a>
                <a class="carousel-control-next" href="#carouselExampleIndicators" role="button" data-slide="next">
                    <span class="carousel-control-next-icon" aria-hidden="true"></span>
                    <span class="sr-only">Next</span>
                </a>
            </div>
        </#if>
    <form action="/product/add_product" method="post" enctype="multipart/form-data">

        <div class="form-group row">
            <label class="col-sm-2 col-form-label">Цена : <#if product??>${product.price}</#if></label>
        </div>

        <div class="form-group row">
            <label class="col-sm-2 col-form-label">Количество : <#if product??>${product.count}</#if></label>
        </div>

        <div class="form-group row">
            <label class="col-sm-2 col-form-label">Скидка %: <#if product??>${product.discount}</#if></label>
        </div>

        <div class="form-group row">
            <label class="col-form-label">Категория товара : <#if product??>${product.category.name}</#if></label>
        </div>


        <div class="form-group row">
            <label for="exampleFormControlSelect2">Имеющиеся размеры : </label><br>
            <ul>
                <#list product.sizeUsers as sizeUsr>
                    <li>${sizeUsr.nameSize}</li>
                </#list>
            </ul>
        </div>


        <div class="form-group row">
            <label class="col-sm-2 col-form-label">Описание :</label>
            <div class="col-sm-3">
                <textarea name="description" class="form-control small ${(descriptionError??)?string('is-invalid', '')}"
                          placeholder="Описание" disabled><#if product??>${product.description}</#if></textarea>
                <#if descriptionError??>
                    <div class="invalid-feedback">
                        ${descriptionError}
                    </div>
                </#if>
            </div>
        </div>

        <div class="form-group row">
            <#if (user.role.name != 'user')>
                <a type="button" class="btn btn-primary ml-4" <#if product??>href="/product/edit_product/${product.id}"</#if>>Изменить данные продукта</a>
            </#if>

            <#if (user.role.name == 'user')>
                <a type="button" class="btn btn-primary ml-2" <#if product??>href="/order/create_order/${product.id}"</#if>>Оформить заказ</a>
            </#if>
        </div>
        <input type="hidden" value="${_csrf.token}" name="_csrf">
    </form>

     <div class="form-group row">
         <h1><label class="col-ml-2 col-form-label">Комментарии</label></h1>
     </div>

    <form action="/product/show_product/${product.id}/add_comment" method="post">
        <div class="form-group row">
            <label class="col-sm-2 col-form-label">Оставить свой :</label>
            <div class="col-sm-3">
                <textarea name="comment" minlength="0" class="form-control small ${(commentError??)?string('is-invalid', '')}"
                          placeholder="Комментарий"></textarea>
                <#if commentError??>
                <div class="invalid-feedback">
                    ${commentError}
                </div>
                </#if>
            </div>
        </div>
        <div class="col-sm-4"><button type="submit" class="btn btn-outline-primary">Отправить</button></div>
        <input type="hidden" value="${_csrf.token}" name="_csrf">
    </form>
    <#if product??>
        <#list orederedComment as comment>
             <div class="col-sm-10 mb-4">
                 <label class="col-form-label">${comment.user.login}</label>
                 <div class="card">
                    <div class="card-body">
                        ${comment.comment}
                    </div>
                 </div>
             </div>
        <#else>
            No message
        </#list>
    </#if>
</@c.page>