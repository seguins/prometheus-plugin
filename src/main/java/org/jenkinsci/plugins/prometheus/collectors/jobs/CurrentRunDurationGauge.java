package org.jenkinsci.plugins.prometheus.collectors.jobs;

import hudson.model.Job;
import hudson.model.Run;
import io.prometheus.client.Gauge;
import org.jenkinsci.plugins.prometheus.collectors.CollectorType;
import org.jenkinsci.plugins.prometheus.collectors.aggregators.MetricAggregator;
import org.jenkinsci.plugins.prometheus.collectors.builds.BuildsMetricCollector;

import java.time.Clock;

public class CurrentRunDurationGauge extends BuildsMetricCollector<Job, Gauge> {

    protected CurrentRunDurationGauge(MetricAggregator[] metricAggregators, String[] labelNames, String namespace, String subSystem) {
        super(metricAggregators, labelNames, namespace, subSystem);
    }

    @Override
    protected Gauge initCollector() {
        return Gauge.build()
                .name(calculateName(CollectorType.CURRENT_RUN_DURATION_GAUGE.getName()))
                .subsystem(subsystem)
                .namespace(namespace)
                .labelNames(labelNames)
                .help("Indicates the runtime of the run currently building if there is a run currently building")
                .create();
    }

    @Override
    public void calculateBuildMetric(Job jenkinsObject, String[] labelValues) {

        Run runningBuild = jenkinsObject.getLastBuild();
        if (runningBuild != null && runningBuild.isBuilding()) {
            long start = runningBuild.getStartTimeInMillis();
            // Using Clock to be able to mock in test
            long end = Clock.systemUTC().millis();
            long duration = Math.max(end - start, 0);
            this.collector.labels(labelValues).set(duration);
        }
    }
}
