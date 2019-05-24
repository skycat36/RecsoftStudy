package com.recsoft.service;

import com.recsoft.data.entity.Product;
import com.recsoft.data.exeption.ProductExeption;
import com.recsoft.data.repository.ProductRepository;
import com.recsoft.data.repository.SizeUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    private Logger log = LoggerFactory.getLogger(ProductService.class.getName());

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SizeUserRepository sizeUserRepository;


    public ProductService() {
    }

    public Map<String, List<Product>> getAllProduct(){
        Map<String, List<Product>> map = new HashMap<>();
        map.put("listProduct", productRepository.findAll());
        log.info("Get all product");
        return map;
    }

    public boolean existProduct(Product product){
        if (productRepository.findProductByName(product.getName()) != null) {
            return true;
        }else {
            return false;
        }
    }

    public void addProduct(Product product) {
        if (product != null){
            productRepository.save(product);
            log.info("Product with name " + product.getName() + " was added.");
        }else {
            log.error("Product with name " + product.getName() + " don't added.");
        }
    }
}
