package com.devmaster.demo.day02.tight_loosely_coupling;

public class LooselyCoupledService {
    private SortAlgorithm sortAlgorithm;

    public LooselyCoupledService() {}

    public LooselyCoupledService(SortAlgorithm sortAlgorithm) {
        this.sortAlgorithm = sortAlgorithm;
    }

    public void complexBusiness(int[] array) {
        sortAlgorithm.sort(array);
    }

    public static void main(String[] args) {
        LooselyCoupledService sortAlgorithm = new LooselyCoupledService(new LooselyBubbleSortAlgorithm());
        sortAlgorithm.complexBusiness(new int[]{11, 21, 13, 42, 15});
    }
}