package org.jenkinsci.plugins.prometheus.collectors.builds;

import hudson.model.Result;
import hudson.model.Run;
import io.prometheus.client.Counter;
import org.jenkinsci.plugins.prometheus.collectors.CollectorType;
import org.jenkinsci.plugins.prometheus.collectors.aggregators.MetricAggregator;

public class BuildSuccessfulCounter extends BuildsMetricCollector<Run, Counter> {

    protected BuildSuccessfulCounter(MetricAggregator[] metricAggregators, String[] labelNames, String namespace, String subSystem) {
        super(metricAggregators, labelNames, namespace, subSystem);
    }

    @Override
    protected Counter initCollector() {
        return Counter.build()
                .name(calculateName(CollectorType.BUILD_SUCCESSFUL_COUNTER.getName()))
                .subsystem(subsystem).namespace(namespace)
                .labelNames(labelNames)
                .help("Successful build count")
                .create();
    }

    @Override
    public void calculateBuildMetric(Run jenkinsObject, String[] labelValues) {
        Result runResult = jenkinsObject.getResult();
        if (runResult != null && !jenkinsObject.isBuilding()) {
            if (runResult.equals(Result.SUCCESS) || runResult.equals(Result.UNSTABLE)) {
                this.collector.labels(labelValues).inc();
            }
        }
    }
}
