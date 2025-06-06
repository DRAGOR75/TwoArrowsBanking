package dragor.com.webapp.repository;

import dragor.com.webapp.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, String> {
    boolean existsByAccountNumber(Long accountNumber);// Replace Account with your actual entity class{

    boolean existsByOwnerUidAndCode(String uid, String code);

    List<Account> findAllByOwnerUid(String uid);
}
