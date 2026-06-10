///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.mycompany.btoc;
//
///**
// *
// * @author Cyber
// */
//    
//import java.io.*;
//import java.nio.file.*;
//import java.util.*;
//
///**
// * GraphvizExporter
// * ================
// * يُنشئ ملفات DOT ثم يستدعي Graphviz (dot.exe) لتحويلها لصور PNG.
// *
// * الصور الأربعة المطلوبة:
// *   1. nfa.png         - رسم الـ NFA
// *   2. dfa.png         - رسم الـ DFA بعد Subset Construction
// *   3. min_dfa.png     - رسم الـ Minimized DFA
// *   4. simulation.png  - مسار الـ simulation على string معين
// *
// * كل الصور تُحفظ في مجلد  diagrams/  داخل مجلد المشروع.
// */
//public class GraphvizExporter {
//private static final String OUTPUT_DIR =
//    "D:\\JAVA2\\zulu25.30.17-ca-fx-jdk25.0.1-win_x64\\BToc\\diagrams";
//
//private static final String DOT_EXE =
//    "D:\\Graphviz\\bin\\dot.exe";
//
//private static final String DOT_PATH = "D:\\Graphviz\\bin\\dot.exe";
//
//private static String getDot() {
//    File f = new File(DOT_PATH);
//    return f.exists() ? DOT_PATH : "dot";
//}
//    /** المجلد الذي تُحفظ فيه الصور (يُنشأ تلقائياً) */
//  //  private static final String OUTPUT_DIR = "diagrams";
//
//    // ================================================================
//    // 1. NFA  -> PNG
//    // ================================================================
//
//    public static void exportNFA(NFA nfa, String regexLabel) {
//        StringBuilder dot = new StringBuilder();
//        dot.append("digraph NFA {\n");
//        dot.append("  rankdir=LR;\n");
//        dot.append("  label=\"NFA for: ").append(escape(regexLabel)).append("\";\n");
//        dot.append("  labelloc=t;\n");
//        dot.append("  fontsize=14;\n\n");
//
//        // نقطة البداية الوهمية
//        dot.append("  __start [shape=none, label=\"\"];\n");
//        dot.append("  __start -> ").append(nfa.start.getName()).append(";\n\n");
//
//        List<NFAState> states = nfa.getAllStates();
//
//        // شكل كل حالة
//        for (NFAState s : states) {
//            String shape = s.isFinal() ? "doublecircle" : "circle";
//            dot.append("  ").append(s.getName())
//               .append(" [shape=").append(shape)
//               .append(", label=\"").append(s.getName()).append("\"");
//            if (s == nfa.start) dot.append(", style=filled, fillcolor=lightblue");
//            dot.append("];\n");
//        }
//        dot.append("\n");
//
//        // الـ transitions
//        for (NFAState s : states) {
//            // نجمع الـ transitions المتعددة بنفس الزوج (src,dst) في label واحد
//            Map<String, List<String>> edgeLabels = new LinkedHashMap<>();
//            for (Map.Entry<Character, List<NFAState>> entry
//                    : s.getAllTransitions().entrySet()) {
//                char sym = entry.getKey();
//                String symStr = (sym == '\0') ? "ε" : String.valueOf(sym);
//                for (NFAState target : entry.getValue()) {
//                    String key = s.getName() + "->" + target.getName();
//                    edgeLabels.computeIfAbsent(key, k -> new ArrayList<>()).add(symStr);
//                }
//            }
//            for (Map.Entry<String, List<String>> edge : edgeLabels.entrySet()) {
//                String[] parts = edge.getKey().split("->");
//                String   label = String.join(", ", edge.getValue());
//                dot.append("  ").append(parts[0]).append(" -> ").append(parts[1])
//                   .append(" [label=\"").append(label).append("\"");
//                if (label.contains("ε")) dot.append(", style=dashed, color=gray");
//                dot.append("];\n");
//            }
//        }
//
//        dot.append("}\n");
//        renderDot(dot.toString(), "nfa");
//    }
//
//    // ================================================================
//    // 2. DFA  -> PNG
//    // ================================================================
////
////    public static void exportDFA(DFA dfa, String regexLabel, String dfa2) {
////        StringBuilder dot = new StringBuilder();
////        dot.append("digraph DFA {\n");
////        dot.append("  rankdir=LR;\n");
////        String filename = null;
////        dot.append("  label=\"").append(escape(filename.equals("dfa")
////                ? "DFA for: " : "Minimized DFA for: "))
////           .append(escape(regexLabel)).append("\";\n");
////        dot.append("  labelloc=t;\n");
////        dot.append("  fontsize=14;\n\n");
////
////        dot.append("  __start [shape=none, label=\"\"];\n");
////        dot.append("  __start -> ").append(dfa.getStartState().getName()).append(";\n\n");
////
////        for (DFAState s : dfa.getStates()) {
////            String shape = s.isFinal() ? "doublecircle" : "circle";
////            dot.append("  ").append(s.getName())
////               .append(" [shape=").append(shape)
////               .append(", label=\"").append(s.getName()).append("\"");
////            if (s.isStart())  dot.append(", style=filled, fillcolor=lightblue");
////            if (s.isFinal())  dot.append(", style=filled, fillcolor=lightgreen");
////            if (s.isStart() && s.isFinal())
////                dot.append(", style=filled, fillcolor=yellow");
////            dot.append("];\n");
////        }
////        dot.append("\n");
////
////        for (DFAState s : dfa.getStates()) {
////            // اجمع edges بنفس الزوج
////            Map<String, List<String>> edgeLabels = new LinkedHashMap<>();
////            for (Map.Entry<Character, DFAState> entry : s.getTransitions().entrySet()) {
////                String key = s.getName() + "->" + entry.getValue().getName();
////                edgeLabels.computeIfAbsent(key, k -> new ArrayList<>())
////                          .add(String.valueOf(entry.getKey()));
////            }
////            for (Map.Entry<String, List<String>> edge : edgeLabels.entrySet()) {
////                String[] parts = edge.getKey().split("->");
////                String   label = String.join(", ", edge.getValue());
////                dot.append("  ").append(parts[0]).append(" -> ").append(parts[1])
////                   .append(" [label=\"").append(label).append("\"];\n");
////            }
////        }
////
////        dot.append("}\n");
////        renderDot(dot.toString(), filename);
////    }
//
//    public static void exportDFA(DFA dfa, String label, String fileName) {
//
//    StringBuilder dot = new StringBuilder();
//
//    dot.append("digraph DFA {\n");
//    dot.append("  rankdir=LR;\n");
//    dot.append("  label=\"").append(escape(label)).append("\";\n");
//    dot.append("  labelloc=t;\n");
//    dot.append("  fontsize=14;\n\n");
//
//    dot.append("  __start [shape=none,label=\"\"];\n");
//    dot.append("  __start -> ").append(dfa.getStartState().getName()).append(";\n\n");
//
//    for (DFAState s : dfa.getStates()) {
//        String shape = s.isFinal() ? "doublecircle" : "circle";
//
//        dot.append("  ").append(s.getName())
//           .append(" [shape=").append(shape)
//           .append(", label=\"").append(s.getName()).append("\"");
//
//        if (s.isStart()) dot.append(", style=filled, fillcolor=lightblue");
//        if (s.isFinal()) dot.append(", style=filled, fillcolor=lightgreen");
//
//        dot.append("];\n");
//    }
//
//    dot.append("\n");
//
//    for (DFAState s : dfa.getStates()) {
//        for (Map.Entry<Character, DFAState> e : s.getTransitions().entrySet()) {
//            dot.append("  ")
//               .append(s.getName())
//               .append(" -> ")
//               .append(e.getValue().getName())
//               .append(" [label=\"")
//               .append(e.getKey())
//               .append("\"];\n");
//        }
//    }
//
//    dot.append("}\n");
//
//    renderDot(dot.toString(), fileName);
//}
//    // ================================================================
//    // 3. Simulation path -> PNG
//    // ================================================================
//
//    /**
//     * يرسم مسار الـ simulation على input string معين.
//     * الحالات المزارة تكون مُلوّنة بالترتيب.
//     */
//    public static void exportSimulation(DFA dfa, String input, String regexLabel) {
//        // نحسب المسار
//        List<DFAState> path    = new ArrayList<>();
//        List<String>   symbols = new ArrayList<>();
//        DFAState current = dfa.getStartState();
//        path.add(current);
//        boolean dead = false;
//
//        for (char sym : input.toCharArray()) {
//            DFAState next = current.getTransition(sym);
//            symbols.add(String.valueOf(sym));
//            if (next == null) {
//                dead = true;
//                break;
//            }
//            path.add(next);
//            current = next;
//        }
//
//        boolean accepted = !dead && current.isFinal();
//        String  displayInput = input.isEmpty() ? "(empty)" : input;
//
//        StringBuilder dot = new StringBuilder();
//        dot.append("digraph SIM {\n");
//        dot.append("  rankdir=LR;\n");
//        dot.append("  label=\"Simulation: \\\"").append(escape(displayInput))
//           .append("\\\"  ->  ")
//           .append(accepted ? "ACCEPTED" : "REJECTED")
//           .append("\\nRegex: ").append(escape(regexLabel)).append("\";\n");
//        dot.append("  labelloc=t;\n");
//        dot.append("  fontsize=14;\n\n");
//
//        dot.append("  __start [shape=none, label=\"\"];\n");
//        dot.append("  __start -> ").append(dfa.getStartState().getName()).append(";\n\n");
//
//        // شكل كل حالة
//        Set<DFAState> pathSet = new LinkedHashSet<>(path);
//        for (DFAState s : dfa.getStates()) {
//            String shape = s.isFinal() ? "doublecircle" : "circle";
//            dot.append("  ").append(s.getName())
//               .append(" [shape=").append(shape)
//               .append(", label=\"").append(s.getName()).append("\"");
//
//            if (pathSet.contains(s)) {
//                if (s == path.get(path.size() - 1) && !dead) {
//                    // الحالة الأخيرة في المسار
//                    dot.append(", style=filled, fillcolor=")
//                       .append(accepted ? "lightgreen" : "salmon");
//                } else {
//                    dot.append(", style=filled, fillcolor=lightyellow");
//                }
//            }
//            dot.append("];\n");
//        }
//        dot.append("\n");
//
//        // كل transitions الـ DFA بشكل خافت
//        for (DFAState s : dfa.getStates()) {
//            Map<String, List<String>> edgeLabels = new LinkedHashMap<>();
//            for (Map.Entry<Character, DFAState> entry : s.getTransitions().entrySet()) {
//                String key = s.getName() + "->" + entry.getValue().getName();
//                edgeLabels.computeIfAbsent(key, k -> new ArrayList<>())
//                          .add(String.valueOf(entry.getKey()));
//            }
//            for (Map.Entry<String, List<String>> edge : edgeLabels.entrySet()) {
//                String[] parts = edge.getKey().split("->");
//                String   label = String.join(", ", edge.getValue());
//                dot.append("  ").append(parts[0]).append(" -> ").append(parts[1])
//                   .append(" [label=\"").append(label)
//                   .append("\", color=lightgray, fontcolor=gray];\n");
//            }
//        }
//
//        // مسار الـ simulation بسهام عريضة ومُلوّنة
//        for (int i = 0; i < path.size() - 1; i++) {
//            dot.append("  ").append(path.get(i).getName())
//               .append(" -> ").append(path.get(i + 1).getName())
//               .append(" [label=\"").append(symbols.get(i))
//               .append("\", color=blue, fontcolor=blue, penwidth=2.5];\n");
//        }
//
//        if (dead) {
//            dot.append("  __dead [shape=none, label=\"DEAD\", fontcolor=red];\n");
//            dot.append("  ").append(path.get(path.size() - 1).getName())
//               .append(" -> __dead [label=\"")
//               .append(symbols.isEmpty() ? "?" : symbols.get(symbols.size() - 1))
//               .append("\", color=red, fontcolor=red, penwidth=2.5];\n");
//        }
//
//        dot.append("}\n");
//
//        // اسم الملف: simulation_<input>.png (نستبدل الأحرف الغير صالحة)
//        String safeName = "simulation_"
//                + (input.isEmpty() ? "empty" : input.replaceAll("[^a-zA-Z0-9]", "_"));
//        renderDot(dot.toString(), safeName);
//    }
//
//    // ================================================================
//    // Core: اكتب DOT واستدعِ Graphviz
//    // ================================================================
//
//    /**
//     * يكتب محتوى DOT في ملف مؤقت ثم يستدعي dot.exe لإنتاج PNG.
//     *
//     * @param dotContent  محتوى الـ DOT
//     * @param baseName    اسم الملف بدون امتداد (مثل "nfa", "dfa", "min_dfa")
//     */
////    private static void renderDot(String dotContent, String baseName) {
////        try {
////            // أنشئ المجلد لو مش موجود
////            Path outDir = Paths.get(OUTPUT_DIR);
////            Files.createDirectories(outDir);
////
////            // اكتب ملف الـ DOT
////            Path dotFile = outDir.resolve(baseName + ".dot");
////            Files.writeString(dotFile, dotContent);
////
////            // اسم ملف الـ PNG
////            Path pngFile = outDir.resolve(baseName + ".png");
////
////            // ابحث عن dot في PATH ثم في المواضع الشائعة
////            String dotExe = findDotExecutable();
////            if (dotExe == null) {
////                System.out.println("  [Graphviz] WARNING: 'dot' not found.");
////                System.out.println("             DOT file saved: " + dotFile.toAbsolutePath());
////                System.out.println("             Install Graphviz and add it to PATH.");
////                return;
////            }
////
////            // نشغّل الأمر: dot -Tpng input.dot -o output.png
////            ProcessBuilder pb = new ProcessBuilder(
////                    dotExe, "-Tpng",
////                    dotFile.toAbsolutePath().toString(),
////                    "-o", pngFile.toAbsolutePath().toString()
////            );
////            pb.redirectErrorStream(true);
////            Process process = pb.start();
////
////            // نقرأ الـ output (لو فيه أخطاء)
////            String output = new String(process.getInputStream().readAllBytes());
////            int exitCode  = process.waitFor();
////
////            if (exitCode == 0) {
////                System.out.println("  [Graphviz] PNG saved: " + pngFile.toAbsolutePath());
////            } else {
////                System.out.println("  [Graphviz] ERROR (exit=" + exitCode + "): " + output);
////                System.out.println("             DOT file: " + dotFile.toAbsolutePath());
////            }
////
////        } catch (Exception e) {
////            System.out.println("  [Graphviz] Exception: " + e.getMessage());
////        }
////    }
//    
//    private static void renderDot(String dotContent, String baseName) {
//    try {
//        Path outDir = Paths.get(OUTPUT_DIR);
//        Files.createDirectories(outDir);
//
//        Path dotFile = outDir.resolve(baseName + ".dot");
//        Files.writeString(dotFile, dotContent);
//
//        Path pngFile = outDir.resolve(baseName + ".png");
//
//        String dotExe = getDot();
//
//        ProcessBuilder pb = new ProcessBuilder(
//                dotExe,
//                "-Tpng",
//                dotFile.toString(),
//                "-o",
//                pngFile.toString()
//        );
//
//        Process p = pb.start();
//        int code = p.waitFor();
//
//        if (code == 0) {
//            System.out.println("[Graphviz] OK -> " + pngFile);
//        } else {
//            System.out.println("[Graphviz] FAILED generating image");
//        }
//
//    } catch (Exception e) {
//        System.out.println("[Graphviz ERROR] " + e.getMessage());
//    }
//}
//
//    /**
//     * يبحث عن تنفيذي dot في:
//     *   1. PATH (عبر ProcessBuilder)
//     *   2. مسارات التثبيت الشائعة على Windows و macOS و Linux
//     */
//    private static String findDotExecutable() {
//        // جرب مباشرة من PATH
//        try {
//            Process p = new ProcessBuilder("dot", "-V")
//                    .redirectErrorStream(true).start();
//            p.waitFor();
//            return "dot";
//        } catch (Exception ignored) {}
//
//        // مسارات شائعة على Windows
//        String[] windowsPaths = {
//            "C:\\Program Files\\Graphviz\\bin\\dot.exe",
//            "C:\\Program Files (x86)\\Graphviz\\bin\\dot.exe",
//            "C:\\Program Files\\Graphviz 2.44.1\\bin\\dot.exe",
//            "C:\\Graphviz\\bin\\dot.exe"
//        };
//        for (String p : windowsPaths) {
//            if (new File(p).exists()) return p;
//        }
//
//        // مسارات شائعة على macOS/Linux
//        String[] unixPaths = {
//            "/usr/bin/dot",
//            "/usr/local/bin/dot",
//            "/opt/homebrew/bin/dot"
//        };
//        for (String p : unixPaths) {
//            if (new File(p).exists()) return p;
//        }
//
//        return null;
//    }
//
//    /** يهرّب الأحرف الخاصة في DOT labels */
//    private static String escape(String s) {
//        return s.replace("\\", "\\\\")
//                .replace("\"", "\\\"")
//                .replace("\n", "\\n");
//    }
//}


