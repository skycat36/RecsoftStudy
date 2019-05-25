package com.recsoft.controller.product;

import com.recsoft.controller.other.ControllerUtils;
import com.recsoft.data.entity.Category;
import com.recsoft.data.entity.Product;
import com.recsoft.data.entity.SizeUser;
import com.recsoft.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/product")
@Api(value = "Product Resource", description = "action with product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/product_list")
    @ApiOperation(value = "List products")
    public ModelAndView getAllProduct() {
        ModelAndView mnv = new ModelAndView("/pages/for_product/productList");
        mnv.addObject("productList", productService.getAllProduct());
        return mnv;
    }

    @GetMapping("/add_product")
    @ApiOperation(value = "add product in database")
    public ModelAndView showAddProduct(){
        ModelAndView mav = new ModelAndView("/pages/for_product/addProduct");
        mav.addObject("listSizeUser", productService.getAllSizeUser());
        mav.addObject("listCategory", productService.getAllCategory());
        return mav;
    }

    @PostMapping("/add_product")
    @ApiOperation(value = "add product in database")
    public ModelAndView addProduct(
            @ModelAttribute @Valid Product product,
            @RequestParam Long categoryProd,
            @RequestParam ArrayList<Long> sizeUsersProd,
            BindingResult bindingResult
    ){
        Map<String, String> errors = new HashMap<>();
        ModelAndView mav = new ModelAndView("redirect:/product/product_list");

        if (productService.existProduct(product)){
            errors.put(ControllerUtils.constructError("Name"), "Продукт с таким именем уже есть");
        }

        if (bindingResult.hasErrors()){
            errors.putAll(ControllerUtils.getErrors(bindingResult));
        }

        if (errors.isEmpty()) {
            productService.addProduct(product, categoryProd, sizeUsersProd);

        }else{
            mav.setViewName("/pages/for_product/addProduct");
            mav.addObject("listSizeUser", productService.getAllSizeUser());
            mav.addObject("listCategory", productService.getAllCategory());
            mav.addAllObjects(errors);
            mav.addObject("product",product);
        }
        return mav;
    }

}
