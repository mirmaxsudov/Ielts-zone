package uz.ieltszone.zonelifeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.ieltszone.zonelifeservice.entity.Reward;
import uz.ieltszone.zonelifeservice.entity.RewardTeachers;

import java.util.List;

@Repository
public interface RewardTeachersRepository extends JpaRepository<RewardTeachers, Long> {
    @Query("""
            select r from Reward as r
            join RewardTeachers as rt
            on r.id = rt.rewardId
            where rt.teacherId = :teacherId
            order by r.id
            """)
    List<Reward> findAllByTeacherId(@Param("teacherId") Long teacherId);

    @Query("delete from RewardTeachers as rt where rt.teacherId = :teacherId and rt.rewardId = :rewardId")
    void deleteByTeacherId(@Param("teacherId") Long teacherId, @Param("rewardId") Long rewardId);
}