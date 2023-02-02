package ru.sstu.lab5;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConcurrentGaussianElimination {
    public void solve(double[][] A, double[] B, int threadsCount) {
        final int N = B.length;
        final ExecutorService executorService = Executors.newFixedThreadPool(threadsCount);

        for (int k = 0; k < N; k++) {
            final Integer threadLocalK = new Integer(k);
            executorService.submit(() -> {
                double[][] localA;
                double[] localB;
                synchronized (A) {
                    synchronized (B) {
                        localA = A;
                        localB = B;
                    }
                }
                int max = threadLocalK;
                for (int i = threadLocalK + 1; i < N; i++)
                    if (Math.abs(localA[i][threadLocalK]) > Math.abs(localA[max][threadLocalK]))
                        max = i;

                double t = localB[threadLocalK];
                localB[threadLocalK] = localB[max];
                localB[max] = t;
                double[] temp = localA[threadLocalK];
                localA[threadLocalK] = localA[max];
                localA[max] = temp;

                for (int i = threadLocalK + 1; i < N; i++) {
                    double factor = localA[i][threadLocalK] / localA[threadLocalK][threadLocalK];
                    localB[i] -= factor * B[threadLocalK];
                    for (int j = threadLocalK; j < N; j++) {
                        localA[i][j] -= factor * localA[threadLocalK][j];
                    }
                }
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(10L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        double[] solution = new double[N];
        for (int i = N - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < N; j++)
                sum += A[i][j] * solution[j];
            solution[i] = (B[i] - sum) / A[i][i];
        }
        printSolution(solution);
    }

    /**
     * function to print in row    echleon form
     **/
    public void printRowEchelonForm(double[][] A, double[] B) {
        int N = B.length;
        System.out.println("\nRow Echelon form : ");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++)
                System.out.printf("%.3f ", A[i][j]);
            System.out.printf("| %.3f\n", B[i]);
        }
        System.out.println();
    }

    public void printSolution(double[] sol) {
        int N = sol.length;
        System.out.print("\nРешение : ");
        for (int i = 0; i < N; i++)
            System.out.printf("%.3f ", sol[i]);
        System.out.println();
    }
}
