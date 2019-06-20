package com.recsoft.service;

import com.recsoft.data.entity.*;
import com.recsoft.data.repository.*;
import com.recsoft.utils.ServiceUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.validation.Valid;
import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* Представляет функционал для работы с продуктами
 * @author Evgeny Popov
 * */
@Service
@Api(value = "Сервис продуктов",
        description = "Класс-сервис выполняет операции связанные с продуктами, " +
                "отвечающий за целостность базы данных продуктов")
public class ProductService {

    private final Integer HEIGHT_IMAGE = 400, WEIGHT_IMAGE = 400;

    private Logger log = LoggerFactory.getLogger(ProductService.class.getName());

    /*Путь к папке хранения данных*/
    @Value("${upload.path}")
    private String uploadPath;

    private final ProductRepository productRepository;

    private final SizeUserRepository sizeUserRepository;

    private final CategoryRepository categoryRepository;

    private final PhotoRepository photoRepository;

    private final UserRepository userRepository;

    private final UserProdComRepository userProdComRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, SizeUserRepository sizeUserRepository, CategoryRepository categoryRepository, PhotoRepository photoRepository, UserRepository userRepository, UserProdComRepository userProdComRepository) {
        this.productRepository = productRepository;
        this.sizeUserRepository = sizeUserRepository;
        this.categoryRepository = categoryRepository;
        this.photoRepository = photoRepository;
        this.userRepository = userRepository;
        this.userProdComRepository = userProdComRepository;
    }

    public Product getProductById(
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) Long idProd){
        return productRepository.findById(idProd).get();
    }

    /*
     * @return - возвращает список всех продуктов в базе данных*/
    @ApiOperation(value = "Отобразить страницу создания заказа")
    public List<Product> getAllProduct(){
        return productRepository.findAll();
    }

    /*
     * @param product - продукт созданный пользователем
     * @return boolean - если продукт есть в базе возвращает true*/
    @ApiOperation(value = "Отобразить страницу создания заказа")
    public boolean existProduct(
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) Product product){
        if (productRepository.findProductByName(product.getName()) != null) {
            return true;
        }else {
            return false;
        }
    }

    /*
     * @return List<SizeUser>- возвращает список всех размеров для продукта*/
    @ApiOperation(value = "Отобразить страницу создания заказа")
    public List<SizeUser> getAllSizeUser(){
        return sizeUserRepository.findAll();
    }

    /*
     * @param -
     * @return List<Category> - возвращает список всех категорий для продукта
     * */
    @ApiOperation(value = "Отобразить страницу создания заказа")
    public List<Category> getAllCategory(){
        return categoryRepository.findAll();
    }

    /* Добавление продукта в базу данных.
     * @param product - продукт созданный пользователем
     * @param idCategory - id выбранной категории товара
     * @param idSizeUser - список id выбранных размеров товара
     * @param file - загруженные пользователем фотографии
     * */
    @ApiOperation(value = "Отобразить страницу создания заказа")
    public void addProduct(
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) @Valid Product product,
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) Long idCategory,
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) List<Long> idSizeUser,
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) List<MultipartFile> files) throws IOException {
        if (product != null){
            product.setCategory(categoryRepository.findById(idCategory).get());

            Set<SizeUser> sizeUserSet = new HashSet<>();
            for (SizeUser sizeUser: sizeUserRepository.findAll()){
                if (idSizeUser.contains(sizeUser.getId())){
                    sizeUserSet.add(sizeUser);
                }
            }
            product.setSizeUsers(sizeUserSet);

            Set<Photo> photoSet = new HashSet<>();

            for (MultipartFile multipartFile: files){
                ImageIO.read(multipartFile.getInputStream());
                if (multipartFile != null && !multipartFile.getOriginalFilename().isEmpty()) {
                    File uploadDir = new File(uploadPath);

                    if (!uploadDir.exists()) {
                        uploadDir.mkdir();
                    }

                    String resultFilename = ServiceUtils.getUnicalUUID() + "." + multipartFile.getOriginalFilename();

                    ImageIO.write(ServiceUtils.changeImage(multipartFile, WEIGHT_IMAGE, HEIGHT_IMAGE), "JPEG", new File(uploadPath + "/" + resultFilename));

                    //multipartFile.transferTo(new File(uploadPath + "/" + resultFilename));
                    photoSet.add(new Photo(resultFilename, product));
                }
                product.setPhotos(photoSet);
            }

            productRepository.save(product);
            log.info("Product with name " + product.getName() + " was added.");
        }else {
            log.error("Product with name " + product.getName() + " don't added.");
        }
    }

    @ApiOperation(value = "Отобразить страницу создания заказа")
    public void deletePhotoProduct(
            @ApiParam(value = "Удалить фотографию продукта", required = true) Long idProduct){
        Product product = productRepository.findById(idProduct).get();

        for (Photo photo: product.getPhotos()){
            File file = new File(uploadPath + "/" + photo.getName());
            file.delete();
        }
        photoRepository.deleteByProduct(idProduct);
        product.setPhotos(new HashSet<>());
        productRepository.save(product);
    }

    @ApiOperation(value = "Отобразить страницу создания заказа")
    public List<Product> getProductListByCategory(
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) Long idCategory){
        if (idCategory != null) {
            Category category = categoryRepository.findById(idCategory).get();
            return productRepository.findAllByCategory(category);
        }
        return productRepository.findAll();
    }

    @ApiOperation(value = "Добавить комментарий к продукту")
    public void addComment(String comment, Long idUser, Long idProduct){
        Product product = productRepository.findById(idProduct).get();
        User user = userRepository.findById(idUser).get();

        UserProdCom userProdCom = new UserProdCom(comment, user, product);

        userProdCom = userProdComRepository.save(userProdCom);
        log.info("Comment with Id " + userProdCom.getId() + " was added.");

    }

    @ApiOperation(value = "Обновить информацию о продукте")
    public void updateProduct(Product product){
        productRepository.save(product);
        log.info("Product with Id " + product.getId() + " was update.");
    }

    @ApiOperation(value = "Обновить заказы пользователя")
    public void updateProductList(
            @ApiParam(value = "Обновляемые заказы", required = true) List<Product> productList){
        productRepository.saveAll(productList);
    }



}
