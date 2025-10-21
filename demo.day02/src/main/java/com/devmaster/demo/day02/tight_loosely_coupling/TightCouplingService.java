package com.devmaster.demo.day02.tight_loosely_coupling;

import java.util.Arrays;

public class TightCouplingService {
    private BubbleSortAlgorithm bubbleSortAlgorithm = new BubbleSortAlgorithm();

    public TightCouplingService() {}

    public TightCouplingService(BubbleSortAlgorithm bubbleSortAlgorithm) {
        this.bubbleSortAlgorithm = bubbleSortAlgorithm;
    }

    public void complexBusinessSort(int[] arr) {
        bubbleSortAlgorithm.sort(arr);
        Arrays.stream(arr).forEach(System.out::println);
    }

    public static void main(String[] args) {
        TightCouplingService tCouplingService = new TightCouplingService();
        tCouplingService.complexBusinessSort(new int[]{11, 21, 13, 42, 15});
    }
}