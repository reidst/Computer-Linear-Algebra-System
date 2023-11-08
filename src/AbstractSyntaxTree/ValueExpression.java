package AbstractSyntaxTree;

public class ValueExpression {
    Value value;

    public ValueExpression(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }
}
