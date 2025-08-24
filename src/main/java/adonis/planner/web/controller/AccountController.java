package adonis.planner.web.controller;

import adonis.planner.domain.model.*;
import adonis.planner.service.*;
import adonis.planner.web.dto.AccountDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService svc;
    private final SecurityUtils sec;

    @GetMapping
    public List<Account> list() { return svc.list(sec.current()); }

    @PostMapping
    public ResponseEntity<Account> create(@Valid @RequestBody AccountDTO d) {
        User u = sec.current();
        Account a = Account.builder().user(u).name(d.getName()).type(d.getType())
                .currency(d.getCurrency()).openingBalance(d.getOpeningBalance()).build();
        return ResponseEntity.ok(svc.save(a));
    }
}
