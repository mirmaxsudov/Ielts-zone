package uz.ieltszone.ieltszonefileservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.ieltszone.ieltszonefileservice.entity.Attachment;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}