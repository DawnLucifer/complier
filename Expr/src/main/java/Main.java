import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main {

    @Deprecated
    public static void main(String[] args) throws Exception {
        // 从标准输入创建 CharStream
        ANTLRInputStream input = new ANTLRInputStream(new FileInputStream("demo.dm"));

        // 创建 lexer，处理输入的 CharStream
        ExprLexer lexer = new ExprLexer(input);

        // 创建 buffer of tokens，保存 lexer 生成的 token
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // 创建 parser，处理 token 缓冲区中的 token
        ExprParser parser = new ExprParser(tokens);

        // 从 init rule，开始语法分析
        ParseTree tree = parser.prog();
        // 输入 LISP 风格的 parse tree，效果类似 -trees

        //print tokens
        for (int i = 0; i < tokens.size(); i++) {
            System.out.println(tokens.get(i));
        }
    }

}
