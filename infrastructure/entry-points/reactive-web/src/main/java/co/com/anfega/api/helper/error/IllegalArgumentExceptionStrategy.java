package co.com.anfega.api.helper.error;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class IllegalArgumentExceptionStrategy implements ExceptionStrategy {
    @Override
    public boolean supports(Throwable ex) {
        return ex instanceof IllegalArgumentException;
    }

    @Override
    public HttpStatus getStatus(Throwable ex) {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getError(Throwable ex) {
        return "Argumento inválido: " + ex.getMessage();
    }
}