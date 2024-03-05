package it.cnr.iasi.saks.cc.benchmark.xacml.tests.firstExperiments;

//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertThat;
import org.junit.Assert;
import org.junit.Test;
import org.junit.After;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.io.OutputStream;

import org.herasaf.xacml.core.SyntaxException;
import org.herasaf.xacml.core.WritingException;
import org.herasaf.xacml.core.api.PDP;
import org.herasaf.xacml.core.api.UnorderedPolicyRepository;

import org.herasaf.xacml.core.simplePDP.SimplePDPFactory;
import org.herasaf.xacml.core.policy.Evaluatable;
import org.herasaf.xacml.core.policy.EvaluatableID;
import org.herasaf.xacml.core.context.ResponseMarshaller;
import org.herasaf.xacml.core.context.impl.RequestType;
import org.herasaf.xacml.core.context.impl.ResponseType;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;

import org.xml.sax.SAXException;
import org.xmlunit.matchers.CompareMatcher;

public class HerasafQueryTests {

	private PDP herasafPDP;

	private static final String STRICT_COMPARISON_FLAG_LABEL = "strictCompare";

	private static final String SINGLE_TEST_CASE_PROPERTY_LABEL = "testCaseID";

	private static final String COMMON_RELATIVE_PATH = "src/test/XACMLResources/";
	
//	private static final String XACML_CONFORMANCE_TS_RELATIVE_PATH = COMMON_RELATIVE_PATH + "xacml2.0-ct-v.0.4/";
	private static final String XACML_CONFORMANCE_TS_RELATIVE_PATH = COMMON_RELATIVE_PATH + "xacml2.0-ct-v.0.4-smaller/";

	//	private static final String RESPONSE_FILE_PATTERN = "/xacml2.0-ct-v.0.4/%sResponse.xml";
//	private static final String REQUEST_FILE_PATTERN = "/xacml2.0-ct-v.0.4/%sRequest.xml";
//	private static final String POLICY_FILE_PATTERN = "/xacml2.0-ct-v.0.4/%sPolicy.xml";

	private static final String RESPONSE_TAIL_PATTERN = "Response.xml";
	private static final String REQUEST_TAIL_PATTERN = "Request.xml";
	private static final String POLICY_TAIL_PATTERN = "Policy.xml";

	private static final String RESPONSE_CONFORMANCE_FILE_PATTERN = XACML_CONFORMANCE_TS_RELATIVE_PATH + "%s" + RESPONSE_TAIL_PATTERN;
	private static final String REQUEST_CONFORMANCE_FILE_PATTERN = XACML_CONFORMANCE_TS_RELATIVE_PATH + "%s" + REQUEST_TAIL_PATTERN;
	private static final String POLICY_CONFORMANCE_FILE_PATTERN = XACML_CONFORMANCE_TS_RELATIVE_PATH + "%s" + POLICY_TAIL_PATTERN;

// *********************************************************************************
	
	private static final String IDS_PROPERTY_LABEL = "idsList";
	private static final String IDS_SEPARATOR = ",";
	private static final String POLICY_REQUEST_SEPARATOR = ":";

	private static final String MUTATED_POLICY_PROPERTY_LABEL = "policyID";
	private static final String MUTATED_REQUEST_PROPERTY_LABEL = "requestID";

	private static final String XACML_MUTATED_TS_RELATIVE_PATH = COMMON_RELATIVE_PATH + "PoliciesRequestsResponses/";

	private static final String MUTATED_RESPONSE_HEAD_PATTERN = "response_";
	private static final String MUTATED_TAIL_PATTERN = ".xml";

