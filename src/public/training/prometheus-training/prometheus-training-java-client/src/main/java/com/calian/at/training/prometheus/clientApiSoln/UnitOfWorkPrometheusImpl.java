package com.calian.at.training.prometheus.clientApiSoln;

import com.calian.at.training.prometheus.CalculationException;
import com.calian.at.training.prometheus.SelectedSatellite;
import com.calian.at.training.prometheus.existingInterface.UnitOfWorkInterface;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

// This is a class that already exists.
// Assume this is some class that does operations you want to monitor or observe
public class UnitOfWorkPrometheusImpl implements UnitOfWorkInterface {

    public static Counter countFoo = Counter.build()
            // The 'name' sets the name of the metric in Prometheus.
            // IMPORTANT: PREFIX ALL OF YOUR METRICS WITH THE SAME NAME.
            // For example: myProject_foo_count
            // This makes it easy for you to know what metrics in Prometheus are coming from your app!
            .name("calian_sampleClientApp_foo_count")

            // The help field describes what is being captured.
            // Required for every metric.
            .help("The number of times Foo has been called")

            // Completes the `Builder` and registers the metrics in the client so it can be exposed
            .register();

    public static Counter countCalculations = Counter.build()
            .name("calian_sampleClientApp_calculation_count")
            .help("The number of calculations being performed")
            .register();

    public static Gauge calculationValue = Gauge.build()
            .name("calian_sampleClientApp_calculation_value")
            .help("The total value of calculations returned")

            // Indicates the metric has a label named "positive" and "satellite"
            // labelNames is vararg so you can add as many labels to this function as needed.
            .labelNames("positive", "satellite")
            .register();

    private Random rand = new Random(System.currentTimeMillis());

    public void Foo() throws InterruptedException {
        countFoo.inc();
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
        countCalculations.inc();
        int calculationResult = rand.nextInt(100);
        boolean shouldBeNegative = rand.nextInt(2) == 1;
        SelectedSatellite selectedSatellite = SelectedSatellite.valueOf(rand.nextInt(5));

        if(shouldBeNegative) {
            calculationResult = calculationResult * -1;
        }

        calculationValue
                // IMPORTANT:
                // .labels(...) sets the value of the labels for the reported metric
                // The values MUST be in the same order that they are specified in the .labelNames() function!!
                // Additionally, labels must be strings.
                .labels(shouldBeNegative ? "true" : "false", selectedSatellite.toString())
                .inc(calculationResult);

        System.out.println("Calculation with result: " + calculationResult);
        return calculationResult;
    }
}
