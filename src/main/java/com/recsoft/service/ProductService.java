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

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
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

    @Value("${weight.img}")
    private Integer HEIGHT_IMAGE;

    @Value("${height.img}")
    private Integer WEIGHT_IMAGE;

    private Logger log = LoggerFactory.getLogger(ProductService.class.getName());

    /*Путь к папке хранения данных*/
    @Value("${upload.path}")
    private String uploadPath;

    private ProductRepository productRepository;

    private SizeUserRepository sizeUserRepository;

    private CategoryRepository categoryRepository;

    private PhotoProductRepository photoProductRepository;

    private UserRepository userRepository;

    private UserProdComRepository userProdComRepository;

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    public void setSizeUserRepository(SizeUserRepository sizeUserRepository) {
        this.sizeUserRepository = sizeUserRepository;
    }

    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Autowired
    public void setPhotoProductRepository(PhotoProductRepository photoProductRepository) {
        this.photoProductRepository = photoProductRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setUserProdComRepository(UserProdComRepository userProdComRepository) {
        this.userProdComRepository = userProdComRepository;
    }

    public Product getProductById(
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) Long idProd){
        return productRepository.findById(idProd).get();
    }

    @ApiOperation(value = "Вернуть список всех продуктов")
    public List<Product> getAllProduct(){
        return productRepository.findAll();
    }

    @ApiOperation(value = "Проверить существует ли продукт.")
    public boolean existProduct(
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) Product product){
        return productRepository.findProductByName(product.getName()) != null;
    }

    @ApiOperation(value = "Вернуть список всех размеров.")
    public List<SizeUser> getAllSizeUser(){
        return sizeUserRepository.findAll();
    }

    @ApiOperation(value = "Вернуть список всех категорий.")
    public List<Category> getAllCategory(){
        return categoryRepository.findAll();
    }

    @ApiOperation(value = "Создать продукт.")
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

            Set<PhotoProduct> photoProductSet = new HashSet<>();

            for (MultipartFile multipartFile: files){
                photoProductSet.add(new PhotoProduct(ServiceUtils.saveFile(multipartFile, WEIGHT_IMAGE, HEIGHT_IMAGE, uploadPath), product));
            }
            product.setPhotoProducts(photoProductSet);
            productRepository.save(product);
            log.info("Product with name " + product.getName() + " was added.");
        }else {
            log.error("Product with name " + product.getName() + " don't added.");
        }
    }

    @ApiOperation(value = "Удалить фотографию продукта.")
    public void deletePhotoProduct(
            @ApiParam(value = "Удалить фотографию продукта", required = true) Long idProduct){
        Product product = productRepository.findById(idProduct).get();

        for (PhotoProduct photoProduct : product.getPhotoProducts()){
            File file = new File(uploadPath + "/" + photoProduct.getName());
            file.delete();
        }
        photoProductRepository.deleteByProduct(idProduct);
        product.setPhotoProducts(new HashSet<>());
        productRepository.save(product);
    }

    @ApiOperation(value = "Отобразить продукты по категории.")
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
