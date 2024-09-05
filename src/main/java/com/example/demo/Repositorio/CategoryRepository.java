package com.example.demo.Repositorio;

import com.example.demo.Dominio.Category;
import com.example.demo.Dominio.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
