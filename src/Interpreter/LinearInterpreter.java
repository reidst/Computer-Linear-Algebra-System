package Interpreter;

import AbstractSyntaxTree.*;
import AbstractSyntaxTree.Boolean;
import Core.Algorithms;
import org.apache.commons.math3.util.Pair;

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

    public void assignAnswer(Value ans) {
        variableMap.put("ANS", ans);
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
                case NEG -> v.negate().asVector();
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
                case Vector r -> switch (binaryOperation.getOp()) {
                    case MUL -> r.multiply(l);
                    default -> throw new IllegalStateException("Unexpected value: " + binaryOperation.getOp());
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
        Expression arg = functionExpression.getArgs().getFirst();
        switch (functionExpression.getFunc()) {
            case INVERSE: {
                Pair<Matrix, RowReductionResult> resultPair = switch (interpretExpression(arg)) {
                    case Matrix m -> Algorithms.inverse(m);
                    case RowReductionResult rrr -> Algorithms.inverse(rrr);
                    default -> throw new IllegalArgumentException("Invalid type: INVERSE requires a matrix.");
                };
                if (arg instanceof Variable) {
                    variableMap.put(((Variable) arg).getName(), resultPair.getSecond());
                }
                return resultPair.getFirst();
            }
            case RREF: {
                RowReductionResult result = switch (interpretExpression(arg)) {
                    case Matrix m -> Algorithms.rref(m);
                    case RowReductionResult rrr -> Algorithms.rref(rrr);
                    default -> throw new IllegalArgumentException("Invalid type: RREF requires a matrix.");
                };
                if (arg instanceof Variable) {
                    variableMap.put(((Variable) arg).getName(), result);
                }
                return result.rrefResult();
            }
            case EF: {
                RowReductionResult result = switch (interpretExpression(arg)) {
                    case Matrix m -> Algorithms.ef(m);
                    case RowReductionResult rrr -> Algorithms.ef(rrr);
                    default -> throw new IllegalArgumentException("Invalid type: EF requires a matrix.");
                };
                if (arg instanceof Variable) {
                    variableMap.put(((Variable) arg).getName(), result);
                }
                return result.efResult();
            }
            case SPAN: {
                return switch (interpretExpression(arg)) {
                    case VectorList vl -> Algorithms.independentSubset(vl);
                    default -> throw new IllegalArgumentException("Invalid type: SPAN requires a set of vectors.");
                };
            }
            case DETERMINANT: {
                RowReductionResult cache = switch (interpretExpression(arg)) {
                    case Matrix m -> Algorithms.ef(m);
                    case RowReductionResult rrr -> Algorithms.ef(rrr);
                    default -> throw new IllegalArgumentException("Invalid type: DETERMINANT requires a matrix.");
                };
                if (arg instanceof Variable) {
                    variableMap.put(((Variable) arg).getName(), cache);
                }
                if (cache.determinant() == null) {
                    throw new IllegalArgumentException("Cannot take the determinant of a non-square matrix.");
                }
                return cache.determinant();
            }
            case PROJECT: {
                Value val1 = interpretExpression(arg);
                Value val2 = interpretExpression(functionExpression.getArgs().getLast());
                if (val1 instanceof Vector && val2 instanceof Vector) {
                    return ((Vector) val1).project((Vector) val2);
                } else {
                    throw new IllegalArgumentException("Invalid type(s): PROJECT requires two vectors.");
                }
            }
            case DIM: {
                return switch (interpretExpression(arg)) {
                    case VectorList vl -> new FractionScalar(Algorithms.independentSubset(vl).size());
                    default -> throw new IllegalArgumentException("Invalid type: DIM requires a set of vectors.");
                };
            }
            case RANK: {
                Pair<Integer, RowReductionResult> resultPair = switch (interpretExpression(arg)) {
                    case Matrix m -> Algorithms.rank(m);
                    case RowReductionResult rrr -> Algorithms.rank(rrr);
                    default -> throw new IllegalArgumentException("Invalid type: RANK requires a matrix.");
                };
                if (arg instanceof Variable) {
                    variableMap.put(((Variable) arg).getName(), resultPair.getSecond());
                }
                return new FractionScalar(resultPair.getFirst());
            }
            case NULLITY: {
                Pair<Integer, RowReductionResult> resultPair = switch (interpretExpression(arg)) {
                    case Matrix m -> Algorithms.nullity(m);
                    case RowReductionResult rrr -> Algorithms.nullity(rrr);
                    default -> throw new IllegalArgumentException("Invalid type: NULLITY requires a matrix.");
                };
                if (arg instanceof Variable) {
                    variableMap.put(((Variable) arg).getName(), resultPair.getSecond());
                }
                return new FractionScalar(resultPair.getFirst());
            }
            case IS_CONSISTENT: {
                Pair<java.lang.Boolean, RowReductionResult> resultPair = switch (interpretExpression(arg)) {
                    case Matrix m -> Algorithms.isConsistent(m);
                    case RowReductionResult rrr -> Algorithms.isConsistent(rrr);
                    default -> throw new IllegalArgumentException("Invalid type: IS_CONSISTENT requires a matrix.");
                };
                if (arg instanceof Variable) {
                    variableMap.put(((Variable) arg).getName(), resultPair.getSecond());
                }
                return new Boolean(resultPair.getFirst());
            }
            case COL: {
                Pair<VectorList, RowReductionResult> resultPair = switch (interpretExpression(arg)) {
                    case Matrix m -> Algorithms.columnSpace(m);
                    case RowReductionResult rrr -> Algorithms.columnSpace(rrr);
                    default -> throw new IllegalArgumentException("Invalid type: COL requires a matrix.");
                };
                if (arg instanceof Variable) {
                    variableMap.put(((Variable) arg).getName(), resultPair.getSecond());
                }
                return resultPair.getFirst();
            }
            case ROW: {
                Pair<VectorList, RowReductionResult> resultPair = switch (interpretExpression(arg)) {
                    case Matrix m -> Algorithms.rowSpace(m);
                    case RowReductionResult rrr -> Algorithms.rowSpace(rrr);
                    default -> throw new IllegalArgumentException("Invalid type: COL requires a matrix.");
                };
                if (arg instanceof Variable) {
                    variableMap.put(((Variable) arg).getName(), resultPair.getSecond());
                }
                return resultPair.getFirst();
            }
            case NUL: {
                Pair<VectorList, RowReductionResult> resultPair = switch (interpretExpression(arg)) {
                    case Matrix m -> Algorithms.nullSpace(m);
                    case RowReductionResult rrr -> Algorithms.nullSpace(rrr);
                    default -> throw new IllegalArgumentException("Invalid type: NUL requires a matrix.");
                };
                if (arg instanceof Variable) {
                    variableMap.put(((Variable) arg).getName(), resultPair.getSecond());
                }
                return resultPair.getFirst();
            }
            case SPANS: {
                Value val1 = interpretExpression(arg);
                Value val2 = interpretExpression(functionExpression.getArgs().getLast());
                if (val1 instanceof VectorList && val2 instanceof VectorList) {
                    return new Boolean(Algorithms.spans((VectorList) val1, (VectorList) val2));
                } else {
                    throw new IllegalArgumentException("Invalid type(s): SPANS requires two sets of vectors.");
                }
            }
            case IS_BASIS: {
                return switch (interpretExpression(arg)) {
                    case VectorList vl -> new Boolean(Algorithms.isBasis(vl));
                    default -> throw new IllegalArgumentException("Invalid type: IS_BASIS requires a set of vectors.");
                };
            }
            case QR: {
                return switch (interpretExpression(arg)) {
                    case Matrix m -> Algorithms.QRAlgorithm(m);
                    case RowReductionResult rrr -> Algorithms.QRAlgorithm(rrr.original());
                    default -> throw new IllegalArgumentException("Invalid type: QR requires a matrix.");
                };
            }
            case AUGMENT: {
                Matrix mat1 = switch (interpretExpression(arg)) {
                    case Matrix m -> m;
                    case RowReductionResult rrr -> rrr.original();
                    default -> throw new IllegalArgumentException("Invalid type(s): AUGMENT requires two matrices.");
                };
                Matrix mat2 = switch (interpretExpression(functionExpression.getArgs().getLast())) {
                    case Matrix m -> m;
                    case RowReductionResult rrr -> rrr.original();
                    default -> throw new IllegalArgumentException("Invalid type(s): AUGMENT requires two matrices.");
                };
                return mat1.augmentColumns(mat2);
            }
            case EIGENSPACE: {
                Matrix mat = switch (interpretExpression(arg)) {
                    case Matrix m -> m;
                    case RowReductionResult rrr -> rrr.original();
                    default -> throw new IllegalArgumentException("Invalid type(s): EIGENSPACE requires a matrix and an eigenvalue.");
                };
                Scalar scalar = switch (interpretExpression(functionExpression.getArgs().getLast())) {
                    case Scalar s -> s;
                    default -> throw new IllegalArgumentException("Invalid type(s): EIGENSPACE requires a matrix and an eigenvalue.");
                };
                return Algorithms.eigenspace(mat, scalar);
            }
            case IS_EIGENVALUE: {
                Matrix mat = switch (interpretExpression(arg)) {
                    case Matrix m -> m;
                    case RowReductionResult rrr -> rrr.original();
                    default -> throw new IllegalArgumentException("Invalid type(s): IS_EIGENVALUE requires a matrix and an eigenvalue.");
                };
                Scalar scalar = switch (interpretExpression(functionExpression.getArgs().getLast())) {
                    case Scalar s -> s;
                    default -> throw new IllegalArgumentException("Invalid type(s): IS_EIGENVALUE requires a matrix and an eigenvalue.");
                };
                return new Boolean(Algorithms.isEigenValue(scalar, mat));
            }
            case IS_EIGENVECTOR: {
                Matrix mat = switch (interpretExpression(arg)) {
                    case Matrix m -> m;
                    case RowReductionResult rrr -> rrr.original();
                    default -> throw new IllegalArgumentException("Invalid type(s): IS_EIGENVECTOR requires a matrix and an eigenvector.");
                };
                Vector vec = switch (interpretExpression(functionExpression.getArgs().getLast())) {
                    case Vector v -> v;
                    default -> throw new IllegalArgumentException("Invalid type(s): IS_EIGENVECTOR requires a matrix and an eigenvector.");
                };
                return new Boolean(Algorithms.isEigenVector(vec, mat));
            }
            case TRANSPOSE: {
                return switch (interpretExpression(arg)) {
                    case Matrix m -> m.transpose();
                    case RowReductionResult rrr -> rrr.original().transpose();
                    default -> throw new IllegalArgumentException("Invalid type: TRANSPOSE requires a matrix.");
                };
            }
            case ORTHO_BASIS: {
                return switch (interpretExpression(arg)) {
                    case VectorList vl -> Algorithms.gramSchmidt(vl);
                    default -> throw new IllegalArgumentException("Invalid type: ORTHO_BASIS requires a set of vectors.");
                };
            }
            case IN_SPAN: {
                VectorList vecList = switch (interpretExpression(arg)) {
                    case VectorList vl -> vl;
                    default -> throw new IllegalArgumentException("Invalid type: IN_SPAN requires a set of vectors and a vector.");
                };
                Vector vec = switch (interpretExpression(functionExpression.getArgs().getLast())) {
                    case Vector v -> v;
                    default -> throw new IllegalArgumentException("Invalid type: IN_SPAN requires a set of vectors and a vector.");
                };
                return new Boolean(Algorithms.withinSpan(vecList, vec));
            }
            case IS_INDEPENDENT: {
                return switch (interpretExpression(arg)) {
                    case VectorList vl -> new Boolean(Algorithms.isLinearlyIndependent(vl));
                    default -> throw new IllegalArgumentException("Invalid type: IS_INDEPENDENT requires a set of vectors.");
                };
            }
        }
        throw new IllegalStateException("Unknown function: " + functionExpression.getFunc());
    }
}
