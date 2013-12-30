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

package sorcer.util.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteEcho extends Remote {
	public Object echo(Object object) throws RemoteException;
}

class RemoteEchoClient {
	public static void main(String[] args) {
		try {
			RemoteEcho echo = RemoteEchoFactory.getEcho();
			System.out.println(echo.echo("o che bon eccho"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
