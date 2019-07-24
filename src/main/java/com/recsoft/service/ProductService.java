package com.recsoft.service;

import com.recsoft.data.entity.*;
import com.recsoft.data.exeption.ProductExeption;
import com.recsoft.data.repository.*;
import com.recsoft.utils.ServiceUtils;
import com.recsoft.utils.constants.ConfigureErrors;
import com.recsoft.validation.MessageGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Api(value = "Сервис продуктов",
        description = "Класс-сервис выполняет операции связанные с продуктами, " +
                "отвечающий за целостность базы данных продуктов")
public class ProductService {

    @ApiModelProperty(notes = "Высота изображения", name = "HEIGHT_IMAGE", required = true)
    @Value("${weight.img}")
    private Integer HEIGHT_IMAGE;

    @ApiModelProperty(notes = "Ширина изображения", name = "WEIGHT_IMAGE",required=true)
    @Value("${height.img}")
    private Integer WEIGHT_IMAGE;

    @ApiModelProperty(notes = "Записывает логи сделанных действий и ошибок.", name = "log", value = "ProductController")
    private Logger log = LoggerFactory.getLogger(ProductService.class.getName());

    @ApiModelProperty(notes = "Путь до файла хранимых изображений", required=true)
    @Value("${upload.path}")
    private String uploadPath;

    private ProductRepository productRepository;

    private SizeUserRepository sizeUserRepository;

    private CategoryRepository categoryRepository;

    private PhotoProductRepository photoProductRepository;

    private UserRepository userRepository;

    private UserProdComRepository userProdComRepository;

    private MessageGenerator messageGenerator;

    @Autowired
    public void setMessageGenerator(MessageGenerator messageGenerator) {
        this.messageGenerator = messageGenerator;
    }

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

    @ApiOperation(value = "Вернуть продукт по ID")
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
            @ApiParam(value = "Генератор сообщений пользователя.", required = true) Language language,
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) Product product,
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) Long idCategory,
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) List<Long> idSizeUser,
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) List<MultipartFile> files) throws IOException, ProductExeption {

        if (product != null){
            this.createRelationsForParamersProduct(language, idCategory, product, idSizeUser, files);
            productRepository.save(product);
            log.info("Product with name " + product.getName() + " was added.");
        }else {
            log.error("Product with name " + product.getName() + " don't added.");
        }
    }

    private void createRelationsForParamersProduct(
            @ApiParam(value = "Генератор сообщений пользователя.", required = true) Language language,
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) Long idCategory,
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) Product product,
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) List<Long> idSizeUser,
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) List<MultipartFile> files) throws IOException, ProductExeption {

        Category category = categoryRepository.findById(idCategory).orElse(null);
        if (category == null) {
            throw new ProductExeption(
                    messageGenerator.getMessageErrorProperty(
                            MessageGenerator.FAIL_WHIS_OTHER_ERROR,
                            ConfigureErrors.HACKER_GO_OUT.toString(),
                            "createRelationsForParamersProduct",
                            language
                    )
            );
        }

        product.setCategory(category);

        if (idSizeUser.isEmpty()){
            throw new ProductExeption(
                    messageGenerator.getMessageErrorProperty(
                            MessageGenerator.FAIL_WHIS_OTHER_ERROR,
                            ConfigureErrors.SELECT_SIZE.toString(),
                            "createRelationsForParamersProduct",
                            language
                    )
            );
        }

        Set<SizeUser> sizeUserSet = new HashSet<>();
        for (SizeUser sizeUser: sizeUserRepository.findAll()){
            if (idSizeUser.contains(sizeUser.getId())){
                sizeUserSet.add(sizeUser);
            }
        }
        product.setSizeUsers(sizeUserSet);

        if (ServiceUtils.proveListOnEmptyFileList(files)) {
            Set<PhotoProduct> photoProductSet = new HashSet<>();

            for (MultipartFile multipartFile : files) {
                if (multipartFile.getSize() > 0) {
                    photoProductSet.add(new PhotoProduct(ServiceUtils.saveFile(language, multipartFile, WEIGHT_IMAGE, HEIGHT_IMAGE, uploadPath), product));
                }
            }
            product.setPhotoProducts(photoProductSet);
        }
    }

    @ApiOperation(value = "Удалить фотографии продукта.")
    public void deletePhotoProduct(
            @ApiParam(value = "Id продукта", required = true) Long idProduct
    ){

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
            @ApiParam(value = "Id категории", required = true) Long idCategory){

        if (idCategory != null) {
            Category category = categoryRepository.findById(idCategory).get();
            return productRepository.findAllByCategory(category);
        }
        return productRepository.findAll();
    }

    @ApiOperation(value = "Добавить комментарий к продукту")
    public void addComment(
            @ApiParam(value = "Текст коментария", required = true) String comment,
            @ApiParam(value = "Id пользователя", required = true) Long idUser,
            @ApiParam(value = "Id продукта", required = true) Long idProduct
    ){

        Product product = productRepository.findById(idProduct).get();
        User user = userRepository.findById(idUser).get();

        UserProdCom userProdCom = new UserProdCom(comment, user, product);

        userProdCom = userProdComRepository.save(userProdCom);
        log.info("Comment with Id " + userProdCom.getId() + " was added.");
    }

    @ApiOperation(value = "Обновить информацию о продукте")
    public void updateProduct(
            @ApiParam(value = "Генератор сообщений пользователя.", required = true) Language language,
            @ApiParam(value = "Обновленный продукт", required = true) Product productReal,
            @ApiParam(value = "Текущий продукт", required = true) Product productOld,
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) Long idCategory,
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) List<Long> idSizeUser,
            @ApiParam(value = "Выдергивает пользователя авторизованного", required = true) List<MultipartFile> files) throws IOException, ProductExeption {

        this.createRelationsForParamersProduct(language, idCategory, productOld, idSizeUser, files);

        productOld.setCount(productReal.getCount());
        productOld.setDiscount(productReal.getDiscount());
        productOld.setDescription(productReal.getDescription());
        productOld.setPrice(productReal.getPrice());
        productOld.setName(productReal.getName());
        productRepository.save(productOld);
        log.info("Product with Id " + productReal.getId() + " was update.");
    }
}
