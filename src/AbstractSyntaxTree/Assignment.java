package AbstractSyntaxTree;

public class Assignment implements Statement{
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
}
