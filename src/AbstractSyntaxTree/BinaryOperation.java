package AbstractSyntaxTree;

public class BinaryOperation {
    BinaryOperator op;
    Expression left;
    Expression right;

    public BinaryOperation(BinaryOperator op, Expression left, Expression right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    public BinaryOperator getOp() {
        return op;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }
}
