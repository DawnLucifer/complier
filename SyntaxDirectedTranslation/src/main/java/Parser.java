import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Stack;

public class Parser {

    interface Symbol {
    }

    enum T implements Symbol {
        MUL(0),
        ADD(1),
        SUB(2),
        LB(3),
        RB(4),
        EQ(5),
        ID(6),
        END(7);

        int value;

        T(int value) {
            this.value = value;
        }
    }

    enum NT implements Symbol {
        E(8),
        S(9);

        int value;

        NT(int value) {
            this.value = value;
        }

    }

    Symbol[][] productions = {
            {},
            {NT.S, T.ID, T.EQ, NT.E},
            {NT.E, NT.E, T.ADD, NT.E},
            {NT.E, NT.E, T.MUL, NT.E},
            {NT.E, T.SUB, NT.E},
            {NT.E, T.LB, NT.E, T.RB},
            {NT.E, T.ID}
    };

    // 0 移进  1 规约  2 goto  3 acc

    int[][][] table = new int[15][10][];


    T TType(char c) throws SyntaxErrorException {
        if (Character.isLowerCase(c) || Character.isUpperCase(c)) {
            return T.ID;
        }
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
        if (c == '-')
            return T.SUB;
        if (c == '=')
            return T.EQ;
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
        if (t == T.SUB)
            return '-';
        if (t == T.EQ)
            return '=';
        throw new SyntaxErrorException("Unknown symbol '" + t + "'");
    }


    void parse(String s) throws SyntaxErrorException {
        Stack<Integer> state = new Stack<>();
        Stack<Symbol> stack = new Stack<>();
        char[] input = s.toCharArray();
        int index = 0;
        stack.push(T.END);
        state.push(0);
        while (!stack.isEmpty()) {
            int currState = state.peek();
            T t = TType(input[index]);
            int[] next;
            next = table[currState][t.value];
            if (next == null || next.length == 0)
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
                printAnswer(production);
                currState = state.peek();
                NT nt = (NT) production[0];
                next = table[currState][nt.value];
                int nextState = next[1];
                state.push(nextState);
                stack.push(nt);
            } else if (next[0] == 3) {
                break;
            } else throw new SyntaxErrorException(index, input[index]);
        }
    }

    private void printAnswer(Symbol[] production) throws SyntaxErrorException {
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
    }

    // 0 移进  1 规约  2 goto  3 acc
    private void scanTable() throws IOException {

        String fileName = "table.CSV";
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        String s;
        int lines = 0;
        while ((s = in.readLine()) != null) {
            String[] split = s.split(",");
            for (int i = 0; i < split.length; i++) {
                if (!split[i].isEmpty()) {
                    if (split[i].equals("acc")) {
                        table[lines][i] = new int[]{3};
                    } else if (split[i].charAt(0) == 's') {
                        int num = Integer.parseInt(split[i].substring(1));
                        table[lines][i] = new int[]{0, num};
                    } else if (split[i].charAt(0) == 'r') {
                        int num = Integer.parseInt(split[i].substring(1));
                        table[lines][i] = new int[]{1, num};
                    } else {
                        int num = Integer.parseInt(split[i]);
                        table[lines][i] = new int[]{2, num};
                    }
                }
            }
            lines++;
        }
    }

    public static void main(String[] args) throws IOException, SyntaxErrorException {
        String fileName = "g.in";
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        String s = in.readLine();
        Parser parser = new Parser();
        parser.scanTable();
        parser.parse(s);
    }

}
