import java.util.Arrays;

/**
 * Created by Raymond on 2/10/14.
 */
public class Percolation {

    private static final int INVALID_INDEX = -1;
    private int VIRTUAL_TOP;

    private final int N;
    private final WeightedQuickUnionUF weightedQuickUnionUF;
    //    private final WeightedQuickUnionUF bottomRowWeightedQuickUnionUF;
//    private final boolean[] openArray;
    private boolean percolates = false;
    private final int topRow;
    private final int bottomRow;
    private final int leftColumn;
    private final int rightColumn;

    private final int bottomRowStartIndex;
    private final int[] bottomArray;
    private final byte[] states;

    private boolean bottomOpened = false;
    private final int theVirtualRoot;

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
//        openArray = new boolean[sizePlusVirtualIndexes];
        topRow = 1;
        bottomRow = N;
        leftColumn = 1;
        rightColumn = N;

        VIRTUAL_TOP = size;

        //union top row to VIRTUAL_TOP
        for (int i = 0; i < N; i++) {
//            weightedQuickUnionUF.union(i, VIRTUAL_TOP);
            weightedQuickUnionUF.union(VIRTUAL_TOP, i);
        }

//        bottomRowIndicesOpen = new boolean[N];
//        bottomRowWeightedQuickUnionUF = new WeightedQuickUnionUF(N);
        states = new byte[sizePlusVirtualIndexes];

