package com.cdweb.Treestore.controller.web;

import com.cdweb.Treestore.domain.Category;
import com.cdweb.Treestore.services.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class CategoryController {
    @Autowired
    private ICategoryService categoryService;

    @PostMapping("/addCategory")
    public CategoryDto save(@RequestBody CategoryDto categoryDTO) {
        return categoryService.save(categoryDTO);
    }
    @GetMapping("/getCategory")
    public List<CategoryDto> get(){
        return categoryService.findAll();
    }

}
