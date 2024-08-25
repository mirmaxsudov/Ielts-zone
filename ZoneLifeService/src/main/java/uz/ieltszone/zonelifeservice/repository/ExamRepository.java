package uz.ieltszone.zonelifeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.ieltszone.zonelifeservice.entity.Exam;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    @Query("select e from Exam as e where e.teacherId = :teacherId")
    List<Exam> findAllByTeacherId(Long teacherId);
}