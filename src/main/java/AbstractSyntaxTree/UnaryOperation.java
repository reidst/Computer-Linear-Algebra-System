package AbstractSyntaxTree;

public final class UnaryOperation implements Expression {
    UnaryOperators op;
    Expression exp;

    public UnaryOperation(UnaryOperators op, Expression exp) {
        this.op = op;
        this.exp = exp;
    }

    public UnaryOperators getOp() {
        return op;
    }

    public Expression getExp() {
        return exp;
    }

    @Override
    public String toString() {
        return String.format("UnaryOperation(%s, %s)", op.toString(), exp.toString());
    }
}
