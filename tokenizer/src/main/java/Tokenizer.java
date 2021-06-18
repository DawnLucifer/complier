import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Tokenizer {

    String[] keywords = {"if", "else", "int", "float", "while", "main", "cout", "endl", "return"};
    String[] operators = {"++", "--", "+", "-", "*", "/", "<", ">", "%", "=", ">>", "<<", "!", "=="};
    char[] separators = {'{', '}', '(', ')', '[', ']', ';', ','};

    ArrayList<Token> tokens;
    Set<Character> singleOperator;
    int row;
    int col;

    public Tokenizer() {
        tokens = new ArrayList<>();
        initSingleOperator();
        state = State.NON;
        curr = new StringBuilder();
        row = 0;
        col = 0;
    }

    void initSingleOperator() {
        singleOperator = new HashSet<>();
        for (String operator : operators)
            for (int i = 0; i < operator.length(); i++)
                singleOperator.add(operator.charAt(i));
    }

    State state;
    State newState;
    StringBuilder curr;

    enum State {
        NON(0),    // 无状态
        ID(1),     // 字符串
        INT(2),    // 整数
        FLOAT(3),  // 浮点数
        OP(4),     // 运算符
        SEP(5),    // 界符
        STR(6),    // 字符串常量
        ERR(7);    // 错误

        final int value;

        State(int value) {
            this.value = value;
        }
    }

    State[][] transformTable = {
            // character   digit   point   blank    separator   operator    quotation
            {State.ID, State.INT, State.ERR, State.NON, State.SEP, State.OP, State.STR}, //NON
            {State.ID, State.ID, State.ERR, State.NON, State.SEP, State.OP, State.ERR}, //ID
            {State.ERR, State.INT, State.FLOAT, State.NON, State.SEP, State.OP, State.ERR}, // INT
            {State.ERR, State.FLOAT, State.ERR, State.NON, State.SEP, State.OP, State.ERR}, //FLOAT
            {State.ID, State.INT, State.ERR, State.NON, State.SEP, State.OP, State.STR}, //OP
            {State.ID, State.INT, State.ERR, State.NON, State.SEP, State.OP, State.STR}, //SEP
            {State.STR, State.STR, State.STR, State.STR, State.STR, State.STR, State.NON}, // STR
            {State.ID, State.INT, State.ERR, State.NON, State.SEP, State.OP, State.STR}, //ERR
    };

    void transformAction() throws SyntaxErrorException {
        String s = curr.toString();
        switch (state) {
            case OP:
                if (isOperator(s))
                    tokens.add(new Token(s, TokenType.OPERATOR));
                else
                    throw new SyntaxErrorException("Unknown Operator " + s, row, col);
                break;
            case INT:
                if (newState == State.FLOAT)
                    return;
            case FLOAT:
                tokens.add(new Token(s, TokenType.NUMBER));
                break;
            case ID:
                if (isKeyword(s))
                    tokens.add(new Token(s, TokenType.KEYWORD));
                else
                    tokens.add(new Token(s, TokenType.IDENTIFIER));
                break;
            case SEP:
                if (newState != State.SEP)
                    tokens.add(new Token(s, TokenType.SEPARATOR));
                break;
            case STR:
                tokens.add(new Token(s, TokenType.STRING));
                break;
            case ERR:
                throw new SyntaxErrorException("\"" + s + "\"", row, col);
        }
        curr = new StringBuilder();
    }

    enum TokenType {
        KEYWORD,    // 关键字
        OPERATOR,   // 运算符
        SEPARATOR,  // 分隔符
        NUMBER,     // 数字
        IDENTIFIER, // 变量
        STRING,
    }

    enum CharType {
        CHAR(0),   // 字母
        NUM(1),    // 数字
        POINT(2),  // 小数点
        BLANK(3),  // 空白符
        SEP(4),    // 分隔符
        OP(5),     // 运算符
        QUOT(6);   // 双引号

        final int value;

        CharType(int value) {
            this.value = value;
        }
    }


    static class Token {
        String text;
        TokenType tokenType;

        public Token(String text, TokenType type) {
            this.text = text;
            this.tokenType = type;
        }

        @Override
        public String toString() {
            return "Token{" +
                    "text='" + text + '\'' +
                    ", type='" + tokenType + '\'' +
                    '}';
        }
    }

    boolean isKeyword(String str) {
        for (String keyword : keywords)
            if (keyword.equals(str))
                return true;
        return false;
    }

    boolean isOperator(String str) {
        for (String operator : operators)
            if (operator.equals(str))
                return true;
        return false;
    }

    boolean isSeparator(char c) {
        for (char separator : separators)
            if (c == separator)
                return true;
        return false;
    }

    boolean isSingleOperator(char c) {
        for (char character : singleOperator)
            if (c == character)
                return true;
        return false;
    }

    CharType charType(char c) throws SyntaxErrorException {
        if (Character.isUpperCase(c) || Character.isLowerCase(c))
            return CharType.CHAR;
        else if (Character.isDigit(c))
            return CharType.NUM;
        else if (c == '.')
            return CharType.POINT;
        else if (Character.isWhitespace(c))
            return CharType.BLANK;
        else if (isSeparator(c))
            return CharType.SEP;
        else if (isSingleOperator(c))
            return CharType.OP;
        else if (c == '"')
            return CharType.QUOT;
        else
            throw new SyntaxErrorException("Unknown Character " + c, row, col);
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    void analysis(String fileName) throws IOException, SyntaxErrorException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        String line;
        while ((line = in.readLine()) != null) {
            row++;
            for (col = 0; col < line.length(); col++) {
                char c = line.charAt(col);
                CharType charType = charType(c);
                newState = transformTable[state.value][charType.value];
                if (state != newState)
                    transformAction();
                state = newState;
                curr.append(c);
            }
        }
        transformAction();
    }
}
