package AbstractSyntaxTree;

import java.util.*;

public final class VectorList implements Value {

    private final List<Vector> vectors;
    private final int vectorDimension;

    public VectorList(List<Vector> vs) {
        vectors = vs;
        if (!vs.isEmpty()) {
            vectorDimension = vs.getFirst().getDimension();
            if (vs.stream().map(Vector::getDimension).anyMatch(i -> i != vectorDimension)) {
                throw new IllegalArgumentException("All vectors in a set must be of equal dimensionality.");
            }
        } else {
            vectorDimension = 0;
        }
    }

    public VectorList normalize() {
        List<Vector> vl = new ArrayList<Vector>();
        for (int i = 0; i < this.size(); i++) {
            vl.add(this.getVector(i).normalize());
        }
        return new VectorList(vl);
    }

    public int getVectorDimension() {
        return vectorDimension;
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

    public Matrix toMatrix() {
        List<Scalar> values = new ArrayList<Scalar>();
        for (int n = 0; n < vectorDimension; n++) {
            for (int m = 0; m < size(); m++) {
                values.add(getVector(m).get(n));
            }
        }
        return new Matrix(values, size(), vectorDimension);
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
        if (size() == 0) {
            return "{ }";
        }
        List<Integer> maxLengths = findMaxLengths();
        StringBuilder sb = new StringBuilder();
        for (int v = 0; v < size(); v++) {
            if (v == 0) {
                sb.append("{ <  ");
            } else {
                sb.append(", <  ");
            }
            for (int d = 0; d < getVectorDimension(); d++) {
                String currentValue = vectors.get(v).get(d).print();
                sb.append(currentValue);
                sb.append(" ".repeat(Math.max(0, (maxLengths.get(d) - currentValue.length() + 2))));
            }
            if (v == size() - 1) {
                sb.append("> }");
            } else {
                sb.append(">\n");
            }
        }
        return sb.toString();
    }
}
