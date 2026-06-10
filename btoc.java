
package com.mycompany.btoc;

import java.util.*;


public class btoc {

   
    public static void processRegex(String regex, List<String> testStrings) {
        System.out.println();
        System.out.println("############################################################");
        System.out.println(" Regex: " + regex);
        System.out.println("############################################################");

       
        System.out.println("\n------------------------------------------------------------");
        System.out.println("REQUIREMENT 1: Convert Regex to NFA (Thompson's Construction)");
        System.out.println("------------------------------------------------------------");

        String withConcat = RegexParser.getWithConcat(regex);
        List<Token> postfix = RegexParser.parse(regex);
        String postfixStr = RegexParser.postfixToString(postfix);

        System.out.println(" Input regex          : " + regex);
        System.out.println(" With explicit concat : " + withConcat);
        System.out.println(" Postfix form         : " + postfixStr);

        NFA nfa = NFA.buildFromPostfix(postfix);
        System.out.println("\nNFA built with " + nfa.getAllStates().size() + " states.\n");

        nfa.display();
        GraphvizExporter.exportNFA(nfa, regex);           

     
        System.out.println("\n------------------------------------------------------------");
        System.out.println("REQUIREMENT 2: Convert NFA to DFA (Subset Construction)");
        System.out.println("------------------------------------------------------------");

        DFA dfa = DFA.fromNFA(nfa);
        System.out.println("\nDFA after conversion:\n");
        dfa.display(true);

        GraphvizExporter.exportDFA(dfa, regex, "dfa");    

       
        System.out.println("\n------------------------------------------------------------");
        System.out.println("REQUIREMENT 3: Minimize the DFA (Table-filling)");
        System.out.println("------------------------------------------------------------");

        DFA minDFA = dfa.minimize();
        System.out.println("\nMinimized DFA:\n");
        minDFA.display(false);

        GraphvizExporter.exportDFA(minDFA, regex, "min_dfa");   // Minimized DFA Image

       
        System.out.println("\n------------------------------------------------------------");
        System.out.println("REQUIREMENT 4 & 5: Simulate DFA on input strings");
        System.out.println("------------------------------------------------------------");
        System.out.println("Regex : " + regex + "\n");

        System.out.println(" Summary:");
        System.out.printf(" %-20s %s%n", "Input", "Result");
        System.out.println(" " + "-".repeat(32));

        for (String s : testStrings) {
            boolean accepted = simulateSilent(minDFA, s);
            String displayS = s.isEmpty() ? "(empty string)" : s;
            System.out.printf(" %-20s %s%n", displayS, accepted ? "ACCEPTED" : "REJECTED");
        }

        System.out.println("\n Detailed traces:");
        System.out.println();
        for (String s : testStrings) {
            minDFA.simulate(s);
            GraphvizExporter.exportSimulation(minDFA, s, regex);
            System.out.println();
        }
    }

  
    private static boolean simulateSilent(DFA dfa, String input) {
        DFAState current = dfa.getStartState();
        for (char sym : input.toCharArray()) {
            DFAState next = current.getTransition(sym);
            if (next == null) return false;
            current = next;
        }
        return current.isFinal();
    }

  
    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("     Regular Expression → NFA → DFA → Simulation");
        System.out.println("          with Automatic Graphviz Diagrams");
        System.out.println("============================================================");
        System.out.println();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            // إدخال الـ Regex
            System.out.print("Enter regex (or type 'exit' to quit): ");
            String regex = scanner.nextLine().trim();

            if (regex.equalsIgnoreCase("exit")) {
                System.out.println("Program ended. Goodbye!");
                break;
            }

            if (regex.isEmpty()) {
                System.out.println("Error: Regex cannot be empty. Please try again.\n");
                continue;
            }

            System.out.println();
            System.out.println("------------------------------------------------------------");
            System.out.println("REQUIREMENT 4 & 5: Test Strings Simulation");
            System.out.println("------------------------------------------------------------");

            while (true) {
                System.out.print("Enter test strings (comma-separated, use EMPTY for empty string)\n"
                              + "or type 'done' to enter a new regex: ");
                
                String testInput = scanner.nextLine().trim();

                if (testInput.equalsIgnoreCase("done")) {
                    break;
                }

                if (testInput.isEmpty()) {
                    System.out.println("Please enter at least one string.");
                    continue;
                }
  
                List<String> testStrings = new ArrayList<>();
                String[] parts = testInput.split(",");
                for (String part : parts) {
                    String s = part.trim();
                    if (s.equalsIgnoreCase("EMPTY")) {
                        testStrings.add("");
                    } else if (!s.isEmpty()) {
                        testStrings.add(s);
                    }
                }

                if (!testStrings.isEmpty()) {
                    processRegex(regex, testStrings);
                    
                    System.out.println("\n All images have been saved successfully in 'diagrams' folder.");
                    System.out.println("   (nfa.png, dfa.png, min_dfa.png, simulation_*.png)");
                    System.out.println();
                }
            }
            System.out.println();
        }

        scanner.close();
    }
}