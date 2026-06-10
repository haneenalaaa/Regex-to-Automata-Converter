
package com.mycompany.btoc;

import java.util.*;


public class NFA {

    public NFAState start;
    public NFAState end;

    private List<NFAState>  allStates;
    private Set<Character>  alphabet;

    public NFA(NFAState start, NFAState end) {
        this.start     = start;
        this.end       = end;
        this.allStates = new ArrayList<>();
        this.alphabet  = new LinkedHashSet<>();
    }

  

    public static NFA buildFromPostfix(List<Token> postfix) {
        NFAState.resetCounter();
        Stack<NFA> stack = new Stack<>();

        for (Token token : postfix) {

            switch (token.getType()) {

                case CHAR: {
                    NFAState s = new NFAState();
                    NFAState e = new NFAState();
                    s.addTransition(token.getValue(), e);
                    stack.push(new NFA(s, e));
                    break;
                }

                case CONCAT: {
                    NFA b = stack.pop(), a = stack.pop();
                    a.end.addTransition('\0', b.start);
                    a.end.setFinal(false);
                    stack.push(new NFA(a.start, b.end));
                    break;
                }

                case UNION: {
                    NFA b = stack.pop(), a = stack.pop();
                    NFAState newStart = new NFAState();
                    NFAState newEnd   = new NFAState();
                    newStart.addTransition('\0', a.start);
                    newStart.addTransition('\0', b.start);
                    a.end.addTransition('\0', newEnd);
                    b.end.addTransition('\0', newEnd);
                    a.end.setFinal(false);
                    b.end.setFinal(false);
                    stack.push(new NFA(newStart, newEnd));
                    break;
                }

                case STAR: {
                    NFA a = stack.pop();
                    NFAState newStart = new NFAState();
                    NFAState newEnd   = new NFAState();
                    newStart.addTransition('\0', a.start);
                    newStart.addTransition('\0', newEnd);
                    a.end.addTransition('\0', a.start);
                    a.end.addTransition('\0', newEnd);
                    a.end.setFinal(false);
                    stack.push(new NFA(newStart, newEnd));
                    break;
                }

                case PLUS: {
                    NFA a = stack.pop();
                    NFAState newStart = new NFAState();
                    NFAState newEnd   = new NFAState();
                    newStart.addTransition('\0', a.start);
                    a.end.addTransition('\0', a.start);
                    a.end.addTransition('\0', newEnd);
                    a.end.setFinal(false);
                    stack.push(new NFA(newStart, newEnd));
                    break;
                }

                case QUESTION: {
                    NFA a = stack.pop();
                    NFAState newStart = new NFAState();
                    NFAState newEnd   = new NFAState();
                    newStart.addTransition('\0', a.start);
                    newStart.addTransition('\0', newEnd);
                    a.end.addTransition('\0', newEnd);
                    a.end.setFinal(false);
                    stack.push(new NFA(newStart, newEnd));
                    break;
                }

                default:
                    break;
            }
        }

        NFA result = stack.pop();
        result.end.setFinal(true);
        result.collectStates();
        return result;
    }

   
    private void collectStates() {
        allStates = new ArrayList<>();
        alphabet  = new LinkedHashSet<>();

        Set<NFAState>   visited = new HashSet<>();
        Queue<NFAState> queue   = new LinkedList<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            NFAState current = queue.poll();
            if (visited.contains(current)) continue;
            visited.add(current);
            allStates.add(current);

            for (Map.Entry<Character, List<NFAState>> entry
                    : current.getAllTransitions().entrySet()) {
                char sym = entry.getKey();
                if (sym != '\0') alphabet.add(sym);
                for (NFAState next : entry.getValue())
                    if (!visited.contains(next)) queue.add(next);
            }
        }
    }

    public List<NFAState> getAllStates() { return allStates; }
    public Set<Character> getAlphabet()  { return alphabet; }

    public void display() {
        System.out.println("+------------------------------------------------------+");
        System.out.println("|                     NFA Details                      |");
        System.out.println("+------------------------------------------------------+");

        System.out.println("  States:");
        for (NFAState s : allStates) {
            String marker = "";
            if (s == start)  marker += "  <- START";
            if (s.isFinal()) marker += "  <- FINAL (accepting)";
            System.out.println("    " + s.getName() + marker);
        }

        System.out.println();
        System.out.println("  Alphabet : " + alphabet);
        System.out.println();
        System.out.println("  Transitions  (\"e\" = epsilon):");
        System.out.println();

        for (NFAState s : allStates) {
            for (Map.Entry<Character, List<NFAState>> entry
                    : s.getAllTransitions().entrySet()) {
                char   sym    = entry.getKey();
                String symStr = (sym == '\0') ? "e" : String.valueOf(sym);
                for (NFAState target : entry.getValue()) {
                    String finalMark = target.isFinal() ? "  [FINAL]" : "";
                    System.out.printf("    %-8s --%s--> %-8s%s%n",
                            s.getName(), symStr, target.getName(), finalMark);
                }
            }
        }

        System.out.println("+------------------------------------------------------+");
    }
}