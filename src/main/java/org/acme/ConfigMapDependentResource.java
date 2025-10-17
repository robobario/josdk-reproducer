package org.acme;

import java.util.HashMap;
import java.util.Map;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;
import org.acme.GettingStartedCustomResource;

@KubernetesDependent
public class ConfigMapDependentResource
        extends CRUDKubernetesDependentResource<ConfigMap, GettingStartedCustomResource> {

    public static final String KEY = "key";

    @Override
    protected ConfigMap desired(GettingStartedCustomResource primary,
                                Context<GettingStartedCustomResource> context) {
        return new ConfigMapBuilder()
                .withMetadata(
                        new ObjectMetaBuilder()
                                .withName(primary.getMetadata().getName())
                                .withNamespace(primary.getMetadata().getNamespace())
                                .build())
                .withData(Map.of(KEY, primary.getSpec().getValue()))
                .build();
    }
}