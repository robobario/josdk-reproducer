package org.acme;

import io.javaoperatorsdk.operator.api.reconciler.EventSourceContext;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent;
import io.javaoperatorsdk.operator.api.reconciler.Workflow;
import io.javaoperatorsdk.operator.processing.event.source.EventSource;

import java.util.List;

@Workflow(dependents = {@Dependent(type = ConfigMapDependentResource.class)})
public class GettingStartedReconciler implements Reconciler<GettingStartedCustomResource> {

    @Override
    public List<EventSource<?, GettingStartedCustomResource>> prepareEventSources(EventSourceContext<GettingStartedCustomResource> context) {
        if (context.getClient().getApiGroup("kafka.strimzi.io") != null) {
            // conditionally install an informer
        } else {
            throw new RuntimeException("kafka-strimzi.io is not available");
        }
        return List.of();
    }

    public UpdateControl<GettingStartedCustomResource> reconcile(GettingStartedCustomResource primary,
                                                                 Context<GettingStartedCustomResource> context) {

        return UpdateControl.noUpdate();
    }
}
