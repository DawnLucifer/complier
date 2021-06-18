import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TopDownParser {

    public TopDownParser(String s) {
        input = s.toCharArray();
        index = 0;
        match = true;
    }

    char[] input;
    int index;
    boolean match;

    void E() throws SyntaxErrorException {
        System.out.println("E->TQ");
        T();
        Q();
    }

    void T() throws SyntaxErrorException {
        System.out.println("T->FR");
        F();
        R();
    }

    void Q() throws SyntaxErrorException {
        if (input[index] == '+') {
            System.out.println("Q->+TQ");
            index++;
            T();
            Q();
        } else {
            System.out.println("Q->e");
        }
    }

    void R() throws SyntaxErrorException {
        if (input[index] == '*') {
            System.out.println("R->*FR");
            index++;
            F();
            R();
        } else {
            System.out.println("R->e");
        }
    }

    void F() throws SyntaxErrorException {
        if (input[index] == 'a') {
            System.out.println("F->a");
            index++;
        } else if (input[index] == '(') {
            System.out.println("F->(E)");
            index++;
            E();
            if (input[index] == ')') {
                index++;
            } else {
                match = false;
                throw new SyntaxErrorException(index, input[index]);
            }
        } else {
            match = false;
            throw new SyntaxErrorException(index, input[index]);
        }
    }

    boolean parse() throws SyntaxErrorException {
        E();
        if (input[index] == '#') {
            return true;
        } else {
            throw new SyntaxErrorException(index, input[index]);
        }
    }


    public static void main(String[] args) throws IOException, SyntaxErrorException {
        String fileName = "g.in";
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        String s = in.readLine();
        TopDownParser parser = new TopDownParser(s);
        boolean parse = parser.parse();
        System.out.println(parse);
    }
}
