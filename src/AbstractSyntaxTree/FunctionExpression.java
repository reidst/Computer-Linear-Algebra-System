package AbstractSyntaxTree;

import java.util.List;

public final class FunctionExpression implements Expression {
    FunctionName func;
    List<Expression> args;

    public FunctionExpression(FunctionName func, List<Expression> args) {
        this.func = func;
        this.args = args;
    }

    public FunctionName getFunc() {
        return func;
    }

    public List<Expression> getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return String.format("FunctionExpression(%s, %s)", func.toString(), args.toString());
    }
}
