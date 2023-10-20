#include "ast.hpp"
#include <vector>
#include <valarray>
#include <boost/rational.hpp>

Matrix rref(const Matrix matrix) {
	Matrix m = Matrix(&matrix);
	for (int row = 0; row < m.col_size; row++) {
		int pivot = row * m.row_size;
		while (m.values[pivot] == 0) {
			pivot++;
		}
		const Scalar rowFactor = 1 / m.values[pivot];
		for (int i = 0; i < m.row_size; i++) {
			m.values[row * m.row_size + i] *= rowFactor;
		}
		for (int row2 = pivot + 1; row2 < m.col_size; row2++) {
			Scalar toSubtract = m.values[row2 * m.row_size + pivot];
			for (int i = 0; i < m.row_size; i++) {
				Scalar row1val = m.values[row * m.row_size + i];
				m.values[row2 * m.row_size + pivot] += row1val * toSubtract;
			}
		}
	}
	return m;
}
