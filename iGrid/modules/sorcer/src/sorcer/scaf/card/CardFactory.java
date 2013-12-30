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

package sorcer.scaf.card;

/**
 *<p>
 * Factory to provide various kinds of Smart Card objects
 * 
 *<p>
 * Right now only one kind of Smart Card, Java Card is supported. JavaCard
 * instance can be created by passing the required value. This class provides
 * the static method to get the required instance of Smart Card. All the
 * instances implement <code>SmartCard</code> interface and provide a defined
 * set of functions.
 * 
 *@author Saurabh Bhatla
 *@see JavaCard
 *@see SmartCard
 */
public class CardFactory {

	/**
	 * JavaCard Type
	 */
	public static final int JAVA_CARD = 1;

	/**
	 * Returns required SmartCard implementation
	 * 
	 * @param which
	 *            type of card instance that is required
	 *@return SmartCard implement if exists othewise null
	 */
	public static SmartCard getCard(int which) {
		switch (which) {
		case JAVA_CARD:
			try {
				return new JavaCard();
			} catch (Exception e) {
				Debug.out("Exception in getCard()" + e);
			}

		default:
			break;

		}
		return null;

	}

}
