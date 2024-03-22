package it.cnr.iasi.saks.cc.benchmark.xacml.tests.firstExperiments;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.herasaf.xacml.core.SyntaxException;
import org.herasaf.xacml.core.context.RequestMarshaller;
import org.herasaf.xacml.core.context.ResponseMarshaller;
import org.herasaf.xacml.core.context.impl.RequestType;
import org.herasaf.xacml.core.context.impl.ResponseType;
import org.herasaf.xacml.core.policy.Evaluatable;
import org.herasaf.xacml.core.policy.PolicyMarshaller;

public class HerasafUtil {

	public static Evaluatable loadPolicyByPath(String filePattern, Object... arguments) throws SyntaxException, FileNotFoundException {
        String file = String.format(filePattern, arguments);
        InputStream is = new FileInputStream(file);
        return PolicyMarshaller.unmarshal(is);
    }

	public static Evaluatable loadPolicy(String filePattern, Object... arguments) throws SyntaxException {
        String file = String.format(filePattern, arguments);
        InputStream is = retreiveStaticClassLoader().getResourceAsStream(file);
        return PolicyMarshaller.unmarshal(is);
    }

    public static RequestType loadRequestByPath(String filePattern, Object... arguments) throws SyntaxException, FileNotFoundException {
        String file = String.format(filePattern, arguments);
        InputStream is = new FileInputStream(file);
        return RequestMarshaller.unmarshal(is);
    }

    public static RequestType loadRequest(String filePattern, Object... arguments) throws SyntaxException {
        String file = String.format(filePattern, arguments);
        InputStream is = retreiveStaticClassLoader().getResourceAsStream(file);
        return RequestMarshaller.unmarshal(is);
    }

    public static ResponseType loadResponseByPath(String filePattern, Object... arguments) throws SyntaxException, FileNotFoundException {
        String file = String.format(filePattern, arguments);
        InputStream is = new FileInputStream(file);
        return ResponseMarshaller.unmarshal(is);
    }

    public static ResponseType loadResponse(String filePattern, Object... arguments) throws SyntaxException {
        String file = String.format(filePattern, arguments);
        InputStream is = retreiveStaticClassLoader().getResourceAsStream(file);
        return ResponseMarshaller.unmarshal(is);
    }
    
    private ClassLoader retreiveClassLoader() {
		ClassLoader cl = this.getClass().getClassLoader();
		if (cl == null) {
			cl = ClassLoader.getSystemClassLoader();
		}
		return cl;
	}

    private static ClassLoader retreiveStaticClassLoader() {
		ClassLoader cl = HerasafUtil.class.getClassLoader();
		return cl;
	}
}
