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

package sorcer.space.channel;

import net.jini.core.entry.Entry;

public class Index implements Entry {
	public String type; // head or tail
	public String channel;
	public Integer position;

	public Index() {
	}

	public Index(String type, String channel) {
		this.type = type;
		this.channel = channel;
	}

	public Index(String type, String channel, Integer position) {
		this.type = type;
		this.channel = channel;
		this.position = position;
	}

	public Integer getPosition() {
		return position;
	}

	public void increment() {
		position = new Integer(position.intValue() + 1);
	}
}
