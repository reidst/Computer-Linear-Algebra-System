package AbstractSyntaxTree;

import org.apache.commons.math3.fraction.Fraction;

import java.util.ArrayList;

public final class FractionScalar implements Scalar {
    Fraction frac;

    public FractionScalar(int num) {
        this.frac = new Fraction(num);
    }
    public FractionScalar(int num, int denom) {
        this.frac = new Fraction(num, denom);
    }
    public FractionScalar(double num) {
        this.frac = new Fraction(num);
    }
    public FractionScalar(Scalar other) {
        switch (other) {
            case FractionScalar f -> this.frac = f.frac;
            case DoubleScalar d -> this.frac = new Fraction(d.value);
        }
    }
    public FractionScalar(Fraction frac) {
        this.frac = frac;
    }

    @Override
    public Scalar add(Scalar other) {
        switch (other) {
            case FractionScalar f -> {
                return new FractionScalar(f.frac.add(frac));
            }
            case DoubleScalar d -> {
                return new FractionScalar(new Fraction(d.value).add(frac));
            }
        }
    }

    @Override
    public Scalar subtract(Scalar other) {
        switch (other) {
            case FractionScalar f -> {
                return new FractionScalar(frac.subtract(f.frac));
            }
            case DoubleScalar d -> {
                return new FractionScalar(frac.subtract(new Fraction(d.value)));
            }
        }
    }

    @Override
    public Scalar multiply(Scalar other) {
        switch (other) {
            case FractionScalar f -> {
                return new FractionScalar(frac.multiply(f.frac));
            }
            case DoubleScalar d -> {
                return new FractionScalar(frac.multiply(new Fraction(d.value)));
            }
        }
    }

    @Override
    public Scalar divide(Scalar other) {
        switch (other) {
            case FractionScalar f -> {
                return new FractionScalar(frac.divide(f.frac));
            }
            case DoubleScalar d -> {
                return new FractionScalar(frac.divide(new Fraction(d.value)));
            }
        }
    }

    @Override
    public Scalar reciprocal() {
        return new FractionScalar(frac.reciprocal());
    }

    @Override
    public Scalar sqrt() { return (new FractionScalar(Math.sqrt(frac.getNumerator())).divide(new FractionScalar(Math.sqrt(frac.getDenominator())))); }

    @Override
    public Scalar negate() {
        return new FractionScalar(frac.negate());
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof FractionScalar) {
            return ((FractionScalar)other).frac.equals(frac);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return frac.hashCode();
    }

    @Override
    public boolean equals(int other) {
        // other == n/d; other*d == n
        return (other * frac.getDenominator()) == frac.getNumerator();
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
        return frac.toString();
    }

    @Override
    public String print() {
        String str = null;
        int denominator = frac.getDenominator();
        int numerator = frac.getNumerator();
        if (denominator == 1) {
            str = Integer.toString(numerator);
        } else if (numerator == 0) {
            str = "0";
        } else {
            str = numerator + "/" + denominator;
        }

        return str;};
}
