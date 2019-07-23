package com.recsoft.controller.product;

import com.recsoft.data.entity.*;
import com.recsoft.data.exeption.ProductExeption;
import com.recsoft.service.ProductService;
import com.recsoft.service.UserService;
import com.recsoft.utils.constants.ConfigureErrors;
import com.recsoft.utils.ControllerUtils;
import com.recsoft.utils.ServiceUtils;
import com.recsoft.validation.MessageGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/product")
@Api(value = "Контроллер продуктов", description = "Класс-контроллер отвечающий за работу с продуктами")
public class ProductController {

    @ApiModelProperty(notes = "Записывает логи сделанных действий и ошибок.", name="log", value="ProductController")
    private Logger log = LoggerFactory.getLogger(ProductController.class.getName());

    /*Путь к папке хранения данных*/
    @ApiModelProperty(notes = "Путь до файла хранимых изображений", required=true)
    @Value("${upload.path}")
    private String uploadPath;

    private ProductService productService;

    private UserService userService;

    private MessageGenerator messageGenerator;

    @Autowired
    public void setMessageGenerator(MessageGenerator messageGenerator) {
        this.messageGenerator = messageGenerator;
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/product_list")
    @ApiOperation(value = "Отображает все имеющиеся продукты")
    public ModelAndView getAllProduct(
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user
    ) {
        ModelAndView mav = new ModelAndView("/pages/for_product/productList");

        user = userService.getUserById(user.getId());
        ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), messageGenerator, "productList", mav);

        mav.addObject("listCategory", productService.getAllCategory());
        mav.addObject("productList", productService.getAllProduct());

