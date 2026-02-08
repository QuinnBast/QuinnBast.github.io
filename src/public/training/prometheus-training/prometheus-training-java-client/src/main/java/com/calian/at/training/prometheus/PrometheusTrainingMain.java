package com.calian.at.training.prometheus;

import com.calian.at.training.prometheus.existingInterface.UnitOfWorkInterface;

public class PrometheusTrainingMain {

    public static void main(String[] args) throws InterruptedException {

        UnitOfWorkInterface existingClass = new UnitOfWorkNoPrometheusImpl();

        while(true) {
            existingClass.Foo();
            existingClass.Bar();
        }
    }
}
