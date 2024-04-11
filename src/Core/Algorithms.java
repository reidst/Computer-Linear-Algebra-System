package Core;

import AbstractSyntaxTree.*;
import AbstractSyntaxTree.Vector;
import Utilities.*;
import org.apache.commons.math3.util.Pair;

import java.util.*;

public class Algorithms {

//    public static void main(String[] args) {
//        System.out.println("Matrix EF/RREF Test");
//        Scanner in = new Scanner(System.in);
//        System.out.print("Enter matrix dimensions: ");
//        final int colSize = in.nextInt();
//        final int rowSize = in.nextInt();
//        in.nextLine();
//        System.out.print("Generate a random matrix? (Y/n): ");
//        final String randomChoice = in.nextLine();
//        final boolean useRandomMatrix = !(randomChoice.equalsIgnoreCase("n"));
//        List<Scalar> values = new ArrayList<>();
//        if (useRandomMatrix) {
//            for (int i = 0; i < colSize * rowSize; i++) {
//                values.add(new FractionScalar((int)(Math.random() * 10)));
//            }
//        } else {
//            for (int row = 0; row < colSize; row++) {
//                for (int col = 0; col < rowSize; col++) {
//                    System.out.printf("A[%d, %d]: ", row, col);
//                    final int valueChoice = in.nextInt();
//                    in.nextLine();
//                    values.add(new FractionScalar(valueChoice));
//                }
//            }
//        }
//        Matrix m = new Matrix(values, rowSize, colSize);
//        System.out.println("Random matrix:");
//        System.out.println(m.print());
//        System.out.println("Echelon form:");
//        RowReductionResult rrr = ef(m);
//        System.out.println(rrr.efResult().print());
//        System.out.println("Row-reduced echelon form:");
//        rrr = rref(rrr);
//        System.out.println(rrr.rrefResult().print());
//        System.out.println("Determinant:");
//        System.out.println(rrr.determinant() == null ? "N/A" : rrr.determinant().print());
//        System.out.printf("Column space (rank = %d):\n", rank(m));
//        System.out.println(columnSpace(m).print());
//        System.out.printf("Null space (nullity = %d):\n", nullity(m));
//        System.out.println(nullSpace(m).print());
//    }

    public static Matrix rowSwap(Matrix m, int r1, int r2) {
        if (r1 < 0 || r1 >= m.colSize()) {
            throw new IndexOutOfBoundsException(
                    String.format("Row index %d out of bounds for matrix with %d rows.", r1, m.colSize())
            );
        }
        if (r2 < 0 || r2 >= m.colSize()) {
            throw new IndexOutOfBoundsException(
                    String.format("Row index %d out of bounds for matrix with %d rows.", r2, m.colSize())
            );
        }
        Matrix ret = new Matrix(m);
        for (int col = 0; col < m.rowSize(); col++) {
            ret.set(r1, col, m.get(r2, col));
            ret.set(r2, col, m.get(r1, col));
        }
        return ret;
    }

    public static Matrix rowScale(Matrix m, int row, Scalar scale) {
        if (row < 0 || row >= m.colSize()) {
            throw new IndexOutOfBoundsException(
                    String.format("Row index %d out of bounds for matrix with %d rows.", row, m.colSize())
            );
        }
        if (scale.equals(0)) {
            throw new IllegalArgumentException("Cannot scale a row by zero.");
        }
        Matrix ret = new Matrix(m);
        for (int col = 0; col < m.rowSize(); col++) {
            ret.set(row, col, scale.multiply(m.get(row, col)));
        }
        return ret;
    }

    public static Matrix rowReplace(Matrix m, int rowTarget, int rowSource, Scalar scale) {
        if (rowTarget < 0 || rowTarget >= m.colSize()) {
            throw new IndexOutOfBoundsException(
                    String.format("Row index %d out of bounds for matrix with %d rows.", rowTarget, m.colSize())
            );
        }
        if (rowSource < 0 || rowSource >= m.colSize()) {
            throw new IndexOutOfBoundsException(
                    String.format("Row index %d out of bounds for matrix with %d rows.", rowSource, m.colSize())
            );
        }
        Matrix ret = new Matrix(m);
        for (int col = 0; col < m.rowSize(); col++) {
            Scalar a = m.get(rowTarget, col);
            Scalar b = scale.multiply((m.get(rowSource, col)));
            ret.set(rowTarget, col, m.get(rowTarget, col).add(scale.multiply(m.get(rowSource, col))));
        }
        return ret;
    }

