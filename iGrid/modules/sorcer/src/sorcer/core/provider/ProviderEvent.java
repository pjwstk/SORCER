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
package sorcer.core.provider;

import sorcer.service.RemoteServiceEvent;

/**
 *  Generic Events produced by SORCER providers
 */
public class ProviderEvent extends RemoteServiceEvent {
    /** An unique id number for the provider event **/
    public static final long ID = 9999999999L;
    
    /** holds the property for the time the event was created */
    private long when;
    
    /** Creates new ProviderEvent */
    public ProviderEvent(Object source) {
        super(source);
        setWhen(System.currentTimeMillis());
    }

    /** Getter for property when.
     * @return Value of property when.
     */
    public long getWhen() {
        return when;
    }
    
    /** Setter for property when.
     * @param when New value of property when.
     */
    public void setWhen(long when) {
        this.when = when;
    }
}
