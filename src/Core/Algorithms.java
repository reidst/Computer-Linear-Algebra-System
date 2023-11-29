package Core;

import AbstractSyntaxTree.*;
import AbstractSyntaxTree.Vector;

import java.util.*;

public class Algorithms {

    public static void main(String[] args) {
        System.out.println("Matrix EF/RREF Test");
        Scanner in = new Scanner(System.in);
        System.out.print("Enter matrix dimensions: ");
        final int colSize = in.nextInt();
        final int rowSize = in.nextInt();
        in.nextLine();
        System.out.print("Generate a random matrix? (Y/n): ");
        final String randomChoice = in.nextLine();
        final boolean useRandomMatrix = !(randomChoice.equalsIgnoreCase("n"));
        List<Scalar> values = new ArrayList<>();
        if (useRandomMatrix) {
            for (int i = 0; i < colSize * rowSize; i++) {
                values.add(new Scalar((int)(Math.random() * 10)));
            }
        } else {
            for (int row = 0; row < colSize; row++) {
                for (int col = 0; col < rowSize; col++) {
                    System.out.printf("A[%d, %d]: ", row, col);
                    final int valueChoice = in.nextInt();
                    in.nextLine();
                    values.add(new Scalar(valueChoice));
                }
            }
        }
        Matrix m = new Matrix(values, rowSize, colSize);
        System.out.println("Random matrix:");
        System.out.println(m.print());
        System.out.println("Echelon form:");
        System.out.println(ef(m).print());
        System.out.println("Row-reduced echelon form:");
        System.out.println(rref(m).print());
    }

    public static Matrix rowSwap(Matrix m, int r1, int r2) {
        assert(r1 >= 0 && r1 < m.colSize());
        assert(r2 >= 0 && r2 < m.colSize());
        Matrix ret = new Matrix(m);
        for (int col = 0; col < m.rowSize(); col++) {
            ret.set(r1, col, m.get(r2, col));
            ret.set(r2, col, m.get(r1, col));
        }
        return ret;
    }

    public static Matrix rowScale(Matrix m, int row, Scalar scale) {
        assert(row >= 0 && row < m.colSize());
        assert(!scale.equals(0));
        Matrix ret = new Matrix(m);
        for (int col = 0; col < m.rowSize(); col++) {
            ret.set(row, col, scale.multiply(m.get(row, col)));
        }
        return ret;
    }

    public static Matrix rowReplace(Matrix m, int rowTarget, int rowSource, Scalar scale) {
        assert(rowTarget >= 0 && rowTarget < m.colSize());
        assert(rowSource >= 0 && rowSource < m.colSize());
        Matrix ret = new Matrix(m);
        for (int col = 0; col < m.rowSize(); col++) {
            ret.set(rowTarget, col, m.get(rowTarget, col).add(scale.multiply(m.get(rowSource, col))));
        }
        return ret;
    }

    public static Matrix ef(Matrix m) {
        Matrix ret = new Matrix(m);
        do {
            // check if all rows have unique pivot columns
            Map<Integer, Integer> pivotRowMap = new HashMap<>();
            Integer rowReplaceTarget = null, rowReplaceSource = null, rowReplaceColumn = null;
            for (int row = 0; row < m.colSize(); row++) {
                int pivotPos = pivotPos(ret, row);
                if (pivotPos == m.rowSize()) {
                    continue; // ignore all-zero rows
                }
                if (pivotRowMap.containsKey(pivotPos)) {
                    rowReplaceTarget = row;
                    rowReplaceSource = pivotRowMap.get(pivotPos);
                    rowReplaceColumn = pivotPos;
                    break;
                } else {
                    pivotRowMap.put(pivotPos, row);
                }
            }
            // two rows had a matching pivot column; perform a row replacement
            if (rowReplaceColumn != null) {
                Scalar scale = ret.get(rowReplaceSource, rowReplaceColumn)
                        .divide(ret.get(rowReplaceTarget, rowReplaceColumn))
                        .negate();
                ret = rowReplace(ret, rowReplaceTarget, rowReplaceSource, scale);
            } else {
                // do the sorting; todo do better than n^2
                int[] pivotColAtRow = new int[m.colSize()];
                for (int row = 0; row < m.colSize(); row++) {
                    pivotColAtRow[row] = pivotPos(ret, row);
                }
                for (int row = 0; row < m.colSize(); row++) {
                    for (int swapRow = row; swapRow > 0; swapRow--) {
                        if (pivotColAtRow[swapRow - 1] > pivotColAtRow[swapRow]) {
                            ret = rowSwap(ret, swapRow - 1, swapRow);
                            int temp = pivotColAtRow[swapRow - 1];
                            pivotColAtRow[swapRow - 1] = pivotColAtRow[swapRow];
                            pivotColAtRow[swapRow] = temp;
                        }
                    }
                }
                return ret;
            }
        } while (true);
    }

