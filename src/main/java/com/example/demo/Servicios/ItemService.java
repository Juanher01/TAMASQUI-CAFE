package com.example.demo.Servicios;

import com.example.demo.Dominio.Category;
import com.example.demo.Dominio.Item;
import com.example.demo.Repositorio.CategoryRepository;
import com.example.demo.Repositorio.ItemRepository;
import com.example.demo.Servicios.dto.CreateItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@Transactional
public class ItemService {

    private final Logger log = LoggerFactory.getLogger(ItemService.class);

    private final ItemRepository itemRepository;

    private final CategoryRepository categoryRepository;


    public ItemService(ItemRepository itemRepository, CategoryRepository categoryRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<Item> getAllItem(Pageable pageable){
        return itemRepository.findAll(pageable);
    } //devuelve lista de todos los item

    public Optional<Item> getOneItem(Long id){
        return itemRepository.findById(id);
    }//devuelve un item especifico

    //Metodo para crear nuevo item
    public Item createItem(CreateItemDTO itemDTO){
        log.debug("Create item {}", itemDTO.toString());
        Item item = new Item();
        item.setName(itemDTO.getName());
        item.setDescription(itemDTO.getDescription());
        item.setImage(itemDTO.getImage());
        item.setPrice(itemDTO.getPrice());
        item.setStock(itemDTO.getStock());
        Category category = categoryRepository.findById(itemDTO.getCategoryId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        item.setCategory(category);
        return itemRepository.save(item);
    }
}
