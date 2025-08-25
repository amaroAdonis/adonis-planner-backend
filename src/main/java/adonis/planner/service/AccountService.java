package adonis.planner.service;

import adonis.planner.domain.enums.TransactionType;
import adonis.planner.domain.model.Account;
import adonis.planner.domain.model.Transaction;
import adonis.planner.domain.model.User;
import adonis.planner.repository.AccountRepository;
import adonis.planner.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repo;
    private final TransactionRepository txRepo;

    public List<Account> list(User u) {
        return repo.findByUser(u);
    }

    public Account get(User u, Long id) {
        return repo.findById(id)
                .filter(a -> a.getUser().getId().equals(u.getId()))
                .orElseThrow();
    }

    public Account save(Account a) {
        return repo.save(a);
    }

    public void delete(User u, Long id) {
        repo.delete(get(u, id));
    }

    /** Saldo atual = openingBalance + (∑ receitas − ∑ despesas) da conta */
    @Transactional(readOnly = true)
    public BigDecimal currentBalance(User u, Long accountId) {
        // garante que a conta existe e pertence ao usuário
        Account acc = repo.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        if (!acc.getUser().getId().equals(u.getId())) {
            throw new SecurityException("Account not owned by user");
        }

        BigDecimal balance = acc.getOpeningBalance() != null ? acc.getOpeningBalance() : BigDecimal.ZERO;

        // busca todas as transações do usuário (intervalo amplo) e filtra pela conta
        List<Transaction> txs = txRepo.findByUserAndDateBetween(u, LocalDate.MIN, LocalDate.MAX);

        for (Transaction t : txs) {
            if (t.getAccount() != null && accountId.equals(t.getAccount().getId())) {
                if (t.getType() == TransactionType.INCOME) {
                    balance = balance.add(t.getAmount());
                } else {
                    // considera qualquer outro tipo como despesa (ex.: EXPENSE)
                    balance = balance.subtract(t.getAmount());
                }
            }
        }

        return balance;
    }
}
