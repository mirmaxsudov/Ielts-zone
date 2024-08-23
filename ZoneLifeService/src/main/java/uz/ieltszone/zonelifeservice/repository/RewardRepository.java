package uz.ieltszone.zonelifeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.ieltszone.zonelifeservice.entity.Reward;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {
}
