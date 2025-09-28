package co.com.anfega.api.helper.error;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class RuntimeExceptionStrategy implements ExceptionStrategy {
    @Override
    public boolean supports(Throwable ex) {
        return ex instanceof RuntimeException;
    }

    @Override
    public org.springframework.http.HttpStatus getStatus(Throwable ex) {
        return org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public String getError(Throwable ex) {
        return ex.getMessage();
    }
}
