import CompressionException.CompressionException;

import java.io.*;

public class Decompressor {

    public static void main(String[] args) throws IOException, ClassNotFoundException, CompressionException {
        //take the .sc file and then decompress
        String compressedFile = "data/shakespeare.txt.sc";
        String decompressedFile = compressedFile+".txt";
        //now we need to read outputFile .sc and put it into outputText file
        //reading an obj from the file
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(compressedFile));
        Object inputObject = inputStream.readObject(); //reading an obj
        if (!(inputObject instanceof CompressionInfoHolder)) {
            throw new CompressionException("Unexpected type received, programm expects " + CompressionInfoHolder.class.getCanonicalName());
        }
        CompressionInfoHolder inputHolder = (CompressionInfoHolder) inputObject;//need to cast, inputholder has a map and bytes arr


        //need to write the decoded resul into file but it wont be writing obj into file, but text into file
        //writer thats putting the data that we decompressed
        BufferedWriter decompressingWriter = new BufferedWriter(new FileWriter(decompressedFile));//take a writer
        byte[] codedBytes = inputHolder.getCodedtext();
        for (int i = 0; i < codedBytes.length; i += 2) {
            //the byte that we read and we need to shift it back
            short high = codedBytes[i];
            //we go over 2 bytes all the time, every itteration
            short low = codedBytes[i + 1];
            //the code that we want to get is short code
            short code = (short) ((high << 8) + low);

            String word = inputHolder.getCodeToWord().get(code);//take each bute and write into
            if (word != null) decompressingWriter.write(word);
        }
        decompressingWriter.flush();
        decompressingWriter.close();

        //
        System.out.println("Compressed file had "+(codedBytes.length/2)+ " words");
        System.out.println("Compressed file had "+(inputHolder.getCodeToWord().size() + " words"));
    }
}
