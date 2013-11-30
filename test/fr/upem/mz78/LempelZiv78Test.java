package fr.upem.mz78;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.junit.Test;

public class LempelZiv78Test {
    @Test
    public void compressText() throws IOException {
        byte[] input = new byte[]{'C','O', 'U', 'C', 'O', 'U','!','!','!'};
        byte[] expectedResult = new byte[]{ 0,'C', 0,'O', 0,'U', 1,'O' ,3,'!', 0,'!', 0,'!'};
        ByteArrayInputStream sourceIS = new ByteArrayInputStream(input);

        PipedInputStream compressData = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(compressData);
        LempelZiv78.compress(sourceIS, out);

        byte[] bytesCompressed = new byte[14];

        compressData.read(bytesCompressed);
        assertArrayEquals(expectedResult, bytesCompressed);
        compressData.close();
        out.close();
    }
    
    @Test
    public void decompressText() throws IOException {
        byte[] input = new byte[]{ 0,'C', 0,'O', 0,'U', 1,'O', 3,'!', 0,'!'};
        byte[] expectedResult = new byte[]{'C','O', 'U', 'C', 'O', 'U','!','!'};
        ByteArrayInputStream sourceIS = new ByteArrayInputStream(input);

        PipedInputStream decompressData = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(decompressData);
        LempelZiv78.decompress(sourceIS, out);

        byte[] bytesDecompressed = new byte[8];

        if (decompressData.read(bytesDecompressed) != -1) {
            assertArrayEquals(expectedResult, bytesDecompressed);
        }
        decompressData.close();
        out.close();
    }
}
