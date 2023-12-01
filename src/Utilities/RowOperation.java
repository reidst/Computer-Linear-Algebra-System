package Utilities;

import AbstractSyntaxTree.Matrix;

public sealed interface RowOperation permits RowSwap, RowScale, RowReplace {
    public abstract Matrix apply(Matrix m);
}
