//package com.mycompany.btoc;
//
//import java.util.*;
//
///**
// * DFA - بتمثل الـ DFA الكامل وبتحتوي على:
// * 1) Subset Construction (تحويل NFA -> DFA)
// * 2) Table-Filling Minimization (تقليل عدد الحالات)
// * 3) Display (عرض الجدول)
// */
//public class DFA {
//
//    private List<DFAState> states;       // كل حالات الـ DFA
//    private DFAState startState;         // حالة البداية
//    private Set<Character> alphabet;     // الأحرف المستخدمة
//
//    private DFA() {
//        states = new ArrayList<>();
//        alphabet = new LinkedHashSet<>();
//    }
//
//    // ================================================================
//    // PART 1: Subset Construction (NFA -> DFA)
//    // ================================================================
//
//    /**
//     * بيحول NFA لـ DFA باستخدام Subset Construction
//     *
//     * الفكرة:
//     * - كل حالة في الـ DFA = مجموعة من حالات الـ NFA
//     * - نبدأ بـ epsilon-closure(start)
//     * - لكل مجموعة ولكل حرف، نحسب المجموعة الجديدة
//     */
//    public static DFA fromNFA(NFA nfa) {
//        DFA dfa = new DFA();
//        dfa.alphabet = nfa.getAlphabet();
//
//        // Map من الـ key للـ DFAState عشان نتجنب التكرار
//        Map<String, DFAState> stateMap = new LinkedHashMap<>();
//
//        // نحسب epsilon-closure لحالة البداية في الـ NFA
//        Set<NFAState> startSet = epsilonClosure(
//            Collections.singleton(nfa.start)
//        );
//
//        System.out.println("  DFA start state:");
//        System.out.println("    e-closure({" + nfa.start.getName() + "}) = "
//                + setToList(startSet)
//                + "  ->  DFA state: " + setToDisplay(startSet));
//        System.out.println();
//
//        // نبني حالة البداية في الـ DFA
//        String startKey = DFAState.makeKey(startSet);
//        DFAState startDFA = new DFAState("G0", startSet);
//        startDFA.setStart(true);
//
//        // لو مجموعة البداية فيها حالة final في الـ NFA
//        startDFA.setFinal(containsFinal(startSet));
//
//        stateMap.put(startKey, startDFA);
//        dfa.states.add(startDFA);
//        dfa.startState = startDFA;
//
//        // Queue لـ BFS: نعالج كل مجموعة جديدة
//        Queue<DFAState> worklist = new LinkedList<>();
//        worklist.add(startDFA);
//
//        int stepNum = 1; // رقم خطوة الـ output
//
//        while (!worklist.isEmpty()) {
//            DFAState current = worklist.poll();
//
//            System.out.println("  Step " + stepNum + ": DFA state "
//                    + current.getNfaSetString()
//                    + " = " + setToList(current.getNfaStates()));
//
//            // لكل حرف في الـ alphabet
//            for (char sym : dfa.alphabet) {
//                // 1) move: كل الحالات النهائية بعد قراءة sym
//                Set<NFAState> moved = move(current.getNfaStates(), sym);
//
//                if (moved.isEmpty()) continue; // مفيش transition بالحرف ده
//
//                // 2) epsilon-closure للـ moved
//                Set<NFAState> closure = epsilonClosure(moved);
//
//                System.out.println("    on '" + sym + "':  move=" + setToList(moved)
//                        + "  e-closure=" + setToList(closure));
//
//                String key = DFAState.makeKey(closure);
//
//                if (!stateMap.containsKey(key)) {
//                    // مجموعة جديدة -> نبني DFA state جديدة
//                    String newName = "G" + stateMap.size();
//                    DFAState newState = new DFAState(newName, closure);
//                    newState.setFinal(containsFinal(closure));
//
//                    stateMap.put(key, newState);
//                    dfa.states.add(newState);
//                    worklist.add(newState);
//
//                    System.out.println("    -> NEW DFA state: " + newState.getNfaSetString());
//                } else {
//                    System.out.println("    -> (already exists)");
//                }
//
//                // نضيف الـ transition
//                DFAState target = stateMap.get(key);
//                current.addTransition(sym, target);
//
//                String finalMark = target.isFinal() ? "  [FINAL]" : "";
//                System.out.println("    -> Transition: "
//                        + current.getNfaSetString()
//                        + " --" + sym + "--> "
//                        + target.getNfaSetString() + finalMark);
//            }
//
//            System.out.println();
//            stepNum++;
//        }
//
//        System.out.println("  Conversion complete. DFA has "
//                + dfa.states.size() + " states.");
//
//        return dfa;
//    }
//
//    // ================================================================
//    // HELPER METHODS للـ Subset Construction
//    // ================================================================
//
//    /**
//     * epsilon-closure: كل الحالات اللي نوصلها من set معينة
//     * عن طريق epsilon transitions فقط (بدون قراءة حروف)
//     */
//    //تجميع كل الحالات اليل بتوصل ل ابسون نحطها في سيت
//    public static Set<NFAState> epsilonClosure(Set<NFAState> states) {
//        Set<NFAState> closure = new LinkedHashSet<>(states);
//        Stack<NFAState> stack = new Stack<>();
//        stack.addAll(states);
//
//        while (!stack.isEmpty()) {
//            NFAState s = stack.pop();
//            // '\0' = epsilon
//            for (NFAState next : s.getTransitions('\0')) {
//                if (!closure.contains(next)) {
//                    closure.add(next);
//                    stack.push(next);
//                }
//            }
//        }
//
//        return closure;
//    }
//
//    /**
//     * move: كل الحالات اللي نوصلها من set معينة بحرف معين
//     * (بدون epsilon)
//     */
//    private static Set<NFAState> move(Set<NFAState> states, char sym) {
//        Set<NFAState> result = new LinkedHashSet<>();
//        for (NFAState s : states) {
//            result.addAll(s.getTransitions(sym));
//        }
//        return result;
//    }
//
//    // بيتحقق هل المجموعة دي فيها حالة final في الـ NFA
//    private static boolean containsFinal(Set<NFAState> states) {
//        for (NFAState s : states) {
//            if (s.isFinal()) return true;
//        }
//        return false;
//    }
//
//    // بيحول Set لـ List مرتب للعرض
//    private static List<String> setToList(Set<NFAState> states) {
//        List<String> names = new ArrayList<>();
//        for (NFAState s : states) names.add(s.getName());
//        return names;
//    }
//
//    // بيحول Set لـ String للعرض {n0,n1,...}
//    private static String setToDisplay(Set<NFAState> states) {
//        List<String> names = new ArrayList<>();
//        for (NFAState s : states) names.add(s.getName());
//        Collections.sort(names);
//        return "{" + String.join(",", names) + "}";
//    }
//
//    // ================================================================
//    // PART 2: DFA Minimization (Table-Filling Algorithm)
//    // ================================================================
//
//    /**
//     * بيقلل عدد حالات الـ DFA بدمج الحالات المتكافئة
//     *
//     * الخوارزمية:
//     * Phase 1: نعلّم كل جوزين (final, non-final) إنهم مختلفين
//     * Phase 2: نكمّل التعليم للجوزين اللي بيوصلوا لجوزين معلّمين
//     * Phase 3: الجوزين الغير معلّمين = متكافئين -> ندمجهم
//     */
//    public DFA minimize() {
//        System.out.println("=== Step: DFA Minimization (Table-filling) ===");
//
//        int n = states.size();
//        // جدول boolean: marked[i][j] = true لو الحالتان i و j مختلفتان
//        boolean[][] marked = new boolean[n][n];
//
//        // ===== Phase 1: علّم الجوزين (final, non-final) =====
//        System.out.println("  Phase 1: mark pairs where one is final and the other is not.");
//
//        for (int i = 0; i < n; i++) {
//            for (int j = 0; j < i; j++) {
//                DFAState si = states.get(i);
//                DFAState sj = states.get(j);
//
//                // لو واحدة final والتانية لأ -> مختلفين
//                if (si.isFinal() != sj.isFinal()) {
//                    marked[i][j] = true;
//                    System.out.println("    Marked ("
//                            + si.getNfaSetString() + ", "
//                            + sj.getNfaSetString()
//                            + ")  [one final / one not]");
//                }
//            }
//        }
//
//        // ===== Phase 2: انشر التعليم =====
//        System.out.println("  Phase 2: propagate - mark pairs whose successors are marked.");
//
//        boolean changed = true;
//        while (changed) {
//            changed = false;
//            for (int i = 0; i < n; i++) {
//                for (int j = 0; j < i; j++) {
//                    if (marked[i][j]) continue; // معلّم أصلاً
//
//                    DFAState si = states.get(i);
//                    DFAState sj = states.get(j);
//
//                    // لكل حرف في الـ alphabet
//                    for (char sym : alphabet) {
//                        DFAState ti = si.getTransition(sym);
//                        DFAState tj = sj.getTransition(sym);
//
//                        if (ti == null || tj == null) continue;
//                        if (ti == tj) continue; // نفس الحالة -> مش مهم
//
//                        // نعرف index الحالتين
//                        int pi = states.indexOf(ti);
//                        int pj = states.indexOf(tj);
//
//                        // نخلي pi > pj
//                        if (pi < pj) { int tmp = pi; pi = pj; pj = tmp; }
//
//                        // لو الجوزين دول معلّمين -> الجوز الأصلي كمان مختلف
//                        if (marked[pi][pj]) {
//                            marked[i][j] = true;
//                            changed = true;
//                            System.out.println("    Marked ("
//                                    + si.getNfaSetString() + ", "
//                                    + sj.getNfaSetString()
//                                    + ")  via '" + sym + "'");
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//
//        // ===== Phase 3: اجمع المجموعات المتكافئة =====
//        // Union-Find بسيط
//        int[] parent = new int[n];
//        for (int i = 0; i < n; i++) parent[i] = i;
//
//        for (int i = 0; i < n; i++) {
//            for (int j = 0; j < i; j++) {
//                if (!marked[i][j]) {
//                    // الحالتان متكافئتان -> ادمجهم
//                    int pi = find(parent, i);
//                    int pj = find(parent, j);
//                    if (pi != pj) parent[pi] = pj;
//                }
//            }
//        }
//
//        // اجمع المجموعات
//        Map<Integer, List<DFAState>> groups = new LinkedHashMap<>();
//        for (int i = 0; i < n; i++) {
//            int root = find(parent, i);
//            groups.computeIfAbsent(root, k -> new ArrayList<>()).add(states.get(i));
//        }
//
//        System.out.println("  Equivalence groups:");
//        int gIdx = 0;
//        for (Map.Entry<Integer, List<DFAState>> entry : groups.entrySet()) {
//            System.out.print("    Group " + gIdx + " : [");
//            List<String> names = new ArrayList<>();
//            for (DFAState s : entry.getValue()) names.add(s.getNfaSetString());
//            System.out.println(String.join(", ", names) + "]");
//            gIdx++;
//        }
//
//        System.out.println();
//        System.out.println("  Before minimization : " + n + " states");
//        System.out.println("  After  minimization : " + groups.size() + " states");
//        System.out.println();
//
//        // ===== بناء الـ DFA الجديد المصغّر =====
//        return buildMinimizedDFA(groups);
//    }
//
//    // Union-Find helper
//    private int find(int[] parent, int i) {
//        if (parent[i] != i) parent[i] = find(parent, parent[i]);
//        return parent[i];
//    }
//
//    /**
//     * بيبني DFA جديد من المجموعات المتكافئة
//     */
//    private DFA buildMinimizedDFA(Map<Integer, List<DFAState>> groups) {
//        DFA minDFA = new DFA();
//        minDFA.alphabet = this.alphabet;
//
//        // Map من ممثّل المجموعة لـ DFAState الجديدة
//        Map<DFAState, DFAState> representativeToNew = new LinkedHashMap<>();
//        Map<DFAState, DFAState> oldToNew = new LinkedHashMap<>();
//
//        int gIdx = 0;
//        for (Map.Entry<Integer, List<DFAState>> entry : groups.entrySet()) {
//            List<DFAState> group = entry.getValue();
//
//            // نختار ممثّل المجموعة (الأول في القائمة)
//            DFAState representative = group.get(0);
//
//            // نبني الحالة الجديدة
//            DFAState newState = new DFAState("G" + gIdx, representative.getNfaStates());
//            newState.setFinal(representative.isFinal());
//
//            // هل فيها حالة البداية؟
//            for (DFAState old : group) {
//                if (old.isStart()) { newState.setStart(true); break; }
//            }
//
//            representativeToNew.put(representative, newState);
//            for (DFAState old : group) oldToNew.put(old, newState);
//
//            minDFA.states.add(newState);
//            if (newState.isStart()) minDFA.startState = newState;
//
//            gIdx++;
//        }
//
//        // نضيف الـ transitions للحالات الجديدة
//        for (Map.Entry<Integer, List<DFAState>> entry : groups.entrySet()) {
//            DFAState representative = entry.getValue().get(0);
//            DFAState newState = oldToNew.get(representative);
//
//            for (char sym : alphabet) {
//                DFAState oldTarget = representative.getTransition(sym);
//                if (oldTarget != null) {
//                    DFAState newTarget = oldToNew.get(oldTarget);
//                    if (newTarget != null) {
//                        newState.addTransition(sym, newTarget);
//                    }
//                }
//            }
//        }
//
//        return minDFA;
//    }
//
//    // ================================================================
//    // PART 3: Display
//    // ================================================================
//
//    /**
//     * بيعرض الـ DFA كـ Transition Table في الـ Console
//     * showNfaSets = true: يعرض اسماء مجموعات الـ NFA
//     * showNfaSets = false: يعرض اسماء الـ DFA states بس (G0, G1, ...)
//     */
//    public void display(boolean showNfaSets) {
//        System.out.println("+------------------------------------------------------+");
//        System.out.println("|            DFA Diagram (Transition Table)            |");
//        System.out.println("+------------------------------------------------------+");
//
//        // Header
//        int colW = showNfaSets ? 38 : 12;
//        int symW = showNfaSets ? 32 : 10;
//
//        System.out.printf("  %-" + colW + "s|", "State");
//        for (char sym : alphabet) {
//            System.out.printf(" %-" + symW + "s|", " " + sym);
//        }
//        System.out.println();
//
//        // Separator
//        System.out.print("  " + "-".repeat(colW) + "+");
//        for (char sym : alphabet) {
//            System.out.print("-".repeat(symW + 1) + "+");
//        }
//        System.out.println();
//
//        // Rows
//        for (DFAState s : states) {
//            String prefix = s.isStart() ? "-> " : (s.isFinal() ? " * " : "   ");
//            String stateName = showNfaSets ? s.getNfaSetString() : s.getName();
//            System.out.printf("  %s%-" + (colW - 3) + "s|", prefix, stateName);
//
//            for (char sym : alphabet) {
//                DFAState target = s.getTransition(sym);
//                String targetName = (target == null) ? "-"
//                        : (showNfaSets ? target.getNfaSetString() : target.getName());
//                System.out.printf(" %-" + symW + "s|", targetName);
//            }
//            System.out.println();
//        }
//
//        System.out.println();
//        System.out.println("  Legend:  ->  start state     *  accepting state");
//        System.out.println();
//
//        // Transitions بشكل arrow
//        System.out.println("  Transitions (arrow notation):");
//        for (DFAState s : states) {
//            String sName = showNfaSets ? s.getNfaSetString() : s.getName();
//            for (Map.Entry<Character, DFAState> entry : s.getTransitions().entrySet()) {
//                DFAState target = entry.getValue();
//                String tName = showNfaSets ? target.getNfaSetString() : target.getName();
//                String finalMark = target.isFinal() ? "  [FINAL]" : "";
//                System.out.printf("    %-40s --%-3s -->  %s%s%n",
//                        sName, entry.getKey(), tName, finalMark);
//            }
//        }
//
//        System.out.println("+------------------------------------------------------+");
//    }
//
//    // ================================================================
//    // PART 4: Simulation
//    // ================================================================
//
//    /**
//     * بيحاكي الـ DFA على string معين
//     * بيطبع كل خطوة ويرجع true لو مقبولة
//     */
//    public boolean simulate(String input) {
//        System.out.println("+------------------------------------------------------+");
//        String header = "Simulation: \"" + input + "\"";
//        System.out.println("|" + center(header, 54) + "|");
//        System.out.println("+------------------------------------------------------+");
//
//        String displayInput = input.isEmpty() ? "(empty string)" : input;
//        System.out.println("  Input  : " + displayInput);
//        System.out.println("  Length : " + input.length() + " character(s)");
//        System.out.println();
//
//        DFAState current = startState;
//        boolean accepted = false;
//
//        for (int i = 0; i < input.length(); i++) {
//            char sym = input.charAt(i);
//            DFAState next = current.getTransition(sym);
//
//            if (next == null) {
//                // مفيش transition -> رفض مباشر
//                System.out.printf("  Step %-4d current=%-20s read='%c'  DEAD STATE (no transition)%n",
//                        i, current.getName(), sym);
//                System.out.println();
//                System.out.println("  Final state: DEAD STATE  ->  REJECTED");
//                System.out.println("+------------------------------------------------------+");
//                return false;
//            }
//
//            String acceptMark = next.isFinal() ? "  [accepting]" : "";
//            System.out.printf("  Step %-4d current=%-20s read='%c'  next=%-20s%s%n",
//                    i, current.getName(), sym, next.getName(), acceptMark);
//
//            current = next;
//        }
//
//        System.out.println();
//
//        if (current.isFinal()) {
//            System.out.println("  Final state: " + current.getName()
//                    + " is in F  ->  ACCEPTED");
//            accepted = true;
//        } else {
//            System.out.println("  Final state: " + current.getName()
//                    + " is NOT in F  ->  REJECTED");
//        }
//
//        System.out.println("+------------------------------------------------------+");
//        return accepted;
//    }
//
//    // بتوسّط النص في عدد معين من الأعمدة
//    private String center(String text, int width) {
//        int pad = Math.max(0, width - text.length());
//        int left = pad / 2;
//        int right = pad - left;
//        return " ".repeat(left) + text + " ".repeat(right);
//    }
//
//    // Getters
//    public List<DFAState> getStates()    { return states; }
//    public DFAState getStartState()      { return startState; }
//    public Set<Character> getAlphabet()  { return alphabet; }
//}

