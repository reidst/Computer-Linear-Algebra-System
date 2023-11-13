package Interpreter;

import AbstractSyntaxTree.*;

import java.util.HashMap;
import java.util.Map;

public class LinearInterpreter {
    private Map<String, Value> variableMap;
    public LinearInterpreter() {
        variableMap = new HashMap<String, Value>();
    }
    public Value interpret(ExpressionBlock ast) {
        return interpretExpressionBlock(ast);
    }

    private Value interpretExpressionBlock(ExpressionBlock expressionBlock) {
        Value ret = null;
        for (Expression expression:
             expressionBlock.getExpressions()) {
            ret = interpretExpression(expression);
        }
        return ret;
    }

    private Value interpretExpression(Expression expression) {
        return switch (expression) {
            case ValueExpression v    -> v.getValue();
            case UnaryOperation u     -> interpretUnaryOperation(u);
            case BinaryOperation b    -> interpretBinaryOperation(b);
            case Variable v           -> interpretVariable(v);
            case FunctionExpression f -> throw new UnsupportedOperationException();
            case Assignment a         -> interpretAssignment(a);
        };
    }

    private Value interpretUnaryOperation(UnaryOperation unaryOperation) {
        Value value = interpretExpression(unaryOperation.getExp());
        return switch (value) {
            case Scalar s -> switch (unaryOperation.getOp()) {
                case NEG -> s.multiply(new Scalar(-1));
            };
            case Matrix s -> switch (unaryOperation.getOp()) {
                case NEG -> s.multiply(new Scalar(-1));
            };
        };
    }

    private Value interpretBinaryOperation(BinaryOperation binaryOperation) {
        Value left = interpretExpression(binaryOperation.getLeft());
        Value right = interpretExpression(binaryOperation.getRight());
        return switch (left) {
            case Scalar l -> switch (right) {
                case Scalar r -> switch (binaryOperation.getOp()) {
                    case ADD -> l.add(r);
                    case SUB -> l.subtract(r);
                    case MUL -> l.multiply(r);
                    case DIV -> l.divide(r);
                };
                case Matrix r -> switch (binaryOperation.getOp()) {
                    case MUL -> l.multiply(r);
                    default -> throw new IllegalStateException("Unexpected value: " + binaryOperation.getOp());
                };
            };
            case Matrix l -> switch (right) {
                case Scalar r -> switch (binaryOperation.getOp()) {
                    case MUL -> l.multiply(r);
                    case DIV -> l.divide(r);
                    default -> throw new IllegalStateException("Unexpected value: " + binaryOperation.getOp());
                };
                case Matrix r -> switch (binaryOperation.getOp()) {
                    case MUL -> l.multiply(r);
                    case ADD -> l.add(r);
                    case SUB -> l.subtract(r);
                    default -> throw new IllegalStateException("Unexpected value: " + binaryOperation.getOp());
                };
            };
        };
    }

    private Value interpretVariable(Variable variable) {
        return variableMap.get(variable.getName());
    }

    private Value interpretAssignment(Assignment assignment) {
        Value value = interpretExpression(assignment.getExp());
        variableMap.put(assignment.getVar().getName(), value);
        return value;
    }
}
