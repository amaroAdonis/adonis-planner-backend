package adonis.planner.service;

import adonis.planner.domain.model.*;
import adonis.planner.repository.*;
import adonis.planner.web.dto.TransactionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository txRepo;
    private final AccountRepository accRepo;
    private final CategoryRepository catRepo;

    /* ----------------- Helpers ----------------- */

    private Account ownedAccount(User u, Long accountId) {
        Account a = accRepo.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        if (!a.getUser().getId().equals(u.getId()))
            throw new SecurityException("Account not owned by user");
        return a;
    }

    private Category ownedCategoryOrNull(User u, Long categoryId) {
        if (categoryId == null) return null;
        Category c = catRepo.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        if (!c.getUser().getId().equals(u.getId()))
            throw new SecurityException("Category not owned by user");
        return c;
    }

    private void validate(TransactionDTO dto) {
        if (dto.getAmount() == null || dto.getAmount().signum() <= 0)
            throw new IllegalArgumentException("Amount must be > 0");
        if (dto.getDate() == null)
            throw new IllegalArgumentException("Date is required");
        if (dto.getType() == null)
            throw new IllegalArgumentException("Type is required");
        if (dto.getAccountId() == null)
            throw new IllegalArgumentException("AccountId is required");
    }

    private void validateRange(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("from/to are required");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from must be <= to");
        }
    }

    /* ----------------- Consultas ----------------- */

    @Transactional(readOnly = true)
    public List<Transaction> find(User u, LocalDate from, LocalDate to) {
        validateRange(from, to);
        return txRepo.findByUserAndDateBetween(u, from, to);
    }

    @Transactional(readOnly = true)
    public Transaction findOne(User u, Long id) {
        Transaction t = txRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        if (!t.getUser().getId().equals(u.getId()))
            throw new SecurityException("Transaction not owned by user");
        return t;
    }

    /* ----------------- Criar ----------------- */

    @Transactional
    public Transaction create(User u, TransactionDTO dto) {
        validate(dto);

        Account acc = ownedAccount(u, dto.getAccountId());
        Category cat = ownedCategoryOrNull(u, dto.getCategoryId());

        Transaction t = Transaction.builder()
                .user(u)
                .account(acc)
                .category(cat)
                .date(dto.getDate())
                .type(dto.getType())
                .amount(dto.getAmount())
                .description(dto.getDescription())
                .build();

        return txRepo.save(t);
    }

    /* ----------------- Atualizar ----------------- */

    @Transactional
    public Transaction update(User u, Long id, TransactionDTO dto) {
        validate(dto);

        Transaction t = txRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        if (!t.getUser().getId().equals(u.getId()))
            throw new SecurityException("Transaction not owned by user");

        Account acc = ownedAccount(u, dto.getAccountId());
        Category cat = ownedCategoryOrNull(u, dto.getCategoryId());

        t.setDate(dto.getDate());
        t.setAccount(acc);
        t.setCategory(cat);
        t.setType(dto.getType());
        t.setAmount(dto.getAmount());
        t.setDescription(dto.getDescription());

        return txRepo.save(t);
    }

    /* ----------------- Apagar ----------------- */

    @Transactional
    public void delete(User u, Long id) {
        Transaction t = txRepo.findById(id)
                .filter(x -> x.getUser().getId().equals(u.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found or not owned by user"));
        txRepo.delete(t);
    }
}
