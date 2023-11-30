package AbstractSyntaxTree;

import java.util.*;

public final class VectorList implements Value {

    private final List<Vector> vectors;
    private final int dimension;

    public VectorList(List<Vector> vs) {
        if (vs.isEmpty()) {
            throw new IllegalArgumentException("Vector sets cannot be empty.");
        }
        vectors = vs;
        dimension = vs.getFirst().getDimension();
        if (vs.stream().map(Vector::getDimension).anyMatch(i -> i != dimension)) {
            throw new IllegalArgumentException("All vectors in a set must be of equal dimensionality.");
        }
    }

    public int getVectorDimension() {
        return dimension;
    }

    public int size() {
        return vectors.size();
    }

    public Vector getVector(int i) {
        if (i < 0 || i >= size()) {
            throw new IndexOutOfBoundsException(
                    String.format("Cannot access vector %d in a VectorList of %d vectors", i, size())
            );
        }
        return vectors.get(i);
    }

    private List<Integer> findMaxLengths() {
        List<Integer> maxLengths = new ArrayList<>();
        for (int d = 0; d < getVectorDimension(); d++) {
            int maxLength = 0;
            for (int v = 0; v < size(); v++) {
                int len = vectors.get(v).get(d).print().length();
                if (len > maxLength) {
                    maxLength = len;
                }
            }
            maxLengths.add(maxLength);
        }
        return maxLengths;
    }

    @Override
    public String print() {
        List<Integer> maxLengths = findMaxLengths();
        StringBuilder sb = new StringBuilder();
        for (int v = 0; v < size(); v++) {
            if (v == 0) {
                sb.append("{  ");
            } else {
                sb.append("   ");
            }
            for (int d = 0; d < getVectorDimension(); d++) {
                String currentValue = vectors.get(v).get(d).print();
                sb.append(currentValue);
                sb.append(" ".repeat(Math.max(0, (maxLengths.get(d) - currentValue.length() + 2))));
            }
            if (v == size() - 1) {
                sb.append('}');
            } else {
                sb.append(",\n");
            }
        }
        return sb.toString();
    }
}
