package it.cnr.iasi.saks.cc.benchmark.xacml.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.herasaf.xacml.core.SyntaxException;
import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractQueryTests {

	protected static final String STRICT_COMPARISON_FLAG_LABEL = "strictCompare";

	protected static final String SINGLE_TEST_CASE_PROPERTY_LABEL = "testCaseID";

	protected static final String COMMON_RELATIVE_PATH = "src/test/XACMLResources/";
	
//	protected static final String XACML_CONFORMANCE_TS_RELATIVE_PATH = COMMON_RELATIVE_PATH + "xacml2.0-ct-v.0.4/";
	protected static final String XACML_CONFORMANCE_TS_RELATIVE_PATH = COMMON_RELATIVE_PATH + "xacml2.0-ct-v.0.4-smaller/";

//	protected static final String RESPONSE_FILE_PATTERN = "/xacml2.0-ct-v.0.4/%sResponse.xml";
//	protected static final String REQUEST_FILE_PATTERN = "/xacml2.0-ct-v.0.4/%sRequest.xml";
//	protected static final String POLICY_FILE_PATTERN = "/xacml2.0-ct-v.0.4/%sPolicy.xml";

	protected static final String RESPONSE_TAIL_PATTERN = "Response.xml";
	protected static final String REQUEST_TAIL_PATTERN = "Request.xml";
	protected static final String POLICY_TAIL_PATTERN = "Policy.xml";

	protected static final String RESPONSE_CONFORMANCE_FILE_PATTERN = XACML_CONFORMANCE_TS_RELATIVE_PATH + "%s" + RESPONSE_TAIL_PATTERN;
	protected static final String REQUEST_CONFORMANCE_FILE_PATTERN = XACML_CONFORMANCE_TS_RELATIVE_PATH + "%s" + REQUEST_TAIL_PATTERN;
	protected static final String POLICY_CONFORMANCE_FILE_PATTERN = XACML_CONFORMANCE_TS_RELATIVE_PATH + "%s" + POLICY_TAIL_PATTERN;

// *********************************************************************************
	
	protected static final String IDS_PROPERTY_LABEL = "idsList";
	protected static final String IDS_SEPARATOR = ",";
	protected static final String POLICY_REQUEST_SEPARATOR = ":";

	protected static final String MUTATED_POLICY_PROPERTY_LABEL = "policyID";
	protected static final String MUTATED_REQUEST_PROPERTY_LABEL = "requestID";

	protected static final String XACML_MUTATED_TS_RELATIVE_PATH = COMMON_RELATIVE_PATH + "PoliciesRequestsResponses/";

	protected static final String MUTATED_RESPONSE_HEAD_PATTERN = "response_";
	protected static final String MUTATED_TAIL_PATTERN = ".xml";

	protected static final String MUTATED_RESPONSE_FILE_PATTERN = XACML_MUTATED_TS_RELATIVE_PATH + "Responses/SomeResponses/%s/" + MUTATED_RESPONSE_HEAD_PATTERN + "%s" + MUTATED_TAIL_PATTERN;
	protected static final String MUTATED_REQUEST_FILE_PATTERN = XACML_MUTATED_TS_RELATIVE_PATH + "Requests/SomeRequest/%s/Multiple_Comb/%s" + MUTATED_TAIL_PATTERN;
	protected static final String MUTATED_POLICY_FILE_PATTERN = XACML_MUTATED_TS_RELATIVE_PATH + "xacml_Mutated_Policies/SomeRequest/%s" + MUTATED_TAIL_PATTERN;
	
	@Test
	public void testConformance() throws Exception {
		List<String> testCasesIDList = this.retreiveConformanceTestCasesIDs();
		for (String testID : testCasesIDList) {
			System.err.println("Processing Conformance Policy: " + testID);

			this.testConformance(testID);

			this.undeployPolicies();
		}
	}

	protected List<String> retreiveConformanceTestCasesIDs() throws FileNotFoundException {
		List<String> testCasesIDList = new ArrayList<String>();

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

	protected List<String> retreiveMutatedRequestsIDsWithResponse(String policyID) throws FileNotFoundException {
		List<String> mutatedRequestsIDList = new ArrayList<String>();
		
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
	
	public abstract void deployPolicy(String policyFilePattern, String testCase) throws FileNotFoundException, SyntaxException;

	public abstract void undeployPolicies() throws Exception;

	protected abstract void testConformance(String testCaseID) throws Exception;

//    @Test
	public void testSingleConformanceUseCase() throws Exception {
		String testCase = "IIC223";
		this.testConformance(testCase);
	}

	@Test
	public void testSingleConformanceUseCaseByProperty() throws Exception {
		String testCase = System.getProperty(SINGLE_TEST_CASE_PROPERTY_LABEL);
		Assert.assertNotNull("The system's property \'" + SINGLE_TEST_CASE_PROPERTY_LABEL + "\' is null or not set", testCase); 
		this.testConformance(testCase);
	}

	@Test
	public void testUseCasesByProperty() throws Exception {
		String idsListString = System.getProperty(IDS_PROPERTY_LABEL);
		Assert.assertNotNull("The system's property \'" + IDS_PROPERTY_LABEL + "\' is null or not set", idsListString); 		
		
		String[] idsList = idsListString.split(IDS_SEPARATOR);
		Assert.assertNotNull("The system's property \'" + IDS_PROPERTY_LABEL + "\' is null or not properly set", idsList); 
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

	@Test
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
////String policyMarker="IIA006Policy_ANR_Mut_00000000"; int start=10; int end=57;
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
//String policyMarker="IIA006Policy_ANR_Mut_00000000"; int start=10; int end=57;
////String policyMarker="IIIF006Policy_CCF_Mut_00000000";  int start=38; int end=38;
//
//for (int id = start; id <= end; id++) {	
//	policyID=policyMarker+id;
//
//	List<String> mutatedRequestsWithResponseIDList = this.retreiveMutatedRequestsIDsWithResponse(policyID);
//	for (String mutatedRequestsWithResponseID : mutatedRequestsWithResponseIDList) {
//		try {
//			processed++;
////			System.err.println("§§§§§§§ Processing: "+ policyID + ", "+mutatedRequestsWithResponseID);
//
//			this.testSingleUseCaseByProperties(policyID, mutatedRequestsWithResponseID);
// 		} catch (AssertionError e) {
//// 		} catch (Throwable e) {
//			counter++;
//		}	
//		this.undeployPolicies();
//	}
//}		
//		System.err.println("Failed : "+counter+" over : "+processed);
//********************************************************
//********************************************************		
	}

	protected void testSingleUseCaseByProperties(String policyID) throws Exception {
		String requestID = System.getProperty(MUTATED_REQUEST_PROPERTY_LABEL);
		Assert.assertNotNull("The system's property \'" + MUTATED_REQUEST_PROPERTY_LABEL + "\' is null or not set", requestID); 
		
		this.testSingleUseCaseByProperties(policyID, requestID);
	}

	protected abstract void testSingleUseCaseByProperties(String policyID, String requestID)  throws Exception;

}
