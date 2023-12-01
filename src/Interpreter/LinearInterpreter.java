package Interpreter;

import AbstractSyntaxTree.*;
import AbstractSyntaxTree.Boolean;
import Core.Algorithms;

import java.util.HashMap;
import java.util.Map;

public class LinearInterpreter {
    private final Map<String, Value> variableMap;
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
            case FunctionExpression f -> interpretFunction(f);
            case Assignment a         -> interpretAssignment(a);
        };
    }

    private Value interpretUnaryOperation(UnaryOperation unaryOperation) {
        Value value = interpretExpression(unaryOperation.getExp());
        return switch (value) {
            case Scalar s -> switch (unaryOperation.getOp()) {
                case NEG -> s.negate();
            };
            case Vector v -> switch (unaryOperation.getOp()) {
                case NEG -> v.negate();
            };
            case Matrix s -> switch (unaryOperation.getOp()) {
                case NEG -> s.negate();
            };
            default -> throw new IllegalStateException("Unexpected value: \n" + value.print());
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
                default -> throw new IllegalStateException("Unexpected value: \n" + right.print());
            };
            case Vector l -> switch (right) {
                case Scalar r -> switch (binaryOperation.getOp()) {
                    case MUL -> l.multiply(r);
                    default -> throw new IllegalStateException("Unexpected value: " + binaryOperation.getOp());
                };
                case Vector r -> switch (binaryOperation.getOp()) {
                    case ADD -> l.add(r).asVector();
                    case SUB -> l.subtract(r).asVector();
                    case MUL -> l.dot(r);
                    default -> throw new IllegalStateException("Unexpected value: " + binaryOperation.getOp());
                };
                case Matrix r -> switch (binaryOperation.getOp()) {
                    case ADD -> l.add(r.asVector()).asVector();
                    case SUB -> l.subtract(r.asVector()).asVector();
                    case MUL -> l.dot(r.asVector());
                    default -> throw new IllegalStateException("Unexpected value: " + binaryOperation.getOp());
                };
                default -> throw new IllegalStateException("Unexpected value: \n" + right.print());
            };
            case Matrix l -> switch (right) {
                case Scalar r -> switch (binaryOperation.getOp()) {
                    case MUL -> l.multiply(r);
                    case DIV -> l.divide(r);
                    default -> throw new IllegalStateException("Unexpected value: " + binaryOperation.getOp());
                };
                case Vector r -> switch (binaryOperation.getOp()) {
                    case MUL -> l.multiply(r).asVector();
                    default -> throw new IllegalStateException("Unexpected value: " + binaryOperation.getOp());
                };
                case Matrix r -> switch (binaryOperation.getOp()) {
                    case MUL -> l.multiply(r);
                    case ADD -> l.add(r);
                    case SUB -> l.subtract(r);
                    default -> throw new IllegalStateException("Unexpected value: " + binaryOperation.getOp());
                };
                default -> throw new IllegalStateException("Unexpected value: \n" + right.print());
            };
            default -> throw new IllegalStateException("Unexpected value: \n" + left.print());
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

    private Value interpretFunction(FunctionExpression functionExpression) {
        return switch (functionExpression.getFunc()) {
            case INVERSE -> throw new UnsupportedOperationException("That function does not exist");
            case RREF -> Algorithms.rref((Matrix)interpretExpression(functionExpression.getArgs().getFirst()));
            case EF -> Algorithms.ef((Matrix)interpretExpression(functionExpression.getArgs().getFirst())).getFirst();
            //default -> throw new UnsupportedOperationException("That function does not exist");
            case SPAN -> Algorithms.independentSubset((VectorList) interpretExpression(functionExpression.getArgs().getFirst()));
            case DETERMINANT -> Algorithms.ef((Matrix)interpretExpression(functionExpression.getArgs().getFirst())).getSecond();
            case PROJECT -> ((Vector)interpretExpression(functionExpression.getArgs().getFirst()));
            case DIM -> new FractionScalar(Algorithms.independentSubset((VectorList) interpretExpression(functionExpression.getArgs().getFirst())).size());
            case RANK -> new FractionScalar(Algorithms.rank((Matrix) interpretExpression(functionExpression.getArgs().getFirst())));
            case NULLITY -> new FractionScalar(Algorithms.nullity((Matrix) interpretExpression(functionExpression.getArgs().getFirst())));
            case IS_CONSISTENT -> new Boolean(Algorithms.isConsistent((Matrix) interpretExpression(functionExpression.getArgs().getFirst())));
            case COL -> Algorithms.columnSpace((Matrix) interpretExpression(functionExpression.getArgs().getFirst()));
            case ROW -> Algorithms.rowSpace((Matrix) interpretExpression(functionExpression.getArgs().getFirst()));
            case NUL -> Algorithms.nullSpace((Matrix) interpretExpression(functionExpression.getArgs().getFirst()));
            case SPANS -> new Boolean(Algorithms.spans((VectorList) interpretExpression(functionExpression.getArgs().getFirst()), (VectorList) interpretExpression(functionExpression.getArgs().getLast())));
            case IS_BASIS -> new Boolean(Algorithms.isBasis((VectorList) interpretExpression(functionExpression.getArgs().getFirst())));
            case QR -> Algorithms.QRAlgorithm((Matrix) interpretExpression(functionExpression.getArgs().getFirst()));
            case AUGMENT -> ((Matrix) interpretExpression(functionExpression.getArgs().getFirst())).augment((Matrix) interpretExpression(functionExpression.getArgs().getLast()));
        };
    }
}
