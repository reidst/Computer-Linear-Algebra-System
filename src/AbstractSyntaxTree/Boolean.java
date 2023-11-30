package AbstractSyntaxTree;

public final class Boolean implements Value {

    boolean value;

    public Boolean(boolean value) {
        this.value = value;
    }

    @Override
    public String print() {
        return value ? "YES" : "NO";
    }
}
