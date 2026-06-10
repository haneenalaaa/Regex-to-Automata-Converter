
package com.mycompany.btoc;

import java.util.*;


public class RegexParser {

  
    public static String addExplicitConcat(String regex) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < regex.length(); i++) {
            char current = regex.charAt(i);
            result.append(current);

            if (i + 1 < regex.length()) {
                char next = regex.charAt(i + 1);

                boolean currentIsAtom = (current != '(' && current != '|');
                boolean nextIsAtom    = (next != ')' && next != '|'
                                     && next != '*' && next != '+'
                                     && next != '?');

                if (currentIsAtom && nextIsAtom) {
                    result.append('.');
                }
            }
        }
        return result.toString();
    }

    private static int precedence(char op) {
        switch (op) {
            case '*': case '+': case '?': return 3;
            case '.':                     return 2;
            case '|':                     return 1;
            default:                      return 0;
        }
    }

    public static List<Token> toPostfix(String regexWithConcat) {
        List<Token>    output  = new ArrayList<>();
        Stack<Character> opStack = new Stack<>();

        for (char c : regexWithConcat.toCharArray()) {

            if (c == '(') {
                opStack.push(c);

            } else if (c == ')') {
                while (!opStack.isEmpty() && opStack.peek() != '(')
                    output.add(operatorToken(opStack.pop()));
                if (!opStack.isEmpty()) opStack.pop(); 

            } else if (isOperator(c)) {
                while (!opStack.isEmpty()
                        && opStack.peek() != '('
                        && precedence(opStack.peek()) >= precedence(c))
                    output.add(operatorToken(opStack.pop()));
                opStack.push(c);

            } else {
                output.add(new Token(c));
            }
        }

        while (!opStack.isEmpty())
            output.add(operatorToken(opStack.pop()));

        return output;
    }

    private static boolean isOperator(char c) {
        return c == '|' || c == '.' || c == '*' || c == '+' || c == '?';
    }

    private static Token operatorToken(char c) {
        switch (c) {
            case '|': return new Token(Token.Type.UNION);
            case '.': return new Token(Token.Type.CONCAT);
            case '*': return new Token(Token.Type.STAR);
            case '+': return new Token(Token.Type.PLUS);
            case '?': return new Token(Token.Type.QUESTION);
            default:  return new Token(c);
        }
    }

    public static List<Token> parse(String regex) {
        return toPostfix(addExplicitConcat(regex));
    }

    public static String getWithConcat(String regex) {
        return addExplicitConcat(regex);
    }

    public static String postfixToString(List<Token> tokens) {
        StringBuilder sb = new StringBuilder();
        for (Token t : tokens) sb.append(t);
        return sb.toString();
    }
}