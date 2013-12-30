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

import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

public class Channel {
	private JavaSpace space;
	private String channelName;

	public Channel(JavaSpace space, String name) {
		this.space = space;
		this.channelName = name;
	}

	private void createChannel(String channel) {
		Index head = new Index("head", channel, new Integer(1));
		Index tail = new Index("tail", channel, new Integer(0));

		System.out.println("Creating new channel " + channel);
		try {
			space.write(head, null, Lease.FOREVER);
			space.write(tail, null, Lease.FOREVER);
		} catch (Exception e) {
			System.out.println("Error creating the channel.");
			e.printStackTrace();
			return;
		}
		System.out.println("Channel " + channel + " created.");
	}

	private void append(String channel, Object data) {
		System.out.println("Looking for channel " + channel + "...");
		Integer messageNum = getMessageNumber(channel);
		ChannelElement elem = new ChannelElement(channel, null, messageNum,
				data);

		System.out.println("Sending message " + messageNum + " to channel "
				+ channel + "...");
		try {
			space.write(elem, null, Lease.FOREVER);
		} catch (Exception e) {
			System.out.println("Error occurred writing to the channel.");
			e.printStackTrace();
			return;
		}
		System.out.println("Sending message " + messageNum + " to channel \""
				+ channel + "\"... Done.");
	}

	private Integer getMessageNumber(String channel) {
		try {
			Index template = new Index("tail", channel);
			Index tail = (Index) space.take(template, null, Long.MAX_VALUE);
			tail.increment();
			space.write(tail, null, Lease.FOREVER);
			return tail.getPosition();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public int getTail(String channel) {
		try {
			Index template = new Index("tail", channel);
			Index tail = (Index) space.read(template, null, Long.MAX_VALUE);
			return tail.getPosition().intValue();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private Index takeHead(String channel) {
		try {
			Index template = new Index("head", channel);
			Index head = (Index) space.take(template, null, Long.MAX_VALUE);
			return head;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void writeHead(String channel, Index head) {
		try {
			space.write(head, null, Lease.FOREVER);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
