package jyotish.main;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/newbirthchart.jsp") // Replace with your actual JSP filename
public class HtmlCaptureFilter implements Filter {

    @Override
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

    // 1. KEEP THIS: This prevents the "Double Run" on Thread 29
    if (request.getAttribute("FILTER_ALREADY_RUN") != null) {
        chain.doFilter(request, response);
        return;
    }
    request.setAttribute("FILTER_ALREADY_RUN", true);

    // 2. KEEP THIS: This creates the "T-Pipe" so the JSP can capture data
    TeeResponseWrapper wrapper = new TeeResponseWrapper((HttpServletResponse) response);
    request.setAttribute("TeeResponseWrapper", wrapper);

    // 3. KEEP THIS: This runs the JSP
    chain.doFilter(request, wrapper);

    // 4. OPTIONAL: Comment this out if you are already doing your 
    // Java logic (saving to DB, etc.) inside the JSP scriptlet.
    /*
    String capturedHtml = wrapper.getCapturedHtml();
    System.out.println("==== SINGLE FILTER CAPTURE (Thread: " + Thread.currentThread().getId() + ") ====");
    System.out.println(capturedHtml);
    */
}

    @Override public void init(FilterConfig filterConfig) {}
    @Override public void destroy() {}
}