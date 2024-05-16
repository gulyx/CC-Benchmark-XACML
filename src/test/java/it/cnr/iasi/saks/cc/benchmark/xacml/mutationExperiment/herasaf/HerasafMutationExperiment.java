package it.cnr.iasi.saks.cc.benchmark.xacml.mutationExperiment.herasaf;

import it.cnr.iasi.saks.cc.benchmark.xacml.mutationExperiment.AbstractMutationExperiment;
import it.cnr.iasi.saks.cc.benchmark.xacml.tests.herasaf.HerasafQueryTests;

public class HerasafMutationExperiment extends AbstractMutationExperiment{

	public HerasafMutationExperiment() {
		this.queryTests = new HerasafQueryTests(); 
	}
	
}
