package org.acme;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.javaoperatorsdk.operator.junit.LocallyRunOperatorExtension;
import io.strimzi.api.kafka.Crds;
import io.strimzi.api.kafka.model.kafka.Kafka;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.File;
import java.io.IOException;

import static org.acme.ConfigMapDependentResource.KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class GettingStartedReconcilerIntegrationTest {

    public static final String RESOURCE_NAME = "test1";
    public static final String INITIAL_VALUE = "initial value";
    public static final String CHANGED_VALUE = "changed value";

    @RegisterExtension
    LocallyRunOperatorExtension extension =
            LocallyRunOperatorExtension.builder()
                    .withReconciler(GettingStartedReconciler.class)
                    .withAdditionalCRD(kafkaCrd())
                    .build();

    private String kafkaCrd() {
        try {
            File tempFile = File.createTempFile("kafkacrd", ".yaml");
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.writer().writeValue(tempFile, Crds.kafka());
            return tempFile.getAbsolutePath();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCRUDOperations() {
        var cr = extension.create(testResource());

        await().untilAsserted(() -> {
            var cm = extension.get(ConfigMap.class, RESOURCE_NAME);
            assertThat(cm).isNotNull();
            assertThat(cm.getData()).containsEntry(KEY, INITIAL_VALUE);
        });

        cr.getSpec().setValue(CHANGED_VALUE);
        cr = extension.replace(cr);

        await().untilAsserted(() -> {
            var cm = extension.get(ConfigMap.class, RESOURCE_NAME);
            assertThat(cm.getData()).containsEntry(KEY, CHANGED_VALUE);
        });

        extension.delete(cr);

        await().untilAsserted(() -> {
            var cm = extension.get(ConfigMap.class, RESOURCE_NAME);
            assertThat(cm).isNull();
        });
    }

    GettingStartedCustomResource testResource() {
        var resource = new GettingStartedCustomResource();
        resource.setMetadata(new ObjectMetaBuilder()
                .withName(RESOURCE_NAME)
                .build());
        resource.setSpec(new GettingStartedSpec());
        resource.getSpec().setValue(INITIAL_VALUE);
        return resource;
    }
}
