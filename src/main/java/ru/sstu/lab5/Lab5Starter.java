package ru.sstu.lab5;

import java.util.Random;

public class Lab5Starter {
    public static void main(String[] args) {
        int N = 2000; // number of variables equals to rowsCount
        double[][] matrix = getMatrix(N);
        double[] solutions = getSolutions(N);

        System.out.printf("1. Вычисление СЛАУ с %d неизвестными при помощи 1 потока...", N);
        case1(matrix, solutions);

        int threadsCount = 2;
        System.out.printf("\n2.1 Вычисление СЛАУ с %d неизвестными при помощи %d потоков...", N, threadsCount);
        case2(matrix, solutions, threadsCount);
        threadsCount = 8;
        System.out.printf("\n2.2 Вычисление СЛАУ с %d неизвестными при помощи %d потоков...", N, threadsCount);
        case2(matrix, solutions, 8);
        threadsCount = 16;
        System.out.printf("\n2.3 Вычисление СЛАУ с %d неизвестными при помощи %d потоков...", N, threadsCount);
        case2(matrix, solutions, 16);
    }

    public static void case1(double[][] matrix, double[] solutions){
        GaussianElimination ge = new GaussianElimination();
        long before = System.currentTimeMillis();
        ge.solve(matrix, solutions);
        long after = System.currentTimeMillis();
        System.out.printf("Время вычисления: %d мс\n", (after - before));
    }

    public static void case2(double[][] matrix, double[] solutions, int threadsCount){
        ConcurrentGaussianElimination cge = new ConcurrentGaussianElimination();
        long before = System.currentTimeMillis();
        cge.solve(matrix, solutions, threadsCount);
        long after = System.currentTimeMillis();
        System.out.printf("Время вычисления: %d мс\n", (after - before));
    }

    public static double[][] getMatrix(final int n) {
        double[][] matrix = new double[n][n];
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = random.nextDouble();
            }
        }
        return matrix;
    }

    public static double[] getSolutions(final int n) {
        double[] solutions = new double[n];
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            solutions[i] = random.nextDouble();
        }
        return solutions;
    }
}
