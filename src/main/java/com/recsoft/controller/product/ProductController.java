package com.recsoft.controller.product;

import com.recsoft.data.entity.PhotoProduct;
import com.recsoft.data.entity.Product;
import com.recsoft.data.entity.User;
import com.recsoft.data.entity.UserProdCom;
import com.recsoft.service.ProductService;
import com.recsoft.service.UserService;
import com.recsoft.utils.ServiceUtils;
import com.recsoft.utils.ControllerUtils;
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
import java.io.*;
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
    public ModelAndView showAddProduct(){
        ModelAndView mav = new ModelAndView("/pages/for_product/addProduct");
        mav.addObject("listSizeUser", productService.getAllSizeUser());
        mav.addObject("listCategory", productService.getAllCategory());
        return mav;
    }

    @PostMapping("/add_product")
    @ApiOperation(value = "Добавить продукты в базу.")
    public ModelAndView addProduct(
            @ApiParam(value = "Выдергивает продукт с формы.", required = true) @ModelAttribute @Valid Product product,
            @ApiParam(value = "Категория выбранного продукта.", required = true) @RequestParam Long categoryProd,
            @ApiParam(value = "Размеры товара выбранного пользователем.", required = true) @RequestParam ArrayList<Long> sizeUsersProd,
            @ApiParam(value = "Выбранные пользователем файлы картинок.", required = true) @RequestParam("file") List<MultipartFile> file,
            @ApiParam(value = "Сообщения о возможных ошибках.", required = true) BindingResult bindingResult
    ){
        Map<String, String> errors = new HashMap<>();
        ModelAndView mav = new ModelAndView("redirect:/product/product_list");

        if (productService.existProduct(product)){
            errors.put(ControllerUtils.constructError("name"), "Продукт с таким именем уже есть");
        }

        if (file.size() > 4){
            errors.put(ControllerUtils.constructError("file"), "Превышено максимальное количество загруженых фотографий.");
        }

        if (bindingResult.hasErrors()){
            errors.putAll(ControllerUtils.getErrors(bindingResult));
        }

        if (errors.isEmpty()) {
            try {
                productService.addProduct(product, categoryProd, sizeUsersProd, file);
            } catch (IOException e) {
                log.error("Ошибка загрузки файла.");

                mav.setViewName("/pages/for_product/addProduct");
                mav.addObject("listSizeUser", productService.getAllSizeUser());
                mav.addObject("listCategory", productService.getAllCategory());
                mav.addObject(ControllerUtils.constructError("file"), "Ошибка загрузки файла.");
                mav.addObject("product",product);
            }

        }else{
            mav.setViewName("/pages/for_product/addProduct");
            mav.addObject("listSizeUser", productService.getAllSizeUser());
            mav.addObject("listCategory", productService.getAllCategory());
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
            @ApiParam(value = "Комментарий для продукта.", required = true) @RequestParam String comment,
            @ApiParam(value = "Авторизированный пользователь системы.", required = true) @AuthenticationPrincipal User user
    ){
        ModelAndView mav = new ModelAndView("/pages/for_product/showProduct");
        Map<String, String> errors = new HashMap<>();


        if (comment.equals("")){
            errors.put(ControllerUtils.constructError("comment"), "Нельзя коментировать пустым текстом");
        }else {
            productService.addComment(comment, user.getId(), Long.parseLong(idProduct));
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
            @ApiParam(value = "Id выбранного продукта.", required = true) @PathVariable String idProduct){
        ModelAndView mav = new ModelAndView("/pages/for_product/editProduct");

        mav.addObject("listSizeUser", productService.getAllSizeUser());
        mav.addObject("listCategory", productService.getAllCategory());

        Product product = productService.getProductById(Long.parseLong(idProduct));
        mav.addObject("price", product.getPrice().toString());

        if (product != null) {
            mav.addObject("product", product);
        }else {
            mav.addObject("prodError", "Такого продукта не существует");
            mav.setViewName("redirect:/product/product_list");
        }
        return mav;
    }

    @PostMapping("/edit_product/{idProduct}")
    @ApiOperation(value = "Отобразить данные пользователя выбранного продавцом.")
    public ModelAndView editProduct(
            @ApiParam(value = "Id выбранного продукта.", required = true) @PathVariable String idProduct,
            @ApiParam(value = "Выдергивает продукт с формы.", required = true) @ModelAttribute @Valid Product product,
            @ApiParam(value = "Id выбранной категории продукта.", required = true) @RequestParam Long categoryProd,
            @ApiParam(value = "Id выбранных размеров товара.", required = true) @RequestParam ArrayList<Long> sizeUsersProd,
            @ApiParam(value = "Выбранные пользователем файлы картинок.", required = true) @RequestParam("file") List<MultipartFile> file,
            @ApiParam(value = "Сообщения о возможных ошибках.", required = true) BindingResult bindingResult){

        ModelAndView mav = new ModelAndView("redirect:/product/show_product/" + idProduct);

        if (productService.getProductById(Long.parseLong(idProduct)) == null) {
            mav.addObject(ControllerUtils.constructError("prod"), "Такого продукта не существует");
            mav.setViewName("redirect:/product/product_list");

            return mav;
        }else {
            product.setId(Long.parseLong(idProduct));
        }

        Map<String, String> errors = new HashMap<>();

        if (file.size() > 4){
            errors.put(ControllerUtils.constructError("file"), "Превышено максимальное количество загруженых фотографий.");
        }

        if (bindingResult.hasErrors()){
            errors.putAll(ControllerUtils.getErrors(bindingResult));
        }

        if (errors.isEmpty()) {
            try {
                productService.deletePhotoProduct(product.getId());
                productService.addProduct(product, categoryProd, sizeUsersProd, file);
            } catch (IOException e) {
                log.error("Ошибка загрузки файла.");

                mav.setViewName("/pages/for_product/editProduct");
                mav.addObject("listSizeUser", productService.getAllSizeUser());
                mav.addObject("listCategory", productService.getAllCategory());
                mav.addObject(ControllerUtils.constructError("file"), "Ошибка загрузки файла.");
                mav.addObject("product",product);
            }
        }else{
            mav.setViewName("/pages/for_product/editProduct");
            mav.addObject("listSizeUser", productService.getAllSizeUser());
            mav.addObject("listCategory", productService.getAllCategory());
            mav.addAllObjects(errors);
            mav.addObject("product",product);
        }

        return mav;
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
