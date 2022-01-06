package main_package.entities;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EditDistanceCalculatorTest {

    @NotNull
    private static final String SAMPLE_WORD_1 = "SAMPLE_1";
    @NotNull
    private static final String SAMPLE_WORD_2 = "SAMPLE_2";
    @NotNull
    private EditDistanceCalculator editDistanceCalculator;

    @BeforeEach
    void setUp() {
        editDistanceCalculator = new EditDistanceCalculator(SAMPLE_WORD_1, SAMPLE_WORD_2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void dontAllowToModifyOperationSequenceAfterHasBeenReturned() {
        List<AppliedEditOperation> listWhichShouldBeImmutable =
                editDistanceCalculator.getSequenceOfOperationComputedToTransformStartingWordToTargetWord();
        try {
            //noinspection ConstantConditions   // this test checks for immutability of the list
            listWhichShouldBeImmutable.remove(0);
            fail("Exception should have be thrown");
        } catch (UnsupportedOperationException listIsImmutableHenceThisExceptionHasThrown) {
            try {
                listWhichShouldBeImmutable.add(new AppliedEditOperation(EditOperation.COPY, 0, 0));
                fail("Exception should have be thrown");
            } catch (UnsupportedOperationException listIsImmutableHenceThisExceptionHasThrownAgain) {
                // correct to be here
            }
        }
    }

    @Test
    void dontAllowToModifyInternalCostMatrixIfObtainedWithGetter() {
        var matrixFromGetter = editDistanceCalculator.getEditCostMatrix();
        assert Arrays.deepEquals(matrixFromGetter, editDistanceCalculator.getEditCostMatrix());
        matrixFromGetter[0][0]++;
        assertFalse(Arrays.deepEquals(matrixFromGetter, editDistanceCalculator.getEditCostMatrix()));
    }

    @Test
    void getFirstWordOfOperation() {
        assertEquals(SAMPLE_WORD_1, editDistanceCalculator.getStartingWord());
    }

    @Test
    void getSecondWordOfOperation() {
        assertEquals(SAMPLE_WORD_2, editDistanceCalculator.getTargetWord());
    }

    @ParameterizedTest
    @CsvSource({                    //     OPERATIONS (first operation is copying the empty word symbol) - NOTE: operation sequence may be different but equivalent in terms of the edit distance
            "Foo,Bar,3",            // C+R+R+R
            ",,0",                  // C
            "A,B,1",                // C+R
            ",B,1",                 // C+I
            "A,,1",                 // C+D
            "hello,hella,1",        // C+C+C+C+C+R
            "HOME,HOUSE,2",         // C+C+C+R+I+C
            "HOME,HoUSE,3",         // C+C+C+R+I+C     // case sensitive
            "Intention,Execution,5",// C+D+R+R+C+I+R+C+C+C+C
            "brt,bart,1",           // C+I
            "caar,car,1",           // C+D
            "arx,art,1"             // C+R
    })
    void getEditDistance(String startingWord, String targetWord, int expectedEditDistance/*edit distance values depend on costs of operation (see where they are saved)*/) {
        startingWord = startingWord == null ? "" : startingWord;    // correction due to csv interpreter
        targetWord = targetWord == null ? "" : targetWord;
        assertEquals(expectedEditDistance, new EditDistanceCalculator(startingWord, targetWord).getEditDistance());
    }
}