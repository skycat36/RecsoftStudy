package com.recsoft.service;

import com.recsoft.data.entity.Category;
import com.recsoft.data.entity.Product;
import com.recsoft.data.entity.SizeUser;
import com.recsoft.data.repository.CategoryRepository;
import com.recsoft.data.repository.ProductRepository;
import com.recsoft.data.repository.SizeUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProductService {

    private Logger log = LoggerFactory.getLogger(ProductService.class.getName());

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SizeUserRepository sizeUserRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public ProductService() {
    }

    public List<Product> getAllProduct(){
        return productRepository.findAll();
    }

    public boolean existProduct(Product product){
        if (productRepository.findProductByName(product.getName()) != null) {
            return true;
        }else {
            return false;
        }
    }

    public List<SizeUser> getAllSizeUser(){
        return sizeUserRepository.findAll();
    }

    public List<Category> getAllCategory(){
        return categoryRepository.findAll();
    }

    public void addProduct(Product product, Long idCategory, List<Long> idSizeUser) {
        if (product != null){
            product.setCategory(categoryRepository.findById(idCategory).get());

            Set<SizeUser> sizeUserSet = new HashSet<>();
            for (SizeUser sizeUser: sizeUserRepository.findAll()){
                if (idSizeUser.contains(sizeUser.getId())){
                    sizeUserSet.add(sizeUser);
                }
            }
            product.setSizeUsers(sizeUserSet);
            productRepository.save(product);
            log.info("Product with name " + product.getName() + " was added.");
        }else {
            log.error("Product with name " + product.getName() + " don't added.");
        }
    }
}
