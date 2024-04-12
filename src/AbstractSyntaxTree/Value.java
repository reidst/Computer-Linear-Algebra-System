package AbstractSyntaxTree;

public sealed interface Value permits Scalar, Matrix, RowReductionResult, Vector, VectorList, Boolean {
    public abstract String print();
}
