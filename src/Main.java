import CompressionException.CompressionException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws CompressionException {
        String inputFile = "data/shakespeare.txt";
        String compressedFile = inputFile + ".sc";
        String decompressedFile = compressedFile + ".txt";
        try {
            //reade the file - next to put words into code, we need the Map bc key is unique, to avoid repeated words
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));

            //we need this for byte, to put it into Stream
            ByteArrayOutputStream codedText = new ByteArrayOutputStream();


            Map<String, Short> wordsToCode = new HashMap<>();
            Map<Short, String> codeToWord = new HashMap<>();
            short codeCounter = 0;
            String line = reader.readLine();

            while (line != null) {
                String[] words = line.split("(?<=\\s)|(?=\\s)");
                for (String w : words) {
                    Short existingCode = wordsToCode.get(w);
                    if (existingCode == null) {

                        wordsToCode.put(w, codeCounter);
                        codeToWord.put(codeCounter, w);
                        //using byte operations in place
                        byte high = (byte) (codeCounter >>> 8); //shifting byte by 8, need to cast since it was short
                        byte low = (byte) codeCounter; //everything else bigger than 1 byte is just cut off
                        codedText.write(high);//when decompressing will be reading from high to low
                        codedText.write(low);

                        codeCounter++;
                        if (codeCounter == Short.MAX_VALUE) {
                            throw new CompressionException("There are too many words in the file");
                        }
                    }
                    //System.out.println(w);
                }
                //System.out.println();
                line = reader.readLine();
            }

            //now we need to write code in the output file, need to put all the codes in place and write the whole map object
            ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(compressedFile));
            //see CompressionInfoFolder for fields
            CompressionInfoHolder holder = new CompressionInfoHolder(codeToWord, codedText.toByteArray());
            writer.writeObject(holder);
            writer.flush();//write it to the disk, buffer when its not completely filled, need to force flush
            writer.close();
            System.out.println("Numb of words found " + codeCounter);
            //now we need to read outputFile .sc and put it into outputText file
            //reading an obj from the file
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(compressedFile));
            Object inputObject = inputStream.readObject();
            if (!(inputObject instanceof CompressionInfoHolder)) {
                throw new CompressionException("Unexpected type received, programm expects " + CompressionInfoHolder.class.getCanonicalName());
            }
            CompressionInfoHolder inputHolder = (CompressionInfoHolder) inputObject;//need to cast, inputholder has a map and bytes arr
            //need to write the decoded resul into file but it wont be writing obj into file, but text into file
            //writer thats putting the data that we decompressed
            BufferedWriter decompressingWriter = new BufferedWriter(new FileWriter(decompressedFile));
            byte[] codedBytes = inputHolder.getCodedtext();
            for (int i = 0; i < codedBytes.length; i += 2) {
                //the byte that we read and we need to shift it back
                short high = codedBytes[i];
                //we go over 2 bytes all the time, every itteration
                short low = codedBytes[i + 1];
                //the code that we want to get is short code
                short code = (short) ((high << 8) + low);

                String word = inputHolder.getCodeToWord().get(code);
                if (word != null) decompressingWriter.write(word);

            }
        } catch (FileNotFoundException e) {
            //wrote myself
            System.err.println("Sorry we couldn't find a file " + inputFile);
        } catch (IOException e) {
            System.err.println("Sorry, error occured during reading " + inputFile + " error " + e);
            e.printStackTrace(); //whenever we write an obj, each of obj has to be serializable
        } catch (ClassNotFoundException e) {
            throw new CompressionException(e);
        }
    }
}