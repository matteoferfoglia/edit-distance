package main_package.utils;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Array;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MatricesUtilityTest {

    @NotNull
    private final String[][] SAMPLE_MATRIX_OF_STRINGS = new String[][]{{"f", "o", "o"}, {"b", "a", "r"}};
    private final int[][] SAMPLE_MATRIX_OF_INTS = new int[][]{{1, 2, 3}, {4, 5, 6}};

    private static void debugAssertions(int columnIndex, final Object sourceMatrix) {
        assert sourceMatrix != null;
        assert sourceMatrix.getClass().isArray();
        int nRows = Array.getLength(sourceMatrix);
        assert nRows > 0;
        int nCols = -1;
        for (int i = 0; i < nRows; i++) {
            Object aRow = Array.get(sourceMatrix, i);
            assert aRow.getClass().isArray();
            int nColsThisRow = Array.getLength(aRow);
            assert nColsThisRow > 0;
            if (nCols >= 0) {
                if (nColsThisRow != nCols) {
                    System.err.println(i + "th row of matrix has " + nColsThisRow + " columns, but previous rows had " + nCols + " columns.");
                }
                assert nColsThisRow == nCols;
            } else {
                nCols = nColsThisRow;
            }
        }
        assert columnIndex >= 0;
        assert columnIndex < nCols;
    }

    @ParameterizedTest
    @CsvSource({"foo,bar,*\tf\to\to\t#b\tnull\tnull\tnull\t#a\tnull\tnull\tnull\t#r\tnull\tnull\tnull\t*"})
    void printEmptyMatrixToStringWithColAndRowHeadings(String colHeading, String rowHeading, String expected) {
        final char CONTENT_DELIMITER = '*';
        final String expectedWithCorrectLineSeparator =
                expected.substring(expected.indexOf(CONTENT_DELIMITER) + 1, expected.lastIndexOf(CONTENT_DELIMITER))
                        .replaceAll("#", System.lineSeparator());
        final String[][] emptyMatrix = new String[rowHeading.length()][colHeading.length()];
        assertEquals(
                expectedWithCorrectLineSeparator,
                MatricesUtility.getMatrixToStringWithHeading(rowHeading, colHeading, emptyMatrix));
    }

    @Test
    void deepCopyMatrixOfObjectsAndAssertEachElementToBeEqualsButModifyingTheCopyDoesntModifyTheInitial() {
        var initialMtx = SAMPLE_MATRIX_OF_STRINGS;
        assert initialMtx.length > 0;
        assert initialMtx[0].length > 0;
        var copiedMtx = MatricesUtility.deepCopyOf(initialMtx, initialMtx[0][0].getClass());
        for (int i = 0; i < initialMtx.length; i++) {
            for (int j = 0; j < initialMtx[i].length; j++) {
                assertEquals(initialMtx[i][j], copiedMtx[i][j]);
                assert copiedMtx[i][j] != null;                     // hypothesis
                copiedMtx[i][j] = null;                             // modify the copy matrix
                assertNotEquals(initialMtx[i][j], copiedMtx[i][j]); // only the copied elements should have changed, hence it will no longer be equal to the initial one
            }
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void copyAndGetColumnOfIntMatrix(int columnIndex) {
        debugAssertions(columnIndex, SAMPLE_MATRIX_OF_INTS);
        int[] copiedColumn = MatricesUtility.getCopyOfColumn(columnIndex, SAMPLE_MATRIX_OF_INTS);
        for (int i = 0; i < SAMPLE_MATRIX_OF_INTS.length; i++) {
            assertEquals(SAMPLE_MATRIX_OF_INTS[i][columnIndex], copiedColumn[i]);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void copyAndGetColumnOfIntMatrixAndDontAllowToModify(int columnIndex) {
        debugAssertions(columnIndex, SAMPLE_MATRIX_OF_INTS);
        int[] copiedColumn = MatricesUtility.getCopyOfColumn(columnIndex, SAMPLE_MATRIX_OF_INTS);
        copiedColumn[0]--;
        assertNotEquals(MatricesUtility.getCopyOfColumn(columnIndex, SAMPLE_MATRIX_OF_INTS), copiedColumn);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void copyAndGetColumnOfStringMatrix(int columnIndex) {
        debugAssertions(columnIndex, SAMPLE_MATRIX_OF_STRINGS);
        String[] copiedColumn = MatricesUtility.getCopyOfColumn(columnIndex, SAMPLE_MATRIX_OF_STRINGS, String.class);
        for (int i = 0; i < SAMPLE_MATRIX_OF_STRINGS.length; i++) {
            assertEquals(SAMPLE_MATRIX_OF_STRINGS[i][columnIndex], copiedColumn[i]);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void copyAndGetColumnOfStringMatrixAndDontAllowToModify(int columnIndex) {
        debugAssertions(columnIndex, SAMPLE_MATRIX_OF_STRINGS);
        String[] copiedColumn = MatricesUtility.getCopyOfColumn(columnIndex, SAMPLE_MATRIX_OF_STRINGS, String.class);
        copiedColumn[0] += " whatever just to change the value";
        assertNotEquals(MatricesUtility.getCopyOfColumn(columnIndex, SAMPLE_MATRIX_OF_STRINGS, String.class), copiedColumn);
    }

}