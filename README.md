# CC-Benchmark-XACML

CrossCoverage Benchmark on XACML


Notes
----------
 * Remove the following dependency in order to enable again logging. It was disabled just to have smaller files during the massive runs for the experiments.
 * Double check why the CompareMatcher in both [BalanaQueryTests](src/test/java/it/cnr/iasi/saks/cc/benchmark/xacml/tests/balana/BalanaQueryTests.java) and [HerasafQueryTests](src/test/java/it/cnr/iasi/saks/cc/benchmark/xacml/tests/herasaf/HerasafQueryTests.java) sometimes do not behave as expected. Indeed the assertion were they are involved return NULL and thus the check fails. Even though it sould work for our case, the current vesion of the assert is not really correct because it should fail in case the 2 xml have the same elements but in a different order. In the code look for the sentence: "I do not understand why"
