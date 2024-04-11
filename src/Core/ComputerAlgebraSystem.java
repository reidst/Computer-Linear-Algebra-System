package Core;

public class ComputerAlgebraSystem {
    public static void main(String[] args) {
        System.out.println("--------------------");
        System.out.println("Welcome to CLAS v1.0");
        System.out.println("--------------------");
        System.out.println("Syntax:");
        System.out.println("\t`name <- expression` saves expression to variable");
        System.out.println("\t`[1 2 3 | 4 5 6]` is a 2x3 matrix");
        System.out.println("\t`<1 2 3>` is a 3-dimensional vector");
        System.out.println("\t`RREF(A)` calculates reduced echelon form of A");
        System.out.println("\t`A * B` multiplies matrices or vectors A and B (vector multiplication uses the dot product)");
        System.out.println("Enjoy!");
        Repl.repl();
    }
}
