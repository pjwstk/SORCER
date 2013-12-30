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

package sorcer.ui;

import sorcer.service.Context;
import sorcer.service.Job;
import sorcer.service.ServiceExertion;
import sorcer.util.CallbackModel;

public interface SorcerModel extends CallbackModel {

	public Job getSelectedJob();

	public Context getSelectedContext();

	public Job getOutJob();

	public ServiceExertion getOutTask();

	public void setOutJob(Job job);

	public void setOutTask(ServiceExertion task);

	public String getDomainId();

	public String getSubdomainId();

	public void setDomainId(String id);

	public void setSubdomainId(String id);

	public boolean isDataModified();
}
