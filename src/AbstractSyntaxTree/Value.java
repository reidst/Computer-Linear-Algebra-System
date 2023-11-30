package AbstractSyntaxTree;

public sealed interface Value permits Scalar, Matrix, VectorList {
    public abstract String print();
}
