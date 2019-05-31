package com.recsoft.service;

import com.recsoft.data.entity.Category;
import com.recsoft.data.entity.Photo;
import com.recsoft.data.entity.Product;
import com.recsoft.data.entity.SizeUser;
import com.recsoft.data.repository.CategoryRepository;
import com.recsoft.data.repository.PhotoRepository;
import com.recsoft.data.repository.ProductRepository;
import com.recsoft.data.repository.SizeUserRepository;
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
import java.util.UUID;

/* Представляет функционал для работы с продуктами
* @author Evgeny Popov
* */
@Service
public class ProductService {

    private Logger log = LoggerFactory.getLogger(ProductService.class.getName());

    /*Путь к папке хранения данных*/
    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SizeUserRepository sizeUserRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PhotoRepository photoRepository;

    public ProductService() {
    }

    public Product getProductById(Long idProd){
        return productRepository.findById(idProd).get();
    }

    /*
     * @return - возвращает список всех продуктов в базе данных*/
    public List<Product> getAllProduct(){
        return productRepository.findAll();
    }

    /*
     * @param product - продукт созданный пользователем
     * @return boolean - если продукт есть в базе возвращает true*/
    public boolean existProduct(Product product){
        if (productRepository.findProductByName(product.getName()) != null) {
            return true;
        }else {
            return false;
        }
    }

    /*
     * @return List<SizeUser>- возвращает список всех размеров для продукта*/
    public List<SizeUser> getAllSizeUser(){
        return sizeUserRepository.findAll();
    }

    /*
     * @param -
     * @return List<Category> - возвращает список всех категорий для продукта*/
    public List<Category> getAllCategory(){
        return categoryRepository.findAll();
    }

    /* Добавление продукта в базу данных.
     * @param product - продукт созданный пользователем
     * @param idCategory - id выбранной категории товара
     * @param idSizeUser - список id выбранных размеров товара
     * @param file - загруженные пользователем фотографии
     * */
    public void addProduct(@Valid Product product, Long idCategory, List<Long> idSizeUser, List<MultipartFile> files) throws IOException {
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
            if (multipartFile != null && !multipartFile.getOriginalFilename().isEmpty()) {
                File uploadDir = new File(uploadPath);

                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }

                String uuidFile = UUID.randomUUID().toString();
                String resultFilename = uuidFile + "." + multipartFile.getOriginalFilename();

                multipartFile.transferTo(new File(uploadPath + "/" + resultFilename));

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

//    private void saveFile(Product product, MultipartFile file) throws IOException {
//        if (file != null && !file.getOriginalFilename().isEmpty()) {
//            File uploadDir = new File(uploadPath);
//
//            if (!uploadDir.exists()) {
//                uploadDir.mkdir();
//            }
//
//            String uuidFile = UUID.randomUUID().toString();
//            String resultFilename = uuidFile + "." + file.getOriginalFilename();
//
//            file.transferTo(new File(uploadPath + "/" + resultFilename));
//
//            Photo photo = photoRepository.save(new Photo(resultFilename, null, product));
//            product.getPhotos().add(photo);
//            productRepository.save(product);
//        }
//    }
}
