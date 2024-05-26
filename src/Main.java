import CompressionException.CompressionException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws CompressionException {
        String inputFile = "data/shakespeare.txt";
        String outputFile = inputFile +".sc";
        try {
            //reade the file - next to put words into code, we need the Map bc key is unique, to avoid repeated words
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));

            //now we need to write code in the output file, need to put all the codes in place and write the whole map object
            ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(outputFile));
            //we need this for byte, to put it into Stream

            Map<String, Short> wordsToCode = new HashMap<>();
            Map<Short, String> codeToWord = new HashMap<>();
            short codeCounter = 0;
            String line = reader.readLine();

            while (line != null) {
                String[] words = line.split("(?<=\\s)|(?=\\s)");
                for (String w : words) {
                    Short existingCode = wordsToCode.get(w);
                    if(existingCode==null){

                        wordsToCode.put(w,codeCounter);
                        codeToWord.put(codeCounter,w);
                        codeCounter++;
                        if(codeCounter==Short.MAX_VALUE){
                            throw new CompressionException("There are too many words in the file");
                        }
                    }
                    System.out.println(w);
                }
                System.out.println();
                line = reader.readLine();
            }
            System.out.println("Numb of words found "+codeCounter);
        } catch (FileNotFoundException e) {
            //wrote myself
            System.err.println("Sorry we couldn't find a file " + inputFile);
        } catch (IOException e) {
            System.err.println("Sorry, error occured during reading " + inputFile + " error " + e);
        }
    }
}