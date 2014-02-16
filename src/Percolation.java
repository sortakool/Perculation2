/**
 * Created by Raymond on 2/10/14.
 */
public class Percolation {

    private static final int INVALID_INDEX = -1;
    private int VIRTUAL_TOP;

    private final int N;
    private final WeightedQuickUnionUF weightedQuickUnionUF;
    private final boolean[] openArray;
    private boolean percolates = false;
    private final int topRow;
    private final int bottomRow;
    private final int leftColumn;
    private final int rightColumn;

    private final int bottomRowStartIndex;
    private final boolean[] bottomRowIndicesOpen;
//    private final boolean[] perculatedBottomRowIndices;

    private boolean bottomOpened = false;

    /**
     * create N-by-N grid, with all sites blocked
     *
     * @param N
     */
    public Percolation(int N) {
        this.N = N;
        int size = N * N;
        int sizePlusVirtualIndexes = size + 1;
        weightedQuickUnionUF = new WeightedQuickUnionUF(sizePlusVirtualIndexes);
        openArray = new boolean[sizePlusVirtualIndexes];
        topRow = 1;
        bottomRow = N;
        leftColumn = 1;
        rightColumn = N;

        VIRTUAL_TOP = size;

        //union top row to VIRTUAL_TOP
        for (int i = 0; i < N; i++) {
            weightedQuickUnionUF.union(i, VIRTUAL_TOP);
        }

        //union bottom row to VIRTUAL_BOTTOM
        bottomRowStartIndex = size - N;
        bottomRowIndicesOpen = new boolean[N];
    }

    /**
     * open site (row i, column j) if it is not already
     *
     * @param i
     * @param j
     */
    public void open(int i, int j) {
        validate(i, j);
        int index = getIndex(i, j);
        openArray[index] = true;

        int topIndex = getTopIndex(i, j);
        int bottomIndex = getBottomIndex(i, j);
        int leftIndex = getLeftIndex(i, j);
        int rightIndex = getRightIndex(i, j);
        if ((topIndex != INVALID_INDEX) && openArray[topIndex]) {
            weightedQuickUnionUF.union(topIndex, index);
        }
        if ((bottomIndex != INVALID_INDEX) && openArray[bottomIndex]) {
            weightedQuickUnionUF.union(bottomIndex, index);
        }
        if ((leftIndex != INVALID_INDEX) && openArray[leftIndex]) {
            weightedQuickUnionUF.union(leftIndex, index);
        }
        if ((rightIndex != INVALID_INDEX) && openArray[rightIndex]) {
            weightedQuickUnionUF.union(rightIndex, index);
        }

        boolean isBottomRow = (index >= bottomRowStartIndex);
        int bottomRowIndex = -1;
        if (isBottomRow) {
            bottomRowIndex = index - bottomRowStartIndex;
            bottomRowIndicesOpen[bottomRowIndex] = true;
            bottomOpened = true;
        }

        if (bottomOpened) {
            for (int bottomRowIndex2 = 0; bottomRowIndex2 < N; bottomRowIndex2++) {
                boolean temp2 = bottomRowIndicesOpen[bottomRowIndex2];
                if (temp2) {
                    int temp = bottomRowIndex2 + bottomRowStartIndex;
                    if (openArray[temp]
                        && weightedQuickUnionUF.connected(temp, VIRTUAL_TOP)) {
                        percolates = true;
                    }
                }
            }
        }
    }

    private int getTopIndex(int i, int j) {
        int calculatedIndex = INVALID_INDEX;
        if ((i > topRow)) {
            int temp = i - 1;
            calculatedIndex = getIndex(temp, j);
        }
        return calculatedIndex;
    }

    private int getBottomIndex(int i, int j) {
        int calculatedIndex = INVALID_INDEX;
        if ((i < bottomRow)) {
            int temp = i + 1;
            calculatedIndex = getIndex(temp, j);
        }
        return calculatedIndex;
    }

    private int getLeftIndex(int i, int j) {
        int calculatedIndex = INVALID_INDEX;
        if ((j > leftColumn)) {
            int temp = j - 1;
            calculatedIndex = getIndex(i, temp);
        }
        return calculatedIndex;
    }

    private int getRightIndex(int i, int j) {
        int calculatedIndex = INVALID_INDEX;
        if ((j < rightColumn)) {
            int temp = j + 1;
            calculatedIndex = getIndex(i, temp);
        }
        return calculatedIndex;
    }

    private void validate(int i, int j) {
        if (i < 1) {
            throw new IndexOutOfBoundsException("Invalid row " + i);
        }
        if (i > N) {
            throw new IndexOutOfBoundsException("Invalid row " + i);
        }
        if (j < 1) {
            throw new IndexOutOfBoundsException("Invalid column " + i);
        }
        if (j > N) {
            throw new IndexOutOfBoundsException("Invalid column " + i);
        }
    }

    private int getIndex(int i, int j) {
        int index = ((i - 1) * N) + (j - 1);
        return index;
    }

    /**
     * is site (row i, column j) open?
     *
     * @param i
     * @param j
     * @return
     */
    public boolean isOpen(int i, int j) {
        validate(i, j);
        int index = getIndex(i, j);
        return openArray[index];
    }

    /**
     * is site (row i, column j) full?
     *
     * @param i
     * @param j
     * @return
     */
    public boolean isFull(int i, int j) {
        validate(i, j);
        boolean isFull = false;
        if (percolates) {
            int index = getIndex(i, j);
            boolean connectedToVirtualTop =
                    weightedQuickUnionUF.connected(index, VIRTUAL_TOP);
            isFull = (openArray[index] && connectedToVirtualTop);
            //not full until it is also connected to a perculated bottom row index
        }
        return isFull;
    }

    /**
     * does the system percolate?
     *
     * @return
     */
    public boolean percolates() {
        return percolates;
    }
}
