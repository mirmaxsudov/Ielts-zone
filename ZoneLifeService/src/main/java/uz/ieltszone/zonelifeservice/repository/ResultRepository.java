package uz.ieltszone.zonelifeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.ieltszone.zonelifeservice.entity.Result;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    @Query("select r from Result r where r.exam.id = :examId")
    void deleteByExamId(@Param("examId") Long examId);

    @Query("select r from Result as r where r.exam.id = :examId")
    List<Result> findByExamId(@Param("examId") Long examId);
}