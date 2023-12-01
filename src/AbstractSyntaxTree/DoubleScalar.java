package AbstractSyntaxTree;

import org.apache.commons.math3.fraction.Fraction;

public final class DoubleScalar implements Scalar {

    public double value;

    private final double epsilon = Math.pow(10, -9);

    public DoubleScalar(int num) {
        this.value = num;
    }
    public DoubleScalar(double num) {
        this.value = num;
    }
    public DoubleScalar(Scalar other) {
        switch (other) {
            case FractionScalar f -> this.value = f.frac.doubleValue();
            case DoubleScalar d -> this.value = d.value;
        }
    }

    @Override
    public Scalar add(Scalar other) {
        switch (other) {
            case FractionScalar f -> {
                return new FractionScalar(new Fraction(value).add(f.frac));
            }
            case DoubleScalar d -> {
                return new DoubleScalar(d.value + value);
            }
        }
    }

    @Override
    public Scalar subtract(Scalar other) {
        switch (other) {
            case FractionScalar f -> {
                return new FractionScalar((new Fraction(value)).subtract(f.frac));
            }
            case DoubleScalar d -> {
                return new DoubleScalar(value - d.value);
            }
        }
    }

    @Override
    public Scalar multiply(Scalar other) {
        switch (other) {
            case FractionScalar f -> {
                return new FractionScalar((new Fraction(value)).multiply(f.frac));
            }
            case DoubleScalar d -> {
                return new DoubleScalar(value * d.value);
            }
        }
    }

    @Override
    public Scalar divide(Scalar other) {
        switch (other) {
            case FractionScalar f -> {
                return new FractionScalar((new Fraction(value)).divide(f.frac));
            }
            case DoubleScalar d -> {
                return new DoubleScalar(value / d.value);
            }
        }
    }

    @Override
    public Scalar reciprocal() {
        return new DoubleScalar(1/value);
    }

    @Override
    public Scalar sqrt() { return (new DoubleScalar(Math.sqrt(value))); }

    @Override
    public Scalar negate() {
        return new DoubleScalar(-value);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof DoubleScalar) {
            return ((DoubleScalar)other).value < (value + epsilon) && ((DoubleScalar)other).value > (value - epsilon);
        }
        return false;
    }

    @Override
    public boolean equals(Object other, double epsilon) {
        if (other instanceof DoubleScalar) {
            return ((DoubleScalar)other).value < (value + epsilon) && ((DoubleScalar)other).value > (value - epsilon);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }

    @Override
    public boolean equals(int other) {
        return ((other < (value + epsilon) && other > (value - epsilon)));
    }

    @Override
    public boolean equals(int other, double epsilon) {
        return ((other < (value + epsilon) && other > (value - epsilon)));
    }

    @Override
    public Matrix multiply(Matrix other) {
        Matrix ret = new Matrix(other);
        for (int i = 0; i < ret.col_size*ret.row_size; i++) {
            ret.values.set(i, other.values.get(i).multiply(this));
        }
        return ret;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public String print() {
        return String.valueOf(value);
    }
}
