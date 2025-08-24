package adonis.planner.web.controller;

import adonis.planner.domain.model.*;
import adonis.planner.service.*;
import adonis.planner.web.dto.TransactionDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService svc;
    private final SecurityUtils sec;

    @GetMapping
    public List<Transaction> list(@RequestParam java.time.LocalDate from, @RequestParam java.time.LocalDate to) {
        return svc.find(sec.current(), from, to);
    }

    @PostMapping
    public ResponseEntity<Transaction> create(@Valid @RequestBody TransactionDTO d) {
        Transaction t = new Transaction();
        t.setDate(d.getDate());
        t.setAmount(d.getAmount());
        t.setType(d.getType());
        t.setDescription(d.getDescription());
        Account a = new Account(); a.setId(d.getAccountId());
        t.setAccount(a);
        return ResponseEntity.ok(svc.create(sec.current(), t));
    }
}
