package Utilities;

import AbstractSyntaxTree.Matrix;
import Core.Algorithms;

public final class RowSwap implements RowOperation {
    int r1, r2;

    public RowSwap(int r1, int r2) {
        this.r1 = r1;
        this.r2 = r2;
    }

    @Override
    public Matrix apply(Matrix m) {
        return Algorithms.rowSwap(m, r1, r2);
    }
}
