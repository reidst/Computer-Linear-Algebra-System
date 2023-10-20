#include "ast.hpp"
#include <cassert>
#include <valarray>

// Scalar operations

Matrix operator*(const Scalar lhs, const Matrix rhs) {
    return Matrix(rhs.values * lhs, rhs.row_size, rhs.col_size);
}

Matrix operator*(const Matrix lhs, const Scalar rhs) {
    return Matrix(lhs.values * rhs, lhs.row_size, lhs.col_size);
}

Matrix operator/(const Matrix lhs, const Scalar rhs) {
    return Matrix(lhs.values / rhs, lhs.row_size, lhs.col_size);
}

Matrix operator+(const Scalar lhs, const Matrix rhs) {
    return Matrix(rhs.values + lhs, rhs.row_size, rhs.col_size);
}

Matrix operator+(const Matrix lhs, const Scalar rhs) {
    return Matrix(lhs.values + rhs, lhs.row_size, lhs.col_size);
}

Matrix operator-(const Matrix lhs, const Scalar rhs) {
    return Matrix(lhs.values - rhs, lhs.row_size, lhs.col_size);
}

// Matrix Arithmetic

Matrix operator*(const Matrix lhs, const Matrix rhs) {
    int size = lhs.values.size();
    assert((rhs.col_size == lhs.row_size));

    Matrix res = Matrix(std::valarray<Scalar>(lhs.col_size*rhs.row_size), rhs.row_size, lhs.col_size);

    for (int i = 0; i < lhs.col_size*rhs.row_size; i++) {
        for (int j = 0; j < lhs.row_size; j++) {
            res.values[i] = lhs.values[(i/rhs.row_size)*lhs.row_size+j] * rhs.values[i%rhs.row_size+j*rhs.row_size];
        }
    }

    return res;
}

Matrix operator+(const Matrix lhs, const Matrix rhs) {
    assert((rhs.col_size == lhs.col_size));
    assert((rhs.row_size == lhs.row_size));

    return Matrix(lhs.values + rhs.values, lhs.row_size, lhs.col_size);
}

Matrix operator-(const Matrix lhs, const Matrix rhs) {
    assert((rhs.col_size == lhs.col_size));
    assert((rhs.row_size == lhs.row_size));

    return Matrix(lhs.values - rhs.values, lhs.row_size, lhs.col_size);
}

Matrix augment(const Matrix lhs, const Matrix rhs) {
    assert((lhs.col_size == rhs.col_size));

    std::valarray<Scalar> vals = std::valarray<Scalar>(lhs.values.size() + rhs.values.size());
    for (int i = 0; i < vals.size(); i++) {
        if (i%lhs.col_size - lhs.row_size < 0) {
            vals[i] = lhs.values[i%lhs.col_size+(i/(lhs.row_size+rhs.row_size))*lhs.row_size];
        } else {
            vals[i] = rhs.values[i%lhs.col_size+(i/(lhs.row_size+rhs.row_size))*rhs.row_size-lhs.row_size];
        }
    }

    return Matrix(vals, lhs.row_size+rhs.row_size, lhs.col_size);
}