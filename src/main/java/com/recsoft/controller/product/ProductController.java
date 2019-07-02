package com.recsoft.controller.product;

import com.recsoft.data.entity.*;
import com.recsoft.data.exeption.ProductExeption;
import com.recsoft.service.ProductService;
import com.recsoft.service.UserService;
import com.recsoft.utils.ControllerUtils;
import com.recsoft.utils.ServiceUtils;
import io.swagger.annotations.Api;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

/* Предоставляет отображение работы с продуктами.
 * @author Евгений Попов */
@RestController
@RequestMapping("/product")
@Api(value = "Контроллер продуктов", description = "Класс-контроллер отвечающий за работу с продуктами")
public class ProductController {

    private Logger log = LoggerFactory.getLogger(ProductController.class.getName());

    /*Путь к папке хранения данных*/
    @Value("${upload.path}")
    private String uploadPath;

    private ProductService productService;

    private UserService userService;

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /* @return ModelAndView - Отображает список имеющихся продуктов. */
    @GetMapping("/product_list")
    @ApiOperation(value = "Отображает все имеющиеся продукты на страницу")
    public ModelAndView getAllProduct(
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user
    ) {
        ModelAndView mnv = new ModelAndView("/pages/for_product/productList");
        mnv.addObject("user", user);
        mnv.addObject("listCategory", productService.getAllCategory());
        mnv.addObject("productList", productService.getAllProduct());
        return mnv;
    }

    /* @return ModelAndView - Отображает список имеющихся продуктов. */
    @GetMapping("/product_list/filter")
    @ApiOperation(value = "Отображает все имеющиеся продукты на страницу")
    public ModelAndView getProductFilter(
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user,
            @ApiParam(value = "Категория выбранного продукта.", required = true) @RequestParam Long selectCategory
    ) {
        ModelAndView mnv = new ModelAndView("/pages/for_product/productList");
        mnv.addObject("user", user);
        mnv.addObject("productList", productService.getProductListByCategory(selectCategory));
        mnv.addObject("categoryProd", selectCategory);
        mnv.addObject("listCategory", productService.getAllCategory());
        return mnv;
    }

    /* @return ModelAndView - отображает интерфейс для добавления продуктов в базу. */
    @GetMapping("/add_product")
    @ApiOperation(value = "Отображает страницу добавления продуктов")
    public ModelAndView showAddProduct(
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user
    ){
        ModelAndView mav = new ModelAndView("/pages/for_product/addProduct");

        user = userService.getUserById(user.getId());
        if (user.getRole().getName().equals(ControllerUtils.SELLER) || user.getRole().getName().equals(ControllerUtils.ADMIN)) {
            mav.addObject("listSizeUser", productService.getAllSizeUser());
            mav.addObject("listCategory", productService.getAllCategory());
        }else {
            return ControllerUtils.createMessageForHacker();
        }
        return mav;
    }

