package co.com.anfega.api.helper.api;

import lombok.Getter;

@Getter
public class LocalApiResponse<T> {
    private final String code;
    private final String message;
    private final String traceId;
    private final T content;

    public LocalApiResponse(String code, String message, String traceId, T content) {
        this.code = code;
        this.message = message;
        this.traceId = traceId;
        this.content = content;
    }
}
