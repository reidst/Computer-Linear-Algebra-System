package AbstractSyntaxTree;

public sealed interface Vector permits ColumnVector, RowVector {
    public abstract int getDimension();
    public abstract Scalar get(int i);
    public abstract Vector project(Vector other);

    public default boolean isZeroVector() {
        for (int i = 0; i < getDimension(); i++) {
            if (!get(i).equals(0)) {
                return false;
            }
        }
        return true;
    }

    public default Scalar dot(Vector other) {
        assert(getDimension() == other.getDimension());
        Scalar sum = new Scalar(0);
        for (int i = 0; i < getDimension(); i++) {
            sum = sum.add(get(i).multiply(other.get(i)));
        }
        return sum;
    }
}
