package edit_distance.entities;

import edit_distance.utils.MatricesUtility;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * An instance of this class computes the edit distance between two words.
 */
public class EditDistanceCalculator {

    //region debug flags
    /**
     * Flag to be true if {@link EditOperation#KILL} can be used.
     */
    private static final boolean USE_KILL_OPERATION = true;
    /**
     * Flag to be true if {@link EditOperation#TWIDDLE} can be used.
     */
    private static final boolean USE_TWIDDLE_OPERATION = true;
    //endregion

    /**
     * The symbol appended at beginning of working words to emulate the case of empty words.
     */
    private final static char EMPTY_WORD_SYMBOL = 0;

    /**
     * Value considered as "infinity".
     */
    private final static int INFINITY = Integer.MAX_VALUE;

    /**
     * The starting word which has to be edited to be transformed into the {@link #targetWord}.
     */
    private final char[] startingWord;

    /**
     * The target word.
     */
    private final char[] targetWord;        // starting word must be transformed to the target word

    /**
     * The matrix which saves at position (i,j) the cost to transform the i-th letter
     * of the {@link #startingWord} into the j-th letter of the{@link #targetWord}.
     */
    private final int[][] editCostMatrix;

    /**
     * The matrix which saves at position (i,j) the operations to transform the i-th letter
     * of the {@link #startingWord} into the j-th letter of the{@link #targetWord}.
     */
    private final EditOperation[][] editOperationMatrix;

    /**
     * The edit distance resulting from the algorithm.
     */
    private final int editDistance;

    /**
     * Sequence of operation computed to transform the starting string to the target one.
     */
    @Unmodifiable
    @Nullable
    private List<AppliedEditOperation> operationSequence = null;

    /**
     * Constructor.
     *
     * @param startingWord The starting word.
     * @param targetWord   The target word.
     */
    public EditDistanceCalculator(@NotNull final String startingWord, @NotNull final String targetWord) {
        this.startingWord = new char[startingWord.length() + 1];
        this.startingWord[0] = EMPTY_WORD_SYMBOL;
        System.arraycopy(startingWord.toCharArray(), 0, this.startingWord, 1, startingWord.length());

        this.targetWord = new char[targetWord.length() + 1];
        this.targetWord[0] = EMPTY_WORD_SYMBOL;
        System.arraycopy(targetWord.toCharArray(), 0, this.targetWord, 1, targetWord.length());

        this.editCostMatrix = new int[this.startingWord.length][this.targetWord.length];
        this.editOperationMatrix =
                new EditOperation[this.startingWord.length][this.targetWord.length];
        this.editDistance = computeEditDistanceAndGet();
    }

    /**
     * @param word The word.
     * @return the string corresponding to the given word in parameter,
     * after having removed the {@link #EMPTY_WORD_SYMBOL} from it.
     */
    @NotNull
    private static String removeEmptyWordSymbolAtBeginningOfWords(final char[] word) {
        if (Objects.requireNonNull(word).length > 0 && word[0] == EMPTY_WORD_SYMBOL) {
            return new String(word).substring(1);
        } else {
            throw new IllegalArgumentException("Not a word of an operation represented by the class.");
        }
    }

    /**
     * @param operations Matrix of {@link AppliedEditOperation} used to transform the
     *                   {@link #startingWord} into the {@link #targetWord}.
     * @return The immutable {@link List} of {@link AppliedEditOperation} applied to transform
     * * the {@link #startingWord} into the {@link #targetWord}.
     */
    public static List<AppliedEditOperation> getSequenceOfOperationComputedToTransformStartingWordToTargetWord(
            final EditOperation[][] operations) {
        assert operations != null;
        if (operations.length > 0) {
            assert operations[0] != null;
            if (operations[0].length > 0) {
                return getOperationSequenceRecursivelyFromMatrixCoordinates(operations, operations.length - 1, operations[0].length - 1);
            }
        }
        return new ArrayList<>(0);
    }