    public static Matrix rref(Matrix m) {
        // convert to echelon form first
        Matrix ret = ef(m);
        for (int row = 0; row < m.colSize(); row++) {
            // find the pivot column
            int pivotCol = 0;
            while (pivotCol < m.rowSize() && ret.get(row, pivotCol).equals(0)) {
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

    private static int pivotPos(Matrix m, int row) {
        assert(row >= 0 && row < m.colSize());
        int zeroCount = 0;
        for (int col = 0; col < m.rowSize(); col++) {
            if (m.get(row, col).equals(0)) {
                zeroCount++;
            } else {
                break;
            }
        }
        return zeroCount;
    }

    public static int rank(Matrix m) {
        final Matrix efMat = ef(m);
        int row;
        for (row = 0; row < m.colSize(); row++) {
            if (pivotPos(efMat, row) == m.rowSize()) {
                break;
            }
        }
        return row;
    }

    public static int nullity(Matrix m) {
        return m.rowSize() - rank(m);
    }

    public static boolean isConsistent(Matrix m) {
        final Matrix efMat = ef(m);
        for (int row = 0; row < m.colSize(); row++) {
            if (pivotPos(efMat, row) == m.rowSize() - 1) {
                return false;
            }
        }
        return true;
    }

    public static VectorSet columnSpace(Matrix m) {
        final Matrix efMat = ef(m);
        List<Vector> includedColumns = new ArrayList<>();
        for (int row = 0; row < m.colSize(); row++) {
            int pivotCol = pivotPos(efMat, row);
            if (pivotCol == m.rowSize()) {
                break;
            }
            includedColumns.add(m.getColumnVector(pivotCol));
        }
        return new VectorSet(includedColumns);
    }

    public static VectorSet rowSpace(Matrix m) {
        final Matrix efMat = ef(m);
        List<Vector> includedRows = new ArrayList<>();
        for (int row = 0; row < m.colSize(); row++) {
            int pivotCol = pivotPos(efMat, row);
            if (pivotCol == m.rowSize()) {
                break;
            }
            includedRows.add(m.getRowVector(row));
        }
        return new VectorSet(includedRows);
    }

    public static VectorSet nullSpace(Matrix m) {
        // todo finish
        throw new RuntimeException("Kernel computation is not yet implemented.");
    }

    public static boolean isLinearlyIndependent(VectorSet vs) {
        if (vs.size() > vs.getDimension()) {
            return false; // more vectors than dimensions
        }
        Vector r = Algorithms.ef(new Matrix(vs)).getRowVector(vs.getDimension() - 1);
        return !r.isZeroVector();
    }

    public static VectorSet independentSubset(VectorSet vs) {
        return columnSpace(new Matrix(vs));
    }

    public static boolean withinSpan(VectorSet vs, Vector u) {
        if (vs.getDimension() != u.getDimension()) {
            throw new IllegalArgumentException("Vector must have same dimension as space that may span it.");
        }
        Matrix mat = new Matrix(vs);
        mat = mat.augment(u);
        final Matrix efMat = ef(mat);
        return isConsistent(efMat);
    }

    public static boolean spans(VectorSet a, VectorSet b) {
        if (a.getDimension() != b.getDimension()) {
            throw new IllegalArgumentException("Vector spaces must have same dimension to test span.");
        }
        VectorSet spanToMeet = independentSubset(b);
        return independentSubset(a).size() == spanToMeet.size();
    }

    public static boolean isBasis(VectorSet vs) {
        return vs.size() == vs.getDimension() && isLinearlyIndependent(vs);
    }
}
