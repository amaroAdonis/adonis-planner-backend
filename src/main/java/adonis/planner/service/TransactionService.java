package adonis.planner.service;

import adonis.planner.domain.model.*;
import adonis.planner.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository txRepo;
    private final AccountRepository accRepo;
    private final CategoryRepository catRepo;

    public List<Transaction> find(User u, LocalDate from, LocalDate to) {
        return txRepo.findByUserAndDateBetween(u, from, to);
    }

    public Transaction create(User u, Transaction tx) {
        tx.setUser(u);
        return txRepo.save(tx);
    }

    public void delete(User u, Long id) {
        Transaction t = txRepo.findById(id).filter(x -> x.getUser().getId().equals(u.getId())).orElseThrow();
        txRepo.delete(t);
    }
}
