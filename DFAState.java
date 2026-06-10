
package com.mycompany.btoc;

import java.util.*;


public class DFAState {

    private final String              name;
    private final Set<NFAState>       nfaStates;
    private boolean                   isStart;
    private boolean                   isFinal;
    private final Map<Character, DFAState> transitions;

    public DFAState(String name, Set<NFAState> nfaStates) {
        this.name        = name;
        this.nfaStates   = new TreeSet<>(Comparator.comparing(NFAState::getName));
        this.nfaStates.addAll(nfaStates);
        this.transitions = new LinkedHashMap<>();
    }

    public void addTransition(char symbol, DFAState target) {
        transitions.put(symbol, target);
    }

    public DFAState getTransition(char symbol) {
        return transitions.get(symbol);
    }

    public String               getName()        { return name; }
    public Set<NFAState>        getNfaStates()   { return nfaStates; }
    public boolean              isStart()        { return isStart; }
    public boolean              isFinal()        { return isFinal; }
    public void                 setStart(boolean s) { isStart = s; }
    public void                 setFinal(boolean f) { isFinal = f; }
    public Map<Character, DFAState> getTransitions() { return transitions; }

    public static String makeKey(Set<NFAState> states) {
        List<String> names = new ArrayList<>();
        for (NFAState s : states) names.add(s.getName());
        Collections.sort(names);
        return String.join(",", names);
    }

    public String getNfaSetString() {
        List<String> names = new ArrayList<>();
        for (NFAState s : nfaStates) names.add(s.getName());
        Collections.sort(names);
        return "{" + String.join(",", names) + "}";
    }

    @Override public String toString()    { return name; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DFAState)) return false;
        return name.equals(((DFAState) obj).name);
    }

    @Override public int hashCode() { return name.hashCode(); }
}