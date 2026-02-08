package com.calian.at.training.prometheus;

import com.calian.at.training.prometheus.existingInterface.UnitOfWorkInterface;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

// This is a class that already exists.
// Assume this is some class that does operations you want to monitor or observe
public class UnitOfWorkNoPrometheusImpl implements UnitOfWorkInterface {

    private Random rand = new Random(System.currentTimeMillis());

    public void Foo() throws InterruptedException {
        // Simulate some work being done by sleeping for a random amount of time, up to 10 seconds.
        long timeToSleep = (long) (rand.nextDouble() * 10000);
        System.out.println("Doing Work for " + timeToSleep + "ms");
        Thread.sleep(timeToSleep);
    }

    public void Bar() throws InterruptedException {
        int counter = 0;
        int calculationsToPerform = rand.nextInt(100);
        while(counter < calculationsToPerform) {
            DoCalculation();
            counter++;
        }
        // Simulate some work being done by sleeping for a random amount of time, up to 10 seconds.
        long timeToSleep = (long) (rand.nextDouble() * 10000);
        Thread.sleep(timeToSleep);

        try {
            throw new CalculationException("Holy crap, some calculation went wrong!");
        } catch (CalculationException e) {
            // So our app doesn't die.
            System.out.println("ERROR: " + e.getMessage());
        }

        System.out.println("Did " + calculationsToPerform + " calculations in " + timeToSleep + "ms");
    }

    private int DoCalculation() {
        int calculationResult = rand.nextInt(100);
        boolean shouldBeNegative = rand.nextInt(2) == 1;
        shouldBeNegative = true;
        SelectedSatellite selectedSatellite = SelectedSatellite.valueOf(rand.nextInt(5));

        if(shouldBeNegative) {
            calculationResult = calculationResult * -1;
        }
        System.out.println("Calculation with result: " + calculationResult);
        return calculationResult;
    }
}
