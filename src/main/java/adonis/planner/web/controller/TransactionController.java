package adonis.planner.web.controller;

import adonis.planner.domain.model.Transaction;
import adonis.planner.domain.model.User;
import adonis.planner.service.TransactionService;
import adonis.planner.web.dto.TransactionDTO;
import adonis.planner.service.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;
    private final SecurityUtils security;

    private User me() {
        return security.current();
    }

    /** Listar transações no intervalo [from, to] (inclusive) */
    @GetMapping
    public List<Transaction> list(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.find(me(), from, to);
    }

    /** Buscar transação por id */
    @GetMapping("/{id}")
    public Transaction get(@PathVariable Long id) {
        return service.findOne(me(), id);
    }

    /** Criar transação */
    @PostMapping
    public ResponseEntity<Transaction> create(@Valid @RequestBody TransactionDTO dto) {
        Transaction saved = service.create(me(), dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    /** Atualizar transação */
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> update(@PathVariable Long id,
                                              @Valid @RequestBody TransactionDTO dto) {
        Transaction updated = service.update(me(), id, dto);
        return ResponseEntity.ok(updated);
    }

    /** Remover transação */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(me(), id);
        return ResponseEntity.noContent().build();
    }
}
