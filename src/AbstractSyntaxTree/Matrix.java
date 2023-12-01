public Matrix augmentColumns(Matrix other) {
        if (col_size != other.col_size) {
            throw new IllegalArgumentException(String.format(
                    "Cannot column-augment matrix with %d rows with matrix with %d rows.",
                    col_size,
                    other.col_size
            ));
        }
        List<Scalar> newValues = new ArrayList<>(col_size * (row_size + other.row_size));
        for (int row = 0; row < col_size; row++) {
            for (int col = 0; col < row_size; col++) {
                newValues.add(get(row, col));
            }
            for (int col = 0; col < other.row_size; col++) {
                newValues.add(other.get(row, col));
            }
        }
        return new Matrix(newValues, row_size + other.row_size, col_size);
    }

    public Matrix augmentRows(Matrix other) {
        if (row_size != other.row_size) {
            throw new IllegalArgumentException("Cannot row-augment matrices with different row sizes.");
        }
        List<Scalar> newValues = new ArrayList<>(values.size() + other.values.size());
        newValues.addAll(values);
        newValues.addAll(other.values);
        return new Matrix(newValues, row_size, col_size + other.col_size);
    }

    public Pair<Matrix, Matrix> partitionColumns(int col) {
        if (col < 0 || col > row_size) {
            throw new IndexOutOfBoundsException(
                    String.format("Cannot partition at column %d on matrix with %d columns.", col, row_size)
            );
        }
        if (col == 0) {
            return new Pair<>(null, new Matrix(this));
        }
        if (col == row_size) {
            return new Pair<>(new Matrix(this), null);
        }
        List<Scalar> leftValues = new ArrayList<>(col * col_size);
        List<Scalar> rightValues = new ArrayList<>((row_size - col) * col_size);
        for (int r = 0; r < col_size; r++) {
            for (int c = 0; c < row_size; c++) {
                if (c < col) {
                    leftValues.add(get(r, c));
                } else {
                    rightValues.add(get(r, c));
                }
            }
        }
        return new Pair<>(
                new Matrix(leftValues, col, col_size),
                new Matrix(rightValues, (row_size - col), col_size)
        );
    }

    public Pair<Matrix, Matrix> partitionRows(int row) {
        if (row < 0 || row > col_size) {
            throw new IndexOutOfBoundsException(
                    String.format("Cannot partition at row %d on matrix with %d rows.", row, col_size)
            );
        }
        if (row == 0) {
            return new Pair<>(null, new Matrix(this));
        }
        if (row == col_size) {
            return new Pair<>(new Matrix(this), null);
        }
        List<Scalar> leftValues = new ArrayList<>(row * row_size);
        List<Scalar> rightValues = new ArrayList<>((col_size - row) * row_size);
        for (int i = 0; i < col_size * row_size; i++) {
            if (i < row * row_size) {
                leftValues.add(values.get(i));
            } else {
                rightValues.add(values.get(i));
            }
        }
        return new Pair<>(
                new Matrix(leftValues, row_size, row),
                new Matrix(rightValues, row_size, (col_size - row))
        );
    }

    public Matrix transpose() {
        List<Scalar> transposedValues = new ArrayList<>(row_size * col_size);
        for (int row = 0; row < row_size; row++) {
            for (int col = 0; col < col_size; col++) {
                transposedValues.add(get(col, row)); // NOTE: col and row are reversed
            }
        }
        return new Matrix(transposedValues, col_size, row_size);
    }

    public boolean isUpperTriangular() {
        if (row_size != col_size) {
            return false;
        }
        for (int row = 1; row < col_size; row++) {
            for (int col = 0; col < row; col++) {
                if (!get(row, col).equals(0)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isUpperTriangular(double epsilon) {
        if (row_size != col_size) {
            return false;
        }
        for (int row = 1; row < col_size; row++) {
            for (int col = 0; col < row; col++) {
                if (!get(row, col).equals(0, epsilon)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isLowerTriangular() {
        if (row_size != col_size) {
            return false;
        }
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

    public boolean isFractionMatrix() {
        for (int i = 0; i < row_size * col_size; i++) {
            if (values.get(i) instanceof DoubleScalar) {
                return false;
            }
        }
        return true;
    }

    public Matrix toDoubleMatrix() {
        List<Scalar> nvalues = new ArrayList<>();
        for (int i = 0; i < row_size * col_size; i++) {
            nvalues.add(new DoubleScalar(values.get(i)));
        }
        return new Matrix(nvalues, row_size, col_size);
    }

    public Matrix toFractionMatrix() {
        List<Scalar> nvalues = new ArrayList<>();
        for (int i = 0; i < row_size * col_size; i++) {
            nvalues.add(new FractionScalar(values.get(i)));
        }
        return new Matrix(nvalues, row_size, col_size);
    }
}
