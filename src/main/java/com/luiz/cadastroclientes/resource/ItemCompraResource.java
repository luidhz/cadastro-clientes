package com.luiz.cadastroclientes.resource;

import com.luiz.cadastroclientes.entity.ItemCompra;
import com.luiz.cadastroclientes.service.ItemCompraService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/itens-compra")
@AllArgsConstructor
public class ItemCompraResource {

    private final ItemCompraService itemCompraService;

    @PostMapping
    public ResponseEntity<ItemCompra> insert(@RequestBody ItemCompra itemCompra) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemCompraService.insert(itemCompra));
    }

    @GetMapping
    public ResponseEntity<List<ItemCompra>> findAll() {
        return ResponseEntity.ok(itemCompraService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemCompra> findById(@PathVariable Long id) {
        return ResponseEntity.ok(itemCompraService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemCompra> update(@PathVariable Long id, @RequestBody ItemCompra itemCompra) {
        return ResponseEntity.ok(itemCompraService.update(id, itemCompra));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        itemCompraService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