    /**
     * Support method for {@link  #getSequenceOfOperationComputedToTransformStartingWordToTargetWord(EditOperation[][])}.
     *
     * @param operations operations Matrix of {@link AppliedEditOperation} used to transform the
     *                   {@link #startingWord} into the {@link #targetWord}.
     * @param i          row index of the given matrix
     * @param j          column index of the given matrix.
     * @return See {@link #getSequenceOfOperationComputedToTransformStartingWordToTargetWord(EditOperation[][])}.
     */
    private static List<AppliedEditOperation> getOperationSequenceRecursivelyFromMatrixCoordinates(
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
                default /*includes case KILL*/:
                    operations[i][j] = EditOperation.KILL; // operation must be "KILL"
                    i--;
                    break;
            }
            toReturn = getOperationSequenceRecursivelyFromMatrixCoordinates(operations, i, j);
        }

        toReturn.add(currentOperation);
        return toReturn;
    }

    /**
     * @return the edit distance.
     */
    private int computeEditDistanceAndGet() {

        setInstanceVariablesForTheCaseEmptyStartingWord();
        setInstanceVariablesForTheCaseEmptyTargetWord();
        setInstanceVariablesForTheCaseEmptyBothStartingWordAndTargetWord();

        for (int i = 1; i < startingWord.length; i++) {
            for (int j = 1; j < targetWord.length; j++) {
                // operations must be sequential because results from previous iterations are used for the current one
                findCheapestOperationToTransformIthLetterOfStartingWordIntoJthOfTargetWordAndSetInstanceVariables(i, j);
            }
        }

        if (USE_KILL_OPERATION) {
            killStartingWordToEqualsTargetWordIfKillingIsCheapestEditOperation();
        }

        return getSequenceOfOperationComputedToTransformStartingWordToTargetWord()
                .stream().unordered()
                .mapToInt(AppliedEditOperation::getOperationCost)
                .sum();
    }

    /**
     * If cheaper than other operation, applies the {@link EditOperation#KILL} operation.
     */
    private void killStartingWordToEqualsTargetWordIfKillingIsCheapestEditOperation() {
        int[] copyOfLastColumnOfCostMatrix =
                MatricesUtility.getCopyOfColumn(targetWord.length - 1, editCostMatrix);
        IntStream.range(0, startingWord.length)
                .unordered()
                .map(i -> copyOfLastColumnOfCostMatrix[i] + EditOperation.KILL.getCost())
                .filter(killCost -> killCost < copyOfLastColumnOfCostMatrix[startingWord.length - 1])
                .findAny()
                .ifPresent(killCost -> {
                    editCostMatrix[startingWord.length - 1][targetWord.length - 1] = killCost;
                    editOperationMatrix[startingWord.length - 1][targetWord.length - 1] = EditOperation.KILL;
                });
    }

    /**
     * Compute the cost to transform the i-th letter of the {@link #startingWord}
     * into the j-th of the {@link #targetWord}.
     *
     * @param i position of character in {@link #startingWord} under examination.
     * @param j position of character in {@link #targetWord} under examination.
     */
    private void findCheapestOperationToTransformIthLetterOfStartingWordIntoJthOfTargetWordAndSetInstanceVariables(int i, int j) {
        var operationCostToTransformIthLetterIfStartingWordInJthLetterOfTargetWord = INFINITY;
        EditOperation operationToTransformIthLetterIfStartingWordInJthLetterOfTargetWord;
        {
            if (startingWord[i] == targetWord[j]) {
                operationCostToTransformIthLetterIfStartingWordInJthLetterOfTargetWord =
                        editCostMatrix[i - 1][j - 1] + EditOperation.COPY.getCost();
                operationToTransformIthLetterIfStartingWordInJthLetterOfTargetWord =
                        EditOperation.COPY;
            } else { // different chars at positions currently under examination
                operationCostToTransformIthLetterIfStartingWordInJthLetterOfTargetWord =
                        editCostMatrix[i - 1][j - 1] + EditOperation.REPLACE.getCost();
                operationToTransformIthLetterIfStartingWordInJthLetterOfTargetWord =
                        EditOperation.REPLACE;
            }
            if (USE_TWIDDLE_OPERATION &&
                    i >= 2 && j >= 2
                    && startingWord[i] == targetWord[j - 1]
                    && startingWord[i - 1] == targetWord[j]) {
                var twiddleCost = editCostMatrix[i - 2][j - 2] + EditOperation.TWIDDLE.getCost();
                if (twiddleCost < operationCostToTransformIthLetterIfStartingWordInJthLetterOfTargetWord) {
                    operationCostToTransformIthLetterIfStartingWordInJthLetterOfTargetWord = twiddleCost;
                    operationToTransformIthLetterIfStartingWordInJthLetterOfTargetWord =
                            EditOperation.TWIDDLE;
                }
            }
            {
                int deleteCost = editCostMatrix[i - 1][j] + EditOperation.DELETE.getCost();
                if (deleteCost < operationCostToTransformIthLetterIfStartingWordInJthLetterOfTargetWord) {
                    operationCostToTransformIthLetterIfStartingWordInJthLetterOfTargetWord = deleteCost;
                    operationToTransformIthLetterIfStartingWordInJthLetterOfTargetWord = EditOperation.DELETE;
                }
            }
            {
                int insertCost = editCostMatrix[i][j - 1] + EditOperation.INSERT.getCost();
                if (insertCost < operationCostToTransformIthLetterIfStartingWordInJthLetterOfTargetWord) {
                    operationCostToTransformIthLetterIfStartingWordInJthLetterOfTargetWord = insertCost;
                    operationToTransformIthLetterIfStartingWordInJthLetterOfTargetWord = EditOperation.INSERT;
                }
            }
        }
        editCostMatrix[i][j] = operationCostToTransformIthLetterIfStartingWordInJthLetterOfTargetWord;
        editOperationMatrix[i][j] = operationToTransformIthLetterIfStartingWordInJthLetterOfTargetWord;
    }

    /**
     * @return the starting word.
     */
    @NotNull
    public String getStartingWord() {
        return removeEmptyWordSymbolAtBeginningOfWords(startingWord);
    }

    /**
     * @return the target word.
     */
    @NotNull
    public String getTargetWord() {
        return removeEmptyWordSymbolAtBeginningOfWords(targetWord);
    }

    /**
     * Getter.
     *
     * @return copy of {@link #editCostMatrix}.
     */
    public int[][] getEditCostMatrix() {
        return Stream.of(editCostMatrix).sequential()
                .map(aRow -> Arrays.copyOf(aRow, aRow.length))
                .toArray(int[][]::new);
    }

    /**
     * @return The string representing the cost matrix.
     */
    public String getCostMatrixToString() {
        return "COST MATRIX:" + System.lineSeparator() +
                MatricesUtility.getMatrixToStringWithHeading(new String(startingWord), new String(targetWord),
                        Arrays.stream(editCostMatrix)
                                .map(ArrayUtils::toObject)
                                .toArray(Integer[][]::new));
    }

    /**
     * @return The unmodifiable {@link List} of {@link AppliedEditOperation} applied to transform
     * the {@link #startingWord} into the {@link #targetWord}.
     */
    public @Unmodifiable List<AppliedEditOperation> getSequenceOfOperationComputedToTransformStartingWordToTargetWord() {
        Supplier<@Unmodifiable List<AppliedEditOperation>> computeSequenceOfOperationOrCacheIfAlreadyComputer = () -> {
            if (operationSequence == null) {
                operationSequence = Collections.unmodifiableList(
                        getSequenceOfOperationComputedToTransformStartingWordToTargetWord(editOperationMatrix));
            }
            return operationSequence;
        };
        return computeSequenceOfOperationOrCacheIfAlreadyComputer.get();
    }

    /**
     * @return the edit distance.
     */
    public int getEditDistance() {
        return editDistance;
    }

    /**
     * Set fields of the object for the case of empty {@link #startingWord}.
     */
    private void setInstanceVariablesForTheCaseEmptyStartingWord() {
        editCostMatrix[0] =
                IntStream.range(0, targetWord.length)
                        .sequential()
                        .map(positionIndexInTargetWord -> positionIndexInTargetWord * EditOperation.INSERT.getCost())
                        .toArray();
        editOperationMatrix[0] =
                IntStream.range(0, targetWord.length)
                        .mapToObj(positionIndexInTargetWord -> EditOperation.INSERT)
                        .toArray(EditOperation[]::new);
    }

    /**
     * Set fields of the object for the case of empty {@link #targetWord}.
     */
    private void setInstanceVariablesForTheCaseEmptyTargetWord() {
        IntStream.range(1, startingWord.length)
                .forEach(positionIndexInStartingWord -> {
                    editCostMatrix[positionIndexInStartingWord][0] =
                            positionIndexInStartingWord * EditOperation.DELETE.getCost();
                    editOperationMatrix[positionIndexInStartingWord][0] =
                            EditOperation.DELETE;
                });
    }

    /**
     * Set fields of the object for the case of empty both {@link #startingWord} and {@link #targetWord}.
     */
    private void setInstanceVariablesForTheCaseEmptyBothStartingWordAndTargetWord() {
        editCostMatrix[0][0] = EditOperation.COPY.getCost();
        editOperationMatrix[0][0] = EditOperation.COPY;
    }

}
