package uz.ieltszone.zonelifeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.ieltszone.zonelifeservice.entity.Exam;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
}