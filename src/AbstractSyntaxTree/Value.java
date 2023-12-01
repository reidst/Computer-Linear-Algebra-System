package AbstractSyntaxTree;

public sealed interface Value permits Scalar, Matrix, Vector, VectorList, Boolean {
    public abstract String print();
}
