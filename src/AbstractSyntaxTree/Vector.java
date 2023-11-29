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
        assert(getDimension() == other.getDimension());
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
}
