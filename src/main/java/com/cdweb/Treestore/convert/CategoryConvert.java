package com.cdweb.Treestore.convert;

import com.cdweb.Treestore.dto.CategoryDto;
import com.cdweb.Treestore.entity.CategoryEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryConvert {
    @Autowired
    private ModelMapper modelMapper;

    public CategoryDto toDTO(CategoryEntity categoryEntity){
        return modelMapper.map(categoryEntity,CategoryDto.class);
    }
    public CategoryEntity toEntity(CategoryDto categoryDTO){
        return modelMapper.map(categoryDTO, CategoryEntity.class);
    }
}
