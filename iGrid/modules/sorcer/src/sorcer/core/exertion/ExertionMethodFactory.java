/*
 * Copyright 2010 the original author or authors.
 * Copyright 2010 SorcerSoft.org.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sorcer.core.exertion;

import java.net.MalformedURLException;
import java.rmi.server.RMIClassLoader;
import java.util.List;

import sorcer.core.signature.MethodFactory;
import sorcer.core.signature.NetSignature;
import sorcer.service.Signature;
import sorcer.service.SignatureException;

/**
 * This class creates instances of appropriate subclasses of
 * ServiceMethod. The appropriate subclass is determined by calling the
 * exertion signatures.
 */
public class ExertionMethodFactory implements MethodFactory {
	private static ExertionMethodFactory factory;

	private ExertionMethodFactory() {
		// do nothing
	}

	public static ExertionMethodFactory getFactory() {
		if (factory == null)
			factory = new ExertionMethodFactory();

		return factory;
	}

	/**
	 * This method returns an instnace of the appropriate subclass of
	 * RemmoteServiceMethod as determined from information provided by the given
	 * ServiceMethod instance.
	 * 
	 * @param method
	 *            The remote method that will be used to perform
	 *            EntryServiceTask execution
	 */
	public Signature createMethod(Signature method) throws SignatureException {
		if (method instanceof NetSignature)
			return method;
		String agentClass = ((NetSignature) method).getAgentClass();
		try {
			if (agentClass != null)
				return getRemoteMethod(method);
		} catch (Throwable ex) {
			ex.printStackTrace();
			throw new SignatureException(
					"Failed to create SORCER mobile method: " + method
							+ " as agent: " + agentClass);
		}
		return method;
	}

	private NetSignature getRemoteMethod(Signature method)
			throws MalformedURLException, InstantiationException,
			ClassNotFoundException, IllegalAccessException {
		Class methodClass = RMIClassLoader.loadClass(
				((NetSignature) method).getAgentCodebase(),
				((NetSignature) method).getAgentClass());
		NetSignature rfm = (NetSignature) methodClass
				.newInstance();
		rfm.copySignature(method);
		return rfm;
	}

	private NetSignature getRemoteMethod(NetSignature method,
			String codebase, String className) throws MalformedURLException,
			InstantiationException, ClassNotFoundException,
			IllegalAccessException {
		Class methodClass = RMIClassLoader.loadClass(codebase, className);
		NetSignature rfm = (NetSignature) methodClass
				.newInstance();
		rfm.copySignature(method);
		return rfm;
	}

	public List<Signature> createMethods(List<Signature> signatures)
			throws SignatureException {
		for (int i = 0; i < signatures.size(); i++)
			signatures.set(i, createMethod(signatures.get(i)));

		return signatures;
	}
}
