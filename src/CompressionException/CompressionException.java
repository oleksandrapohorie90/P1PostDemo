package CompressionException;

public class CompressionException extends Exception {
    public CompressionException(String s) {

    }

    public CompressionException(Exception e) {
    super(e);
    }
}
