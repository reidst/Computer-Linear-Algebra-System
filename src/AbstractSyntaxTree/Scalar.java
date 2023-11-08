package AbstractSyntaxTree;

import org.apache.commons.math3.fraction.Fraction;

public class Scalar implements Value{
    Fraction frac;

    public Scalar(int num) {
        this.frac = new Fraction(num);
    }
    public Scalar(int num, int denom) {
        this.frac = new Fraction(num, denom);
    }
    public Scalar(double num) {
        this.frac = new Fraction(num);
    }
    public Scalar(Scalar other) {
        this.frac = other.frac;
    }
    public Scalar(Fraction frac) {
        this.frac = frac;
    }

    public Scalar add(Scalar other) {
        return new Scalar(other.frac.add(frac));
    }

    public Scalar subtract(Scalar other) {
        return new Scalar(other.frac.subtract(frac));
    }

    public Scalar multiply(Scalar other) {
        return new Scalar(other.frac.multiply(frac));
    }

    public Scalar divide(Scalar other) {
        return new Scalar(other.frac.divide(frac));
    }

    public Matrix multiply(Matrix other) {
        Matrix ret = new Matrix(other);
        for (int i = 0; i < ret.col_size*ret.row_size; i++) {
            ret.values.set(i, other.values.get(i).multiply(this));
        }
        return ret;
    }
}
