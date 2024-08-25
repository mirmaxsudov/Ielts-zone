package uz.ieltszone.zonelifeservice.entity.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private LocalDate birthDate;
    private LocalDate joinedAt;
    private String role;
    private AttachmentResponse attachmentResponse;
}