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
        END(5);

        int value;

        T(int value) {
            this.value = value;
        }
    }

    enum NT implements Symbol {
        E(6),
        T(7),
        F(8);

        int value;

        NT(int value) {
            this.value = value;
        }

    }

    Symbol[][] productions = {
            {},
            {NT.E, NT.E, T.ADD, NT.T},
            {NT.E, NT.T},
            {NT.T, NT.T, T.MUL, NT.F},
            {NT.T, NT.F},
            {NT.F, T.LB, NT.E, T.RB},
            {NT.F, T.ID}
    };

    // 0 移进  1 规约  2 goto  3 acc

    int[][][] table = {
            //   id   +   *   (   )   #   E   T   F
            {{0, 5}, {}, {}, {0, 4}, {}, {}, {2, 1}, {2, 2}, {2, 3}},   //0
            {{}, {0, 6}, {}, {}, {}, {3}, {}, {}, {}},                  //1
            {{}, {1, 2}, {0, 7}, {}, {1, 2}, {1, 2}, {}, {}, {}},       //2
            {{}, {1, 4}, {1, 4}, {}, {1, 4}, {1, 4}, {}, {}, {}},       //3
            {{0, 5}, {}, {}, {0, 4}, {}, {}, {2, 8}, {2, 2}, {2, 3}},   //4
            {{}, {1, 6}, {1, 6}, {}, {1, 6}, {1, 6}, {}, {}, {}},       //5
            {{0, 5}, {}, {}, {0, 4}, {}, {}, {}, {2, 9}, {2, 3}},       //6
            {{0, 5}, {}, {}, {0, 4}, {}, {}, {}, {}, {2, 10}},          //7
            {{}, {0, 6}, {}, {}, {0, 11}, {}, {}, {}, {}},              //8
            {{}, {1, 1}, {0, 7}, {}, {1, 1}, {1, 1}, {}, {}, {}},       //9
            {{}, {1, 3}, {1, 3}, {}, {1, 3}, {1, 3}, {}, {}, {}},       //10
            {{}, {1, 5}, {1, 5}, {}, {1, 5}, {1, 5}, {}, {}, {}},       //11
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
        throw new SyntaxErrorException("Unknown symbol '" + t + "'");
    }


    void parse(String s) throws SyntaxErrorException {
        Stack<Integer> state = new Stack<>();
        Stack<Symbol> stack = new Stack<>();
        char[] input = s.toCharArray();
        int index = 0;
        stack.push(T.END);
        state.push(0);
        NT currNT = null;
        while (!stack.isEmpty()) {
            int currState = state.peek();
            T t = TType(input[index]);
            int[] next;
            if (currNT == null) {
                next = table[currState][t.value];
            } else {
                next = table[currState][currNT.value];
            }
            if (next.length == 0)
                throw new SyntaxErrorException(index, input[index]);
            // 0 移进  1 规约  2 goto  3 acc
            if (next[0] == 0) {
                int nextState = next[1];
                state.push(nextState);
                stack.push(t);
                index++;
            } else if (next[0] == 1) {
                Symbol[] production = productions[next[1]];
                int times = production.length - 1;
                for (int i = 0; i < times; i++) {
                    state.pop();
                    stack.pop();
                }

                // print
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < production.length; i++) {
                    if (production[i] instanceof NT)
                        sb.append(((NT) production[i]).name());
                    else if (production[i] instanceof T)
                        sb.append(TValue((T) production[i]));
                }
                sb.append("->");
                sb.append(((NT) production[0]).name());
                System.out.println(sb);
                // end print


                currNT = (NT) production[0];
            } else if (next[0] == 2) {
                int nextState = next[1];
                state.push(nextState);
                stack.push(currNT);
                currNT = null;
            } else if (next[0] == 3) {
                break;
            } else throw new SyntaxErrorException(index, input[index]);
        }
    }

    public static void main(String[] args) throws IOException, SyntaxErrorException {
        String fileName = "g.in";
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        String s = in.readLine();
        Parser parser = new Parser();
        parser.parse(s);
    }

}
