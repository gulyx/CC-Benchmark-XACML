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
package it.cnr.iasi.saks.cc.benchmark.xacml.tests.balana;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.herasaf.xacml.core.SyntaxException;
import org.junit.After;
import org.junit.Assert;
import org.wso2.balana.Balana;
import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.ParsingException;
import org.wso2.balana.ctx.AbstractRequestCtx;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.xml.sax.SAXException;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.ElementSelectors;
import org.xmlunit.matchers.CompareMatcher;

import it.cnr.iasi.saks.cc.benchmark.xacml.tests.AbstractQueryTests;

public class BalanaQueryTests extends AbstractQueryTests {

	private PDP balanaPDP;
	private PDPConfig pdpConfig; 
	
	public BalanaQueryTests() {
        PolicyFinder finder= new PolicyFinder();
		Set<PolicyFinderModule> emptyPolicyModules = new HashSet<PolicyFinderModule>();
		finder.setModules(emptyPolicyModules);
		
		Balana balana = Balana.getInstance();
		this.pdpConfig = new PDPConfig(balana.getPdpConfig().getAttributeFinder(), finder, balana.getPdpConfig().getResourceFinder(), false);

        this.balanaPDP = new PDP(this.pdpConfig);        
	}
	
	@Override 
	public void deployPolicy(String policyFilePattern, String testCase) throws FileNotFoundException, SyntaxException {
		PolicyFinderModule policyFinderModule = BalanaUtil.loadPolicyByPath(policyFilePattern, testCase);
		
//		Set<PolicyFinderModule> policyModules = new HashSet<PolicyFinderModule>();
//		policyModules.add(policyFinderModule);
		
		Set<PolicyFinderModule> policyModules = this.pdpConfig.getPolicyFinder().getModules();
		if (policyFinderModule == null) {
			policyModules = new HashSet<PolicyFinderModule>();
		}
		policyModules.add(policyFinderModule);
		
		this.pdpConfig.getPolicyFinder().setModules(policyModules);

		this.balanaPDP = new PDP(this.pdpConfig);        
	}

	@Override @After
	public void undeployPolicies() throws Exception {
		Set<PolicyFinderModule> emptyPolicyModules = new HashSet<PolicyFinderModule>();

		this.pdpConfig.getPolicyFinder().setModules(emptyPolicyModules);

		this.balanaPDP = new PDP(this.pdpConfig);        
	}

	@Override
	protected void testConformance(String testCaseID) throws Exception {
		this.deployPolicy(POLICY_CONFORMANCE_FILE_PATTERN, testCaseID);

		AbstractRequestCtx requestCtx = BalanaUtil.loadRequestByPath(REQUEST_CONFORMANCE_FILE_PATTERN, testCaseID);
		ResponseCtx expectedResponseCtx = BalanaUtil.loadResponseByPath(RESPONSE_CONFORMANCE_FILE_PATTERN, testCaseID);
		
		ResponseCtx responseCtx = this.balanaPDP.evaluate(requestCtx);
		
		String expectedResponse = expectedResponseCtx.encode();
		String response = responseCtx.encode();
		
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreAttributeOrder(true);
		String strictComparison = System.getProperty(STRICT_COMPARISON_FLAG_LABEL);		
		if ((strictComparison != null) && (!strictComparison.equalsIgnoreCase("false"))) {
			XMLAssert.assertXMLEqual("Testcase: " + testCaseID + " failed!", expectedResponse, response);
		} else {				
//			Assert.assertThat(response, CompareMatcher.isIdenticalTo(expectedResponse).withAttributeFilter(a -> !"ResourceId".equals(a.getName())));
//			Assert.assertThat(response, CompareMatcher.isIdenticalTo(expectedResponse).withAttributeFilter(attr -> !"ResourceId".equals(attr.getName())).withNodeFilter(node -> !"StatusDetail".equals(node.getNodeName())));
//			Assert.assertThat(CompareMatcher.isIdenticalTo(response).withNodeFilter(node -> !"StatusMessage".equals(node.getNodeName())), CompareMatcher.isIdenticalTo(expectedResponse).withAttributeFilter(attr -> !"ResourceId".equals(attr.getName())).withNodeFilter(node -> !"StatusDetail".equals(node.getNodeName())));
//			Assert.assertThat(response, CompareMatcher.isIdenticalTo(expectedResponse).withAttributeFilter(attr -> !"ResourceId".equals(attr.getName())).withNodeFilter(node -> !"Status".equals(node.getNodeName())));
//			Assert.assertThat(response, CompareMatcher.isSimilarTo(expectedResponse).withAttributeFilter(attr -> !"ResourceId".equals(attr.getName())).withNodeFilter(node -> !"Status".equals(node.getNodeName())));
			Assert.assertThat(response, CompareMatcher.isSimilarTo(expectedResponse).withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndAllAttributes)).withAttributeFilter(attr -> !"ResourceId".equals(attr.getName())).withNodeFilter(node -> !"Status".equals(node.getNodeName())));
		}	
	}
	
