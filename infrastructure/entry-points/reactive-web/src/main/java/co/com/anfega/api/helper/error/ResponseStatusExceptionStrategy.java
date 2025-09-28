package co.com.anfega.api.helper.error;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class ResponseStatusExceptionStrategy implements ExceptionStrategy {
    @Override
    public boolean supports(Throwable ex) {
        return ex instanceof org.springframework.web.server.ResponseStatusException;
    }

    @Override
    public HttpStatus getStatus(Throwable ex) {
        return (HttpStatus) ((org.springframework.web.server.ResponseStatusException) ex).getStatusCode();
    }

    @Override
    public String getError(Throwable ex) {
        return getStatus(ex).getReasonPhrase();
    }
}
