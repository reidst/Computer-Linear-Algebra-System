package AbstractSyntaxTree;

import Utilities.RowOperation;

import java.util.List;

public final class RowReductionResult implements Value {
    private final Matrix original;
    private final Matrix efResult;
    private final Matrix rrefResult;
    private final Scalar determinant;
    private final List<RowOperation> efOperations;
    private final List<RowOperation> rrefOperations;

    public RowReductionResult(
            Matrix original,
            Matrix efResult,
            Matrix rrefResult,
            Scalar determinant,
            List<RowOperation> efOperations,
            List<RowOperation> rrefOperations) {
        this.original = original;
        this.efResult = efResult;
        this.rrefResult = rrefResult;
        this.determinant = determinant;
        this.efOperations = efOperations;
        this.rrefOperations = rrefOperations;
    }

    public Matrix original() {
        return original;
    }

    public Matrix efResult() {
        return efResult;
    }

    public Matrix rrefResult() {
        return rrefResult;
    }

    public Scalar determinant() {
        return determinant;
    }

    public List<RowOperation> efOperations() {
        return efOperations;
    }

    public List<RowOperation> rrefOperations() {
        return rrefOperations;
    }

    @Override
    public String print() {
        return original.print();
    }
}
