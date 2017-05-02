package de.familiep.mobileinformationgain.compression;

import java.util.zip.Deflater;

public class CompressionHelper {

    //returns byte-size
    public static long compress(String stringToCompress){

        Deflater deflater = new Deflater();

        byte[] stringBytes = stringToCompress.getBytes();
        byte[] output = new byte[8 + stringBytes.length]; //use min size as uncompressed string size but at least 8 bytes

        deflater.setInput(stringBytes);
        deflater.finish();
        deflater.deflate(output);

        long result = deflater.getBytesWritten();

        return result - 8;
    }
}
