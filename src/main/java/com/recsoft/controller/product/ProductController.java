package com.recsoft.controller.product;

import com.recsoft.controller.other.ControllerUtils;
import com.recsoft.data.entity.Photo;
import com.recsoft.data.entity.Product;
import com.recsoft.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Предоставляет отображение работы с продуктами.
 * @author Евгений Попов */
@RestController
@RequestMapping("/product")
@Api(value = "Product Resource", description = "action with product")
public class ProductController {

    private Logger log = LoggerFactory.getLogger(ProductController.class.getName());

    /*Путь к папке хранения данных*/
    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private ProductService productService;

    /* @return ModelAndView - Отображает список имеющихся продуктов. */
    @GetMapping("/product_list")
    @ApiOperation(value = "List products")
    public ModelAndView getAllProduct() {
        ModelAndView mnv = new ModelAndView("/pages/for_product/productList");
        mnv.addObject("productList", productService.getAllProduct());
        mnv.addObject("pathFile", this.uploadPath);
        return mnv;
    }

    /* @return ModelAndView - отображает интерфейс для добавления продуктов в базу. */
    @GetMapping("/add_product")
    @ApiOperation(value = "add product in database")
    public ModelAndView showAddProduct(){
        ModelAndView mav = new ModelAndView("/pages/for_product/addProduct");
        mav.addObject("listSizeUser", productService.getAllSizeUser());
        mav.addObject("listCategory", productService.getAllCategory());
        return mav;
    }

    /* @param product - продукт созданный пользователем.
     * @param categoryProd - выбранная категория товара.
     * @param sizeUsersProd - список выбранных размеров.
     * @param file - загруженный пользователем.
     * @param bindingResult - проверка данных на ошибки.
     * @return ModelAndView - добавляет продукт в базу и если нет ошибок возвращает на список товаров.*/
    @PostMapping("/add_product")
    @ApiOperation(value = "add product in database")
    public ModelAndView addProduct(HttpServletRequest request,
                                   @ModelAttribute @Valid Product product,
                                   @RequestParam Long categoryProd,
                                   @RequestParam ArrayList<Long> sizeUsersProd,
                                   @RequestParam("file") List<MultipartFile> file,
                                   BindingResult bindingResult
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
    public ModelAndView showProduct(
            @PathVariable String idProduct){
        ModelAndView mav = new ModelAndView("/pages/for_product/showProduct");

        Product product = productService.getProductById(Long.parseLong(idProduct));

        if (product != null) {
            mav.addObject("product", product);
        }else {
            mav.addObject("prodError", "Такого продукта не существует");
            mav.setViewName("redirect:/product/product_list");
        }
        return mav;
    }

    @GetMapping("/edit_product/{idProduct}")
    public ModelAndView showEditProduct(
            @PathVariable String idProduct){
        ModelAndView mav = new ModelAndView("/pages/for_product/editProduct");

        mav.addObject("listSizeUser", productService.getAllSizeUser());
        mav.addObject("listCategory", productService.getAllCategory());

        Product product = productService.getProductById(Long.parseLong(idProduct));

        if (product != null) {
            mav.addObject("product", product);
        }else {
            mav.addObject("prodError", "Такого продукта не существует");
            mav.setViewName("redirect:/product/product_list");
        }
        return mav;
    }

    @PostMapping("/edit_product/{idProduct}")
    public ModelAndView editProduct(
            @PathVariable String idProduct,
            @ModelAttribute @Valid Product product,
            @RequestParam Long categoryProd,
            @RequestParam ArrayList<Long> sizeUsersProd,
            @RequestParam("file") List<MultipartFile> file,
            BindingResult bindingResult){

        ModelAndView mav = new ModelAndView("redirect:/product/show_product/" + idProduct);

        if (productService.getProductById(Long.parseLong(idProduct)) == null) {
            mav.addObject("prodError", "Такого продукта не существует");
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

        //Product product = productService.getProductById(Long.parseLong(idProduct));
        return mav;
    }

    @GetMapping("/download3/{idProduct}")
    public void downloadFile3(HttpServletResponse resonse,
                              @PathVariable String idProduct) throws IOException {

        Product product = productService.getProductById(Long.parseLong(idProduct));
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        resonse.setContentType(mediaType.getType());
        Photo photo = product.getPhotos().stream().findFirst().get();

        System.out.println("fileName: " + photo.getName());
        System.out.println("mediaType: " + mediaType);

        File file = new File(uploadPath + "/" + photo.getName());


        // Content-Type
        // application/pdf


        // Content-Disposition
        resonse.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName());

        // Content-Length
        resonse.setContentLength((int) file.length());

        BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(file));
        BufferedOutputStream outStream = new BufferedOutputStream(resonse.getOutputStream());


        byte[] buffer = new byte[1024];
        int bytesRead = 0;

        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }

        inStream.close();
        outStream.flush();
    }

//        @GetMapping("/download3/{idProduct}")
//        public void downloadFile3(HttpServletResponse resonse,
//                @PathVariable String idProduct) throws IOException {
//
//            Product product = productService.getProductById(Long.parseLong(idProduct));
////        BufferedInputStream inStream = new BufferedInputStream(null);
////        BufferedOutputStream outStream = new BufferedOutputStream(resonse.getOutputStream());
//            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
//            resonse.setContentType(mediaType.getType());
//            for (Photo photo : product.getPhotos()) {
//                //MediaType mediaType = ControllerUtils.getMediaTypeForFileName(this.servletContext, photo.getName());
//                System.out.println("fileName: " + photo.getName());
//                System.out.println("mediaType: " + mediaType);
//
//                File file = new File(uploadPath + "/" + photo.getName());
//
//
//                // Content-Type
//                // application/pdf
//
//
//                // Content-Disposition
//                resonse.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName());
//
//                // Content-Length
//                resonse.setContentLength((int) file.length());
//
//                BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(file));
//                BufferedOutputStream outStream = new BufferedOutputStream(resonse.getOutputStream());
//
//
//                byte[] buffer = new byte[1024];
//                int bytesRead = 0;
//
//                while ((bytesRead = inStream.read(buffer)) != -1) {
//                    outStream.write(buffer, 0, bytesRead);
//                }
//
//
//                inStream.close();
//                outStream.flush();
//            }
//        }



}
