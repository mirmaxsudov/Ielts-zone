package uz.ieltszone.zonelifeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;
import uz.ieltszone.zonelifeservice.entity.Result;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    @Query("select r from Result r where r.exam.id = :examId")
    void deleteByExamId(@RequestParam("examId") Long examId);
}