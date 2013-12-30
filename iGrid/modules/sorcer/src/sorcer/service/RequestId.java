/*
 * Copyright 2009 the original author or authors.
 * Copyright 2009 SorcerSoft.org.
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

package sorcer.service;

import java.io.Serializable;
import java.rmi.server.ObjID;

/**
 * Encapsulates a request id for an invoked exertion.
 * 
 * @author Mike Sobolewski
 */
public class RequestId implements Serializable {

	private ObjID id = new ObjID();

	/** {@inheritDoc} */
	public boolean equals(Object obj) {
		if (obj instanceof RequestId) {
			return (id.equals(((RequestId) obj).id));
		}
		return (false);
	}
	
	/** {@inheritDoc} */
	public int hashCode() {
		return (id.hashCode());
	}

	/** {@inheritDoc} */
	public String toString() {
		return (id.toString());
	}
}
