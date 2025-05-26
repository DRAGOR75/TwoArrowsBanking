package dragor.com.webapp.repository;

import dragor.com.webapp.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> { // JpaRepository provides CRUD operations for Transaction entity{
}
