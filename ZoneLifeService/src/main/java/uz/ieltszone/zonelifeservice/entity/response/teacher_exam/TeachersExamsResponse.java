package uz.ieltszone.zonelifeservice.entity.response.teacher_exam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.ieltszone.zonelifeservice.entity.response.TeacherResponse;

import java.util.List;

@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeachersExamsResponse {
    private TeacherResponse teacherResponse;
    private List<ExamTeacherResponse> examTeacherResponses;
}