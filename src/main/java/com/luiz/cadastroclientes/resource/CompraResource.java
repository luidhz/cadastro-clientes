package com.luiz.cadastroclientes.resource;

import com.luiz.cadastroclientes.entity.Compra;
import com.luiz.cadastroclientes.service.CompraService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compras")
@AllArgsConstructor
public class CompraResource {

    private final CompraService compraService;

    @PostMapping
    public ResponseEntity<Compra> insert(@RequestBody Compra compra) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compraService.insert(compra));
    }

    @GetMapping
    public ResponseEntity<List<Compra>> findAll() {
        return ResponseEntity.ok(compraService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Compra> findById(@PathVariable Long id) {
        return ResponseEntity.ok(compraService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Compra> update(@PathVariable Long id, @RequestBody Compra compra) {
        return ResponseEntity.ok(compraService.update(id, compra));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        compraService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
