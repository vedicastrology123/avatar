package jyotish.main;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.IOException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

public class HtmlResponseWrapper extends HttpServletResponseWrapper {
    private final StringWriter sw = new StringWriter();

    public HtmlResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public PrintWriter getWriter() {
        return new PrintWriter(sw);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        // Redirect the output stream to write into our StringWriter
        return new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                sw.write(b);
            }
            @Override
            public boolean isReady() { return true; }
            @Override
            public void setWriteListener(WriteListener writeListener) {}
        };
    }

    @Override
    public String toString() {
        return sw.toString();
    }
}
