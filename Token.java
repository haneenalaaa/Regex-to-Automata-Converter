
package com.mycompany.btoc;


public class Token {

    public enum Type {
        CHAR,       
        UNION,      
        CONCAT,     
        STAR,       
        PLUS,       
        QUESTION,   
        LPAREN,     
        RPAREN      
    }

    private final Type type;
    private final char value;

    public Token(char value) {
        this.type  = Type.CHAR;
        this.value = value;
    }

    public Token(Type type) {
        this.type = type;
        switch (type) {
            case UNION:    this.value = '|'; break;
            case CONCAT:   this.value = '.'; break;
            case STAR:     this.value = '*'; break;
            case PLUS:     this.value = '+'; break;
            case QUESTION: this.value = '?'; break;
            case LPAREN:   this.value = '('; break;
            case RPAREN:   this.value = ')'; break;
            default:       this.value = 0;
        }
    }

    public Type getType()  { return type; }
    public char getValue() { return value; }

    public boolean isBinary() {
        return type == Type.UNION || type == Type.CONCAT;
    }

    public boolean isUnary() {
        return type == Type.STAR || type == Type.PLUS || type == Type.QUESTION;
    }

    @Override
    public String toString() { return String.valueOf(value); }
}