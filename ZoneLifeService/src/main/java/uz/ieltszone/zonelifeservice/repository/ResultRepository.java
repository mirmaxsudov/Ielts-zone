package uz.ieltszone.zonelifeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.ieltszone.zonelifeservice.entity.Result;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
}