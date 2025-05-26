package dragor.com.webapp.repository;

import dragor.com.webapp.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> { // Replace Account with your actual entity class{
}