	private static final String MUTATED_RESPONSE_FILE_PATTERN = XACML_MUTATED_TS_RELATIVE_PATH + "Responses/SomeResponses/%s/" + MUTATED_RESPONSE_HEAD_PATTERN + "%s" + MUTATED_TAIL_PATTERN;
	private static final String MUTATED_REQUEST_FILE_PATTERN = XACML_MUTATED_TS_RELATIVE_PATH + "Requests/SomeRequest/%s/Multiple_Comb/%s" + MUTATED_TAIL_PATTERN;
	private static final String MUTATED_POLICY_FILE_PATTERN = XACML_MUTATED_TS_RELATIVE_PATH + "xacml_Mutated_Policies/SomeRequest/%s" + MUTATED_TAIL_PATTERN;

	public HerasafQueryTests() {
		this.herasafPDP = SimplePDPFactory.getSimplePDP();
	}

	public void deployPolicies(String policyFilePattern, String testCase) throws FileNotFoundException, SyntaxException {
		List<Evaluatable> evals = new ArrayList<Evaluatable>();
		evals.add(HerasafUtil.loadPolicyByPath(policyFilePattern, testCase));
		UnorderedPolicyRepository deploymentRepo = (UnorderedPolicyRepository) this.herasafPDP.getPolicyRepository();
		deploymentRepo.deploy(evals);
	}

	@After
	public void undeployPolicies() throws Exception {
		UnorderedPolicyRepository deploymentRepo = (UnorderedPolicyRepository) this.herasafPDP.getPolicyRepository();
		List<EvaluatableID> evaluatableIds = deploymentRepo.getDeployment().stream()
				.map((evaluatable) -> evaluatable.getId()).collect(Collectors.toList());
		deploymentRepo.undeploy(evaluatableIds);
	}

	@Test
	public void testUseCasesByProperty() throws Exception {
		String[] idsList = System.getProperty(IDS_PROPERTY_LABEL).split(IDS_SEPARATOR);
		Assert.assertNotNull("The system's property \'" + IDS_PROPERTY_LABEL + "\' is null or not set", idsList); 
		boolean processedAtLeastOnce = false;

		for (String id : idsList) {
			if (id != "") {
				String policyID = id.split(POLICY_REQUEST_SEPARATOR)[0];
				String requestID = id.split(POLICY_REQUEST_SEPARATOR)[1];

				System.setProperty(MUTATED_POLICY_PROPERTY_LABEL,policyID);
				System.setProperty(MUTATED_REQUEST_PROPERTY_LABEL,requestID);
			
				this.testSingleUseCaseByProperties();
				processedAtLeastOnce = true;
				this.undeployPolicies();
			}
		}
		
		Assert.assertTrue("The system's property \'" + IDS_PROPERTY_LABEL + "\' is wrongly set", processedAtLeastOnce);
	}

//	@Test
	public void testSingleUseCaseByProperties() throws Exception {
		String policyID = System.getProperty(MUTATED_POLICY_PROPERTY_LABEL);

//********************************************************
//********************************************************
		Assert.assertNotNull("The system's property \'" + MUTATED_POLICY_PROPERTY_LABEL + "\' is null or not set", policyID); 

		this.testSingleUseCaseByProperties(policyID);
//********************************************************
//int counter=0;
//int processed=0;
//
////String policyMarker="IIA001Policy_ANR_Mut_00000000"; int start=10; int end=10;
////String policyMarker="IIA001Policy_ANR_Mut_00000000"; int start=10; int end=33;
//String policyMarker="IIA006Policy_ANR_Mut_00000000"; int start=10; int end=57;
////String policyMarker="IIB003Policy_CCF_Mut_00000000"; int start=16; int end=23;
////String policyMarker="IIC034Policy_CCF_Mut_00000000"; int start=18; int end=29;
////String policyMarker="IIA001Policy_ANR_Mut_00000000"; int start=10; int end=33;
////String policyMarker="IIA001Policy_CCF_Mut_00000000"; int start=34; int end=34;
////String policyMarker="IIA002Policy_ANR_Mut_00000000"; int start=10; int end=33;
////String policyMarker="IIA002Policy_CCF_Mut_00000000"; /* int start=34; */ int start=39; int end=57;
////String policyMarker="IIA003Policy_ANR_Mut_00000000"; int start=10; int end=33;
////String policyMarker="IIA003Policy_CCF_Mut_00000000"; int start=34; int end=34;
////String policyMarker="IIA005Policy_ANR_Mut_00000000"; int start=10; int end=33;
////String policyMarker="IIA005Policy_CCF_Mut_00000000"; /* int start=34; */ int start=39; int end=56;
////String policyMarker="IIA006Policy_ANR_Mut_00000000"; int start=10; int end=57;
//
//for (int id = start; id <= end; id++) {	
//	policyID=policyMarker+id;
//
//	List<String> mutatedRequestsWithResponseIDList = this.retreiveMutatedRequestsIDsWithResponse(policyID);
//	for (String mutatedRequestsWithResponseID : mutatedRequestsWithResponseIDList) {
////		try {
//			processed++;
////			System.err.println("§§§§§§§ Processing: "+ policyID + ", "+mutatedRequestsWithResponseID);
//
//			this.testSingleUseCaseByProperties(policyID, mutatedRequestsWithResponseID);
//// 		} catch (AssertionError e) {
////			counter++;
////		}	
//		this.undeployPolicies();
//	}
//}		
//		System.err.println("Failed : "+counter+" over : "+processed);
//********************************************************
//********************************************************		
	}

