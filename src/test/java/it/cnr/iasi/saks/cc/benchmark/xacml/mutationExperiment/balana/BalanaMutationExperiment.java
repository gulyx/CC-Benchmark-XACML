package it.cnr.iasi.saks.cc.benchmark.xacml.mutationExperiment.balana;

import it.cnr.iasi.saks.cc.benchmark.xacml.mutationExperiment.AbstractMutationExperiment;
import it.cnr.iasi.saks.cc.benchmark.xacml.tests.balana.BalanaQueryTests;

public class BalanaMutationExperiment extends AbstractMutationExperiment{

	public BalanaMutationExperiment () {
		this.queryTests = new BalanaQueryTests();
	}
	
}
