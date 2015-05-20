package de.is24.common.infrastructure.config.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;


@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public final class GlobalExceptionLoggingFilter implements Filter {
  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionLoggingFilter.class);

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
                throws IOException, ServletException {
    try {
      filterChain.doFilter(servletRequest, servletResponse);
    } catch (Throwable ex) {
      LOGGER.error(ex.getMessage(), ex);
      throw new RuntimeException(ex);
    }
  }

  @Override
  public void destroy() {
  }

}
