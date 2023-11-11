package AbstractSyntaxTree;

public final class ValueExpression implements Expression{
    Value value;

    public ValueExpression(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

    public String toString() {
        return String.format("ValueExpression(%s)", value.toString());
    }
}
