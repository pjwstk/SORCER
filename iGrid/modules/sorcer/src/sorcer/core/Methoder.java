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

package sorcer.core;

import java.rmi.Remote;

import sorcer.core.signature.NetSignature;

/**
 * The marker interface for provider's executiny any service task. This provider
 * gets any task and executes the provider method (inserted code) on this
 * provider. Note that any requestor who needs its task to be processed by this
 * provider must also provide the own executing method (subclass of
 * {@link NetSignature}) or the agent along with its task.
 */
public interface Methoder extends AdministratableProvider, Remote {

}
