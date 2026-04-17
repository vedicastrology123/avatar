package jyotish.main;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.IOException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

public class TeeResponseWrapper extends HttpServletResponseWrapper {
    private final StringWriter buffer = new StringWriter();
    private PrintWriter teePassThrough;

    public TeeResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);
        // Split the output: Original Response + Our Buffer
        this.teePassThrough = new TeePrintWriter(response.getWriter(), buffer);
    }

    @Override
    public PrintWriter getWriter() {
        return teePassThrough;
    }

    public String getCapturedHtml() {
        return buffer.toString();
    }
}