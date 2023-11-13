package AbstractSyntaxTree;

import java.util.List;

import static java.lang.String.join;

public class ExpressionBlock {
    List<Expression> expressions;

    public ExpressionBlock(List<Expression> expressions){
        this.expressions = expressions;
    }

    public List<Expression> getExpressions() { return expressions; }

    @Override
    public String toString() {
        return String.format("AssignmentBlock(%s)", expressions.toString());
    }
}
