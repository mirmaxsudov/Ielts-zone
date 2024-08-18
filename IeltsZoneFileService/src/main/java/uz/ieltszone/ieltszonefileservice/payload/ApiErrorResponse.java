package uz.ieltszone.ieltszonefileservice.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ApiErrorResponse {
    String message;
    HttpStatus httpStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime localDateTime;
    private int code;

}
