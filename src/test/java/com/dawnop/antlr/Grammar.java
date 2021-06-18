package com.dawnop.antlr;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;

import java.io.IOException;


public class Grammar {


    @Deprecated
    @Test
    public void testGrammar() throws IOException {
        String source = "Demo.java";
        CharStream cs = CharStreams.fromFileName(source);
        Lexer lexer = new JavaLexer(cs);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        ParseTree tree = parser.classBody();
    }
}
