package main_package.deprecated;

import main_package.entities.AppliedEditOperation;
import main_package.entities.EditOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public class DeprecatedFunctionsForEditDistanceComputation {

    /**
     * This algorithm determines an optimal sequence of operations that converts the first param into the second.
     *
     * @param str1 starting word
     * @param str2 target word
     * @return the edit distance between given words.     *
     */
    public static int computeEditDistance(final char[] str1, final char[] str2) {
        assert str1 != null;
        assert str2 != null;

        // prefix with 0 to emulate the empty string (special symbol)
        final char[] x = new char[str1.length + 1];
        final char[] y = new char[str2.length + 1];
        x[0] = 0;
        y[0] = 0;
        System.arraycopy(str1, 0, x, 1, str1.length);
        System.arraycopy(str2, 0, y, 1, str2.length);

        // declare working matrices of costs and edit operations
        final int[][] transformCostMatrix = new int[str1.length + 1][str2.length + 1];
        final EditOperation[][] operations = new EditOperation[str1.length + 1][str2.length + 1];

        // declare support constants
        final int INFINITY = Integer.MAX_VALUE;


        // Special case: target word is empty, so "delete" operations must be applied to the first one
        for (int i = 1; i <= str1.length; i++) {
            transformCostMatrix[i][0] = i * EditOperation.DELETE.getCost();
            operations[i][0] = EditOperation.DELETE;
        }

        // Special case: first word is empty, so "insert" operations must be applied to the first one
        for (int j = 1; j <= str2.length; j++) {
            transformCostMatrix[0][j] = j * EditOperation.INSERT.getCost();
            operations[0][j] = EditOperation.INSERT;
        }

        // Special case: both string are empty
        transformCostMatrix[0][0] = EditOperation.COPY.getCost();
        operations[0][0] = EditOperation.COPY;

        EditOperation[] operationsArray = new EditOperation[]{
                EditOperation.COPY, EditOperation.REPLACE, EditOperation.TWIDDLE, EditOperation.DELETE, EditOperation.INSERT};

        // choose for each position the cheapest operation to transform the first word into the second
        for (int i = 1; i <= str1.length; i++) {
            for (int j = 1; j <= str2.length; j++) {

                boolean copyAllowed = x[i] == y[j];
                boolean twiddleAllowed = i >= 2 && j >= 2 && x[i] == y[j - 1] && x[i - 1] == y[j];

                // Cost of possible operations in same order than operationsArray
                int[] operationCostsArray = new int[]{
                        /*copy   */ copyAllowed ? transformCostMatrix[i - 1][j - 1] + EditOperation.COPY.getCost() : INFINITY,
                        /*replace*/ transformCostMatrix[i - 1][j - 1] + EditOperation.REPLACE.getCost(),
                        /*twiddle*/ twiddleAllowed ? transformCostMatrix[i - 2][j - 2] + EditOperation.TWIDDLE.getCost() : INFINITY,
                        /*delete */ transformCostMatrix[i - 1][j] + EditOperation.DELETE.getCost(),
                        /*insert */ transformCostMatrix[i][j - 1] + EditOperation.INSERT.getCost()
                };

                short indexOfOperationWithMinCost = indexOfMin(operationCostsArray);
                transformCostMatrix[i][j] = operationCostsArray[indexOfOperationWithMinCost];
                operations[i][j] = operationsArray[indexOfOperationWithMinCost];

            }
        }

        // evaluate if killing the first word is cheaper
        for (int i = 0; i < str1.length; i++) {
            int killCost = transformCostMatrix[i][str2.length] + EditOperation.KILL.getCost();
            if (killCost < transformCostMatrix[str1.length][str2.length]) {
                transformCostMatrix[str1.length][str2.length] = killCost;
                operations[str1.length][str2.length] = EditOperation.KILL;
            }
        }

        // print the resulting matrix
        System.out.println(costMatrixToString(x, y, transformCostMatrix));

        // get and print the operation sequence to transform the first word into the second
        List<AppliedEditOperation> operationSequence = getOperationSequenceOfEditDistance(operations);
        System.out.println(operationSequence);

        return operationSequence.get(operationSequence.size() - 1).getOperationCost();
    }

    /**
     * @param values values
     * @return min of values.
     */
    private static short indexOfMin(int... values) {
        short indexOfMinValue = 0;
        for (short i = 1; i < values.length; i++) {
            if (values[i] < values[indexOfMinValue]) {
                indexOfMinValue = i;
            }
        }
        return indexOfMinValue;
    }

    /**
     * This algorithm determines an optimal sequence of operations that converts the first param into the second.
     *
     * @param str1 starting word
     * @param str2 target word
     * @return the edit distance.
     */
    public static int computeEditDistanceFirstVersion(final char[] str1, final char[] str2) {
        assert str1 != null;
        assert str2 != null;

        // prefix with 0 to emulate the empty string (special symbol)
        final char[] x = new char[str1.length + 1];
        final char[] y = new char[str2.length + 1];
        x[0] = 0;
        y[0] = 0;
        System.arraycopy(str1, 0, x, 1, str1.length);
        System.arraycopy(str2, 0, y, 1, str2.length);

        // declare working matrices of costs and edit operations
        final int[][] transformCostMatrix = new int[str1.length + 1][str2.length + 1];
        final EditOperation[][] operations = new EditOperation[str1.length + 1][str2.length + 1];

        // declare support constants
        final int INFINITY = Integer.MAX_VALUE;

        // declare functions to make the program clearer
        BiFunction<Integer, Integer, Integer> getCopyCost = (i, j) -> transformCostMatrix[i - 1][j - 1] + EditOperation.COPY.getCost();
        BiFunction<Integer, Integer, Integer> getReplaceCost = (i, j) -> transformCostMatrix[i - 1][j - 1] + EditOperation.REPLACE.getCost();
        BiFunction<Integer, Integer, Integer> getTwiddleCost = (i, j) -> transformCostMatrix[i - 2][j - 2] + EditOperation.TWIDDLE.getCost();
        BiFunction<Integer, Integer, Integer> getDeleteCost = (i, j) -> transformCostMatrix[i - 1][j] + EditOperation.DELETE.getCost();
        BiFunction<Integer, Integer, Integer> getInsertCost = (i, j) -> transformCostMatrix[i][j - 1] + EditOperation.INSERT.getCost();
        Function<Integer, Integer> getKillCost = i -> transformCostMatrix[i][str2.length] + EditOperation.KILL.getCost();
        Function<Integer, Integer> getCostIfFirstIsEmpty = j -> j * EditOperation.INSERT.getCost();
        Function<Integer, Integer> getCostIfSecondIsEmpty = i -> i * EditOperation.DELETE.getCost();
        Supplier<Integer> getCostIfBothAreEmpty = EditOperation.COPY::getCost;


        // Special case: target word is empty, so "delete" operations must be applied to the first one
        for (int i = 0; i <= str1.length; i++) {
            transformCostMatrix[i][0] = getCostIfSecondIsEmpty.apply(i);
            operations[i][0] = EditOperation.DELETE;
        }

        // Special case: first word is empty, so "insert" operations must be applied to the first one
        for (int j = 0; j <= str2.length; j++) {
            transformCostMatrix[0][j] = getCostIfFirstIsEmpty.apply(j);
            operations[0][j] = EditOperation.INSERT;
        }

        // Special case: both string are empty
        transformCostMatrix[0][0] = getCostIfBothAreEmpty.get();
        operations[0][0] = EditOperation.COPY;

        // choose for each position the cheapest operation to transform the first word into the second
        for (int i = 1; i <= str1.length; i++) {
            for (int j = 1; j <= str2.length; j++) {
                transformCostMatrix[i][j] = INFINITY;
                if (x[i] == y[j]) {
                    transformCostMatrix[i][j] = getCopyCost.apply(i, j);
                    operations[i][j] = EditOperation.COPY;
                }
                if (x[i] != y[j]) {
                    int replaceCost = getReplaceCost.apply(i, j);
                    if (replaceCost < transformCostMatrix[i][j]) {
                        transformCostMatrix[i][j] = replaceCost;
                        operations[i][j] = EditOperation.REPLACE;
                    }
                }
                if (i >= 2 && j >= 2 && x[i] == y[j - 1] && x[i - 1] == y[j]) {
                    int twiddleCost = getTwiddleCost.apply(i, j);
                    if (twiddleCost < transformCostMatrix[i][j]) {
                        transformCostMatrix[i][j] = twiddleCost;
                        operations[i][j] = EditOperation.TWIDDLE;
                    }
                }
                int deleteCost = getDeleteCost.apply(i, j);
                if (deleteCost < transformCostMatrix[i][j]) {
                    transformCostMatrix[i][j] = deleteCost;
                    operations[i][j] = EditOperation.DELETE;
                }
                int insertCost = getInsertCost.apply(i, j);
                if (insertCost < transformCostMatrix[i][j]) {
                    transformCostMatrix[i][j] = insertCost;
                    operations[i][j] = EditOperation.INSERT;
                }
            }
        }

        // evaluate if killing the first word is cheaper
        for (int i = 0; i <= str1.length - 1; i++) {
            int killCost = getKillCost.apply(i);
            if (killCost < transformCostMatrix[str1.length][str2.length]) {
                transformCostMatrix[str1.length][str2.length] = killCost;
                operations[str1.length][str2.length] = EditOperation.KILL;
            }
        }

        // print the resulting matrix
        System.out.println(costMatrixToString(x, y, transformCostMatrix));

        // get and print the operation sequence to transform the first word into the second
        List<AppliedEditOperation> operationSequence = getOperationSequenceOfEditDistance(operations);
        System.out.println(operationSequence);

        return operationSequence.get(operationSequence.size() - 1).getOperationCost();  // edit distance
    }

    /**
     * @param word1      starting word
     * @param word2      target word
     * @param costMatrix The cost matrix
     * @return the string representing the cost matrix.
     */
    private static String costMatrixToString(final char[] word1, char[] word2, int[][] costMatrix) {
        StringBuilder stringBuilder = new StringBuilder("COST MATRIX:" + System.lineSeparator() + "\t");
        for (char c : word2) stringBuilder.append(c).append("\t");
        for (int i = 0; i < word1.length; i++) {
            stringBuilder.append(System.lineSeparator()).append(word1[i]).append("\t");
            for (int j = 0; j < word2.length; j++) stringBuilder.append(costMatrix[i][j]).append("\t");
        }
        return stringBuilder.toString();
    }

    /**
     * @param operations Matrix of operations.
     * @return the list of applied operation.
     */
    public static List<AppliedEditOperation> getOperationSequenceOfEditDistance(final EditOperation[][] operations) {
        assert operations != null;
        if (operations.length > 0) {
            assert operations[0] != null;
            if (operations[0].length > 0) {
                return getOperationSequenceRecursivelyFromCoordinates(operations, operations.length - 1, operations[0].length - 1);
            }
        }
        return new ArrayList<>(0);
    }

    /**
     * @param operations .
     * @param i          .
     * @param j          .
     * @return the list of applied operation.
     */
    private static List<AppliedEditOperation> getOperationSequenceRecursivelyFromCoordinates(
            final EditOperation[][] operations, int i, int j) {
        assert operations != null;
        assert operations[0] != null;
        assert i <= operations.length;
        assert j <= operations[0].length;
        assert i >= 0;
        assert j >= 0;

        AppliedEditOperation currentOperation = new AppliedEditOperation(operations[i][j], i, j);
        List<AppliedEditOperation> toReturn;

        if (i == 0 && j == 0) {
            toReturn = new ArrayList<>();
        } else {
            switch (operations[i][j]) {
                case COPY:
                case REPLACE:
                    i--;
                    j--;
                    break;
                case TWIDDLE:
                    i -= 2;
                    j -= 2;
                    break;
                case DELETE:
                    i--;
                    break;
                case INSERT:
                    j--;
                    break;
                case KILL:
                default:
                    operations[i][j] = EditOperation.KILL; // must be KILL
                    i--;
                    break;
            }

            toReturn = getOperationSequenceRecursivelyFromCoordinates(operations, i, j);
        }

        toReturn.add(currentOperation);
        return toReturn;
    }
}