    public static RowReductionResult ef(Matrix m) {
        Matrix ret = new Matrix(m);
        Scalar determinant;
        List<RowOperation> rowOperations = new ArrayList<>();
        boolean isFraction = m.isFractionMatrix();
        if (isFraction) {
            determinant = new FractionScalar(1);
        } else {
            determinant = new DoubleScalar(1);
        }
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
                Scalar scale = ret.get(rowReplaceTarget, rowReplaceColumn)
                        .divide(ret.get(rowReplaceSource, rowReplaceColumn))
                        .negate();
                RowOperation rowOp = new RowReplace(rowReplaceTarget, rowReplaceSource, scale);
                rowOperations.add(rowOp);
                ret = rowOp.apply(ret);
            } else {
                // do the sorting; todo do better than n^2
                int[] pivotColAtRow = new int[m.colSize()];
                for (int row = 0; row < m.colSize(); row++) {
                    pivotColAtRow[row] = pivotPos(ret, row);
                }
                for (int row = 0; row < m.colSize(); row++) {
                    for (int swapRow = row; swapRow > 0; swapRow--) {
                        if (pivotColAtRow[swapRow - 1] > pivotColAtRow[swapRow]) {
                            RowOperation rowOp = new RowSwap(swapRow - 1, swapRow);
                            rowOperations.add(rowOp);
                            ret = rowOp.apply(ret);
                            determinant = determinant.negate();
                            int temp = pivotColAtRow[swapRow - 1];
                            pivotColAtRow[swapRow - 1] = pivotColAtRow[swapRow];
                            pivotColAtRow[swapRow] = temp;
                        }
                    }
                }
                if (m.colSize() == m.rowSize()) {
                    Scalar diag;
                    if (isFraction) {
                        diag = new FractionScalar(1);
                    } else {
                        diag = new DoubleScalar(1);
                    }
                    for (int i = 0; i < m.colSize(); i++) {
                        diag = diag.multiply(ret.get(i, i));
                    }
                    return new RowReductionResult(
                            m,
                            ret,
                            null,
                            determinant.multiply(diag),
                            rowOperations,
                            null);
                }
                return new RowReductionResult(
                        m,
                        ret,
                        null,
                        null,
                        rowOperations,
                        null);
            }
        } while (true);
    }

    public static RowReductionResult ef(RowReductionResult rrr) {
        if (rrr.efResult() != null) {
            return rrr;
        }
        return ef(rrr.original());
    }

    public static RowReductionResult rref(Matrix m) {
        return rref(ef(m));
    }

    public static RowReductionResult rref(RowReductionResult rrr) {
        if (rrr.rrefResult() != null) {
            return rrr;
        }
        rrr = ef(rrr);
        Matrix m = rrr.original();
        Matrix ret = new Matrix(rrr.efResult());
        List<RowOperation> rrefOperations = new ArrayList<>();
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
            RowOperation rowOp = new RowScale(row, normalizingScalar);
            rrefOperations.add(rowOp);
            ret = rowOp.apply(ret);
            // zero-out the column above the pivot
            for (int targetRow = row - 1; targetRow >= 0; targetRow--) {
                Scalar targetScalar = ret.get(targetRow, pivotCol).negate();
                rowOp = new RowReplace(targetRow, row, targetScalar);
                rrefOperations.add(rowOp);
                ret = rowOp.apply(ret);
            }
        }
        return new RowReductionResult(
                m,
                rrr.efResult(),
                ret,
                rrr.determinant(),
                rrr.efOperations(),
                rrefOperations);
    }

    private static int pivotPos(Matrix m, int row) {
        if (row < 0 || row >= m.colSize()) {
            throw new IllegalArgumentException(
                    String.format("Row index %d out of bounds for matrix with %d rows.", row, m.colSize())
            );
        }
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

    public static Pair<Matrix, RowReductionResult> inverse(Matrix m) {
        return inverse(rref(m));
    }

    public static Pair<Matrix, RowReductionResult> inverse(RowReductionResult rrr) {
        rrr = rref(rrr);
        if (rrr.original().colSize() != rrr.original().rowSize()) {
            throw new IllegalArgumentException("Cannot take inverse of a non-square matrix.");
        }
        if (rrr.determinant().equals(0)) {
            throw new IllegalStateException("Matrix is not invertible.");
        }
        Matrix ret = new Matrix(rrr.original().rowSize());
        for (RowOperation rowOp : rrr.efOperations()) {
            ret = rowOp.apply(ret);
        }
        for (RowOperation rowOp : rrr.rrefOperations()) {
            ret = rowOp.apply(ret);
        }
        return new Pair<>(ret, rrr);
    }

    public static Pair<Integer, RowReductionResult> rank(Matrix m) {
        return rank(ef(m));
    }

    public static Pair<Integer, RowReductionResult> rank(RowReductionResult rrr) {
        rrr = ef(rrr);
        Matrix m = rrr.original();
        int row;
        for (row = 0; row < m.colSize(); row++) {
            if (pivotPos(rrr.efResult(), row) == m.rowSize()) {
                break;
            }
        }
        return new Pair<>(row, rrr);
    }

    public static Pair<Integer, RowReductionResult> nullity(Matrix m) {
        return nullity(ef(m));
    }

    public static Pair<Integer, RowReductionResult> nullity(RowReductionResult rrr) {
        Pair<Integer, RowReductionResult> rankPair = rank(rrr);
        return new Pair<>(rrr.original().rowSize() - rankPair.getFirst(), rankPair.getSecond());
    }

    public static Pair<java.lang.Boolean, RowReductionResult> isConsistent(Matrix m) {
        return isConsistent(ef(m));
    }

    public static Pair<java.lang.Boolean, RowReductionResult> isConsistent(RowReductionResult rrr) {
        rrr = ef(rrr);
        Matrix m = rrr.original();
        for (int row = 0; row < m.colSize(); row++) {
            if (pivotPos(rrr.efResult(), row) == m.rowSize() - 1) {
                return new Pair<>(false, rrr);
            }
        }
        return new Pair<>(true, rrr);
    }

    public static Pair<VectorList, RowReductionResult> columnSpace(Matrix m) {
        return columnSpace(ef(m));
    }

    public static Pair<VectorList, RowReductionResult> columnSpace(RowReductionResult rrr) {
        rrr = ef(rrr);
        Matrix m = rrr.original();
        List<Vector> includedColumns = new ArrayList<>();
        for (int row = 0; row < m.colSize(); row++) {
            int pivotCol = pivotPos(rrr.efResult(), row);
            if (pivotCol == m.rowSize()) {
                break;
            }
            includedColumns.add(m.getColumnVector(pivotCol));
        }
        return new Pair<>(new VectorList(includedColumns), rrr);
    }

    public static Pair<VectorList, RowReductionResult> rowSpace(Matrix m) {
        return rowSpace(ef(m));
    }

    public static Pair<VectorList, RowReductionResult> rowSpace(RowReductionResult rrr) {
        rrr = ef(rrr);
        Matrix m = rrr.original();
        List<Vector> includedRows = new ArrayList<>();
        for (int row = 0; row < m.colSize(); row++) {
            int pivotCol = pivotPos(rrr.efResult(), row);
            if (pivotCol == m.rowSize()) {
                break;
            }
            includedRows.add(m.getRowVector(row));
        }
        return new Pair<>(new VectorList(includedRows), rrr);
    }

    public static Pair<VectorList, RowReductionResult> nullSpace(Matrix m) {
        return nullSpace(ef(m));
    }

    public static Pair<VectorList, RowReductionResult> nullSpace(RowReductionResult rrr) {
        rrr = ef(rrr);
        Matrix m = rrr.original();
        Matrix aug = m.transpose();
        List<RowOperation> ops = ef(aug).efOperations();
        aug = aug.augmentColumns(new Matrix(m.rowSize()));
        for (RowOperation op : ops) {
            aug = op.apply(aug);
        }
        Pair<Matrix, Matrix> part = aug.partitionColumns(m.colSize());
        List<Vector> nullBasis = new ArrayList<>();
        for (int row = aug.colSize() - 1; row >= 0; row--) {
            if (part.getFirst().getRowVector(row).isZeroVector()) {
                nullBasis.add(part.getSecond().getRowVector(row));
            } else {
                break;
            }
        }
        return new Pair<>(new VectorList(nullBasis), rrr);
    }

    public static boolean isLinearlyIndependent(VectorList vs) {
        if (vs.size() == 0) {
            return false;
        }
        if (vs.size() > vs.getVectorDimension()) {
            return false; // more vectors than dimensions
        }
        Vector r = Algorithms.ef(new Matrix(vs)).efResult().getRowVector(vs.size() - 1);
        return !r.isZeroVector();
    }

    public static VectorList independentSubset(VectorList vs) {
        if (vs.size() == 0) {
            return new VectorList(Collections.emptyList());
        }
        return columnSpace(new Matrix(vs)).getFirst();
    }

    public static boolean withinSpan(VectorList vs, Vector u) {
        if (vs.size() == 0) {
            return false;
        }
        if (vs.getVectorDimension() != u.getDimension()) {
            throw new IllegalArgumentException("Vector must have same dimension as space that may span it.");
        }
        Matrix mat = new Matrix(vs);
        mat = mat.augmentColumns(u);
        final Matrix efMat = ef(mat).efResult();
        return isConsistent(efMat).getFirst();
    }

    public static boolean spans(VectorList a, VectorList b) {
        if (a.size() == 0) {
            return false;
        }
        if (b.size() == 0) {
            return true;
        }
        if (a.getVectorDimension() != b.getVectorDimension()) {
            throw new IllegalArgumentException("Vector spaces must have same dimension to test span.");
        }
        VectorList spanToMeet = independentSubset(b);
        return independentSubset(a).size() == spanToMeet.size();
    }

    public static boolean isBasis(VectorList vs) {
        if (vs.size() == 0) {
            return false;
        }
        return vs.size() == vs.getVectorDimension() && isLinearlyIndependent(vs);
    }

    public static VectorList gramSchmidt(VectorList vs) {
        List<Vector> vl = new ArrayList<Vector>();
        for (int i = 0; i < vs.size(); i++) {
            Vector vi = vs.getVector(i);
            for (int j = 0; j < i; j++) {
                Scalar num = vs.getVector(i).dot(vl.get(j));
                Scalar denom = vl.get(j).dot(vl.get(j));
                Scalar div = num.divide(denom);
                Vector sub = div.multiply(vl.get(j)).asVector();
                vi = vi.subtract(sub).asVector();
            }
            vl.add(vi);
        }
        return new VectorList(vl);
    }

    public static Pair<Matrix, Matrix> QRFactorize(Matrix A) {
        VectorList W = columnSpace(A).getFirst();
        if (W.size() != A.rowSize()) {
            throw new IllegalArgumentException("The columns of A must form a basis for Col A");
        }
        VectorList U = gramSchmidt(W).normalize();
        List<Vector> r = new ArrayList<Vector>();
        for (int k = 0; k < W.size(); k++) {
            List<Vector> uk = new ArrayList<Vector>();
            for (int j = 0; j <= k; j++) {
                uk.add(U.getVector(j));
            }
            Vector v = W.getVector(k);
            Matrix Ukx = (new VectorList(uk)).toMatrix().augmentColumns(v);
            Matrix rkm = rref(Ukx).rrefResult();
            Vector rk = rkm.partitionColumns(k+1).getSecond().asVector();
            r.add(rk);
        }
        Matrix Q = U.toMatrix();
        Matrix R = (new VectorList(r)).toMatrix();
        return new Pair<>(Q, R);
    }

    public static Matrix QRAlgorithm(Matrix A) {
        Matrix Ak = A.toDoubleMatrix();
        while (!Ak.isUpperTriangular(Math.pow(10, -6))) {
            Pair<Matrix, Matrix> QR = QRFactorize(Ak);
            Matrix Q = QR.getFirst();
            Matrix R = QR.getSecond();
            Ak = R.multiply(Q);
        }
        Ak = Ak.toFractionMatrix();
        return Ak;
    }

    public static VectorList eigenspace(Matrix A, Scalar lambda) {
        return nullSpace(A.subtract(lambda.multiply(new Matrix(A.rowSize())))).getFirst();
    }

    public static boolean isEigenVector(Vector v, Matrix A) {
        List<Vector> vl = new ArrayList<>();
        vl.add(v);
        vl.add(A.multiply(v).asVector());
        return !isLinearlyIndependent(new VectorList(vl));
    }

    public static boolean isEigenValue(Scalar lambda, Matrix A) {
        return isLinearlyIndependent(eigenspace(A, lambda));
    }
}
