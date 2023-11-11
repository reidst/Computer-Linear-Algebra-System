package Core;

import AbstractSyntaxTree.Matrix;
import AbstractSyntaxTree.Scalar;

import java.util.Arrays;

public class Algorithms {

    public static Matrix rowSwap(Matrix m, int r1, int r2) {
        Matrix ret = new Matrix(m);
        for (int col = 0; col < m.rowSize(); col++) {
            ret.set(r1, col, m.get(r2, col));
            ret.set(r2, col, m.get(r1, col));
        }
        return ret;
    }

    public static Matrix rowScale(Matrix m, int row, Scalar scale) {
        Matrix ret = new Matrix(m);
        for (int col = 0; col < m.rowSize(); col++) {
            ret.set(row, col, scale.multiply(m.get(row, col)));
        }
        return ret;
    }

    public static Matrix rowReplace(Matrix m, int rowTarget, int rowSource, Scalar scale) {
        Matrix ret = new Matrix(m);
        for (int col = 0; col < m.rowSize(); col++) {
            ret.set(rowTarget, col, m.get(rowTarget, col).add(scale.multiply(m.get(rowSource, col))));
        }
        return ret;
    }

    public static Matrix ef(Matrix m) {
        Matrix ret = new Matrix(m);
        // sort rows in increasing order of leading zero count
        int[] leadingZeroesInRow = new int[m.colSize()];
        Arrays.fill(leadingZeroesInRow, 0);
        for (int row = 0; row < m.colSize(); row++) {
            for (int col = 0; ret.get(row, col).equals(0); col++) {
                leadingZeroesInRow[row]++;
            }
            // move row into sorted position, also swapping precomputed zero counts
            for (int swapRow = row; swapRow > 0; swapRow--) {
                if (leadingZeroesInRow[swapRow] < leadingZeroesInRow[swapRow - 1]) {
                    ret = rowSwap(ret, swapRow, swapRow - 1);
                    int tmp = leadingZeroesInRow[swapRow];
                    leadingZeroesInRow[swapRow] = leadingZeroesInRow[swapRow - 1];
                    leadingZeroesInRow[swapRow - 1] = tmp;
                } else {
                    break;
                }
            }
        }
        // ensure no two rows have the same number of leading zeroes
        for (int row = 1; row < m.colSize(); row++) {
            if (leadingZeroesInRow[row] == leadingZeroesInRow[row - 1]) {
                int pivotCol = leadingZeroesInRow[row];
                Scalar scalar = ret.get(row, pivotCol).divide(ret.get(row - 1, pivotCol)).negate();
                ret = rowReplace(ret, row, row - 1, scalar);
                leadingZeroesInRow[row]++;
            }
        }
        return ret;
    }

    public static Matrix rref(Matrix m) {
        // convert to echelon form first
        Matrix ret = ef(m);
        for (int row = 0; row < m.colSize(); row++) {
            // find the pivot column
            int pivotCol = 0;
            while (ret.get(row, pivotCol).equals(0) && pivotCol < m.rowSize()) {
                pivotCol++;
            }
            if (pivotCol == m.rowSize()) {
                continue;
            }
            // normalize each row
            Scalar normalizingScalar = ret.get(row, pivotCol).reciprocal();
            ret = rowScale(ret, row, normalizingScalar);
            // zero-out the column above the pivot
            for (int targetRow = row - 1; targetRow >= 0; targetRow--) {
                Scalar targetScalar = ret.get(targetRow, pivotCol).negate();
                ret = rowReplace(ret, targetRow, row, targetScalar);
            }
        }
        return ret;
    }
}