	private void testSingleUseCaseByProperties(String policyID) throws Exception {
		String requestID = System.getProperty(MUTATED_REQUEST_PROPERTY_LABEL);
		Assert.assertNotNull("The system's property \'" + MUTATED_REQUEST_PROPERTY_LABEL + "\' is null or not set", requestID); 
		
		this.testSingleUseCaseByProperties(policyID, requestID);
	}

	private void testSingleUseCaseByProperties(String policyID, String requestID) throws SyntaxException, WritingException, SAXException, IOException {
//policyID="IIA006Policy_ANR_Mut_0000000010";
//requestID="014521_4405_4409_4412_Null";
		
		System.err.println("§§§§§§§ Processing: "+ policyID + ", "+requestID);

		this.deployPolicies(MUTATED_POLICY_FILE_PATTERN, policyID);

		RequestType request = HerasafUtil.loadRequestByPath(MUTATED_REQUEST_FILE_PATTERN, policyID, requestID);
		ResponseType expectedResponse = HerasafUtil.loadResponseByPath(MUTATED_RESPONSE_FILE_PATTERN, policyID, requestID);

		ResponseType response = this.herasafPDP.evaluate(request);

		OutputStream responseOS = new ByteArrayOutputStream();
		ResponseMarshaller.marshal(response, responseOS);

		OutputStream expectedOS = new ByteArrayOutputStream();
		ResponseMarshaller.marshal(expectedResponse, expectedOS);

		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreAttributeOrder(true);

//System.err.println("expected: " + expectedOS.toString());
//System.err.println("response: " + responseOS.toString());

		String strictComparison = System.getProperty(STRICT_COMPARISON_FLAG_LABEL);		
		if ((strictComparison != null) && (!strictComparison.equalsIgnoreCase("false"))) {
			XMLAssert.assertXMLEqual("Testcase <Policy,Request>: <" + policyID + ", " + requestID +"> failed!", expectedOS.toString(), responseOS.toString());
		} else {				
//			Assert.assertThat(responseOS.toString(), CompareMatcher.isIdenticalTo(expectedOS.toString()).withAttributeFilter(a -> !"ResourceId".equals(a.getName())));
//			Assert.assertThat(responseOS.toString(), CompareMatcher.isIdenticalTo(expectedOS.toString()).withAttributeFilter(attr -> !"ResourceId".equals(attr.getName())).withNodeFilter(node -> !"StatusDetail".equals(node.getNodeName())));
//			Assert.assertThat(CompareMatcher.isIdenticalTo(responseOS.toString()).withNodeFilter(node -> !"StatusMessage".equals(node.getNodeName())), CompareMatcher.isIdenticalTo(expectedOS.toString()).withAttributeFilter(attr -> !"ResourceId".equals(attr.getName())).withNodeFilter(node -> !"StatusDetail".equals(node.getNodeName())));
			Assert.assertThat(responseOS.toString(), CompareMatcher.isIdenticalTo(expectedOS.toString()).withAttributeFilter(attr -> !"ResourceId".equals(attr.getName())).withNodeFilter(node -> !"Status".equals(node.getNodeName())));
		}	
	}

