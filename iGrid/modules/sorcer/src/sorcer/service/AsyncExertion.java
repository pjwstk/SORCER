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

import java.util.Set;

import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;

/**
 * An asynchronus exertion is a service-oriented indirect message with an
 * enclosed service {@link sorcer.service.Context} and a collection of service
 * {@link sorcer.service.Signature}s. It is structured the same way as the
 * {@link Exertion} but is is invoked asynchronousely.
 * 
 * @see sorcer.service.Exertion
 * @author Mike Sobolewski
 */
public interface AsyncExertion extends Exertion {

	/**
	 * Gives this exerion asynchronously to its assigned provider for to a
	 * dynamically bound {@link sorcer.service.Service} matching its
	 * {@link Operator.Type} signature. If a service provider is not
	 * set, then this exertion finds dynamically
	 * any availale service provider matching its process signature for its
	 * asynchronous execution.
	 * 
	 * @param txn
	 *            the transaction (if any) under which to exert
	 * @return a resulting exertion
	 * @throws TransactionException
	 *             if a transaction error occurs
	 * @throws InterruptedException
	 *             if the thread in which the asynchrounues exertion invocation
	 *             occurs is interrupted
	 * @throws ExertionException
	 *             is an exertion invocation failed for any reason
	 * @see Exertion#exert
	 * @see Exertion#setService
	 */
	public Exertion request(Transaction txn) throws TransactionException,
			InterruptedException, ExertionException;

	/**
	 * Returns the identifier for the last asynchronous <code>exert</code>invocation.
	 * 
	 * @return the identifier for the last exert
	 */
	public RequestId getLastRequestId();

	/**
	 * Returns the resulting exertion for the <code>exert</code> call with the
	 * given identifier <code>id</code>.
	 * 
	 * @param id
	 *            an identifier for the <code>exert</code> call
	 * @return the resulting exertion
	 */
	public Exertion getRequestResult(RequestId id);

	/**
	 * Returns <code>true</code> if the last <code>exert</code> call is
	 * done, otherwise false.
	 * 
	 * @return the status of the last <code>exert</code> call
	 */
	public boolean isLastRequestDone();

	/**
	 * Returns a collection of all <code>exert</code> call identifiers for
	 * this exertion.
	 * 
	 * @return a collection of all <code>exert</code> call identifiers
	 */
	public Set getRequestIds();
	
	/**
	 * Returns a collection of all pending <code>exert</code> call identifiers for
	 * this exertion.
	 * 
	 * @return a collection of all pending <code>exert</code> call identifiers
	 */
	public Set getAllPendingRequestsIds();

	/**
	 * Returns a collection of all done <code>exert</code> call identifiers for
	 * this exertion.
	 * 
	 * @return a collection of all done <code>exert</code> call identifiers
	 */
	public Set getAllDoneRequestsIds();

	/**
	 * Returns a number of total pending <code>exert</code> call identifiers for
	 * this exertion.
	 * 
	 * @return a number of total pending <code>exert</code> call identifiers
	 */
	public int getTotalDoneRequests();

	/**
	 * Returns a number of total pending <code>exert</code> call identifiers for
	 * this exertion.
	 * 
	 * @return a number of total pending <code>exert</code> call identifiers
	 */
	public int getTotalPendingRequests();

}
