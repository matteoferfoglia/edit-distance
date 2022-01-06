package edit_distance.entities;

import org.jetbrains.annotations.NotNull;

/**
 * An instance of this class represents the edit operations applied on the
 * first word to transform it into the second one.
 */
public class AppliedEditOperation {

    /**
     * The {@link EditOperation}.
     */
    private final EditOperation editOperation;

    /**
     * Position of the letter in the starting word to which the operation refers.
     */
    private final int positionOfLetterInFirstWord;

    /**
     * Position of the letter in the target word to which the operation refers.
     */
    private final int positionOfLetterInSecondWord;


    /**
     * A valid operation applicable in the contex of the edit distance.
     *
     * @param editOperation                The {@link EditOperation}.
     * @param positionOfLetterInFirstWord  Position of the letter in the starting word to which the operation refers.
     * @param positionOfLetterInSecondWord Position of the letter in the target word to which the operation refers.
     */
    public AppliedEditOperation(@NotNull EditOperation editOperation, int positionOfLetterInFirstWord, int positionOfLetterInSecondWord) {
        this.editOperation = editOperation;
        this.positionOfLetterInFirstWord = positionOfLetterInFirstWord;
        this.positionOfLetterInSecondWord = positionOfLetterInSecondWord;
    }

    /**
     * @return The cost of the operation.
     */
    public int getOperationCost() {
        return editOperation.getCost();
    }

    @Override
    public String toString() {
        return "{" + editOperation +
                " (" + positionOfLetterInFirstWord + "," + positionOfLetterInSecondWord + ")}";
    }
}
