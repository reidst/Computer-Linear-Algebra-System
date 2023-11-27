package AbstractSyntaxTree;

import java.util.List;

public final class RowVector extends Matrix implements Vector {

    public RowVector(List<Scalar> values) {
        super(values, values.size(), 1);
    }

    @Override
    public int getDimension() {
        return values.size();
    }

    @Override
    public Scalar get(int i) {
        assert(0 <= i && i < row_size);
        return values.get(i);
    }

    @Override
    public RowVector multiply(Scalar s) {
        return new RowVector(values.stream().map(s::multiply).toList());
    }

    @Override
    public RowVector project(Vector other) {
        assert(getDimension() == other.getDimension());
        final Scalar s = this.dot(other).divide(this.dot(this));
        return this.multiply(s);
    }
}
