package Parser;

import AbstractSyntaxTree.*;
import org.jparsec.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class LinearParser {
    private static final Parser<?> integerTokenizer = Terminals.IntegerLiteral.TOKENIZER;
    private static final Parser<?> doubleTokenizer = Terminals.DecimalLiteral.TOKENIZER;

    private static final Terminals functions = Terminals.operators(Arrays.stream(FunctionName.values()).map(FunctionName::toString).toList());
    private static final Terminals terminals = Terminals.operators("+","-","*","/","(",")","=",";","[","]","|", "<-");

    private static final Parser<?> identifiers = Terminals.Identifier.TOKENIZER;

    private static final Parser<Void> ignored = Scanners.WHITESPACES.skipMany();

    public static final Parser<?> tokenizer = Parsers.or(doubleTokenizer, integerTokenizer, terminals.tokenizer(), functions.tokenizer(), identifiers);

    private static Parser<ExpressionBlock> statementBlockParser(){
        return Parsers.or(assignmentParser(), operatorExpressionParser()).sepBy(terminals.token(";")).map(ExpressionBlock::new);
    }

    private static Parser<Assignment> assignmentParser() {
        return Parsers.sequence(
                variableParser(),
                terminals.token("<-"),
                operatorExpressionParser(),
                (variable, unused, expression) -> new Assignment(variable, expression)
        );
    }

    // Expressions

    private static Parser<Expression> expressionParser() {
        return Parsers.or(
                valueExpressionParser(),
                variableParser()
//                functionParser()
        );
    }

    private static Parser<Variable> variableParser() {
        return Terminals.Identifier.PARSER.map(Variable::new);
    }

    private static Parser<FunctionExpression> functionParser() {
        return Parsers.or(
                rrefParser()
                //determinantParser(),
                //inverseParser()
        );
    }

    private static Parser<FunctionExpression> rrefParser() {
        Parser.Reference<Expression> ref = Parser.newReference();
        Parser<FunctionExpression> func = Parsers.sequence(
                functions.token("RREF"),
                ref.lazy().between(terminals.token("("), terminals.token(")")),
                (s, expression) -> new FunctionExpression(FunctionName.valueOf(s.toString()), new ArrayList<Expression>(Collections.singleton(expression))));
        ref.set(operatorExpressionParser());
        return func;

    }

    private static Parser<ValueExpression> valueExpressionParser() {
        return Parsers.or(
                scalarParser(),
                matrixParser()
        ).map(ValueExpression::new);
    }

    private static Parser<Matrix> matrixParser() {
        return Parsers.between(
                terminals.token("["),
                rowParser().sepBy1(terminals.token("|")),
                terminals.token("]")).map(lists -> {
                    List<Scalar> values = lists.stream().flatMap(List::stream).toList();
                    assert(values.size() == lists.getFirst().size() * lists.size());
                    return new Matrix(values, lists.getFirst().size(), lists.size());
                });
    }

    private static Parser<List<Scalar>> rowParser() {
        return scalarParser().many1();
    }

    private static Parser<Scalar> scalarParser() {
        return Parsers.or(
                decimalScalar(),
                integerScalar()
        );
    }

    private static Parser<Scalar> decimalScalar() {
        return Terminals.DecimalLiteral.PARSER.map(s -> new Scalar(Double.parseDouble(s)));
    }

    private static Parser<Scalar> integerScalar() {
        return Terminals.IntegerLiteral.PARSER.map(s -> new Scalar(Integer.parseInt(s)));
    }

    // Operators

    private static final UnaryOperator<Expression> NEG = e -> new UnaryOperation(UnaryOperators.NEG, e);
    
    private static final BinaryOperator<Expression> SUB = (e1, e2) -> new BinaryOperation(BinaryOperators.SUB, e1, e2);

    private static final BinaryOperator<Expression> ADD = (e1, e2) -> new BinaryOperation(BinaryOperators.ADD, e1, e2);

    private static final BinaryOperator<Expression> MUL = (e1, e2) -> new BinaryOperation(BinaryOperators.MUL, e1, e2);

    private static final BinaryOperator<Expression> DIV = (e1, e2) -> new BinaryOperation(BinaryOperators.DIV, e1, e2);

    private static Parser<Expression> operatorExpressionParser() {
        Parser.Reference<Expression> ref = Parser.newReference();
        Parser<Expression> atom = ref.lazy().between(terminals.token("("), terminals.token(")")).or(expressionParser());
        Parser<Expression> operatorTable = new OperatorTable<Expression>()
                .prefix(op("-", NEG), 100)
                .infixl(op("*", MUL), 20)
                .infixl(op("/", DIV), 20)
                .infixl(op("+", ADD), 10)
                .infixl(op("-", SUB), 10)
                .build(atom);
        ref.set(operatorTable);
        return operatorTable;
    }
    
    private static <T> Parser<T> op(String token, T value) {
        return terminals.token(token).retn(value);
    }

    public static Parser<ExpressionBlock> parser() {
        return statementBlockParser().from(tokenizer, ignored.skipMany());
    }
}
