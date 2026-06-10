# Regex-to-Automata-Converter
Bonus project for Theory of Computation course —  converts a Regular Expression to NFA, DFA, and  Minimized DFA with visual diagrams via Graphviz.
# 🔤 BToc — Regular Expression to Automata Converter

A console-based Java application that takes a **Regular Expression** as input, converts it step-by-step into NFA → DFA → Minimized DFA, simulates it on test strings, and automatically generates **visual diagrams** using Graphviz.

> Developed as a team project — Theory of Computation course

---

## ✨ Features

### 1. 🔁 Regex → NFA (Thompson's Construction)
- Parses the regular expression using the **Shunting-Yard algorithm** to convert it to **Postfix form**
- Adds explicit concatenation operators automatically
- Builds the NFA fragment by fragment using **Thompson's Construction**
- Supports: `|` (union), `.` (concat), `*` (Kleene star), `+` (one or more), `?` (zero or one), `()` (grouping)

### 2. 🔄 NFA → DFA (Subset Construction)
- Converts the NFA to a **Deterministic Finite Automaton**
- Uses the **Subset Construction (Powerset)** algorithm
- Computes epsilon-closures to handle non-determinism

### 3. ✂️ DFA Minimization (Table-Filling Algorithm)
- Minimizes the DFA by merging equivalent states
- Uses the **Table-Filling (Mark & Merge)** algorithm
- Produces the smallest possible DFA for the given regex

### 4. ▶️ String Simulation
- Accepts comma-separated test strings from the user
- Runs each string through the **Minimized DFA**
- Outputs `ACCEPTED` or `REJECTED` for each string
- Shows a detailed **step-by-step trace** of the simulation path
- Supports empty string input using the keyword `EMPTY`

### 5. 🖼️ Automatic Graphviz Diagrams
- Exports visual `.png` diagrams automatically after each run:
  - `nfa.png` — the NFA diagram
  - `dfa.png` — the DFA diagram
  - `min_dfa.png` — the Minimized DFA diagram
  - `simulation_*.png` — simulation path highlighted on the DFA
- Diagrams are saved in the `/diagrams` folder

---

## 🧠 How It Works — The Pipeline

```
User Input (Regex)
       ↓
  RegexParser
  ├── Add explicit concat operator
  └── Shunting-Yard → Postfix tokens
       ↓
     NFA
  └── Thompson's Construction (fragment by fragment)
       ↓
     DFA
  └── Subset Construction (epsilon-closure + powerset)
       ↓
  Minimized DFA
  └── Table-Filling Algorithm
       ↓
  Simulation on test strings → ACCEPTED / REJECTED
       ↓
  GraphvizExporter → .dot files → .png images
```

---

## 🏗️ Project Structure

```
src/main/java/com/mycompany/btoc/
├── btoc.java             # Main entry point — runs the full pipeline
├── RegexParser.java      # Regex parser: adds concat + Shunting-Yard → Postfix
├── Token.java            # Token types: CHAR, UNION, CONCAT, STAR, PLUS, QUESTION
├── NFA.java              # NFA builder using Thompson's Construction
├── NFAState.java         # NFA state with epsilon + symbol transitions
├── DFA.java              # DFA builder (Subset Construction) + Minimization + Simulation
├── DFAState.java         # DFA state with deterministic transitions
├── GraphvizExporter.java # Exports automata to .dot files and generates .png diagrams
diagrams/
├── nfa.png               # NFA diagram
├── dfa.png               # DFA diagram
├── min_dfa.png           # Minimized DFA diagram
└── simulation_*.png      # Simulation path diagrams
```

---

## 🖥️ Example Usage

```
Enter regex (or type 'exit' to quit): a(b|c)*

Enter test strings (comma-separated, use EMPTY for empty string): ab, ac, abbc, a, b, EMPTY

 Summary:
 Input                Result
 --------------------------------
 ab                   ACCEPTED
 ac                   ACCEPTED
 abbc                 ACCEPTED
 a                    ACCEPTED
 b                    REJECTED
 (empty string)       REJECTED

✅ All images saved in 'diagrams' folder.
   (nfa.png, dfa.png, min_dfa.png, simulation_*.png)
```

---

## ⚙️ How to Run

### Requirements
- Java JDK 8+
- [Graphviz](https://graphviz.org/download/) installed and added to system PATH
- Maven (or open directly in NetBeans/IntelliJ)

### Steps
1. Install **Graphviz** and make sure `dot` command works in terminal
2. Clone or download the project
3. Open in **NetBeans** or build with Maven:
   ```
   mvn compile
   mvn exec:java
   ```
4. Enter a regex and test strings when prompted
5. Check the `/diagrams` folder for the generated images

---

## 🔬 Supported Regex Operators

| Operator | Meaning | Example |
|----------|---------|---------|
| `a` | Literal character | `a` matches "a" |
| `\|` | Union (OR) | `a\|b` matches "a" or "b" |
| `*` | Kleene Star (0 or more) | `a*` matches "", "a", "aa"... |
| `+` | One or more | `a+` matches "a", "aa"... |
| `?` | Zero or one | `a?` matches "" or "a" |
| `()` | Grouping | `(ab)+` matches "ab", "abab"... |

---

## 👥 Team

Developed by a team of students — Alexandria University, Faculty of Science  
Course: Theory of Computation (TOC)
