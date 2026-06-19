package co.com.anfega.api.util;

import co.com.anfega.model.common.constants.Constants;
import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.UUID;

@UtilityClass
public class EntryPointUtils {

    public static String getTraceId(ServerRequest request) {
        return request.attribute(Constants.TRACE_ID_ATTRIBUTE)
                .map(Object::toString)
                .orElseGet(() -> request.headers().firstHeader(Constants.TRACE_ID_HEADER) != null
                        ? request.headers().firstHeader(Constants.TRACE_ID_HEADER)
                        : UUID.randomUUID().toString());
    }
}