package com.mycompany.btoc;

import java.io.*;
import java.nio.file.*;
import java.util.*;


public class GraphvizExporter {

    private static final String OUTPUT_DIR = "diagrams";
    private static final String DOT_EXE = "D:\\Graphviz\\bin\\dot.exe"; 

   
    public static void exportNFA(NFA nfa, String regexLabel) {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph NFA {\n");
        dot.append("    rankdir=LR;\n");
        dot.append("    label=\"NFA for: ").append(escape(regexLabel)).append("\";\n");
        dot.append("    labelloc=t;\n");
        dot.append("    fontsize=14;\n\n");

        dot.append("    __start [shape=none, label=\"\"];\n");
        dot.append("    __start -> ").append(nfa.start.getName()).append(";\n\n");

        for (NFAState s : nfa.getAllStates()) {
            String shape = s.isFinal() ? "doublecircle" : "circle";
            dot.append("    ").append(s.getName())
               .append(" [shape=").append(shape)
               .append(", label=\"").append(s.getName()).append("\"");

            if (s == nfa.start) dot.append(", style=filled, fillcolor=lightblue");
            dot.append("];\n");
        }

        dot.append("\n");

        for (NFAState s : nfa.getAllStates()) {
            Map<String, List<String>> edgeLabels = new LinkedHashMap<>();
            for (Map.Entry<Character, List<NFAState>> entry : s.getAllTransitions().entrySet()) {
                char sym = entry.getKey();
                String symStr = (sym == '\0') ? "ε" : String.valueOf(sym);
                for (NFAState target : entry.getValue()) {
                    String key = s.getName() + "->" + target.getName();
                    edgeLabels.computeIfAbsent(key, k -> new ArrayList<>()).add(symStr);
                }
            }

            for (Map.Entry<String, List<String>> edge : edgeLabels.entrySet()) {
                String[] parts = edge.getKey().split("->");
                String label = String.join(", ", edge.getValue());
                dot.append("    ").append(parts[0]).append(" -> ").append(parts[1])
                   .append(" [label=\"").append(label).append("\"");
                if (label.contains("ε")) dot.append(", style=dashed, color=gray");
                dot.append("];\n");
            }
        }

        dot.append("}\n");
        renderDot(dot.toString(), "nfa");
    }

   
    public static void exportDFA(DFA dfa, String regexLabel, String fileName) {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph DFA {\n");
        dot.append("    rankdir=LR;\n");
        dot.append("    label=\"").append(escape(fileName.equals("dfa") ? "DFA" : "Minimized DFA"))
           .append(" for: ").append(escape(regexLabel)).append("\";\n");
        dot.append("    labelloc=t;\n");
        dot.append("    fontsize=14;\n\n");

        dot.append("    __start [shape=none, label=\"\"];\n");
        dot.append("    __start -> ").append(dfa.getStartState().getName()).append(";\n\n");

        for (DFAState s : dfa.getStates()) {
            String shape = s.isFinal() ? "doublecircle" : "circle";
            dot.append("    ").append(s.getName())
               .append(" [shape=").append(shape)
               .append(", label=\"").append(s.getName()).append("\"");

            if (s.isStart()) dot.append(", style=filled, fillcolor=lightblue");
            if (s.isFinal()) dot.append(", style=filled, fillcolor=lightgreen");
            dot.append("];\n");
        }

        dot.append("\n");

        for (DFAState s : dfa.getStates()) {
            for (Map.Entry<Character, DFAState> e : s.getTransitions().entrySet()) {
                dot.append("    ").append(s.getName())
                   .append(" -> ").append(e.getValue().getName())
                   .append(" [label=\"").append(e.getKey()).append("\"];\n");
            }
        }

        dot.append("}\n");
        renderDot(dot.toString(), fileName);
    }

   
    public static void exportSimulation(DFA dfa, String input, String regexLabel) {
        List<DFAState> path = new ArrayList<>();
        List<String> symbols = new ArrayList<>();
        DFAState current = dfa.getStartState();
        path.add(current);
        boolean dead = false;

        for (char sym : input.toCharArray()) {
            DFAState next = current.getTransition(sym);
            symbols.add(String.valueOf(sym));
            if (next == null) {
                dead = true;
                break;
            }
            path.add(next);
            current = next;
        }

        boolean accepted = !dead && current.isFinal();
        String displayInput = input.isEmpty() ? "(empty)" : input;

        StringBuilder dot = new StringBuilder();
        dot.append("digraph SIM {\n");
        dot.append("    rankdir=LR;\n");
        dot.append("    label=\"Simulation: \\\"").append(escape(displayInput))
           .append("\\\" -> ").append(accepted ? "ACCEPTED" : "REJECTED")
           .append("\\nRegex: ").append(escape(regexLabel)).append("\";\n");
        dot.append("    labelloc=t;\n    fontsize=14;\n\n");

        dot.append("    __start [shape=none, label=\"\"];\n");
        dot.append("    __start -> ").append(dfa.getStartState().getName()).append(";\n\n");

        Set<DFAState> pathSet = new LinkedHashSet<>(path);
        for (DFAState s : dfa.getStates()) {
            String shape = s.isFinal() ? "doublecircle" : "circle";
            dot.append("    ").append(s.getName())
               .append(" [shape=").append(shape)
               .append(", label=\"").append(s.getName()).append("\"");

            if (pathSet.contains(s)) {
                if (s == path.get(path.size() - 1) && !dead) {
                    dot.append(", style=filled, fillcolor=")
                       .append(accepted ? "lightgreen" : "salmon");
                } else {
                    dot.append(", style=filled, fillcolor=lightyellow");
                }
            }
            dot.append("];\n");
        }

        dot.append("\n");

        for (DFAState s : dfa.getStates()) {
            for (Map.Entry<Character, DFAState> e : s.getTransitions().entrySet()) {
                dot.append("    ").append(s.getName()).append(" -> ")
                   .append(e.getValue().getName())
                   .append(" [label=\"").append(e.getKey())
                   .append("\", color=lightgray, fontcolor=gray];\n");
            }
        }

        for (int i = 0; i < path.size() - 1; i++) {
            dot.append("    ").append(path.get(i).getName())
               .append(" -> ").append(path.get(i + 1).getName())
               .append(" [label=\"").append(symbols.get(i))
               .append("\", color=blue, penwidth=3];\n");
        }

        if (dead) {
            dot.append("    __dead [shape=none, label=\"DEAD\", fontcolor=red];\n");
            dot.append("    ").append(path.get(path.size() - 1).getName())
               .append(" -> __dead [label=\"").append(symbols.get(symbols.size() - 1))
               .append("\", color=red, penwidth=3];\n");
        }

        dot.append("}\n");

        String safeName = "simulation_" + (input.isEmpty() ? "empty" : input.replaceAll("[^a-zA-Z0-9]", "_"));
        renderDot(dot.toString(), safeName);
    }

  
    private static void renderDot(String dotContent, String baseName) {
        try {
            Path outDir = Paths.get(OUTPUT_DIR);
            Files.createDirectories(outDir);

            Path dotFile = outDir.resolve(baseName + ".dot");
            Files.writeString(dotFile, dotContent);

            Path pngFile = outDir.resolve(baseName + ".png");

            ProcessBuilder pb = new ProcessBuilder(
                DOT_EXE,
                "-Tpng",
                dotFile.toString(),
                "-o",
                pngFile.toString()
            );

            pb.redirectErrorStream(true);
            Process p = pb.start();
            int exitCode = p.waitFor();

            if (exitCode == 0) {
                System.out.println("✅ Image generated: " + pngFile);
            } else {
                System.out.println("❌ Graphviz failed (exit code: " + exitCode + ")");
            }
        } catch (Exception e) {
            System.out.println("❌ Graphviz Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }
}