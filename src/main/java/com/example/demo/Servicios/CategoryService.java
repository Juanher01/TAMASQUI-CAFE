package com.example.demo.Servicios;

import com.example.demo.Dominio.Category;
import com.example.demo.Repositorio.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryService {

    private final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    //metodo para obtener todas las categorias
    public Page<Category> getAllcategory(Pageable pageable){
        return categoryRepository.findAll(pageable);
    }
}