    @PostMapping("/add_product")
    @ApiOperation(value = "Добавить продукты в базу.")
    public ModelAndView addProduct(
            @ApiParam(value = "Выдергивает продукт с формы.", required = true) @ModelAttribute @Valid Product product,
            @ApiParam(value = "Сообщения о возможных ошибках.", required = true) BindingResult bindingResult,
            @ApiParam(value = "Категория выбранного продукта.", required = true) @RequestParam Long categoryProd,
            @ApiParam(value = "Размеры товара выбранного пользователем.", required = true) @RequestParam ArrayList<Long> sizeUsersProd,
            @ApiParam(value = "Выбранные пользователем файлы картинок.", required = true) @RequestParam("file") List<MultipartFile> file
    ){
        Map<String, String> errors = new HashMap<>();
        ModelAndView mav = new ModelAndView("redirect:/product/product_list");

        if (productService.existProduct(product)){
            errors.put(ControllerUtils.constructError("name"), "Продукт с таким именем уже есть");
        }

        if (file.size() > 4){
            errors.put(ControllerUtils.constructError("message"), "Превышено максимальное количество загруженых фотографий.");
        }

        if (bindingResult.hasErrors()){
            errors.putAll(ControllerUtils.getErrors(bindingResult));
        }

        if (errors.isEmpty()) {
            try {
                productService.addProduct(product, categoryProd, sizeUsersProd, file);
            } catch (IOException | ProductExeption e) {
                log.error(e.getMessage());

                mav.setViewName("/pages/for_product/addProduct");
                constructPageActionWithProd(categoryProd, sizeUsersProd, mav);
                mav.addObject(ControllerUtils.constructError("message"), e.getMessage());
                mav.addObject("product",product);
            }

        }else{
            mav.setViewName("/pages/for_product/addProduct");
            constructPageActionWithProd(categoryProd, sizeUsersProd, mav);
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

        Map<String, String> errors = new HashMap<>();

        if (idProduct.equals("")){
            errors.put(ControllerUtils.constructError("prod"), "Такого продукта не существует");
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
    public ModelAndView addComment(
            @ApiParam(value = "Id выбранного продукта.", required = true) @PathVariable String idProduct,
            @ApiParam(value = "Комментарий для продукта.", required = true) @ModelAttribute @Valid UserProdCom comment,
            BindingResult bindingResult,
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user
    ){
        ModelAndView mav = new ModelAndView("/pages/for_product/showProduct");
        Map<String, String> errors = new HashMap<>();

        if (bindingResult.hasErrors()){
            errors.putAll(ControllerUtils.getErrors(bindingResult));
        }else {
            productService.addComment(comment.getComment(), user.getId(), Long.parseLong(idProduct));
        }
        Product product = productService.getProductById(Long.parseLong(idProduct));

        this.showInfoProduct(mav, user, product, errors);

        return mav;
    }

    private void showInfoProduct(
            @ApiParam(value = "Модель хранящая параметры для передачи на экран.", required = true)  ModelAndView mav,
            @ApiParam(value = "Данные пользователя.", required = true) User user,
            @ApiParam(value = "Id выбранного продукта.", required = true) Product product,
            @ApiParam(value = "Информация об ошибках.", required = true) Map<String, String> errors
    ){
        mav.addObject("user", user);
        mav.addObject("product", product);
        List<UserProdCom> coments = new ArrayList<>(product.getComents());
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
        if (user.getRole().getName().equals(ControllerUtils.SELLER) || user.getRole().getName().equals(ControllerUtils.ADMIN)) {
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
                mav.addObject("prodError", "Такого продукта не существует");
                mav.setViewName("redirect:/product/product_list");
            }
        }else {
            return ControllerUtils.createMessageForHacker();
        }
        return mav;
    }

    @PostMapping("/edit_product/{idProduct}")
    @ApiOperation(value = "Отобразить данные пользователя выбранного продавцом.")
    public ModelAndView editProduct(
            @ApiParam(value = "Id выбранного продукта.", required = true) @PathVariable String idProduct,
            @ApiParam(value = "Выдергивает продукт с формы.", required = true) @ModelAttribute @Valid Product product,
            @ApiParam(value = "Сообщения о возможных ошибках.", required = true) BindingResult bindingResult,
            @ApiParam(value = "Id выбранной категории продукта.", required = true) @RequestParam Long categoryProd,
            @ApiParam(value = "Id выбранных размеров товара.", required = true) @RequestParam ArrayList<Long> sizeUsersProd,
            @ApiParam(value = "Выбранные пользователем файлы картинок.", required = true) @RequestParam("file") List<MultipartFile> file
            ){

        ModelAndView mav = new ModelAndView("redirect:/product/show_product/" + idProduct);

        Product oldProduct = productService.getProductById(Long.parseLong(idProduct));

        if (oldProduct == null) {
            mav.addObject(ControllerUtils.constructError("prod"), "Такого продукта не существует");
            mav.setViewName("redirect:/product/product_list");

            return mav;
        }

        product.setId(oldProduct.getId());
        Map<String, String> errors = new HashMap<>();

        if (categoryProd == null){
            errors.put(ControllerUtils.constructError("categoryProd"), "Выбирете категорию товара.");
        }

        if (sizeUsersProd.size() == 0){
            errors.put(ControllerUtils.constructError("sizeUsersProd"), "Выбирете размер товара.");
        }

        if (file.size() > 4){
            errors.put(ControllerUtils.constructError("message"), "Превышено максимальное количество загруженых фотографий.");
        }

        if (bindingResult.hasErrors()){
            errors.putAll(ControllerUtils.getErrors(bindingResult));
        }

        if (errors.isEmpty()) {
            try {
                productService.deletePhotoProduct(oldProduct.getId());
                productService.updateProduct(product, oldProduct, categoryProd, sizeUsersProd, file);
            } catch (IOException | ProductExeption e) {
                log.error(e.getMessage());

                mav.setViewName("/pages/for_product/editProduct");
                this.constructPageActionWithProd(categoryProd, sizeUsersProd, mav);
                mav.addObject(ControllerUtils.constructError("message"), e.getMessage());
                mav.addObject("product", product);
            }
        }else{
            mav.setViewName("/pages/for_product/editProduct");
            this.constructPageActionWithProd(categoryProd, sizeUsersProd, mav);
            mav.addAllObjects(errors);
            mav.addObject("product",product);
        }

        return mav;
    }

    private void constructPageActionWithProd(Long categoryProd, ArrayList<Long> sizeUsersProd, ModelAndView mav) {
        mav.addObject("listSizeUser", productService.getAllSizeUser());
        mav.addObject("listCategory", productService.getAllCategory());
        mav.addObject("sizeUsersProd", sizeUsersProd);
        mav.addObject("categoryProd", categoryProd);
    }

    @GetMapping("/download/{idProduct}")
    @ApiOperation(value = "Отобразить данные пользователя выбранного продавцом.")
    public void downloadFile(
            @ApiParam(value = "Для передачи файла на сторону клиента и информации о нем.", required = true) HttpServletResponse resonse,
            @ApiParam(value = "Id выбранного продукта.", required = true) @PathVariable String idProduct) throws IOException {

        Product product = productService.getProductById(Long.parseLong(idProduct));

        PhotoProduct photoProduct = product.getPhotoProducts().stream().findFirst().get();

        String path = uploadPath + "/" + photoProduct.getName();
        ServiceUtils.downloadFile(resonse, path);
    }

}
