import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws SyntaxErrorException, IOException {
        Tokenizer tokenizer = new Tokenizer();
        tokenizer.analysis("test.c");
        ArrayList<Tokenizer.Token> tokens = tokenizer.getTokens();
        for (Tokenizer.Token token : tokens) {
            System.out.println(token);
        }
    }
}
