#include "ast.hpp"
#include <vector>
#include <valarray>
#include <boost/rational.hpp>

void swapRows(Matrix* m, int a, int b) {
	for (int i = 0; i < m->row_size; i++) {
		Scalar temp = m->values[a * m->row_size + i];
		m->values[a * m->row_size + i] = m->values[b * m->row_size + i];
		m->values[b * m->row_size + i] = temp;
	}
}

Matrix rref(const Matrix matrix) {
	Matrix m = Matrix(&matrix);

	// move rows with more leading 0's lower
	int pivotRows[m.row_size];
	std::fill_n(pivotRows, m.row_size, -1);
	int firstZeroRow = 0;
	for (int col = 0; col < m.row_size; col++) {
		for (int row = firstZeroRow; row < m.col_size; row++) {
			if (m.values[row * m.row_size + col] != 0) {
				swapRows(&m, row, firstZeroRow);
				pivotRows[col] = firstZeroRow;
				firstZeroRow++;
			}
		}
		if (firstZeroRow == m.col_size) {
			break;
		}
	}

	// put 1's in pivot positions
	for (int pivotCol = 0; pivotCol < m.row_size; pivotCol++) {
		if (pivotRows[pivotCol] == -1) {
			continue; // skip non-pivot columns
		}
		int pivot = pivotRows[pivotCol] * m.row_size + pivotCol;
		Scalar normalizer = 1 / m.values[pivot];
		for (int colOffset = 0; colOffset + pivotCol < m.row_size; colOffset++) {
			m.values[pivot + colOffset] *= normalizer;
		}

		// zero-out rows below pivot position using multiples of pivot row
		for (int rowOffset = m.row_size; pivot + rowOffset < m.values.size() && m.values[pivot + rowOffset] != 0; rowOffset += m.row_size) {
			Scalar scaleFactor = m.values[pivot + rowOffset];
			for (int colOffset = 0; colOffset + pivotCol < m.row_size; colOffset++) {
				int pos = pivot + colOffset + rowOffset;
				Scalar newValue = m.values[pos] - scaleFactor * m.values[pivot + colOffset];
				m.values[pos] = newValue;
				// if we just zeroed-out a pivot, move that pivot up
				if (newValue == 0 && pivotRows[pivotCol + colOffset] != -1) {
					pivotRows[pivotCol + colOffset] -= 1;
				}
			}
		}
	}

	// zero-out values above pivots
	for (int pivotCol = m.row_size; pivotCol >= 0; pivotCol--) {
		int pivotRow = pivotRows[pivotCol];
		if (pivotRow == -1) {
			continue;
		}
		for (int otherRow = pivotRow - 1; otherRow >= 0; otherRow--) {
			Scalar scaleFactor = m.values[otherRow * m.row_size + pivotCol];
			for(int c = m.row_size - 1; c >= 0 && m.values[pivotRow * m.row_size + c] != 0; c--) {
				m.values[otherRow * m.row_size + c] -= scaleFactor * m.values[pivotRow * m.row_size + c];
			}
		}
	}

	return m;
}
