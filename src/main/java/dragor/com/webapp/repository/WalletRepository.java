package dragor.com.webapp.repository;

import dragor.com.webapp.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Card, String> {
   Optional<Card > findByOwnerUid(String uid);

    boolean existsByCardNumber(long CardNumber);
}
