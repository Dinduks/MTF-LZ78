import fr.upem.mz78.LempelZiv78;
import fr.upem.mz78.MoveToFront;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Incorrect parameters.\n");
            System.err.println(getHelp());
            return;
        }

        switch (args[0]) {
            case "-c":
                compress(new File(args[1]), new File(args[1] + ".mz78"));
                break;
            case "-d":
                File destinationFile;
                if (args[1].endsWith(".mz78"))
                    destinationFile = new File(args[1].replaceAll(".mz78$", ""));
                else
                    destinationFile = new File(args[1] + ".raw");
                decompress(new File(args[1]), destinationFile);
                break;
            default:
                System.err.println("Incorrect parameters.\n");
                System.err.println(getHelp());
        }
    }

    private static void compress(File sourceFile, File destinationFile)
            throws IOException {
        long startTime = System.currentTimeMillis();

        PipedInputStream mtfIn = new PipedInputStream();
        final PipedOutputStream mtfOut = new PipedOutputStream(mtfIn);

        final BufferedInputStream bis =
                new BufferedInputStream(new FileInputStream(sourceFile));
        new Thread(new Runnable() {
            @Override public void run() { MoveToFront.encode(bis, mtfOut); }
        }).start();

        BufferedOutputStream bos =
                new BufferedOutputStream(new FileOutputStream(destinationFile));
        LempelZiv78.compress(mtfIn, bos);

        String comparison = null;
        if (destinationFile.length() > sourceFile.length()) comparison = "bigger";
        else comparison = "lesser";

        System.out.println(String.format(
                "Compression completed in %s seconds.\n" +
                "Size of the compressed file: %s bytes.\n" +
                "Which is %s than the original file (%s bytes).",
                (System.currentTimeMillis() - startTime) / 1000,
                destinationFile.length(),
                comparison,
                sourceFile.length()
        ));

        mtfIn.close();
        bis.close();
    }

    private static void decompress(File sourceFile, File destinationFile)
            throws IOException {
        long startTime = System.currentTimeMillis();

        final PipedInputStream mtfIn = new PipedInputStream();
        final PipedOutputStream mtfOut = new PipedOutputStream(mtfIn);

        final BufferedInputStream bis =
                new BufferedInputStream(new FileInputStream(sourceFile));

        new Thread(new Runnable() {
            @Override public void run() { LempelZiv78.decompress(bis, mtfOut); }
        }).start();

        BufferedOutputStream bos =
                new BufferedOutputStream(new FileOutputStream(destinationFile));
        MoveToFront.decode(mtfIn, bos);

        System.out.println(String.format(
                "Decompression completed in %s seconds.\n",
                (System.currentTimeMillis() - startTime) / 1000
        ));

        mtfIn.close();
        bis.close();
    }

    public static String getHelp() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Help\n")
                .append("====\n")
                .append("Compress a file:   ./mz78 -c filename\n")
                .append("Decompress a file: ./mz78 -d filename.mz78");

        return stringBuilder.toString();
    }
}