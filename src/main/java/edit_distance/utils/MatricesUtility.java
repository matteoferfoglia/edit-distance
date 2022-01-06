package edit_distance.utils;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class for matrices.
 */
public class MatricesUtility {

    /**
     * @param <T>        type of each element of the matrix.
     * @param colHeading Column heading.
     * @param rowHeading Row heading.
     * @param matrix     The matrix to be printed.
     * @return The string representation of the matrix.
     */
    @NotNull
    public static <T> String getMatrixToStringWithHeading(
            @NotNull final String rowHeading, @NotNull final String colHeading, @NotNull final T[][] matrix) {

        Objects.requireNonNull(rowHeading);
        Objects.requireNonNull(colHeading);
        Objects.requireNonNull(matrix);

        final String TAB_SEPARATOR = "\t";
        final StringBuilder stringBuilder = new StringBuilder(TAB_SEPARATOR);

        for (char c : colHeading.toCharArray()) {
            stringBuilder.append(c).append(TAB_SEPARATOR);
        }
        for (int i = 0; i < rowHeading.length(); i++) {
            stringBuilder.append(System.lineSeparator()).append(rowHeading.charAt(i)).append(TAB_SEPARATOR);
            for (int j = 0; j < colHeading.length(); j++) {
                stringBuilder.append(matrix[i][j]).append(TAB_SEPARATOR);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * @param sourceMatrix            The matrix to be copied.
     * @param <T>                     Type of each element of the input matrix.
     * @param classOfElementsInMatrix class of each element of the input matrix.
     * @return A deep copy of the input matrix.
     */
    @NotNull
    public static <T> T[][] deepCopyOf(
            @NotNull final T[][] sourceMatrix, @NotNull final Class<? extends T> classOfElementsInMatrix) {
        //noinspection unchecked
        return Stream.of(sourceMatrix).sequential()
                .map(aRow -> Arrays.copyOf(aRow, aRow.length))
                .collect(Collectors.toUnmodifiableList())
                .toArray((T[][]) Array.newInstance(
                        Array.newInstance(classOfElementsInMatrix, sourceMatrix[0].length).getClass(),
                        sourceMatrix.length));
    }

    /**
     * @param sourceMatrix            The matrix to be copied.
     * @param <T>                     Type of each element of the input matrix.
     * @param classOfElementsInMatrix class of each element of the input matrix.
     * @param columnIndex             The index of the column to be copied.
     * @return A copy of the column from the given matrix at the given column index.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public static <T> T[] getCopyOfColumn(
            int columnIndex, @NotNull final T[][] sourceMatrix, @NotNull Class<? extends T> classOfElementsInMatrix) {
        return Arrays.stream(sourceMatrix)
                .sequential()
                .map(aRow -> aRow[columnIndex])
                .collect(Collectors.toUnmodifiableList())
                .toArray((T[]) Array.newInstance(classOfElementsInMatrix, sourceMatrix[0].length));
    }

    /**
     * @param sourceMatrix The matrix to be copied.
     * @param columnIndex  The index of the column to be copied.
     * @return A copy of the column from the given matrix at the given column index.
     */
    public static int[] getCopyOfColumn(int columnIndex, final int[][] sourceMatrix) {
        return Arrays.stream(sourceMatrix)
                .sequential()
                .mapToInt(aRow -> aRow[columnIndex])
                .toArray();
    }

}
