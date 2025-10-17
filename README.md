# Generated Project Skeleton

A simple operator that copies the value in a spec to a ConfigMap. 

Initial commit generated using:

```
 mvn io.javaoperatorsdk:bootstrapper:5.1.4:create   -DprojectGroupId=org.acme   -DprojectArtifactId=getting-started
```

per the quickstart documentation.

Then in https://github.com/robobario/josdk-reproducer/commit/f20cd685efe6efe32280371d49aa7d991c465063 I've demonstrated a
behaviour I found surprising. The additional CRD added to the `LocallyRunOperatorExtension` is not applied when the
Reconciler is registered and its event sources are prepared.

In `LocallyRunOperatorExtension#before` it appears we apply CRDs configured this way after registration. Whereas the
class based method `.withAdditionalCustomResourceDefinition(MyResource.class)` would be applied before event source
preparation.

Then in https://github.com/robobario/josdk-reproducer/commit/498e9d4bee8ee4f15e1216e9bd4b8dade6c077e0 Ive demonstrated a workaround.
Using the fabric8 APIs directly to create and destroy the CRD.

Running `mvn verify` yields:

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running org.acme.GettingStartedReconcilerIntegrationTest
... snip ...
[ERROR] Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 8.408 s <<< FAILURE! -- in org.acme.GettingStartedReconcilerIntegrationTest
[ERROR] org.acme.GettingStartedReconcilerIntegrationTest.testCRUDOperations -- Time elapsed: 7.507 s <<< ERROR!
java.lang.RuntimeException: kafka-strimzi.io is not available
	at org.acme.GettingStartedReconciler.prepareEventSources(GettingStartedReconciler.java:21)
	at io.javaoperatorsdk.operator.processing.Controller.initAndRegisterEventSources(Controller.java:244)
	at io.javaoperatorsdk.operator.processing.Controller.<init>(Controller.java:110)
	at io.javaoperatorsdk.operator.Operator.register(Operator.java:252)
	at io.javaoperatorsdk.operator.junit.LocallyRunOperatorExtension.before(LocallyRunOperatorExtension.java:289)
	at io.javaoperatorsdk.operator.junit.AbstractOperatorExtension.beforeEachImpl(AbstractOperatorExtension.java:137)
	at io.javaoperatorsdk.operator.junit.AbstractOperatorExtension.beforeEach(AbstractOperatorExtension.java:79)
... snip ...
[INFO] Running org.acme.GettingStartedReconcilerIntegrationFixedTest
... snip ...
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 7.908 s -- in org.acme.GettingStartedReconcilerIntegrationFixedTest
```
