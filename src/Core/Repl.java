package Core;

import AbstractSyntaxTree.*;
import Interpreter.LinearInterpreter;
import Parser.LinearParser;
import org.jparsec.Parser;
import org.jparsec.error.ParserException;

import java.util.Objects;
import java.util.Scanner;

public class Repl {

    public Repl() {}

    public static void repl() {
        Scanner cin = new Scanner(System.in);

        LinearInterpreter interpreter = new LinearInterpreter();

        while (true) {
            System.out.print(">> ");
            String input = cin.nextLine();
            if (Objects.equals(input, "EXIT()")) {
                break;
            }

            Parser<ExpressionBlock> parser = LinearParser.parser();

            boolean endedEarly;
            ExpressionBlock ast = null;
            do {
                endedEarly = false;
                try {
                    ast = parser.parse(input);
                } catch (ParserException e) {
                    if (Objects.equals(e.getErrorDetails().getEncountered(), "EOF")) {
                        endedEarly = true;
                        System.out.print(".. ");
                        input = input + cin.nextLine();
                    } else {
                        throw e;
                    }
                }

            }
            while (endedEarly);

            if (ast != null) {
                Value result = interpreter.interpret(ast);

                System.out.println(result.print());
            }
        }
    }
}