        theVirtualRoot = weightedQuickUnionUF.find(VIRTUAL_TOP);
        bottomRowStartIndex = size - N;
        bottomArray = new int[N];
        for(int temp=0; temp<N; temp++) {
            bottomArray[temp] = temp+bottomRowStartIndex;
        }
        System.out.println(Arrays.toString(bottomArray));
    }


    private static class State {
        public static final byte OPEN = 1 << 1;
        public static final byte FULL = 1 << 2;
        public static final byte CONNECTED_TO_TOP = 1 << 3;
        public static final byte CONNECTED_TO_BOTTOM = 1 << 4;
    }

    private boolean isOpen(byte[] tempArray, int index) {
        return isSet(tempArray, index, State.OPEN);
    }

    private boolean isFull(byte[] tempArray, int index) {
        return isSet(tempArray, index, State.FULL);
    }

    private boolean isConnectedToTop(byte[] tempArray, int index) {
        return isSet(tempArray, index, State.CONNECTED_TO_TOP);
    }

    private boolean isConnectedToBottom(byte[] tempArray, int index) {
        return isSet(tempArray, index, State.CONNECTED_TO_BOTTOM);
    }

    private boolean isSet(byte[] tempArray, int index, byte bit) {
        byte value = tempArray[index];
        return ((value & bit) != 0);
    }

    private int setBit(byte[] tempArray, int index, byte bit) {
        byte newBitValue = (byte) (tempArray[index] | bit);
        tempArray[index] = newBitValue;
        return newBitValue;
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
//        openArray[index] = true;
        setBit(states, index, State.OPEN);

        int root = weightedQuickUnionUF.find(index);
        int virtualRoot = weightedQuickUnionUF.find(VIRTUAL_TOP);

        StringBuilder bottomSB = new StringBuilder("\tbottomRoots:\t");
        for(int temp=((N*N)-N); temp<(N*N); temp++) {
            int[] xy = getCoordinate(temp);
            int bottomRoot = weightedQuickUnionUF.find(temp);
            bottomSB.append("[").append(xy[0]).append(",").append(xy[1]).append("@").append(temp).append("]").append("=").append(bottomRoot).append("\t");
        }

        boolean isTop = isTop(i);
        if (isTop) {
            setBit(states, index, State.FULL);
            setBit(states, index, State.CONNECTED_TO_TOP);
        }

        boolean isBottom = isBottom(i);
        if (isBottom) {
            setBit(states, index, State.CONNECTED_TO_BOTTOM);
            bottomOpened = true;
        }

        int topIndex = getTopIndex(i, j);
        int bottomIndex = getBottomIndex(i, j);
        int leftIndex = getLeftIndex(i, j);
        int rightIndex = getRightIndex(i, j);

        int topIndexRoot = -1;
        int bottomIndexRoot = -1;
        int leftIndexRoot = -1;
        int rightIndexRoot = -1;

        boolean isTopOpen = false;
        boolean isBottomOpen = false;
        boolean isLeftOpen = false;
        boolean isRightOpen = false;

        if ((topIndex != INVALID_INDEX) && isOpen(states, topIndex)) {
            isTopOpen = true;

            topIndexRoot = weightedQuickUnionUF.find(topIndex);
            if( (root != topIndexRoot) ) {
                weightedQuickUnionUF.union(topIndex, index);
            }

            if (isFull(states, topIndex)) {
                setBit(states, index, State.FULL);
            }
            if (isConnectedToTop(states, topIndex)) {
                setBit(states, index, State.CONNECTED_TO_TOP);
            }
            if (isConnectedToBottom(states, topIndex)) {
                setBit(states, index, State.CONNECTED_TO_BOTTOM);
            }


        }

        if ((bottomIndex != INVALID_INDEX) && isOpen(states, bottomIndex)) {
            isBottomOpen = true;

            bottomIndexRoot = weightedQuickUnionUF.find(bottomIndex);
            if( (root != bottomIndexRoot) || (topIndexRoot != bottomIndexRoot)) {
                weightedQuickUnionUF.union(bottomIndex, index);
            }

            if (isFull(states, bottomIndex)) {
                setBit(states, index, State.FULL);
            }
            if (isConnectedToTop(states, bottomIndex)) {
                setBit(states, index, State.CONNECTED_TO_TOP);
            }
            if (isConnectedToBottom(states, bottomIndex)) {
                setBit(states, index, State.CONNECTED_TO_BOTTOM);
            }

            int tempJ = j;
            for(int temp=i; temp<=N; temp++) {
                int tempIndex = getIndex(temp, tempJ);
                if(isConnectedToBottom(states, tempIndex)) {
                    setBit(states, tempIndex, State.CONNECTED_TO_BOTTOM);
                }
                else {
                    break;
                }
            }


        }

        if ((leftIndex != INVALID_INDEX) && isOpen(states, leftIndex)) {
            isLeftOpen = true;

            leftIndexRoot = weightedQuickUnionUF.find(leftIndex);

            if( (root != leftIndexRoot) || (topIndexRoot != leftIndexRoot)|| (bottomIndexRoot != leftIndexRoot)) {
                weightedQuickUnionUF.union(leftIndex, index);
            }

            weightedQuickUnionUF.union(leftIndex, index);
            if (isFull(states, leftIndex)) {
                setBit(states, index, State.FULL);
            }
            if (isConnectedToTop(states, leftIndex)) {
                setBit(states, index, State.CONNECTED_TO_TOP);
            }
            if (isConnectedToBottom(states, leftIndex)) {
                setBit(states, index, State.CONNECTED_TO_BOTTOM);
            }

            int tempJ = j-1;
            for(int temp=i; temp<=N; temp++) {
                int tempIndex = getIndex(temp, tempJ);
                if(isConnectedToBottom(states, tempJ)) {
                    setBit(states, tempIndex, State.CONNECTED_TO_BOTTOM);
                }
                else {
                    break;
                }
            }


        }

        if ((rightIndex != INVALID_INDEX) && isOpen(states, rightIndex)) {
            isRightOpen = true;

            rightIndexRoot = weightedQuickUnionUF.find(rightIndex);

            if( (root != rightIndexRoot) || (topIndexRoot != rightIndexRoot)|| (bottomIndexRoot != rightIndexRoot)|| (leftIndexRoot != rightIndexRoot)) {
                weightedQuickUnionUF.union(rightIndex, index);
            }

            if (isFull(states, rightIndex)) {
                setBit(states, index, State.FULL);
            }
            if (isConnectedToTop(states, rightIndex)) {
                setBit(states, index, State.CONNECTED_TO_TOP);
            }
            if (isConnectedToBottom(states, rightIndex)) {
                setBit(states, index, State.CONNECTED_TO_BOTTOM);
            }

            int tempJ = j+1;
            for(int temp=i; temp<=N; temp++) {
                int tempIndex = getIndex(temp, tempJ);
                if(isConnectedToBottom(states, tempJ)) {
                    setBit(states, tempIndex, State.CONNECTED_TO_BOTTOM);
                }
                else {
                    break;
                }
            }


        }

        int myNewRoot = weightedQuickUnionUF.find(index);
        int newRoot = -1;
        int newTopIndexRoot = -1;
        int newBottomIndexRoot = -1;
        int newLeftIndexRoot = -1;
        int newRightIndexRoot = -1;

        int toFind = -1;

        if(isTopOpen) {
            newTopIndexRoot = weightedQuickUnionUF.find(topIndex);
            if(topIndexRoot != newTopIndexRoot) {
                newRoot = newTopIndexRoot;
                if(newRoot == theVirtualRoot) {
                    toFind = topIndexRoot;
                }
            }
        }
        if(isBottomOpen) {
            newBottomIndexRoot = weightedQuickUnionUF.find(bottomIndex);
            if(bottomIndexRoot != newBottomIndexRoot) {
                newRoot = newBottomIndexRoot;
                if(newRoot == theVirtualRoot) {
                    toFind = bottomIndexRoot;
                }
            }
        }
        if(isLeftOpen) {
            newLeftIndexRoot = weightedQuickUnionUF.find(leftIndex);
            if(leftIndexRoot != newLeftIndexRoot) {
                newRoot = newLeftIndexRoot;
                if(newRoot == theVirtualRoot) {
                    toFind = leftIndexRoot;
                }
            }
        }
        if(isRightOpen) {
            newRightIndexRoot = weightedQuickUnionUF.find(rightIndex);
            if(rightIndexRoot != newRightIndexRoot) {
                newRoot = newRightIndexRoot;
                if(newRoot == theVirtualRoot) {
                    toFind = rightIndexRoot;
                }
            }
        }

        int newVirtualRoot = weightedQuickUnionUF.find(VIRTUAL_TOP);
        System.out.println("open["+i+","+j+"]:"
                + "\n\t" + "theVirtualRoot=" + theVirtualRoot
                + "\n\t" + "myNewRoot=" + myNewRoot
                + "\n\t" + "virtualRoot=" + virtualRoot + ", newVirtualRoot=" + newVirtualRoot
                + "\n\t" + printIndex(index) + "root=" + root + ", newRoot=" + newRoot
                + "\n\t" + printIndex(topIndex) + "topIndexRoot=" + topIndexRoot + ", newTopIndexRoot=" + newTopIndexRoot
                + "\n\t" + printIndex(bottomIndex) + "bottomIndexRoot=" + bottomIndexRoot + ", newBottomIndexRoot=" + newBottomIndexRoot
                + "\n\t" + printIndex(leftIndex) + "leftIndexRoot=" + leftIndexRoot + ", newLeftIndexRoot=" + newLeftIndexRoot
                + "\n\t" + printIndex(rightIndex) + "rightIndexRoot=" + rightIndexRoot + ", newRightIndexRoot=" + newRightIndexRoot
        );
        StringBuilder newBottomSB = new StringBuilder("\tnewBottomRoots:\t");
        for(int temp=((N*N)-N); temp<(N*N); temp++) {
            int[] xy = getCoordinate(temp);
            int bottomRoot = weightedQuickUnionUF.find(temp);
            newBottomSB.append("[").append(xy[0]).append(",").append(xy[1]).append("@").append(temp).append("]").append("=").append(bottomRoot).append("\t");
        }
        System.out.println(bottomSB.toString());
        System.out.println(newBottomSB.toString());

        if(isBottom) {
            bottomArray[index-bottomRowStartIndex] = myNewRoot;
        }

        if(toFind != -1) {
            for(int temp=0; temp < N; temp++) {
                if(bottomArray[temp] == toFind) {
                    bottomArray[temp] = theVirtualRoot;
                    percolates = true;
                }
            }
        }
    }

    private String printIndex(int index) {
        StringBuilder sb = new StringBuilder();
        int[] xy = getCoordinate(index);
        sb.append("[").append(xy[0]).append(",").append(xy[1]).append("@").append(index).append("] ");
        return sb.toString();
    }


    private boolean isTop(int i) {
        return i == 1;
    }

    private boolean isBottom(int i) {
        return i == N;
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

    private int[] getCoordinate(int index) {
        int[] xy = new int[2];
        xy[0] = (index / N) + 1;
        xy[1] = (index % N) + 1;
        return xy;
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
        return isOpen(states, index);
    }

    /**
     * A full site is an open site that can be connected
     * to an open site in the top row
     * via a chain of neighboring (left, right, up, down) open sites.
     * is site (row i, column j) full?
     *
     * @param i
     * @param j
     * @return
     */
    public boolean isFull(int i, int j) {
        validate(i, j);
        int index = getIndex(i, j);
//        return isFull(states, index);
        boolean isFull = false;
        if (isOpen(states, index)) {
            isFull = weightedQuickUnionUF.connected(index, VIRTUAL_TOP);
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
