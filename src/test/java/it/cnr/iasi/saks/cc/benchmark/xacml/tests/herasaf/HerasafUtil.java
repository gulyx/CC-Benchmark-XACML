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
package it.cnr.iasi.saks.cc.benchmark.xacml.tests.herasaf;

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
