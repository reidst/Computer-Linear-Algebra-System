package AbstractSyntaxTree;

import java.util.*;

import static java.util.Collections.max;

public sealed class Matrix implements Value permits Vector {
    final List<Scalar> values;
    int row_size;
    int col_size;

    public Matrix(List<Scalar> values, int row_size, int col_size) {
        this.values = values;
        this.row_size = row_size;
        this.col_size = col_size;
    }
    public Matrix(Matrix other) {
        this.values = new ArrayList<Scalar>(other.col_size * other.row_size);
        for (int i = 0; i < other.row_size * other.col_size; i++) {
            this.values.add(other.values.get(i));
        }
        this.row_size = other.row_size;
        this.col_size = other.col_size;
    }
    public Matrix(int dim) {
        this.row_size = dim;
        this.col_size = dim;
        this.values = new ArrayList<Scalar>();
        for (int i = 0; i < dim*dim; i++) {
            this.values.add(new Scalar(0));
        }
        for (int i = 0; i < dim; i++) {
            this.values.set(i*dim+i, new Scalar(1));
        }
    }

    public Matrix(VectorSet vs) {
        this.row_size = vs.size();
        this.col_size = vs.getDimension();
        this.values = new ArrayList<Scalar>(row_size * col_size);
        for (int row = 0; row < col_size; row++) {
            for (int col = 0; col < row_size; col++) {
                this.values.add(vs.getVector(col).get(row));
            }
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Matrix) {
            return ((Matrix)other).values.equals(values);
        }
        return false;
    }

    public int rowSize() { return row_size; }
    public int colSize() { return col_size; }

    public boolean isColumnVector() {
        return row_size == 1;
    }
    public boolean isRowVector() {
        return col_size == 1;
    }

    public Vector getColumnVector(int col) {
        assert(0 <= col && col < row_size);
        List<Scalar> newValues = new ArrayList<>();
        for (int row = 0; row < col_size; row++) {
            newValues.add(get(row, col));
        }
        return new Vector(newValues);
    }

    public Vector getRowVector(int row) {
        assert(0 <= row && row < col_size);
        List<Scalar> newValues = new ArrayList<>();
        for (int col = 0; col < row_size; row++) {
            newValues.add(get(row, col));
        }
        return new Vector(newValues);
    }

    public Vector asVector() {
        if (isColumnVector() || isRowVector()) {
            return new Vector(this.values);
        }
        else {
            throw new ClassCastException("The given matrix is not a vector.");
        }
    }

    public Scalar get(int row, int col) {
        assert(row >= 0 && row < col_size);
        assert(col >= 0 && col < row_size);
        return values.get(row * row_size + col);
    }

    public void set(int row, int col, Scalar value) {
        assert(row >= 0 && row < col_size);
        assert(col >= 0 && col < row_size);
        values.set(row * row_size + col, value);
    }

    public Matrix multiply(Scalar other) {
        Matrix ret = new Matrix(this);
        for (int i = 0; i < ret.col_size*ret.row_size; i++) {
            ret.values.set(i, this.values.get(i).multiply(other));
        }
        return ret;
    }

    public Matrix divide(Scalar other) {
        Matrix ret = new Matrix(this);
        for (int i = 0; i < ret.col_size*ret.row_size; i++) {
            ret.values.set(i, values.get(i).divide(other));
        }
        return ret;
    }

    public Matrix multiply(Matrix other) {
        assert(col_size == other.row_size);

        Matrix ret = new Matrix(new ArrayList<Scalar>(col_size*other.row_size), other.row_size, col_size);

        for (int i = 0; i < other.row_size*col_size; i++) {
            ret.values.add(new Scalar(0));
            for (int j = 0; j < row_size; j++) {
                Scalar current = ret.values.get(i);
                Scalar a = values.get((i/other.row_size)*row_size+j);
                Scalar b = other.values.get(i%other.row_size+j*other.row_size);
                ret.values.set(i, current.add(a.multiply(b)));
            }
        }
        return ret;
    }

    public Matrix add(Matrix other) {
        assert(col_size == other.col_size);
        assert(row_size == other.row_size);

        Matrix ret = new Matrix(this);
        for (int i = 0; i < ret.col_size*ret.row_size; i++) {
            ret.values.set(i, values.get(i).add(other.values.get(i)));
        }
        return ret;
    }

    public Matrix subtract(Matrix other) {
        assert(col_size == other.col_size);
        assert(row_size == other.row_size);

        Matrix ret = new Matrix(this);
        for (int i = 0; i < ret.col_size*ret.row_size; i++) {
            ret.values.set(i, values.get(i).subtract(other.values.get(i)));
        }
        return ret;
    }

    public Matrix augment(Matrix other) {
        assert(col_size == other.col_size);

        Matrix ret = new Matrix(new ArrayList<Scalar>(row_size*col_size + other.row_size*other.col_size), row_size+other.row_size, col_size);
        for (int i = 0; i < ret.row_size*ret.col_size; i++) {
            if (i%col_size - row_size < 0) {
                ret.values.set(i, values.get(i % col_size + (i / (row_size + other.row_size)) * row_size));
            } else {
                ret.values.set(i, other.values.get(i % col_size + (i / (row_size + other.row_size)) * other.row_size - row_size));
            }
        }
        return ret;
    }

    public Matrix transpose() {
        List<Scalar> transposedValues = new ArrayList<>(row_size * col_size);
        for (int row = 0; row < col_size; row++) {
            for (int col = 0; col < row_size; col++) {
                transposedValues.add(get(col, row)); // NOTE: col and row are reversed
            }
        }
        return new Matrix(transposedValues, col_size, row_size);
    }

    public boolean isUpperTriangular() {
        assert(row_size == col_size);
        for (int row = 1; row < col_size; row++) {
            for (int col = 0; col < row; col++) {
                if (!get(row, col).equals(0)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isLowerTriangular() {
        assert(row_size == col_size);
        for (int col = 1; col < row_size; col++) {
            for (int row = 0; row < col; row++) {
                if (!get(row, col).equals(0)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isTriangular() {
        return isLowerTriangular() || isUpperTriangular();
    }

    public boolean isDiagonal() {
        return isLowerTriangular() && isUpperTriangular();
    }

    @Override
    public String toString() {
        return values.toString();
    }

    private List<Integer> findMaxLengths() {
        List<Integer> maxLengths = new ArrayList<>();
        for (int c = 0; c < row_size; c++) {
            int maxLength = 0;
            for (int r = 0; r < col_size; r++) {
                int len = values.get(r*row_size + c).print().length();
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
        List<Integer> maxLengths = findMaxLengths();
        StringBuilder stringBuilder = new StringBuilder();
        for (int r = 0; r < col_size; r++) {
            if (r == 0) {
                stringBuilder.append("[  ");
            } else {
                stringBuilder.append("|  ");
            }
            for (int c = 0; c < row_size; c++) {
                String currentValue = values.get(r*row_size + c).print();
                stringBuilder.append(currentValue);
                stringBuilder.append(" ".repeat(Math.max(0, (maxLengths.get(c) - currentValue.length() + 2))));
            }
            if (r == col_size - 1){
                stringBuilder.append("]");
            } else {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
