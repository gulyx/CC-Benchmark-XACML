package it.cnr.iasi.saks.cc.benchmark.xacml.tests.balana;

import org.w3c.dom.Document;
import org.wso2.balana.ParsingException;
import org.wso2.balana.ctx.AbstractRequestCtx;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.RequestCtxFactory;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.impl.FileBasedPolicyFinderModule;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

public class BalanaUtil {

	/**
	 * Loads a XACML request from a given path
	 *
	 * @param filePattern the base pattern of the request file path
	 * @param arguments   the variable parts in the filePatterns
	 * @return the XACML Request
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws FileNotFoundException
	 * @throws TransformerException
	 * @throws ParsingException 
	 */
	public static AbstractRequestCtx loadRequestByPath(String filePattern, Object... arguments) throws ParserConfigurationException,
			FileNotFoundException, SAXException, IOException, TransformerException, ParsingException {

		StringWriter writer = new StringWriter(0);
		String filePath = String.format(filePattern, arguments);

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setNamespaceAware(true);
			DocumentBuilder db = factory.newDocumentBuilder();
			Document doc = db.parse(new FileInputStream(filePath));
			DOMSource domSource = new DOMSource(doc);

			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}

//System.err.println("Request: "+ writer.toString().replaceAll(">\\s+<", "><"));		

		AbstractRequestCtx requestCtx = RequestCtxFactory.getFactory().getRequestCtx(writer.toString().replaceAll(">\\s+<", "><"));
		return requestCtx;
	}

	/**
	 * Loads a XACML response from a given path
	 *
	 * @param filePattern the base pattern of the request file path
	 * @param arguments   the variable parts in the filePatterns
	 * @return the XACML Request
	 * @return The ResponseCtx 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws FileNotFoundException 
	 * @throws ParsingException 
	 */
	public static ResponseCtx loadResponseByPath(String filePattern, Object... arguments) throws ParserConfigurationException, FileNotFoundException, SAXException, IOException, ParsingException {
		String filePath = String.format(filePattern, arguments);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringComments(true);
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		
		DocumentBuilder db = factory.newDocumentBuilder();
		Document doc = db.parse(new FileInputStream(filePath));

//try {
//StringWriter writer = new StringWriter(0);
//DOMSource domSource = new DOMSource(doc);
//StreamResult result = new StreamResult(writer);
//TransformerFactory tf = TransformerFactory.newInstance();
//Transformer transformer;
//transformer = tf.newTransformer();
//transformer.transform(domSource, result);
//System.err.println("Response: "+ writer.toString().replaceAll(">\\s+<", "><"));		
//} catch (TransformerException e) {
//	// TODO Auto-generated catch block
//	e.printStackTrace();
//}

		return ResponseCtx.getInstance(doc.getDocumentElement());
	}

	/**
	 * Loads a XACML policy from a given path
	 *
	 */
	public static PolicyFinderModule loadPolicyByPath(String filePattern, Object... arguments) {
		String filePath = String.format(filePattern, arguments);
//filePath="/home/gulyx/Documenti/Dottorato/CrossCoverage/EsperimentoXACML/CC-Benchmark-XACML/"+filePath;
        Set<String> policyLocations = new HashSet<String>();
        policyLocations.add(filePath);
        
        FileBasedPolicyFinderModule policyFinderModule = new FileBasedPolicyFinderModule(policyLocations);
        
        return policyFinderModule;
	}
	
//	public static PolicyFinder loadPolicyByPath(String filePattern, Object... arguments) {
//		String filePath = String.format(filePattern, arguments);
//		
//		PolicyFinder finder= new PolicyFinder();
//        Set<String> policyLocations = new HashSet<String>();
//        policyLocations.add(filePath);
//        
//        FileBasedPolicyFinderModule policyFinderModule = new FileBasedPolicyFinderModule(policyLocations);
//        Set<PolicyFinderModule> policyModules = new HashSet<PolicyFinderModule>();
//        policyModules.add(policyFinderModule);
//        finder.setModules(policyModules);
//        
//        return finder;
//	}


}
