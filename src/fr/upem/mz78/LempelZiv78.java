package fr.upem.mz78;

import java.io.IOException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Sybille Crimet <crimet.sybille@gmail.com>
 *
 */
public class LempelZiv78 {
    /**
     * Compresses the given InputStream using the LZ78 algorithm into the given
     * OutputStream. The latter is closed automatically.
     * @param fins
     * @param outs
     * @throws IOException
     */
    public static void compress(InputStream fins, OutputStream outs) {
        int value = 1;
        byte[] pattern = new byte[0];
        byte[] byteArray = new byte[8192];
        boolean isEmpty = true;
        Map<Byte, byte[]> dictionary = new TreeMap<>();
        boolean patternAlreadyExist;
        int numberOfBytesRead;

        try {
            // While the end of file is not reached.
            while ((numberOfBytesRead = fins.read(byteArray)) != -1) {
                for (int i = 0; i < numberOfBytesRead; i++) {
                    //Compress the stream if the dictionary size exceeds 127,
                    // and construct a new dictionary
                    if (dictionary.size() == Byte.MAX_VALUE) {
                        if (!isEmpty) {
                            dictionary.put((byte) value, pattern);
                        }
                        compressStream(dictionary, outs);
                        dictionary.clear();
                        value = 1;
                    }

                    // Read pattern corresponding to an array of bytes.
                    byte[] temp = Util.mergeBytes(pattern, byteArray[i]);
                    pattern = temp;
                    patternAlreadyExist = doesPatternExist(dictionary, pattern);

                    // Add the pattern to the dictionary if its doesn't exist.
                    if (!patternAlreadyExist) {
                        dictionary.put((byte) value, pattern.clone());
                        value++;
                        pattern = new byte[0];
                        isEmpty = true;

                    // If it already exists, read the next byte in the IS
                    } else {
                        isEmpty = false;
                        continue;
                    }
                }
            }

            // Insert the pattern in the dictionary if the end of the file is
            // reached and the pattern already exist.
            if (!isEmpty) {
                dictionary.put((byte) value, pattern.clone());
            }
            compressStream(dictionary, outs);
            outs.close();
        } catch (IOException e) {
            System.err.println("An error happened while decompressing.");
        }
    }

    /**
     * Decompresses the specified InputStream to the specified OutputStream
     * @param src
     * @param dst
     * @throws IOException
     */
    public static void decompress(InputStream src, OutputStream dst) {
        int key = 1;
        byte[] byteKey = new byte[1];
        byte[] byteValue = new byte[1];
        Map<Integer, byte[]> dictionary = new TreeMap<>();

        try {
            // While the end of the file src not reached, read byte.
            while (src.read(byteKey) != -1) {
                // Decompress the stream if the dictionary size exceeds 127 and
                // construct a new dictionary
                if (dictionary.size() == Byte.MAX_VALUE) {
                    decompressStream(dictionary, dst);
                    dictionary.clear();
                    key = 1;
                }
    
                // Get the key
                int result = Util.byteToInt(byteKey[0]);
                
                // Cet the associated value and put it in the dictionary.
                src.read(byteValue);
                if (result == 0) {
                    dictionary.put(key, byteValue.clone());
                    key++;   
                } else if (result > 0) {
                    byte[] val = dictionary.get(result);
                    byte[] buf = Util.mergeBytes(val, byteValue.clone());
                    dictionary.put(key, buf.clone());
                    key++;
                }
            }
            decompressStream(dictionary, dst);
            dst.close();
        } catch (IOException e) {
            System.err.println("An error happened while decompressing.");
        }
    }

    /**
     * Writes a compressed stream from the dictionary in the specified
     OutputStream
     * @param dictionary
     * @param outs
     * @throws IOException
     */
    private static void compressStream(Map<Byte, byte[]> dictionary,
                                       OutputStream outs) throws IOException {
        int current_pos = 0;
        byte[] byteStream = new byte[dictionary.size()*2];
        boolean patternExistTwice = false;
        for (Byte key : dictionary.keySet()) {
            patternExistTwice =
                    doesPatternExistTwice(dictionary, dictionary.get(key));

            // If the pattern already exists twice in the dictionary then it is
            // definitely the end of the dictionary
            if (patternExistTwice && key == dictionary.size()) {
                if (dictionary.get(key).length == 1) {
                    byteStream[current_pos] = 0;
                    current_pos++;
                } else {
                    int realKey =
                            getKeyOfSubstring(dictionary, dictionary.get(key));
                    byteStream[current_pos] = (byte) realKey;
                    current_pos++;
                }
                byteStream[current_pos] = Util.getLastByte(dictionary.get(key));
                current_pos++;
                break;
            }

            // Write Node number following by last byte of the corresponding
            // value.
            else {
                if (dictionary.get(key).length == 1) {
                    byteStream[current_pos] = 0;
                    current_pos++;
                } else {
                    int realKey =
                            getKeyOfSubstring(dictionary, dictionary.get(key));
                    byteStream[current_pos] = (byte) realKey;
                    current_pos++;
                }
                byteStream[current_pos] = Util.getLastByte(dictionary.get(key));
                current_pos++;
            }
        }

        outs.write(byteStream);
    }

    /**
     * method that writes a decompressed stream from the dictionary in the
     specified output stream
     * @param dictionary
     * @param os
     * @throws IOException
     */
    static void decompressStream(Map<Integer, byte[]> dictionary,
                                 OutputStream os) {
        byte[] byteStream = new byte[0];

        for (Integer key : dictionary.keySet()) {
            byteStream = Util.mergeBytes(byteStream,
                    dictionary.get(key).clone());
        }
        try {
            os.write(byteStream);
        } catch (IOException e) {
            System.err.println("An error happened while decompressing.");
        }
    }

    /**
     * Checks if the specified pattern exists in the specified map.
     * @param haystack
     * @param needle
     * @return true if the pattern is found, false otherwise.
     */
    private static boolean doesPatternExist(Map<Byte, byte[]> haystack,
                                            byte[] needle) {
        if (needle == null)
            return false;
        for (Byte key : haystack.keySet()) {
            if (Arrays.equals(haystack.get(key), needle)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the specified pattern exists twice in the specified map
     * @param haystack
     * @param needle
     * @return true if the pattern is found twice, false otherwise
     */
    private static boolean doesPatternExistTwice(Map<Byte, byte[]> haystack,
                                                 byte[] needle) {
        int twice = 0;

        for (Byte key : haystack.keySet()) {
            if (Arrays.equals(haystack.get(key), needle)) {
                twice++;
                if (twice == 2) return true;
            }
        }

        return false;
    }

    /**
     * Return the key of the specified pattern in the specified map.
     * @param haystack
     * @param needle
     * @return key corresponding of the subPattern.
     */
    private static int getKeyOfSubstring(Map<Byte, byte[]> haystack,
                                         byte[] needle) {
        int key = 0;
        byte[] array = Arrays.copyOfRange(needle, 0, needle.length-1);

        for (Byte b : haystack.keySet()) {
            if (Arrays.equals(haystack.get(b), array)) {
                key = b;
                break;
            }
        }
        return key;
    }
}
