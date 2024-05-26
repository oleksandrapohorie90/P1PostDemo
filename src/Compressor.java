import CompressionException.CompressionException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Compressor {
    public static void main(String[] args) throws CompressionException {
        //every word that we had had to be replaced by two bytes, twice as many bytes as number of words

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
            //must assign code to a new line if we want to write lines from the new line
            short newLineCode = 0;

            //we need to understand line
            wordsToCode.put(System.lineSeparator(), newLineCode);
            codeToWord.put(newLineCode, System.lineSeparator());

            short codeCounter = (short) (newLineCode + 1);
            int totalNumOfWords = 0;
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
                        totalNumOfWords++;

                        codeCounter++;
                        if (codeCounter == Short.MAX_VALUE) {
                            throw new CompressionException("There are too many words in the file");
                        }
                    }
                    //System.out.println(w);
                }
                //System.out.println();
                codedText.write(0);
                codedText.write(0);
                //whenevr we put a word-we count it
                totalNumOfWords++;
                line = reader.readLine();
            }

            //now we need to write code in the output file, need to put all the codes in place and write the whole map object
            ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(compressedFile));
            //see CompressionInfoFolder for fields
            CompressionInfoHolder holder = new CompressionInfoHolder(codeToWord, codedText.toByteArray());
            writer.writeObject(holder);
            writer.flush();//write it to the disk, buffer when its not completely filled, need to force flush
            writer.close();
            System.out.println("Numb of unique words found " + codeCounter);
            System.out.println("Numb of total words found " + codeCounter);
            // here was decompressor file

        } catch (FileNotFoundException e) {
            //wrote myself
            System.err.println("Sorry we couldn't find a file " + inputFile);
        } catch (IOException e) {
            System.err.println("Sorry, error occured during reading " + inputFile + " error " + e);
            e.printStackTrace(); //whenever we write an obj, each of obj has to be serializable
        }
    }
}