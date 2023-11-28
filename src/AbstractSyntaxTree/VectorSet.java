package AbstractSyntaxTree;

import Core.Algorithms;

import java.util.List;

public class VectorSet {

    private final List<? extends Vector> vectors;
    private final int dimension;

    public VectorSet(List<? extends Vector> vs) {
        this.vectors = vs;
        if (vectors.isEmpty()) {
            throw new IllegalArgumentException("Vector sets cannot be empty.");
        }
        dimension = vectors.getFirst().getDimension();
        if (vectors.stream().map(Vector::getDimension).anyMatch(i -> i != dimension)) {
            throw new IllegalArgumentException("All vectors in a set must be of equal dimensionality.");
        }
    }

    public Matrix asColumnSpaceMatrix() {
        Matrix m = null;
        for (Vector v : vectors) {
            Matrix vectorMat = switch (v) {
                case ColumnVector cv -> (Matrix)cv;
                case RowVector rv -> ((Matrix)rv).transpose();
            };
            m = (m == null
                ? vectorMat
                : m.augment(vectorMat)
            );
        }
        return m;
    }

    public boolean isLinearlyIndependent() {
        if (vectors.size() > dimension) {
            return false; // more vectors than dimensions
        }
        RowVector r = Algorithms.ef(asColumnSpaceMatrix()).getRowVector(dimension - 1);
        return !r.isZeroVector();
    }
}
