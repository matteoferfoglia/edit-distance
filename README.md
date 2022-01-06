Edit (Levenshtein) distance
===========================

This project proposes an implementation for the edit distance algorithm, used to compute the distance (as measure of
similarity) between two strings.

**Edit operations** are:

- *copy* of a character: input words have the same character at a given position (no edits required, it is like a *skip*
  but it can have a cost)
- *replacement* of a character: input words have different characters at a given position and replacing it solves the
  problem
- *twiddle* of a character: input words show a transposition of character and twiddling solves the problem (insert the
  next two characters in reverse order)
- *deletion* of a character: one of the input words has one more character than the latter
- *insertion* of a character: one of the input words has one less character than the latter
- *kill*: stop the string.

The cost of edit operations is customizable.

## Javadoc

Javadoc is available for this project and can be generated with `mvn site` command.

## Packaging and execution

### Packaging and executable JAR creation

The project can be compiled, tested and package with `mvn package` command. During the packaging phase, for this
project (see configuration in *pom.xml*)
two *jar* files are provided: the fattest one includes all the dependencies used in the project, hence can be directly
distributed (without any support file).

## Execution

The project can be executed from the root directory of the project with the command `mvn exec:java` or launching the *
jar* file from a terminal (Java is required) with the command `java -jar jarFileName` (launched from the terminal).

## Idea of the algorithm (Edit distance)

In the algorithm two words are given, let they be *x* and *y*, where
*x* is the **starting word** and *y* is the **target word**. Let *z* be a word which is initially empty and will support
the algorithm. The aim of the algorithm is to construct the word *z* such that at end of the algorithm *z* will be equal
to *y* (i.e., to the target string), but this must be done starting from *x* by editing it as few as possible. the *edit
distance* from *x* to *y* is the sum of the costs due to the operations (i.e., the *edit operations*) made to create *z*
editing *x*
such that at end *z* is equal to *y*.

### From [CLRS, "Edit distance"](https://walkccc.me/CLRS/Chap15/Problems/15-5/):

> In order to transform one source string of text *x[1..m]* to a target string *y[1..n]*, we can perform various transformation operations. Our goal is, given *x* and *y*, to produce a series of transformations that change *x* to *y*. We use an array *z*—assumed to be large enough to hold all the characters it will need—to hold the intermediate results. Initially, *z* is empty, and at termination, we should have *z[j] = y[j]* for *j = 1, 2, \ldots, n*. We maintain current indices *i* into *x* and *j* into *z*, and the operations are allowed to alter *z* and these indices. Initially, *i = j = 1*. We are required to examine every character in *x* during the transformation, which means that at the end of the sequence of transformation operations, we must have *i = m + 1*.
>
> We may choose from among six transformation operations:
> - **Copy** a character from *x* to *z* by setting *z[j] = x[i]* and then incrementing both *i* and *j*. This operation examines *x[i]*.
>
> - **Replace** a character from *x* by another character *c*, by setting *z[j] = c*, and then incrementing both *i* and *j*. This operation examines *x[i]*.
>
> - **Delete** a character from *x* by incrementing *i* but leaving *j* alone. This operation examines *x[i]*.
>
> - **Insert** the character *c* into *z* by setting *z[j] = c* and then incrementing *j*, but leaving *i* alone. This operation examines no characters of *x*.
>
> - **Twiddle** (i.e., exchange) the next two characters by copying them from *x* to *z* but in the opposite order; we do so by setting *z[j] = x[i + 1]* and *z[j + 1] = x[i]* and then setting *i = i + 2* and *j = j + 2*. This operation examines *x[i]* and *x[i + 1]*.
>
> - **Kill** the remainder of *x* by setting *i = m + 1*. This operation examines all characters in *x* that have not yet been examined. This operation, if performed, must be the final operation.
>
*[...]*
> Each of the transformation operations has an associated cost. The cost of an operation depends on the specific application, but we assume that each operation's cost is a constant that is known to us. We also assume that the individual costs of the copy and replace operations are less than the combined costs of the delete and insert operations; otherwise, the copy and replace operations would not be used. The cost of a given sequence of transformation operations is the sum of the costs of the individual operations in the sequence.
>
*[...]*
> Given two sequences *x[1..m]* and *y[1..n]* and set of transformation-operation costs, the **_edit distance_** from *x* to *y* is the cost of the least expensive operatoin sequence that transforms *x* to *y*. Describe a dynamic-programming algorithm that finds the edit distance from *x[1..m]* to *y[1..n]* and prints an optimal opeartion sequence. Analyze the running time and space requirements of your algorithm.
*[...]*
> Since*twiddle* and *kill* have infinite costs, we will have neither of them in a minimal cost solution.
> 