package Utilities;

import AbstractSyntaxTree.Matrix;
import AbstractSyntaxTree.Scalar;

import java.util.List;

public record RowReductionResult(Matrix result, Scalar determinant, List<RowOperation> rowOperations) {}
