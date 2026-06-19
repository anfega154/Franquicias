package co.com.anfega.api.config;

import co.com.anfega.model.common.constants.Constants;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class SecurityHeadersConfig implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpHeaders headers = exchange.getResponse().getHeaders();
        headers.set(Constants.CONTENT_SECURITY_POLICY_HEADER, Constants.CONTENT_SECURITY_POLICY_VALUE);
        headers.set(Constants.STRICT_TRANSPORT_SECURITY_HEADER, Constants.STRICT_TRANSPORT_SECURITY_VALUE);
        headers.set(Constants.X_CONTENT_TYPE_OPTIONS_HEADER, Constants.X_CONTENT_TYPE_OPTIONS_VALUE);
        headers.set(Constants.CACHE_CONTROL_HEADER, Constants.CACHE_CONTROL_VALUE);
        headers.set(Constants.PRAGMA_HEADER, Constants.PRAGMA_VALUE);
        headers.set(Constants.REFERRER_POLICY_HEADER, Constants.REFERRER_POLICY_VALUE);
        return chain.filter(exchange);
    }
}
