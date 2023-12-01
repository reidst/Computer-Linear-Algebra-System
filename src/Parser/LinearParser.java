package Parser;

import AbstractSyntaxTree.*;
import org.jparsec.*;
import org.junit.runner.manipulation.Ordering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class LinearParser {
    private static final Parser<?> integerTokenizer = Terminals.IntegerLiteral.TOKENIZER;
    private static final Parser<?> doubleTokenizer = Terminals.DecimalLiteral.TOKENIZER;

    private static final Terminals terminals = Terminals.operators(
            "+","-","*","/","(",")","=",";","[","]","|","<-",",","<",">","{","}",
            "RREF","EF","INVERSE","SPAN","DETERMINANT","PROJECT","DIM","RANK","NULLITY",
            "IS_CONSISTENT","COL","ROW","NUL","SPANS","IS_BASIS","QR","AUGMENT","EIGENSPACE",
            "IS_EIGENVALUE","IS_EIGENVECTOR","TRANSPOSE","ORTHO_BASIS","IN_SPAN","IS_INDEPENDENT");

    private static final Parser<?> identifiers = Terminals.Identifier.TOKENIZER;

    private static final Parser<Void> ignored = Scanners.WHITESPACES.skipMany();

    public static final Parser<?> tokenizer = Parsers.or(doubleTokenizer, integerTokenizer, terminals.tokenizer(), identifiers);

    private static Parser<ExpressionBlock> statementBlockParser(){
        return Parsers.or(assignmentParser(), expressionParser()).sepBy(terminals.token(";")).map(ExpressionBlock::new);
    }

    private static Parser<Assignment> assignmentParser() {
        return Parsers.sequence(
                variableParser(),
                terminals.token("<-"),
                expressionParser(),
                (variable, unused, expression) -> new Assignment(variable, expression)
        );
    }

    // Expressions

    private static Parser<Expression> expressionParser() {
        Parser.Reference<Expression> ref = Parser.newReference();
        Parser<Expression> atom = Parsers.or(
                valueExpressionParser(),
                variableParser(),
                functionParser(ref.lazy())
        );
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

    private static Parser<Variable> variableParser() {
        return Terminals.Identifier.PARSER.map(Variable::new);
    }

    static Parser<Expression> functionParser(Parser<Expression> arg) {
        return Parsers.or(
                rrefParser(arg),
                efParser(arg),
                inverseParser(arg),
                spanParser(arg),
                detParser(arg),
                projParser(arg),
                dimParser(arg),
                rankParser(arg),
                nullityParser(arg),
                isConsParser(arg),
                colParser(arg),
                rowfParser(arg),
                nulParser(arg),
                spansParser(arg),
                isBasisParser(arg),
                QRParser(arg),
                augmentParser(arg),
                transposeParser(arg),
                orthoBasisParser(arg),
                inSpanParser(arg),
                isIndependentParser(arg),
                isEigenvalueParser(arg)
                isEigenvectorParser(arg),
                eigenspaceParser(arg),
        );
    }

    static Parser<FunctionExpression> rrefParser(Parser<Expression> arg) {
        return Parsers.sequence(
                terminals.token("RREF"),
                argumentList(arg),
                (unused, args) ->
                        new FunctionExpression(FunctionName.RREF, args));
    }

    static Parser<FunctionExpression> efParser(Parser<Expression> arg) {
        return Parsers.sequence(
                terminals.token("EF"),
                argumentList(arg),
                (unused, args) ->
                        new FunctionExpression(FunctionName.EF, args));
    }

    static Parser<FunctionExpression> inverseParser(Parser<Expression> arg) {
        return Parsers.sequence(
                terminals.token("INVERSE"),
                argumentList(arg),
                (unused, args) ->
                        new FunctionExpression(FunctionName.INVERSE, args));
    }

    static Parser<FunctionExpression> spanParser(Parser<Expression> arg) {
        return Parsers.sequence(
                terminals.token("SPAN"),
                argumentList(arg),
                (unused, args) ->
                        new FunctionExpression(FunctionName.SPAN, args));
    }

    static Parser<FunctionExpression> detParser(Parser<Expression> arg) {
        return Parsers.sequence(
                terminals.token("DETERMINANT"),
                argumentList(arg),
                (unused, args) ->
                        new FunctionExpression(FunctionName.DETERMINANT, args));
    }

    static Parser<FunctionExpression> projParser(Parser<Expression> arg) {
        return Parsers.sequence(
                terminals.token("PROJECT"),
                argumentList(arg),
                (unused, args) ->
                        new FunctionExpression(FunctionName.PROJECT, args));
    }

    static Parser<FunctionExpression> dimParser(Parser<Expression> arg) {
        return Parsers.sequence(
                terminals.token("DIM"),
                argumentList(arg),
                (unused, args) ->
                        new FunctionExpression(FunctionName.DIM, args));
    }

    static Parser<FunctionExpression> rankParser(Parser<Expression> arg) {
        return Parsers.sequence(
                terminals.token("RANK"),
                argumentList(arg),
                (unused, args) ->
                        new FunctionExpression(FunctionName.RANK, args));
    }

    static Parser<FunctionExpression> nullityParser(Parser<Expression> arg) {
        return Parsers.sequence(
                terminals.token("NULLITY"),
                argumentList(arg),
                (unused, args) ->
                        new FunctionExpression(FunctionName.NULLITY, args));
    }

    static Parser<FunctionExpression> isConsParser(Parser<Expression> arg) {
        return Parsers.sequence(
                terminals.token("IS_CONSISTENT"),
                argumentList(arg),
                (unused, args) ->
                        new FunctionExpression(FunctionName.IS_CONSISTENT, args));
    }

    static Parser<FunctionExpression> colParser(Parser<Expression> arg) {
        return Parsers.sequence(
                terminals.token("COL"),
                argumentList(arg),
                (unused, args) ->
                        new FunctionExpression(FunctionName.COL, args));
    }

    static Parser<FunctionExpression> rowfParser(Parser<Expression> arg) {
        return Parsers.sequence(
                terminals.token("ROW"),
                argumentList(arg),
                (unused, args) ->
                        new FunctionExpression(FunctionName.ROW, args));
    }

    static Parser<FunctionExpression> nulParser(Parser<Expression> arg) {
        return Parsers.sequence(
                terminals.token("NUL"),
                argumentList(arg),
                (unused, args) ->
                        new FunctionExpression(FunctionName.NUL, args));
    }

    static Parser<FunctionExpression> spansParser(Parser<Expression> arg) {
        return Parsers.sequence(
                terminals.token("SPANS"),
                argumentList(arg),
                (unused, args) ->
                        new FunctionExpression(FunctionName.SPANS, args));
    }

    static Parser<FunctionExpression> isBasisParser(Parser<Expression> arg) {
        return Parsers.sequence(
                terminals.token("IS_BASIS"),
                argumentList(arg),
                (unused, args) ->
                        new FunctionExpression(FunctionName.IS_BASIS, args));
    }

    static Parser<FunctionExpression> QRParser(Parser<Expression> arg) {
        return Parsers.sequence(
                terminals.token("QR"),
                argumentList(arg),
                (unused, args) ->
                        new FunctionExpression(FunctionName.QR, args));
    }

    static Parser<FunctionExpression> augmentParser(Parser<Expression> arg) {
        return Parsers.sequence(
                terminals.token("AUGMENT"),
                argumentList(arg),
                (unused, args) ->
                        new FunctionExpression(FunctionName.AUGMENT, args));
    }

    static Parser<FunctionExpression> eigenspaceParser(Parser<Expression> arg) {
        return Parsers.sequence(
                terminals.token("EIGENSPACE"),
                argumentList(arg),
                (unused, args) ->
                        new FunctionExpression(FunctionName.EIGENSPACE, args));
    }

    static Parser<FunctionExpression> isEigenvectorParser(Parser<Expression> arg) {
        return Parsers.sequence(
                terminals.token("IS_EIGENVECTOR"),
                argumentList(arg),
                (unused, args) ->
                        new FunctionExpression(FunctionName.IS_EIGENVECTOR, args));
    }

    static Parser<FunctionExpression> isEigenvalueParser(Parser<Expression> arg) {
        return Parsers.sequence(
                terminals.token("IS_EIGENVALUE"),
                argumentList(arg),
                (unused, args) ->
                        new FunctionExpression(FunctionName.IS_EIGENVALUE, args));
    }

    static Parser<FunctionExpression> transposeParser(Parser<Expression> arg) {
        return Parsers.sequence(
                terminals.token("TRANSPOSE"),
                argumentList(arg),
                (unused, args) ->
                        new FunctionExpression(FunctionName.TRANSPOSE, args));
    }

    static Parser<FunctionExpression> orthoBasisParser(Parser<Expression> arg) {
        return Parsers.sequence(
                terminals.token("ORTHO_BASIS"),
                argumentList(arg),
                (unused, args) ->
                        new FunctionExpression(FunctionName.ORTHO_BASIS, args));
    }

    static Parser<FunctionExpression> inSpanParser(Parser<Expression> arg) {
        return Parsers.sequence(
                terminals.token("IN_SPAN"),
                argumentList(arg),
                (unused, args) ->
                        new FunctionExpression(FunctionName.IN_SPAN, args));
    }

    static Parser<FunctionExpression> isIndependentParser(Parser<Expression> arg) {
        return Parsers.sequence(
                terminals.token("IS_INDEPENDENT"),
                argumentList(arg),
                (unused, args) ->
                        new FunctionExpression(FunctionName.IS_INDEPENDENT, args));
    }

    static Parser<List<Expression>> argumentList(Parser<Expression> arg) {
        return parens(arg.sepBy(terminals.token(",")));
    }

    static <T> Parser<T> parens(Parser<T> arg) {
        return arg.between(terminals.token("("), terminals.token(")"));
    }

    private static Parser<ValueExpression> valueExpressionParser() {
        return Parsers.or(
                scalarParser(),
                vectorParser(),
                matrixParser(),
                vectorListParser()
        ).map(ValueExpression::new);
    }

    private static Parser<Vector> vectorParser() {
        return Parsers.between(
                terminals.token("<"),
                rowParser(),
                terminals.token(">")
        ).map(Vector::new);
    }

    private static Parser<VectorList> vectorListParser() {
        return Parsers.between(
                terminals.token("{"),
                vectorParser().sepBy1(terminals.token(",")),
                terminals.token("}")
        ).map(VectorList::new);
    }

    private static Parser<Matrix> matrixParser() {
        return Parsers.between(
                terminals.token("["),
                rowParser().sepBy1(terminals.token("|")),
                terminals.token("]")).map(lists -> {
                    List<Scalar> values = lists.stream().flatMap(List::stream).toList();
                    if (values.size() != lists.getFirst().size() * lists.size()) {
                        throw new IllegalArgumentException("All rows in a matrix must be of equal length.");
                    }
                    return new Matrix(values, lists.getFirst().size(), lists.size());
                });
    }

    private static Parser<List<Scalar>> rowParser() {
        return Parsers.or(scalarParser(), Parsers.sequence(terminals.token("-"), scalarParser().map(Scalar::negate))).many1();
    }

    private static Parser<Scalar> scalarParser() {
        return Parsers.or(
                decimalScalar(),
                integerScalar()
        );
    }

    private static Parser<Scalar> decimalScalar() {
        return Terminals.DecimalLiteral.PARSER.map(s -> new FractionScalar(Double.parseDouble(s)));
    }

    private static Parser<Scalar> integerScalar() {
        return Terminals.IntegerLiteral.PARSER.map(s -> new FractionScalar(Integer.parseInt(s)));
    }

    // Operators

    private static final UnaryOperator<Expression> NEG = e -> new UnaryOperation(UnaryOperators.NEG, e);
    
    private static final BinaryOperator<Expression> SUB = (e1, e2) -> new BinaryOperation(BinaryOperators.SUB, e1, e2);

    private static final BinaryOperator<Expression> ADD = (e1, e2) -> new BinaryOperation(BinaryOperators.ADD, e1, e2);

    private static final BinaryOperator<Expression> MUL = (e1, e2) -> new BinaryOperation(BinaryOperators.MUL, e1, e2);

    private static final BinaryOperator<Expression> DIV = (e1, e2) -> new BinaryOperation(BinaryOperators.DIV, e1, e2);
    
    private static <T> Parser<T> op(String token, T value) {
        return terminals.token(token).retn(value);
    }

    public static Parser<ExpressionBlock> parser() {
        return statementBlockParser().from(tokenizer, ignored.skipMany());
    }
}
