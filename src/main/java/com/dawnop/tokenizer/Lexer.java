package com.dawnop.tokenizer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    private final Map<String, String> regs;
    private final Map<String, String> keywords;


    public Lexer() {
        regs = new HashMap<>();
        keywords = new HashMap<>();
    }

    public void readReg(String fileName) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        String line;
        int count = 0;
        String reg = "\\s*([A-Z_]+)\\s*:\\s*(.*)\\s*;";
        Pattern p = Pattern.compile(reg);
        while ((line = in.readLine()) != null) {
            count++;
            if (line.isEmpty())
                continue;
            Matcher m = p.matcher(line);
            if (m.find()) {
                String symbol = m.group(1);
                String keyword = m.group(2);
                if (keyword.charAt(0) == '\'' && keyword.charAt(keyword.length() - 1) == '\'')
                    keywords.put(symbol, keyword.substring(1, keyword.length() - 1));
                else
                    regs.put(symbol, keyword);
            } else {
                throw new InvalidPropertiesFormatException("line " + count + " syntax error");
            }
        }
        in.close();
    }

    static class TokenNode {
        String token;
        String type;

        public TokenNode(String token, String type) {
            this.token = token;
            this.type = type;
        }

        @Override
        public String toString() {
            return "TokenNode{" +
                    "token='" + token + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }


    public ArrayList<TokenNode> getTokens(String fileName) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        String line;
        ArrayList<TokenNode> tokens = new ArrayList<>();
        int count = 0;
        while ((line = in.readLine()) != null) {
            count++;
            String[] split = line.split("\\s");
            for (String word : split) {
                boolean flag = false;
                for (String keyword : keywords.keySet()) {
                    if (word.equals(keywords.get(keyword))) {
                        tokens.add(new TokenNode(word, keyword));
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    for (String reg : regs.keySet()) {
                        if (Pattern.matches(regs.get(reg), word)) {
                            tokens.add(new TokenNode(word, reg));
                            flag = true;
                            break;
                        }
                    }
                }
                if (!flag)
                    throw new InvalidPropertiesFormatException("line " + count + " " + word + " syntax error");
            }
        }
        return tokens;
    }

    public static void main(String[] args) throws IOException {
        Lexer lexer = new Lexer();
        lexer.readReg("java.reg");
        for (String s : lexer.regs.keySet()) {
            System.out.println(s + " : " + lexer.regs.get(s));
        }
        for (String s : lexer.keywords.keySet()) {
            System.out.println(s + " : " + lexer.keywords.get(s));
        }
        ArrayList<TokenNode> tokens = lexer.getTokens("test.c");
        for (TokenNode token : tokens) {
            System.out.println(token);
        }
    }
}
