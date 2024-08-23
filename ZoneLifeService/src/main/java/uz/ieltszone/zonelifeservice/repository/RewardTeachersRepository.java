package uz.ieltszone.zonelifeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.ieltszone.zonelifeservice.entity.RewardTeachers;

@Repository
public interface RewardTeachersRepository extends JpaRepository<RewardTeachers, Long> {
}