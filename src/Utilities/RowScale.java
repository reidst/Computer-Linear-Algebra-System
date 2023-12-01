package Utilities;

import AbstractSyntaxTree.Matrix;
import AbstractSyntaxTree.Scalar;
import Core.Algorithms;

public final class RowScale implements RowOperation {
    int row;
    Scalar scale;
    public RowScale(int row, Scalar scale) {
        this.row = row;
        this.scale = scale;
    }
    public Matrix apply(Matrix m) {
        return Algorithms.rowScale(m, row, scale);
    }
}