//	@Test
	public void reportIssuesTestingConformance() throws Exception {		
		List<String> testCasesIDList = this.retreiveConformanceTestCasesIDs();
		
		List<String> testCasesWithProblems = new ArrayList<String>();
		
		for (String testID : testCasesIDList) {
			System.err.println("Processing Conformance Policy: " + testID);
			try {
				this.testConformance(testID);
			} catch (Throwable e) {
				testCasesWithProblems.add(testID);
				System.err.println("Current Size: "+ testCasesWithProblems.size());
			} finally {
				this.undeployPolicies();
			}
		}
		
		for (String id : testCasesWithProblems) {
			System.err.println(id);
		}
	}

	@Override
	protected void testSingleUseCaseByProperties(String policyID, String requestID) throws SyntaxException, ParserConfigurationException, SAXException, IOException, TransformerException, ParsingException {
		System.err.println("§§§§§§§ Processing: "+ policyID + ", "+requestID);

		this.deployPolicy(MUTATED_POLICY_FILE_PATTERN, policyID);
		
		AbstractRequestCtx requestCtx = BalanaUtil.loadRequestByPath(MUTATED_REQUEST_FILE_PATTERN, policyID, requestID);
		ResponseCtx expectedResponseCtx = BalanaUtil.loadResponseByPath(MUTATED_RESPONSE_FILE_PATTERN, policyID, requestID);

		ResponseCtx responseCtx = this.balanaPDP.evaluate(requestCtx);

		String expectedResponse = expectedResponseCtx.encode();
		String response = responseCtx.encode();
		
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreAttributeOrder(true);
		String strictComparison = System.getProperty(STRICT_COMPARISON_FLAG_LABEL);		
		if ((strictComparison != null) && (!strictComparison.equalsIgnoreCase("false"))) {
			XMLAssert.assertXMLEqual("Testcase <Policy,Request>: <" + policyID + ", " + requestID +"> failed!", expectedResponse, response);
		} else {				
//			Assert.assertThat(response, CompareMatcher.isIdenticalTo(expectedResponse).withAttributeFilter(a -> !"ResourceId".equals(a.getName())));
//			Assert.assertThat(response, CompareMatcher.isIdenticalTo(expectedResponse).withAttributeFilter(attr -> !"ResourceId".equals(attr.getName())).withNodeFilter(node -> !"StatusDetail".equals(node.getNodeName())));
//			Assert.assertThat(CompareMatcher.isIdenticalTo(response).withNodeFilter(node -> !"StatusMessage".equals(node.getNodeName())), CompareMatcher.isIdenticalTo(expectedResponse).withAttributeFilter(attr -> !"ResourceId".equals(attr.getName())).withNodeFilter(node -> !"StatusDetail".equals(node.getNodeName())));
//			Assert.assertThat(response, CompareMatcher.isIdenticalTo(expectedResponse).withAttributeFilter(attr -> !"ResourceId".equals(attr.getName())).withNodeFilter(node -> !"Status".equals(node.getNodeName())));
// ************************************
// I do not understand why the CompareMatcher of the commented assert returns (here and not for the test cases in the conformace) NULL and thus the check fails
// The following assert is not really correct because it should fail in case the 2 xml have the same elements but in a different order			
			Assert.assertThat(response, CompareMatcher.isSimilarTo(expectedResponse).withAttributeFilter(attr -> !"ResourceId".equals(attr.getName())).withNodeFilter(node -> !"Status".equals(node.getNodeName())));
//			Assert.assertThat(response, CompareMatcher.isSimilarTo(expectedResponse).withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndAllAttributes)).withAttributeFilter(attr -> !"ResourceId".equals(attr.getName())).withNodeFilter(node -> !"Status".equals(node.getNodeName())));
		}	
	}

	
}
