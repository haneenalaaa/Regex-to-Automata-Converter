
package com.mycompany.btoc;

import java.util.*;


public class NFAState {

    private static int counter = 0;

    private final String name;
    private boolean isFinal;
    private final Map<Character, List<NFAState>> transitions;

    public NFAState() {
        this.name        = "q" + counter++;
        this.isFinal     = false;
        this.transitions = new LinkedHashMap<>();
    }

    public void addTransition(char symbol, NFAState target) {
        transitions.computeIfAbsent(symbol, k -> new ArrayList<>()).add(target);
    }

    public List<NFAState> getTransitions(char symbol) {
        return transitions.getOrDefault(symbol, new ArrayList<>());
    }

    public Map<Character, List<NFAState>> getAllTransitions() {
        return transitions;
    }

    public String  getName()           { return name; }
    public boolean isFinal()           { return isFinal; }
    public void    setFinal(boolean f) { isFinal = f; }

    public static void resetCounter()  { counter = 0; }

    @Override
    public String toString() { return name; }
}