package com.vishvakta.example.metrics;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/{sku}")
    public ResponseEntity<Integer> getQuantity(@PathVariable String sku) {
        Integer qty = stockService.getQuantity(sku);
        if (qty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(qty);
    }

    @PutMapping("/{sku}")
    public ResponseEntity<Void> addStock(
            @PathVariable String sku,
            @RequestParam int quantity) {
        if (!stockService.addStock(sku, quantity)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{sku}/reserve")
    public ResponseEntity<Boolean> reserve(
            @PathVariable String sku,
            @RequestParam int quantity) {
        boolean ok = stockService.reserve(sku, quantity);
        return ResponseEntity.ok(ok);
    }

    @PostMapping("/{sku}/release")
    public ResponseEntity<Boolean> release(
            @PathVariable String sku,
            @RequestParam int quantity) {
        boolean ok = stockService.release(sku, quantity);
        return ResponseEntity.ok(ok);
    }

    @GetMapping
    public ResponseEntity<Map<String, Integer>> listAll() {
        return ResponseEntity.ok(stockService.getAll());
    }
}
