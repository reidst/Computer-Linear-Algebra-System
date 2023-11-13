package AbstractSyntaxTree;

public sealed interface Value permits Scalar, Matrix {
    public abstract String print();
}
