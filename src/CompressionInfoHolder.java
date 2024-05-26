import java.io.Serializable;
import java.util.Map;

public class CompressionInfoHolder implements Serializable {


    private final Map<Short, String> codeToWord;
    private final byte[] codedtext;
    Map<Short, String> codeMap;
    //instead of each word we put byte which is a coded word
    byte[] codedWords;
//option +enter + create a field
    public CompressionInfoHolder(Map<Short, String> codeToWord, byte[] codedtext) {

        this.codeToWord = codeToWord;
        this.codedtext = codedtext;
    }

    public byte[] getCodedtext() {
        return codedtext;
    }

    public Map<Short, String> getCodeToWord() {
        return codeToWord;
    }
}