        return mav;
    }

    @GetMapping("/product_list/filter")
    @ApiOperation(value = "Отображает  продукты по выбранной категории на страницу")
    public ModelAndView getProductFilter(
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user,
            @ApiParam(value = "Категория продукта.", required = true) @RequestParam Long selectCategory
    ) {
        ModelAndView mav = new ModelAndView("/pages/for_product/productList");

        user = userService.getUserById(user.getId());
        ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), messageGenerator, "productList", mav);

        mav.addObject("productList", productService.getProductListByCategory(selectCategory));
        mav.addObject("categoryProd", selectCategory);
        mav.addObject("listCategory", productService.getAllCategory());

        return mav;
    }

    //@RolesAllowed(value = "ROLE_seller")
    @GetMapping("/add_product")
    @ApiOperation(value = "Отображает страницу добавления продуктов")
    public ModelAndView showAddProduct(
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user
    ){
        ModelAndView mav = new ModelAndView("/pages/for_product/addProduct");

        user = userService.getUserById(user.getId());

        ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), messageGenerator, "addProduct", mav);

        if (user.getRole().getName().equals(Role.SELLER) || user.getRole().getName().equals(Role.ADMIN)) {
            mav.addObject("listSizeUser", productService.getAllSizeUser());
            mav.addObject("listCategory", productService.getAllCategory());
        }else {
            return messageGenerator.createMessageForHacker(user.getLanguage());
        }
        return mav;
    }

    @PostMapping("/add_product")
    @ApiOperation(value = "Добавить продукт в базу.")
    public ModelAndView addProduct(
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user,
            @ApiParam(value = "Выдергивает продукт с формы.", required = true) @ModelAttribute @Valid Product product,
            @ApiParam(value = "Сообщения о возможных ошибках.") BindingResult bindingResult,
            @ApiParam(value = "Категория выбранного продукта.", required = true) @RequestParam Long categoryProd,
            @ApiParam(value = "Размеры товара выбранного пользователем.", required = true) @RequestParam ArrayList<Long> sizeUsersProd,
            @ApiParam(value = "Выбранные пользователем файлы картинок.", required = true) @RequestParam("file") List<MultipartFile> file
    ){
        Map<String, String> errors = new HashMap<>();
        ModelAndView mav = new ModelAndView("redirect:/product/product_list");

        user = userService.getUserById(user.getId());

        if (productService.existProduct(product)){
                errors.put(
                        ControllerUtils.constructError("name"),
                        messageGenerator.getMessageErrorFromProperty(
                                user.getLanguage(),
                                MessageGenerator.FAIL_WHIS_OTHER_ERROR,
                                ControllerUtils.constructFieldsForProperty(
                                        "addProduct",
                                        ConfigureErrors.SELECT_ENTITY_EXIST.toString()
                                )
                        )
                );
        }

        if (file.size() > 4){
                errors.put(
                        ControllerUtils.constructError("message"),
                        messageGenerator.getMessageErrorProperty(
                                MessageGenerator.FAIL_WHIS_OTHER_ERROR,
                                ConfigureErrors.COUNT_PHOTO_BEGER.toString(),
                                "addProduct", user.getLanguage()
                        )
                );
        }

        if (sizeUsersProd == null){
            errors.put(
                    ControllerUtils.constructError("sizeUsersProd"),
                    messageGenerator.getMessageErrorProperty(
                            MessageGenerator.FAIL_WHIS_OTHER_ERROR,
                            ConfigureErrors.SELECT_SIZE.toString(),
                            "addProduct", user.getLanguage()
                    )
            );
        }

        if (bindingResult.hasErrors()){
                errors.putAll(messageGenerator.getErrors(bindingResult, user.getLanguage()));
        }

        if (errors.isEmpty()) {
            try {
                productService.addProduct(user.getLanguage(), product, categoryProd, sizeUsersProd, file);
            } catch (IOException | ProductExeption e) {
                log.error(e.getMessage());
                mav.setViewName("/pages/for_product/addProduct");
                ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), messageGenerator, "addProduct", mav);

                this.constructPageActionWithProd(categoryProd, sizeUsersProd, mav);
                mav.addObject(ControllerUtils.constructError("message"), e.getMessage());
                mav.addObject("product",product);
            }

        }else{
            mav.setViewName("/pages/for_product/addProduct");
            ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), messageGenerator, "addProduct", mav);

            this.constructPageActionWithProd(categoryProd, sizeUsersProd, mav);
            mav.addAllObjects(errors);
            mav.addObject("product",product);
        }
        return mav;
    }

    @GetMapping("/show_product/{idProduct}")
    @ApiOperation(value = "Отобразить страницу с информацией о продукте.")
    public ModelAndView showProduct(
            @ApiParam(value = "Id выбранного продукта.", required = true) @PathVariable String idProduct,
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user){
        ModelAndView mav = new ModelAndView("/pages/for_product/showProduct");

        user = userService.getUserById(user.getId());

        ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), messageGenerator, "showProduct", mav);

        Map<String, String> errors = new HashMap<>();

        if (idProduct.equals("")){
                errors.put(
                        ControllerUtils.constructError("prod"),
                        messageGenerator.getMessageErrorProperty(
                                MessageGenerator.FAIL_WHIS_OTHER_ERROR,
                                ConfigureErrors.SELECT_ENTITY_NOTHIN.toString(), "showProduct", user.getLanguage()
                        )
                );
        }

        Product product = productService.getProductById(Long.parseLong(idProduct));

        if (product == null){
            mav.setViewName("redirect:/product/product_list");
        }else {
            this.showInfoProduct(mav, user, product, errors);
        }

        return mav;
    }

    @PostMapping("/show_product/{idProduct}/add_comment")
    @ApiOperation(value = "Добавить камментарий к продукту.")
    public ModelAndView addComment(
            @ApiParam(value = "Id выбранного продукта.", required = true) @PathVariable String idProduct,
            @ApiParam(value = "Комментарий для продукта.", required = true) @ModelAttribute @Valid UserProdCom comment,
            @ApiParam(value = "Сообщения о возможных ошибках.") BindingResult bindingResult,
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user
    ){
        ModelAndView mav = new ModelAndView("/pages/for_product/showProduct");
        Map<String, String> errors = new HashMap<>();

        user = userService.getUserById(user.getId());

        ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), messageGenerator, "showProduct", mav);

        if (bindingResult.hasErrors()){
                errors.putAll(messageGenerator.getErrors(bindingResult, user.getLanguage()));
        }else {
            productService.addComment(comment.getComment(), user.getId(), Long.parseLong(idProduct));
        }
        Product product = productService.getProductById(Long.parseLong(idProduct));

        this.showInfoProduct(mav, user, product, errors);

        return mav;
    }

    @ApiOperation(value = "Дописывает необходимую информацию о продукте.")
    private void showInfoProduct(
            @ApiParam(value = "Модель хранящая параметры для передачи на экран.", required = true)  ModelAndView mav,
            @ApiParam(value = "Данные пользователя.", required = true) User user,
            @ApiParam(value = "Id выбранного продукта.", required = true) Product product,
            @ApiParam(value = "Информация об ошибках.", required = true) Map<String, String> errors
    ){

        mav.addObject("product", product);
        List<UserProdCom> coments = new ArrayList<>(product.getComents());
        mav.addObject("user", user);
        Collections.sort(coments);
        mav.addObject("orederedComment", coments);
        mav.addAllObjects(errors);
    }

    @GetMapping("/edit_product/{idProduct}")
    @ApiOperation(value = "Отобразить страницу для обновления информации о продукте.")
    public ModelAndView showEditProduct(
            @ApiParam(value = "Id выбранного продукта.", required = true) @PathVariable String idProduct,
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user
    ){
        ModelAndView mav = new ModelAndView("/pages/for_product/editProduct");

        user = userService.getUserById(user.getId());

        ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), messageGenerator, "editProduct", mav);

        if (user.getRole().getName().equals(Role.SELLER) || user.getRole().getName().equals(Role.ADMIN)) {
            mav.addObject("listSizeUser", productService.getAllSizeUser());
            mav.addObject("listCategory", productService.getAllCategory());

            Product product = productService.getProductById(Long.parseLong(idProduct));

            if (product != null) {
                mav.addObject("price", product.getPrice().toString());
                mav.addObject("product", product);
                ArrayList<Long> sizeUsers = new ArrayList<>();
                for (SizeUser sUser : product.getSizeUsers()) {
                    sizeUsers.add(sUser.getId());
                }
                this.constructPageActionWithProd(product.getCategory().getId(), sizeUsers, mav);
            } else {
                    mav.addObject(ControllerUtils.constructError("prod"),
                            messageGenerator.getMessageErrorProperty(
                            MessageGenerator.FAIL_WHIS_OTHER_ERROR,
                            ConfigureErrors.SELECT_ENTITY_NOTHIN.toString(),
                            "showEditProduct",
                            user.getLanguage()
                            )
                    );
                mav.setViewName("redirect:/product/product_list");
            }
        }else {
                return messageGenerator.createMessageForHacker(user.getLanguage());
        }
        return mav;
    }

    @PostMapping("/edit_product/{idProduct}")
    @ApiOperation(value = "Изменить данные продукта.")
    public ModelAndView editProduct(
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user,
            @ApiParam(value = "Id выбранного продукта.", required = true) @PathVariable String idProduct,
            @ApiParam(value = "Выдергивает продукт с формы.", required = true) @ModelAttribute @Valid Product product,
            @ApiParam(value = "Сообщения о возможных ошибках.", required = true) BindingResult bindingResult,
            @ApiParam(value = "Id выбранной категории продукта.", required = true) @RequestParam Long categoryProd,
            @ApiParam(value = "Id выбранных размеров товара.", required = true) @RequestParam ArrayList<Long> sizeUsersProd,
            @ApiParam(value = "Выбранные пользователем файлы картинок.", required = true) @RequestParam("file") List<MultipartFile> file
            ){

        ModelAndView mav = new ModelAndView("redirect:/product/show_product/" + idProduct);

        user = userService.getUserById(user.getId());

        Product oldProduct = productService.getProductById(Long.parseLong(idProduct));

        if (oldProduct == null) {
                mav.addObject(ControllerUtils.constructError("prod"),
                        messageGenerator.getMessageErrorProperty(
                                MessageGenerator.FAIL_WHIS_OTHER_ERROR,
                                ConfigureErrors.SELECT_ENTITY_NOTHIN.toString(),
                                "editProduct",
                                user.getLanguage()
                        )
                );
            mav.setViewName("redirect:/product/product_list");

            return mav;
        }

        product.setId(oldProduct.getId());
        Map<String, String> errors = new HashMap<>();

        if (categoryProd == null){
                errors.put(
                        ControllerUtils.constructError("categoryProd"),
                        messageGenerator.getMessageErrorProperty(
                                MessageGenerator.FAIL_WHIS_OTHER_ERROR,
                                ConfigureErrors.SELECT_CATEGORY.toString(),
                                "editProduct", user.getLanguage()
                        )
                );
        }

        if (sizeUsersProd.size() == 0){
                errors.put(
                        ControllerUtils.constructError("sizeUsersProd"),
                        messageGenerator.getMessageErrorProperty(
                                MessageGenerator.FAIL_WHIS_OTHER_ERROR,
                                ConfigureErrors.SELECT_SIZE.toString(),
                                "editProduct", user.getLanguage()
                        )
                );
        }

        if (file.size() > 4){
                errors.put(
                        ControllerUtils.constructError("message"),
                        messageGenerator.getMessageErrorProperty(
                                MessageGenerator.FAIL_WHIS_OTHER_ERROR,
                                ConfigureErrors.COUNT_PHOTO_BEGER.toString(),
                                "editProduct", user.getLanguage()
                        )
                );
        }

        if (bindingResult.hasErrors()){
                errors.putAll(messageGenerator.getErrors(bindingResult, user.getLanguage()));
        }

        if (errors.isEmpty()) {
            try {
                productService.deletePhotoProduct(oldProduct.getId());
                productService.updateProduct(user.getLanguage(), product, oldProduct, categoryProd, sizeUsersProd, file);
            } catch (IOException | ProductExeption e) {
                log.error(e.getMessage());

                mav.setViewName("/pages/for_product/editProduct");
                ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), messageGenerator, "editProduct", mav);
                this.constructPageActionWithProd(categoryProd, sizeUsersProd, mav);
                mav.addObject(ControllerUtils.constructError("message"), e.getMessage());
                mav.addObject("product", product);
            }
        }else{
            mav.setViewName("/pages/for_product/editProduct");
            ControllerUtils.addNeedForLanguage(user, userService.getListNamesLanguage(), messageGenerator, "editProduct", mav);
            this.constructPageActionWithProd(categoryProd, sizeUsersProd, mav);
            mav.addAllObjects(errors);
            mav.addObject("product",product);
        }

        return mav;
    }

    @ApiOperation(value = "Дописывает информацию необходимую для выбора продукта.")
    private void constructPageActionWithProd(
            @ApiParam(value = "Категория продукта.", required = true) Long categoryProd,
            @ApiParam(value = "Размеры товара.", required = true) ArrayList<Long> sizeUsersProd,
            @ApiParam(value = "Модель хранящая параметры для передачи на экран.", required = true) ModelAndView mav) {

        mav.addObject("listSizeUser", productService.getAllSizeUser());
        mav.addObject("listCategory", productService.getAllCategory());
        mav.addObject("sizeUsersProd", sizeUsersProd);
        mav.addObject("categoryProd", categoryProd);
    }

    @GetMapping("/download/{idProduct}")
    @ApiOperation(value = "Скачать изображение товара.")
    public void downloadFile(
            @ApiParam(value = "Для передачи файла на сторону клиента и информации о нем.", required = true) HttpServletResponse resonse,
            @ApiParam(value = "Id выбранного продукта.", required = true) @PathVariable String idProduct) throws IOException {

        Product product = productService.getProductById(Long.parseLong(idProduct));

        PhotoProduct photoProduct = product.getPhotoProducts().stream().findFirst().get();

        String path = uploadPath + "/" + photoProduct.getName();
        ServiceUtils.downloadFile(resonse, path);
    }

}
