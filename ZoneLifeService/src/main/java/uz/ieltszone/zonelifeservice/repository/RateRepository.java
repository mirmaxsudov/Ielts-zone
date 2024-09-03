package uz.ieltszone.zonelifeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.ieltszone.zonelifeservice.entity.Rate;
import uz.ieltszone.zonelifeservice.entity.enums.RateStatus;

import java.time.Month;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {
    @Query("select r from Rate as r where r.month = :month and r.teacherId = :teacherId and r.status = :status")
    Rate findByTeacherIdAndMonthRateLevel(@Param("teacherId") Long teacherId, @Param("month") Month month, @Param("status") RateStatus status);
}