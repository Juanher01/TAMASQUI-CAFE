package com.example.demo.Controlador;

import com.example.demo.Dominio.Item;
import com.example.demo.Seguridad.AuthoritiesConstants;
import com.example.demo.Servicios.ItemService;
import com.example.demo.Servicios.dto.CreateItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("http://127.0.0.1:5501")
public class ItemController {

    private final Logger log = LoggerFactory.getLogger(ItemController.class);

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    //api para obtener todos los items
    @GetMapping("/items")
    public ResponseEntity<List<Item>> getAllItem(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page,size);
        return new ResponseEntity<>(itemService.getAllItem(pageable).getContent(), HttpStatus.OK);
    }

    //api para obtener un item especifico por su id
    @GetMapping("/item/{id}")
    public ResponseEntity<Item> getOneItem(@PathVariable Long id){
        return itemService.getOneItem(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    //api para crear un nuevo item-- solo admin
    @PostMapping("/item")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Item> createItem(@RequestBody CreateItemDTO itemDTO){
        return new ResponseEntity<>(itemService.createItem(itemDTO), HttpStatus.CREATED);
    }
}