package com.mycompany.btoc;

import java.util.*;

/**
 * DFA - بتمثل الـ DFA الكامل وبتحتوي على:
 *   1) Subset Construction   (NFA  -> DFA)
 *   2) Table-Filling Minimization (DFA -> Minimized DFA)
 *   3) Display  (Transition Table في الـ Console)
 *   4) Simulation (step-by-step على input strings)
 */
public class DFA {

    private List<DFAState>  states;
    private DFAState        startState;
    private Set<Character>  alphabet;

    private DFA() {
        states   = new ArrayList<>();
        alphabet = new LinkedHashSet<>();
    }


 
    public static DFA fromNFA(NFA nfa) {
        DFA dfa    = new DFA();
        dfa.alphabet = nfa.getAlphabet();

        Map<String, DFAState> stateMap = new LinkedHashMap<>();

        Set<NFAState> startSet = epsilonClosure(Collections.singleton(nfa.start));

        System.out.println("  ε-closure({" + nfa.start.getName() + "}) = "
                + stateNames(startSet) + "  ->  DFA state G0");
        System.out.println();

        DFAState startDFA = new DFAState("G0", startSet);
        startDFA.setStart(true);
        startDFA.setFinal(containsFinal(startSet));

        stateMap.put(DFAState.makeKey(startSet), startDFA);
        dfa.states.add(startDFA);
        dfa.startState = startDFA;

        Queue<DFAState> worklist = new LinkedList<>();
        worklist.add(startDFA);

        int step = 1;
        while (!worklist.isEmpty()) {
            DFAState current = worklist.poll();

            System.out.println("  ── Step " + step + " : process DFA state "
                    + current.getName() + " = " + current.getNfaSetString());

            for (char sym : dfa.alphabet) {
                Set<NFAState> moved   = move(current.getNfaStates(), sym);
                if (moved.isEmpty()) {
                    System.out.println("      on '" + sym + "' : move = {}  (dead / no transition)");
                    continue;
                }
                Set<NFAState> closure = epsilonClosure(moved);

                System.out.println("      on '" + sym + "' :"
                        + "  move = " + stateNames(moved)
                        + "  ε-closure = " + stateNames(closure));

                String key = DFAState.makeKey(closure);
                if (!stateMap.containsKey(key)) {
                    String newName = "G" + stateMap.size();
                    DFAState newState = new DFAState(newName, closure);
                    newState.setFinal(containsFinal(closure));
                    stateMap.put(key, newState);
                    dfa.states.add(newState);
                    worklist.add(newState);
                    System.out.println("        -> NEW state " + newName
                            + " = " + newState.getNfaSetString()
                            + (newState.isFinal() ? "  [FINAL]" : ""));
                } else {
                    System.out.println("        -> (already exists as "
                            + stateMap.get(key).getName() + ")");
                }

                DFAState target = stateMap.get(key);
                current.addTransition(sym, target);
                System.out.println("        -> Transition: "
                        + current.getName() + " --" + sym + "--> " + target.getName());
            }
            System.out.println();
            step++;
        }

        System.out.println("  Subset Construction complete. DFA has "
                + dfa.states.size() + " states.");
        return dfa;
    }


