package com.cdweb.Treestore.controller.web;

<<<<<<< Updated upstream
import com.cdweb.Treestore.domain.Category;
import com.cdweb.Treestore.dto.CategoryDto;
import com.cdweb.Treestore.services.ICategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
=======
import com.cdweb.Treestore.dto.CategoryDto;
import com.cdweb.Treestore.services.ICategoryService;

import org.springframework.beans.factory.annotation.Autowired;

>>>>>>> Stashed changes
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
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
