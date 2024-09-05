package com.example.demo.Controlador;

import com.example.demo.Dominio.Category;
import com.example.demo.Servicios.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
@CrossOrigin("http://127.0.0.1:5501")
public class CategoryController {

    private final Logger log = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;


    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // api para obtener todas las categorías de forma paginada
    @GetMapping("/category")
    public ResponseEntity<List<Category>> getAllCategory(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page,size);
        return new ResponseEntity<>(categoryService.getAllcategory(pageable).getContent(), HttpStatus.OK);}
}
