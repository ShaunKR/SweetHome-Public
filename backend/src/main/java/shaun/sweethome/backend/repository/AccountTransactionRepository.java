package shaun.sweethome.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shaun.sweethome.backend.entity.AccountTransaction;

import java.time.LocalDate;
import java.util.List;

public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {

    List<AccountTransaction> findByTransactionDateBetweenOrderByTransactionDateDescIdDesc(
            LocalDate from,
            LocalDate to
    );
}
