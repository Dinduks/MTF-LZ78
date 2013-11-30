package fr.upem.mz78;

class Util {
    public static int byteToInt(byte b) {
        if (b >= 0) return b;
        else        return b + 256;
    }

    public static byte getLastByte(byte[] bytesArray) {
        return bytesArray[bytesArray.length - 1];
    }

    /**
     * Merges the specified byte arrays into another byte array
     More formally: (A, B) -> A u B
     * @param byteArray1
     * @param byteArray2
     * @return A byte array composed of the first array followed by the second
     */
    public static byte[] mergeBytes(byte[] byteArray1, byte[] byteArray2) {
        byte[] newByteArray = new byte[byteArray1.length + byteArray2.length];

        System.arraycopy(byteArray1, 0,
                newByteArray, 0, byteArray1.length);
        System.arraycopy(byteArray2, 0,
                newByteArray, byteArray1.length, byteArray2.length);

        return newByteArray;
    }

    /**
     * Adds the specified byte to the specified byte array
     * @param byteArray1
     * @param byte_
     * @return A byte array composed of the first array followed by the
     * specified byte
     */
    public static byte[] mergeBytes(byte[] byteArray1, byte byte_) {
        byte[] newByteArray = new byte[byteArray1.length + 1];
        System.arraycopy(byteArray1, 0, newByteArray, 0, byteArray1.length);
        newByteArray[byteArray1.length] = byte_;

        return newByteArray;
    }
}