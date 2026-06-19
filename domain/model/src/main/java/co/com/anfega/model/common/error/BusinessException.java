package co.com.anfega.model.common.error;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String traceId;
    private final List<String> params;

    public BusinessException(ErrorCode errorCode, String traceId) {
        this(errorCode, traceId, errorCode.getMessage(), Collections.emptyList());
    }

    public BusinessException(ErrorCode errorCode, String param, String traceId) {
        this(errorCode, traceId, formatMessage(errorCode.getMessage(), param), Collections.singletonList(param));
    }

    public static BusinessException withMessage(ErrorCode errorCode, String traceId, String message) {
        return new BusinessException(errorCode, traceId, message, Collections.emptyList());
    }

    private BusinessException(ErrorCode errorCode, String traceId, String message, List<String> params) {
        super(message);
        this.errorCode = errorCode;
        this.traceId = traceId;
        this.params = params;
    }

    private static String formatMessage(String pattern, Object param) {
        return pattern != null && pattern.contains("%")
                ? String.format(pattern, param)
                : pattern;
    }
}
