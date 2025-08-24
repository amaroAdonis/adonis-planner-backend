package adonis.planner.service;

import adonis.planner.domain.model.Account;
import adonis.planner.domain.model.User;
import adonis.planner.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository repo;

    public List<Account> list(User u) { return repo.findByUser(u); }
    public Account get(User u, Long id) { return repo.findById(id).filter(a -> a.getUser().getId().equals(u.getId())).orElseThrow(); }
    public Account save(Account a) { return repo.save(a); }
    public void delete(User u, Long id) { repo.delete(get(u, id)); }
}
