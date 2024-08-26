package uz.ieltszone.zonelifeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.ieltszone.zonelifeservice.entity.Reward;

import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {
}