/**
 * Created by Raymond on 2/10/14.
 */
public class PercolationStats {

    private double mean;
    private double stddev;
    private double confidenceLo;
    private double confidenceHi;

    /**
     *
     * @param N
     * @param T
     */
    public PercolationStats(int N, int T) {
        if(N <= 0) {
            throw new IllegalArgumentException("Invalid grid dimension " + N);
        }
        if(T <= 0) {
            throw new IllegalArgumentException("Invalid number of runs " + N);
        }
        double[] openCountPercents = new double[T];
        for(int i=0; i<T; i++) {
            Percolation percolation = new Percolation(N);
            int openCount = 0;
            while(!percolation.percolates()) {
                int row = StdRandom.uniform(N)+1;
                int column = StdRandom.uniform(N)+1;
                boolean open = percolation.isOpen(row, column);
                if(!open) {
                    percolation.open(row, column);
                    openCount++;
                }
            }
            openCountPercents[i] = (double)openCount/((double)(N*N));
        }
        mean = StdStats.mean(openCountPercents);
        stddev = StdStats.stddev(openCountPercents);
        confidenceLo = mean() - (1.96 * stddev()/ Math.sqrt(T));
        confidenceHi = mean() + (1.96 * stddev()/ Math.sqrt(T));
    }

    /**
     * sample mean of percolation threshold
     * @return
     */
    public double mean() {
        return mean;
    }

    /**
     * // sample standard deviation of percolation threshold
     * @return
     */
    public double stddev() {
        return stddev;
    }

    /**
     * // returns lower bound of the 95% confidence interval
     * @return
     */
    public double confidenceLo()   {
        return confidenceLo;
    }

    /**
     * // returns upper bound of the 95% confidence interval
     * @return
     */
    public double confidenceHi() {
        return confidenceHi;
    }

    /**
     * // test client, described below
     * @param args
     */
    public static void main(String[] args) {
        int N = Integer.valueOf(args[0]);
        int T = Integer.valueOf(args[1]);
        PercolationStats stats = new PercolationStats(N, T);


        StdOut.println("mean\t\t\t\t\t = " + stats.mean());
        StdOut.println("stddev\t\t\t\t\t = " + stats.stddev());
        StdOut.println("95% confidence interval\t = " + stats.confidenceLo() + ", " + stats.confidenceHi());
    }


        /*
% java PercolationStats 200 100
mean                    = 0.5929934999999997
stddev                  = 0.00876990421552567
95% confidence interval = 0.5912745987737567, 0.5947124012262428

% java PercolationStats 200 100
mean                    = 0.592877
stddev                  = 0.009990523717073799
95% confidence interval = 0.5909188573514536, 0.5948351426485464


% java PercolationStats 2 10000
mean                    = 0.666925
stddev                  = 0.11776536521033558
95% confidence interval = 0.6646167988418774, 0.6692332011581226

% java PercolationStats 2 100000
mean                    = 0.6669475
stddev                  = 0.11775205263262094
95% confidence interval = 0.666217665216461, 0.6676773347835391
         */


}