    public static Set<NFAState> epsilonClosure(Set<NFAState> states) {
        Set<NFAState>   closure = new LinkedHashSet<>(states);
        Stack<NFAState> stack   = new Stack<>();
        stack.addAll(states);
        while (!stack.isEmpty()) {
            NFAState s = stack.pop();
            for (NFAState next : s.getTransitions('\0')) {
                if (closure.add(next)) stack.push(next);
            }
        }
        return closure;
    }

    private static Set<NFAState> move(Set<NFAState> states, char sym) {
        Set<NFAState> result = new LinkedHashSet<>();
        for (NFAState s : states) result.addAll(s.getTransitions(sym));
        return result;
    }

    private static boolean containsFinal(Set<NFAState> states) {
        for (NFAState s : states) if (s.isFinal()) return true;
        return false;
    }

    private static String stateNames(Set<NFAState> states) {
        List<String> names = new ArrayList<>();
        for (NFAState s : states) names.add(s.getName());
        return "{" + String.join(", ", names) + "}";
    }


    public DFA minimize() {
        System.out.println("=== DFA Minimization (Table-filling Algorithm) ===");
        System.out.println();

        int n = states.size();
        boolean[][] marked = new boolean[n][n];

        System.out.println("  Phase 1 : mark pairs (final , non-final)");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (states.get(i).isFinal() != states.get(j).isFinal()) {
                    marked[i][j] = true;
                    System.out.println("    Marked  ("
                            + states.get(i).getName() + ", "
                            + states.get(j).getName() + ")");
                }
            }
        }
        System.out.println();

        System.out.println("  Phase 2 : propagate marks");
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < i; j++) {
                    if (marked[i][j]) continue;
                    for (char sym : alphabet) {
                        DFAState ti = states.get(i).getTransition(sym);
                        DFAState tj = states.get(j).getTransition(sym);
                        if (ti == null || tj == null || ti == tj) continue;

                        int pi = states.indexOf(ti);
                        int pj = states.indexOf(tj);
                        if (pi < pj) { int t = pi; pi = pj; pj = t; }

                        if (marked[pi][pj]) {
                            marked[i][j] = true;
                            changed = true;
                            System.out.println("    Marked  ("
                                    + states.get(i).getName() + ", "
                                    + states.get(j).getName()
                                    + ")  via '" + sym + "'");
                        }
                    }
                }
            }
        }
        System.out.println();

        // ── Phase 3: Union-Find ────────
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < i; j++)
                if (!marked[i][j]) {
                    int pi = find(parent, i), pj = find(parent, j);
                    if (pi != pj) parent[pi] = pj;
                }

        Map<Integer, List<DFAState>> groups = new LinkedHashMap<>();
        for (int i = 0; i < n; i++)
            groups.computeIfAbsent(find(parent, i), k -> new ArrayList<>())
                  .add(states.get(i));

        System.out.println("  Equivalence groups:");
        int gIdx = 0;
        for (List<DFAState> group : groups.values()) {
            List<String> names = new ArrayList<>();
            for (DFAState s : group) names.add(s.getName());
            System.out.println("    Group " + gIdx + " : [" + String.join(", ", names) + "]");
            gIdx++;
        }
        System.out.println();
        System.out.println("  Before minimization : " + n + " states");
        System.out.println("  After  minimization : " + groups.size() + " states");
        System.out.println();

        return buildMinimizedDFA(groups);
    }

    private int find(int[] parent, int i) {
        if (parent[i] != i) parent[i] = find(parent, parent[i]);
        return parent[i];
    }

    private DFA buildMinimizedDFA(Map<Integer, List<DFAState>> groups) {
        DFA minDFA = new DFA();
        minDFA.alphabet = this.alphabet;

        Map<DFAState, DFAState> oldToNew = new LinkedHashMap<>();

        int gIdx = 0;
        for (List<DFAState> group : groups.values()) {
            DFAState rep      = group.get(0);
            DFAState newState = new DFAState("G" + gIdx, rep.getNfaStates());
            newState.setFinal(rep.isFinal());
            for (DFAState old : group) {
                if (old.isStart()) { newState.setStart(true); break; }
            }
            for (DFAState old : group) oldToNew.put(old, newState);
            minDFA.states.add(newState);
            if (newState.isStart()) minDFA.startState = newState;
            gIdx++;
        }

        for (List<DFAState> group : groups.values()) {
            DFAState rep      = group.get(0);
            DFAState newState = oldToNew.get(rep);
            for (char sym : alphabet) {
                DFAState oldTarget = rep.getTransition(sym);
                if (oldTarget != null) {
                    DFAState newTarget = oldToNew.get(oldTarget);
                    if (newTarget != null) newState.addTransition(sym, newTarget);
                }
            }
        }

        return minDFA;
    }


    
     
    public void display(boolean showNfaSets) {
        System.out.println("+------------------------------------------------------+");
        System.out.println("|          DFA Transition Table                        |");
        System.out.println("+------------------------------------------------------+");

        int colW = showNfaSets ? 36 : 10;
        int symW = showNfaSets ? 30 : 8;

        // ── Header ───────────────────────────────────────────────
        System.out.printf("  %-" + colW + "s |", "State");
        for (char sym : alphabet) System.out.printf(" %-" + symW + "s |", " " + sym);
        System.out.println();

        System.out.print("  " + "-".repeat(colW) + "-+");
        for (char sym : alphabet) System.out.print("-".repeat(symW + 1) + "-+");
        System.out.println();

        for (DFAState s : states) {
            String prefix = s.isStart() ? "-> " : (s.isFinal() ? " * " : "   ");
            String label  = showNfaSets ? s.getNfaSetString() : s.getName();
            System.out.printf("  %s%-" + (colW - 3) + "s |", prefix, label);

            for (char sym : alphabet) {
                DFAState target = s.getTransition(sym);
                String tLabel   = (target == null) ? "-"
                        : (showNfaSets ? target.getNfaSetString() : target.getName());
                System.out.printf(" %-" + symW + "s |", tLabel);
            }
            System.out.println();
        }

        System.out.println();
        System.out.println("  Legend :  ->  start state     *  accepting state");
        System.out.println();

        System.out.println("  Transitions (arrow notation):");
        for (DFAState s : states) {
            String sLabel = showNfaSets ? s.getNfaSetString() : s.getName();
            for (Map.Entry<Character, DFAState> entry : s.getTransitions().entrySet()) {
                DFAState target = entry.getValue();
                String tLabel   = showNfaSets ? target.getNfaSetString() : target.getName();
                System.out.printf("    %-38s --%-3s-->  %s%s%n",
                        sLabel, entry.getKey(), tLabel,
                        target.isFinal() ? "  [FINAL]" : "");
            }
        }

        System.out.println("+------------------------------------------------------+");
    }

  
 
    public boolean simulate(String input) {
        System.out.println("+------------------------------------------------------+");
        String header = "Simulation: \"" + (input.isEmpty() ? "(empty)" : input) + "\"";
        System.out.println("|" + center(header, 54) + "|");
        System.out.println("+------------------------------------------------------+");

        System.out.println("  Input  : " + (input.isEmpty() ? "(empty string)" : input));
        System.out.println("  Length : " + input.length() + " character(s)");
        System.out.println();

        DFAState current = startState;

        if (input.isEmpty()) {
            System.out.println("  (empty string - no characters to read)");
        }

        for (int i = 0; i < input.length(); i++) {
            char     sym  = input.charAt(i);
            DFAState next = current.getTransition(sym);

            if (next == null) {
                System.out.printf("  Step %-3d  current=%-8s  read='%c'  -> DEAD STATE%n",
                        i + 1, current.getName(), sym);
                System.out.println();
                System.out.println("  Result : REJECTED  (no transition on '" + sym + "')");
                System.out.println("+------------------------------------------------------+");
                return false;
            }

            System.out.printf("  Step %-3d  current=%-8s  read='%c'  -> next=%-8s%s%n",
                    i + 1, current.getName(), sym, next.getName(),
                    next.isFinal() ? "  [accepting]" : "");
            current = next;
        }

        System.out.println();
        boolean accepted = current.isFinal();
        System.out.println("  Final state : " + current.getName()
                + (accepted ? "  is in F  ->  ACCEPTED" : "  is NOT in F  ->  REJECTED"));
        System.out.println("+------------------------------------------------------+");
        return accepted;
    }


    private String center(String text, int width) {
        int pad = Math.max(0, width - text.length());
        int left = pad / 2;
        return " ".repeat(left) + text + " ".repeat(pad - left);
    }


    public List<DFAState>  getStates()     { return states; }
    public DFAState        getStartState() { return startState; }
    public Set<Character>  getAlphabet()   { return alphabet; }
}