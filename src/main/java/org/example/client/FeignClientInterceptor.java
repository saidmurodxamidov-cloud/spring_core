package org.example.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();

        String username = request.getHeader("X-Auth-Username");
        String roles = request.getHeader("X-Auth-Roles");

        if (username != null) {
            template.header("X-Auth-Username", username);
        }
        if (roles != null) {
            template.header("X-Auth-Roles", roles);
        }
    }
}