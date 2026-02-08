package com.calian.at.training.prometheus.clientApiSoln;

import com.calian.at.training.prometheus.UnitOfWorkNoPrometheusImpl;
import com.calian.at.training.prometheus.existingInterface.UnitOfWorkInterface;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;

import java.io.IOException;

public class PrometheusTrainingSoln {

    public static void main(String[] args) throws InterruptedException, IOException {
        HTTPServer server = new HTTPServer.Builder()
                .withPort(1234)
                .build();

        DefaultExports.initialize();

        UnitOfWorkInterface existingClass = new UnitOfWorkPrometheusImpl();

        while(true) {
            existingClass.Foo();
            existingClass.Bar();
        }
    }
}
