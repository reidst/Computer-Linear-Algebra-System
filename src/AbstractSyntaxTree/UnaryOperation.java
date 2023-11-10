package AbstractSyntaxTree;

public class UnaryOperation {
    UnaryOperator op;
    Expression exp;

    public UnaryOperation(UnaryOperator op, Expression exp) {
        this.op = op;
        this.exp = exp;
    }

    public UnaryOperator getOp() {
        return op;
    }

    public Expression getExp() {
        return exp;
    }
}
