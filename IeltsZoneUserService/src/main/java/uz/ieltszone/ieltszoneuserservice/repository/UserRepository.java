package uz.ieltszone.ieltszoneuserservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.ieltszone.ieltszoneuserservice.model.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
