/**
 * Created by Raymond on 2/10/14.
 */
public class Percolation {

    public static final int INVALID_INDEX = -1;
    public int VIRTUAL_TOP;
    public int VIRTUAL_BOTTOM;


    private final int N;
    private final WeightedQuickUnionUF weightedQuickUnionUF;
    private final boolean[] openArray;
    private boolean percolates = false;
    private final int topRow;
    private final int bottomRow;
    private final int leftColumn;
    private final int rightColumn;

    /**
     * create N-by-N grid, with all sites blocked
     * @param N
     */
    public Percolation(int N) {
        this.N = N;
        int size = N*N;
        int sizePlusVirtualIndexes = size + 2;
        weightedQuickUnionUF = new WeightedQuickUnionUF(sizePlusVirtualIndexes);
        openArray = new boolean[sizePlusVirtualIndexes];
        topRow = 1;
        bottomRow = N;
        leftColumn = 1;
        rightColumn = N;

        VIRTUAL_TOP = size;
        VIRTUAL_BOTTOM = size + 1;

        //union top row to VIRTUAL_TOP
        for(int i=0; i<N; i++) {
            weightedQuickUnionUF.union(i, VIRTUAL_TOP);
        }

        //union bottom row to VIRTUAL_BOTTOM
        for(int i=size-N; i<size; i++) {
            weightedQuickUnionUF.union(i, VIRTUAL_BOTTOM);
        }
    }

    /**
     * open site (row i, column j) if it is not already
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
        if((topIndex != INVALID_INDEX) && openArray[topIndex]) {
            weightedQuickUnionUF.union(topIndex, index);
        }
        if((bottomIndex != INVALID_INDEX) && openArray[bottomIndex]) {
            weightedQuickUnionUF.union(bottomIndex, index);
        }
        if((leftIndex != INVALID_INDEX) && openArray[leftIndex]) {
            weightedQuickUnionUF.union(leftIndex, index);
        }
        if((rightIndex != INVALID_INDEX) && openArray[rightIndex]) {
            weightedQuickUnionUF.union(rightIndex, index);
        }

        if(weightedQuickUnionUF.connected(VIRTUAL_TOP, VIRTUAL_BOTTOM)) {
            percolates = true;
        }
    }

    private int getTopIndex(int i, int j) {
        int calculatedIndex = INVALID_INDEX;
        if( (i > topRow) ) {
            i--;
            calculatedIndex = getIndex(i, j);
        }
        return calculatedIndex;
    }

    private int getBottomIndex(int i, int j) {
        int calculatedIndex = INVALID_INDEX;
        if( (i < bottomRow) ) {
            i++;
            calculatedIndex = getIndex(i, j);
        }
        return calculatedIndex;
    }

    private int getLeftIndex(int i, int j) {
        int calculatedIndex = INVALID_INDEX;
        if( (j > leftColumn) ) {
            j--;
            calculatedIndex = getIndex(i, j);
        }
        return calculatedIndex;
    }

    private int getRightIndex(int i, int j) {
        int calculatedIndex = INVALID_INDEX;
        if( (j < rightColumn) ) {
            j++;
            calculatedIndex = getIndex(i, j);
        }
        return calculatedIndex;
    }

    private void validate(int i, int j) {
        if(i < 1) {
            throw new IllegalArgumentException("Invalid row " + i);
        }
        if(i > N) {
            throw new IllegalArgumentException("Invalid row " + i);
        }
        if(j < 1) {
            throw new IllegalArgumentException("Invalid column " + i);
        }
        if(j > N) {
            throw new IllegalArgumentException("Invalid column " + i);
        }
    }

    private int getIndex(int i, int j) {
        int index = ((i-1)*N) + (j-1);
        return index;
    }

    /**
     * is site (row i, column j) open?
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
     * @param i
     * @param j
     * @return
     */
    public boolean isFull(int i, int j) {
        return (percolates() && isOpen(i, j));
    }

    /**
     * does the system percolate?
     * @return
     */
    public boolean percolates() {
        return percolates;
    }
}
