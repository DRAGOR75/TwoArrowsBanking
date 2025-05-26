package dragor.com.webapp.repository;

import dragor.com.webapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    // Use JpaRepository for basic CRUD operations{
    User findByUsernameIgnoreCase(String username);
}
