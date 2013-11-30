package fr.upem.mz78;

import java.io.*;

/**
 * @author Samy Dindane <samy@dindane.com>
 *
 */
public class MoveToFront {
    /**
     * Encodes the given InputStream using the MTF algorithm into the given
     * OutputStream. The latter is closed automatically.
     * @param src
     * @param dst
     */
    public static void encode(InputStream src, OutputStream dst) {
        int bytePosition;
        byte[] bytesRead = new byte[8192];
        byte[] bytesToWrite = new byte[8192];
        byte[] alphabet = getBytesAlphabet();
        int numberOfBytesRead;

        try {
            while ((numberOfBytesRead = src.read(bytesRead)) != -1) {
                for (int i = 0; i < numberOfBytesRead; i++) {
                    bytePosition = findBytePosition(bytesRead[i], alphabet);
                    bytesToWrite[i] = (byte) bytePosition;
                    moveByteToFirstPos(alphabet, bytePosition);
                }
                dst.write(bytesToWrite, 0, numberOfBytesRead);
            }
            dst.close();
        } catch (IOException e) {
            System.err.println("An error happened while encoding.");
            e.printStackTrace();
        }
    }

    /**
     * Encodes the given MTF encoded InputStream into the given OutputStream.
     * The latter is closed automatically.
     * @param src
     * @param dst
     */
    public static void decode(InputStream src, OutputStream dst) {
        byte[] bytesRead = new byte[8192];
        byte[] bytesToWrite = new byte[8192];
        byte[] alphabet = getBytesAlphabet();
        int numberOfBytesRead;

        try {
            int i;
            while ((numberOfBytesRead = src.read(bytesRead)) != -1) {
                for (i = 0; i < numberOfBytesRead; i++) {
                    bytesToWrite[i] = alphabet[Util.byteToInt(bytesRead[i])];
                    moveByteToFirstPos(alphabet, Util.byteToInt(bytesRead[i]));
                }
                dst.write(bytesToWrite, 0, numberOfBytesRead);
            }
            dst.close();
        } catch (IOException e) {
            System.err.println("An error happened while decoding.");
            e.printStackTrace();
        }
    }

    private static byte[] getBytesAlphabet() {
        byte[] alphabet = new byte[256];
        for (int i = 0; i < 256; i++) {
            alphabet[i] = (byte) i;
        }

        return alphabet;
    }

    private static int findBytePosition(byte needle, byte[] haystack) {
        int i = 0;
        while (true) {
            if (needle == haystack[i]) return i;
            i++;
        }
    }

    /**
     * Move the byte in the specified position to the first position.
     * @param bytesArray
     * @param position
     */
    private static void moveByteToFirstPos(byte[] bytesArray, int position) {
        byte tmp = bytesArray[position];
        System.arraycopy(bytesArray, 0, bytesArray, 1, position);
        bytesArray[0] = tmp;
    }
}
