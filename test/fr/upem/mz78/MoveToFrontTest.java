package fr.upem.mz78;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.*;

public class MoveToFrontTest {
    @Test
    public void testEncode() throws IOException {
        byte[] input = { 1, 0, (byte) 241, 0, (byte) 241, 0, 0, 0 };
        byte[] expectedResult = { 1, 1, (byte) 241, 1, 1, 1, 0, 0 };

        ByteArrayInputStream sourceIS = new ByteArrayInputStream(input);

        PipedInputStream encodedData = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(encodedData);
        MoveToFront.encode(sourceIS, out);

        byte[] readBytes = new byte[8];
        encodedData.read(readBytes);
        assertArrayEquals(expectedResult, readBytes);
    }

    @Test
    public void testDecode() throws IOException {
        byte[] expectedResult = { 1, 0, (byte) 241, 0, (byte) 241, 0, 0, 0 };
        byte[] input = { 1, 1, (byte) 241, 1, 1, 1, 0, 0 };

        ByteArrayInputStream sourceIS = new ByteArrayInputStream(input);

        PipedInputStream decodedData = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(decodedData);
        MoveToFront.decode(sourceIS, out);

        byte[] readBytes = new byte[8];
        decodedData.read(readBytes);
        assertArrayEquals(expectedResult, readBytes);
    }
}
