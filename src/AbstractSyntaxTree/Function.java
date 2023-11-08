package AbstractSyntaxTree;

import java.util.List;

public class Function {
    FunctionName func;
    List<Expression> args;

    public Function(FunctionName func, List<Expression> args) {
        this.func = func;
        this.args = args;
    }

    public FunctionName getFunc() {
        return func;
    }

    public List<Expression> getArgs() {
        return args;
    }
}
