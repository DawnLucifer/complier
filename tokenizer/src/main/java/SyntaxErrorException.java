public class SyntaxErrorException extends Exception {

    public SyntaxErrorException() {
    }

    public SyntaxErrorException(String message) {
        super(message);
    }

    public SyntaxErrorException(String message, int row) {
        super(message + " at row: " + row);
    }

    public SyntaxErrorException(String message, int row, int col) {
        super(message + " at row: " + row + " col: " + col);
    }
}
