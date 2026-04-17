package jyotish.main;

import java.io.PrintWriter;
import java.io.Writer;

public class TeePrintWriter extends PrintWriter {
    private final Writer branch;

    public TeePrintWriter(Writer main, Writer branch) {
        super(main);
        this.branch = branch;
    }

    @Override
    public void write(char[] buf, int off, int len) {
        super.write(buf, off, len); // Write to browser
        try {
            branch.write(buf, off, len); // Write to memory buffer
        } catch (Exception e) { /* log error */ }
    }
    
    @Override
    public void flush() {
        super.flush();
        try { branch.flush(); } catch (Exception e) {}
    }
}