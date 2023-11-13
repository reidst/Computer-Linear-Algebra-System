package AbstractSyntaxTree;

public final class Variable implements Expression {
    String name;

    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() { return String.format("Variable(%s)", name); }
}
