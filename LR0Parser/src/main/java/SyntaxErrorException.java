public class SyntaxErrorException extends Exception {
    public SyntaxErrorException() {
    }

    public SyntaxErrorException(String message) {
        super(message);
    }

    public SyntaxErrorException(int index, char curr) {
        super("At index of " + index + " '" + curr + "' Syntax ERROR");
    }
}
