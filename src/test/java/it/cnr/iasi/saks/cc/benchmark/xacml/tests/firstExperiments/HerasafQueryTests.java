package it.cnr.iasi.saks.cc.benchmark.xacml.tests.firstExperiments;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.herasaf.xacml.core.api.PDP;
import org.herasaf.xacml.core.api.UnorderedPolicyRepository;

import org.herasaf.xacml.core.simplePDP.SimplePDPFactory;
import org.herasaf.xacml.core.policy.Evaluatable;
import org.herasaf.xacml.core.context.ResponseMarshaller;
import org.herasaf.xacml.core.context.impl.RequestType;
import org.herasaf.xacml.core.context.impl.ResponseType;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;

import org.junit.Test;

public class HerasafQueryTests {

	private PDP herasafPDP;
	
	private static final String RESPONSE_FILE_PATTERN = "/conformanceTests/functionEvaluation/%sResponse.xml";
	private static final String REQUEST_FILE_PATTERN = "/conformanceTests/functionEvaluation/%sRequest.xml";
//	private static final String POLICY_FILE_PATTERN = "/conformanceTests/functionEvaluation/%sPolicy.xml";
	private static final String POLICY_FILE_PATTERN = "/xacml2.0-ct-v.0.4/%sPolicy.xml";

	
	public HerasafQueryTests() {
		this.herasafPDP = SimplePDPFactory.getSimplePDP();
	}
	
	public void deployPolicies(String testCase) throws Exception {
        List<Evaluatable> evals = new ArrayList<Evaluatable>();
        evals.add(HerasafUtil.loadPolicy(POLICY_FILE_PATTERN, testCase));
        UnorderedPolicyRepository deploymentRepo = (UnorderedPolicyRepository) this.herasafPDP.getPolicyRepository();
        deploymentRepo.deploy(evals);
    }

// Nella versione originale serviva perchè il PDP veniva instanziato con un before class
//    @AfterMethod
//    public void afterMethod() throws Exception {
//        UnorderedPolicyRepository deploymentRepo = (UnorderedPolicyRepository) this.herasafPDP.getPolicyRepository();
//        List<EvaluatableID> evaluatableIds = deploymentRepo.getDeployment().stream()
//                .map((evaluatable) -> evaluatable.getId()).collect(Collectors.toList());
//        deploymentRepo.undeploy(evaluatableIds);
//    }

    @Test
    public void testSingleUseCase() throws Exception {
        String testCase = "IIC223";
        testAttrRefConformance(testCase);
    }

//    @Test
    public void testAttrRefConformance(String testCase) throws Exception {
        deployPolicies(testCase);

        RequestType request = HerasafUtil.loadRequest(REQUEST_FILE_PATTERN, testCase);
        ResponseType expectedResponse = HerasafUtil.loadResponse(RESPONSE_FILE_PATTERN, testCase);

        ResponseType response = this.herasafPDP.evaluate(request);

        OutputStream responseOS = new ByteArrayOutputStream();
        ResponseMarshaller.marshal(response, responseOS);

        OutputStream expectedOS = new ByteArrayOutputStream();
        ResponseMarshaller.marshal(expectedResponse, expectedOS);

        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);
        XMLAssert.assertXMLEqual("Testcase: " + testCase + " failed!", expectedOS.toString(), responseOS.toString());

    }

}
