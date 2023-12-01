package AbstractSyntaxTree;

import java.util.List;

public final class Vector extends Matrix {

    public Vector(List<Scalar> values) {
        super(values, 1, values.size());
    }

    public int getDimension() {
        return values.size();
    }

    public Scalar get(int i) {
        if (i < 0 || i >= getDimension()) {
            throw new IndexOutOfBoundsException(
                    String.format("Tried to index a %d-dimensional vector at %d.", getDimension(), i)
            );
        }
        return values.get(i);
    }

    public boolean isZeroVector() {
        return this.values.stream().allMatch(v -> v.equals(0));
    }

    @Override
    public Vector multiply(Scalar s) {
        return (Vector)super.multiply(s);
    }

    public Scalar dot(Vector other) {
        if (getDimension() != other.getDimension()) {
            throw new IllegalArgumentException("Vectors must have the same dimension to perform dot product.");
        }
        Scalar sum = new Scalar(0);
        for (int i = 0; i < getDimension(); i++) {
            sum = sum.add(get(i).multiply(other.get(i)));
        }
        return sum;
    }

    public Vector project(Vector other) {
        if (getDimension() != other.getDimension()) {
            throw new IllegalArgumentException("Cannot project vectors with different dimensions onto each other.");
        }
        final Scalar s = this.dot(other).divide(this.dot(this));
        return this.multiply(s);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof Vector)) {
            return false;
        }
        final Vector v = (Vector)other;
        if (getDimension() != v.getDimension()) {
            return false;
        }
        for (int i = 0; i < getDimension(); i++) {
            if (!get(i).equals(v.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public String print() {
        StringBuilder sb = new StringBuilder();
        sb.append('<');
        sb.append(' ');
        for (Scalar s : values) {
            sb.append(s.print());
            sb.append(' ');
        }
        sb.append('>');
        return sb.toString();
    }
}
