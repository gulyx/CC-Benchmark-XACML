/* 
 * This file is part of the CC-Benchmark-XACML project.
 * 
 * CC-Benchmark-XACML is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CC-Benchmark-XACML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with CC-Benchmark-XACML.  If not, see <https://www.gnu.org/licenses/>
 *
 */
package it.cnr.iasi.saks.cc.benchmark.xacml.mutationExperiment;

import org.junit.Test;

import it.cnr.iasi.saks.cc.benchmark.xacml.tests.AbstractQueryTests;

public abstract class AbstractMutationExperiment {
	protected AbstractQueryTests queryTests;
		
	@Test
	public void testConformance() throws Exception {
		this.queryTests.testConformance();
	}
	
	@Test
	public void testUseCasesByProperty() throws Exception {
		this.queryTests.testUseCasesByProperty();
	}
	

}
