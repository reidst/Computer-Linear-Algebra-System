package AbstractSyntaxTree;

import java.util.List;

public final class VectorSet {

    private final List<Vector> vectors;
    private final int dimension;

    public VectorSet(List<Vector> vs) {
        this.vectors = vs;
        if (vectors.isEmpty()) {
            throw new IllegalArgumentException("Vector sets cannot be empty.");
        }
        dimension = vectors.getFirst().getDimension();
        if (vectors.stream().map(Vector::getDimension).anyMatch(i -> i != dimension)) {
            throw new IllegalArgumentException("All vectors in a set must be of equal dimensionality.");
        }
    }

    public int getDimension() {
        return dimension;
    }

    public int size() {
        return vectors.size();
    }

    public Vector getVector(int i) {
        if (i < 0 || i >= size()) {
            throw new IndexOutOfBoundsException(
                    String.format("Cannot access vector %d in a VectorSet of %d vectors", i, size())
            );
        }
        return vectors.get(i);
    }
}
