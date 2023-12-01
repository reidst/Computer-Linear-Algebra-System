package AbstractSyntaxTree;

import org.apache.commons.math3.fraction.Fraction;

public sealed interface Scalar extends Value permits DoubleScalar, FractionScalar{

    public Scalar add(Scalar other);

    public Scalar subtract(Scalar other);

    public Scalar multiply(Scalar other);

    public Scalar divide(Scalar other);

    public Scalar reciprocal();

    public Scalar sqrt();

    public Scalar negate();

    @Override
    public boolean equals(Object other);

    @Override
    public int hashCode();

    public boolean equals(int other);

    public Matrix multiply(Matrix other);

    @Override
    public String toString();

    @Override
    public String print();
}
