package AbstractSyntaxTree;

import java.util.List;

public final class ColumnVector extends Matrix implements Vector {

    public ColumnVector(List<Scalar> values) {
        super(values, 1, values.size());
    }

    @Override
    public int getDimension() {
        return values.size();
    }

    @Override
    public Scalar get(int i) {
        assert(0 <= i && i < col_size);
        return values.get(i);
    }

    @Override
    public ColumnVector multiply(Scalar s) {
        return new ColumnVector(values.stream().map(s::multiply).toList());
    }

    @Override
    public ColumnVector project(Vector other) {
        assert(getDimension() == other.getDimension());
        final Scalar s = this.dot(other).divide(this.dot(this));
        return this.multiply(s);
    }
}
