package uz.ieltszone.zonelifeservice.exceptions.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CustomFeignErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());

        return switch (status) {
            case INTERNAL_SERVER_ERROR ->
                    new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Something went wrong");
            case BAD_REQUEST -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
            default -> defaultErrorDecoder.decode(methodKey, response);
        };
    }
}