	//    @Test
	public void testSingleConformanceUseCase() throws Exception {
		String testCase = "IIC223";
		this.testConformance(testCase);
	}

//	@Test
	public void testSingleConformanceUseCaseByProperty() throws Exception {
		String testCase = System.getProperty(SINGLE_TEST_CASE_PROPERTY_LABEL);
		Assert.assertNotNull("The system's property \'" + SINGLE_TEST_CASE_PROPERTY_LABEL + "\' is null or not set", testCase); 
		this.testConformance(testCase);
	}

	@Test
	public void testConformance() throws Exception {
		List<String> testCasesIDList = this.retreiveConformanceTestCasesIDs();
		for (String testID : testCasesIDList) {
			System.err.println("Processing Conformance Policy: " + testID);

			this.testConformance(testID);

			this.undeployPolicies();
		}
	}

	private void testConformance(String testCase) throws Exception {
		this.deployPolicies(POLICY_CONFORMANCE_FILE_PATTERN, testCase);

		RequestType request = HerasafUtil.loadRequestByPath(REQUEST_CONFORMANCE_FILE_PATTERN, testCase);
		ResponseType expectedResponse = HerasafUtil.loadResponseByPath(RESPONSE_CONFORMANCE_FILE_PATTERN, testCase);

		ResponseType response = this.herasafPDP.evaluate(request);

		OutputStream responseOS = new ByteArrayOutputStream();
		ResponseMarshaller.marshal(response, responseOS);

		OutputStream expectedOS = new ByteArrayOutputStream();
		ResponseMarshaller.marshal(expectedResponse, expectedOS);

		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreAttributeOrder(true);
		XMLAssert.assertXMLEqual("Testcase: " + testCase + " failed!", expectedOS.toString(), responseOS.toString());

	}

	private List<String> retreiveConformanceTestCasesIDs() throws FileNotFoundException {
		List<String> testCasesIDList = new ArrayList();

		File conformanceTSDir = new File(XACML_CONFORMANCE_TS_RELATIVE_PATH);
		if (conformanceTSDir.isDirectory()) {
			FilenameFilter policyFilter = (dir, name) -> name.matches(".*" + POLICY_TAIL_PATTERN);
			for (String testCaseFileName : conformanceTSDir.list(policyFilter)) {
				String testCaseID = testCaseFileName.replace(POLICY_TAIL_PATTERN, "");
				testCasesIDList.add(testCaseID);
			}
		}
		return testCasesIDList;
	}

	private List<String> retreiveMutatedRequestsIDsWithResponse(String policyID) throws FileNotFoundException {
		List<String> mutatedRequestsIDList = new ArrayList();
		
		String validResponsesDirName = String.format(XACML_MUTATED_TS_RELATIVE_PATH + "Responses/SomeResponses/%s", policyID);		
		File validResponsesDir = new File(validResponsesDirName);
		if (validResponsesDir.isDirectory()) {
			FilenameFilter policyFilter = (dir, name) -> name.matches(MUTATED_RESPONSE_HEAD_PATTERN + ".*" + MUTATED_TAIL_PATTERN);
			for (String responseFileName : validResponsesDir.list(policyFilter)) {
				String mutatedRequestID = responseFileName.replace(MUTATED_RESPONSE_HEAD_PATTERN , "").replace(MUTATED_TAIL_PATTERN, "");
				mutatedRequestsIDList.add(mutatedRequestID);
			}
		}
		return mutatedRequestsIDList;
	}
}
