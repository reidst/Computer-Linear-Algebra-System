package Utilities;

import AbstractSyntaxTree.Matrix;
import AbstractSyntaxTree.Scalar;
import Core.Algorithms;

public final class RowReplace implements RowOperation {
    int rowTarget, rowSource;
    Scalar scale;
    public RowReplace(int rowTarget, int rowSource, Scalar scale) {
        this.rowTarget = rowTarget;
        this.rowSource = rowSource;
        this.scale = scale;
    }
    public Matrix apply(Matrix m) {
        return Algorithms.rowReplace(m, rowTarget, rowSource, scale);
    }
}
