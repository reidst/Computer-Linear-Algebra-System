package AbstractSyntaxTree;

public final class BinaryOperation implements Expression {
    BinaryOperators op;
    Expression left;
    Expression right;

    public BinaryOperation(BinaryOperators op, Expression left, Expression right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    public BinaryOperators getOp() {
        return op;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public String toString() {
        return String.format("BinaryOperation(%s, %s, %s)", op.toString(), left.toString(), right.toString());
    }
}
