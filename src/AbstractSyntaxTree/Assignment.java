package AbstractSyntaxTree;

public final class Assignment implements Expression {
    Variable var;
    Expression exp;

    public Assignment(Variable var, Expression exp) {
        this.var = var;
        this.exp = exp;
    }

    public Variable getVar() {
        return var;
    }

    public Expression getExp() {
        return exp;
    }

    @Override
    public String toString() {
        return String.format("Assignment(%s, %s)", var.toString(), exp.toString());
    }
}
