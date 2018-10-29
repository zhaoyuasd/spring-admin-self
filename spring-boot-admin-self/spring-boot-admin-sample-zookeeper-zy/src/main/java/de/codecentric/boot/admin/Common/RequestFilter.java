package de.codecentric.boot.admin.Common;

/**
 * Created by viruser on 2018/10/25.
 */
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import  javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Order(0)
@Component
@WebFilter(filterName = "requestFilter",urlPatterns = "/*")
public class RequestFilter  implements  Filter{
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req=(HttpServletRequest)servletRequest;
        System.out.println("------>"+req.getRequestURL().toString());
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
