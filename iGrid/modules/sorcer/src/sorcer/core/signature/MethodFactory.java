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

package sorcer.core.signature;

import java.util.List;

import sorcer.service.Signature;
import sorcer.service.SignatureException;

/**
 * This interface must be implemented by all factory classes used to create
 * instances of subclasses of {@link NetSignature}. These subclasses provide
 * implementations that can be inserted into service providers to override their
 * own out-of-date implementation.
 */
public interface MethodFactory {
	/**
	 * This method returns an instance of the appropriate subclass of
	 * ServiceSignature as determined from information provided by the given
	 * instance of Signature.
	 * 
	 * @param signature
	 *            The signature that will be used to perform the associated
	 *            exertion execution.
	 */
	public Signature createMethod(Signature signature)
			throws SignatureException;

	public List<Signature> createMethods(List<Signature> signatures)
			throws SignatureException;
}
