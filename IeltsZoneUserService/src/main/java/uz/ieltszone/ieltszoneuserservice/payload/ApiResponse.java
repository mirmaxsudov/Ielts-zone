package uz.ieltszone.ieltszoneuserservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private String message;
    private Boolean success;
    private T data;

    public ApiResponse<T> success(T data) {
        return new ApiResponse<>("Success", true, data);
    }

    public ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, true, data);
    }

    public ApiResponse<T> success(String message) {
        return new ApiResponse<>(message, true, null);
    }

    public ApiResponse<T> success() {
        return new ApiResponse<>("Success", true, null);
    }

    public ApiResponse<T> error(String message) {
        return new ApiResponse<>(message, false, null);
    }
}