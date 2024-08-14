package uz.ieltszone.writequestionsbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.ieltszone.writequestionsbot.entity.LearningCenter;

@Repository
public interface LearningCenterRepository extends JpaRepository<LearningCenter, Long> {
}