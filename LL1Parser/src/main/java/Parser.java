import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;

public class Parser {

    interface Symbol {
    }

    enum T implements Symbol {
        ID(0),
        ADD(1),
        MUL(2),
        LB(3),
        RB(4),
        END(5),
        NON(6);

        int value;

        T(int value) {
            this.value = value;
        }
    }

    enum NT implements Symbol {
        E(0),
        Q(1),
        T(2),
        R(3),
        F(4);

        int value;

        NT(int value) {
            this.value = value;
        }

    }

    Symbol[][][] predictiveParserTable = {
            // a            +           *           (           )           #
            {{NT.T, NT.Q}, {}, {}, {NT.T, NT.Q}, {}, {}},
            {{}, {T.ADD, NT.T, NT.Q}, {}, {}, {T.NON}, {T.NON}},
            {{NT.F, NT.R}, {}, {}, {NT.F, NT.R}, {}, {}},
            {{}, {T.NON}, {T.MUL, NT.F, NT.R}, {}, {T.NON}, {T.NON}},
            {{T.ID}, {}, {}, {T.LB, NT.E, T.RB}, {}, {}},
    };

    String[][] production = {
            {"E->TQ"},
            {"Q->+TQ", "T->e"},
            {"T->FR"},
            {"R->*FR", "R->e"},
            {"F->a", "F->(E)"}
    };

    T TType(char c) throws SyntaxErrorException {
        if (c == 'a')
            return T.ID;
        if (c == '+')
            return T.ADD;
        if (c == '*')
            return T.MUL;
        if (c == '(')
            return T.LB;
        if (c == ')')
            return T.RB;
        if (c == '#')
            return T.END;
        throw new SyntaxErrorException("Unknown symbol '" + c + "'");
    }

    char TValue(T t) throws SyntaxErrorException {
        if (t == T.ID)
            return 'a';
        if (t == T.ADD)
            return '+';
        if (t == T.MUL)
            return '*';
        if (t == T.LB)
            return '(';
        if (t == T.RB)
            return ')';
        if (t == T.END)
            return '#';
        if (t == T.NON)
            return 'e';
        throw new SyntaxErrorException("Unknown symbol '" + t + "'");
    }

    void parse(String s) throws SyntaxErrorException {
        Stack<Symbol> stack = new Stack<>();
        char[] input = s.toCharArray();
        int index = 0;
        stack.push(NT.E);
        while (!stack.isEmpty()) {
            Symbol symbol = stack.pop();
            T t = TType(input[index]);
            if (symbol instanceof NT) {
                Symbol[] next = predictiveParserTable[((NT) symbol).value][t.value];
                if (next.length == 0)
                    throw new SyntaxErrorException(index, input[index]);

                // print
                StringBuilder sb = new StringBuilder();
                sb.append(((NT) symbol).name());
                sb.append("->");
                for (Symbol value : next) {
                    if (value != null) {
                        if (value instanceof NT)
                            sb.append(((NT) value).name());
                        else if (value instanceof T)
                            sb.append(TValue((T) value));
                    }
                }
                System.out.println(sb);
                // end print

                for (int i = next.length - 1; i >= 0; i--) {
                    if (next[i] != null)
                        stack.push(next[i]);
                }
            } else {
                T curr = (T) symbol;
                if (curr != T.NON)
                    index++;
            }
        }
        if (index != input.length - 1)
            throw new SyntaxErrorException(index, input[index]);
    }

    public static void main(String[] args) throws IOException, SyntaxErrorException {
        String fileName = "g.in";
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        String s = in.readLine();
        Parser parser = new Parser();
        parser.parse(s);
    }
}
