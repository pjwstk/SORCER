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

package sorcer.util.html;

public interface HorizontalAligned {

	// Define a few constants
	final public static int UNALIGNED = 0; // Element is unaligned

	// Horizontal alignment

	final public static int LEFT = 1; // Component is left aligned
	final public static int CENTER = 2; // Element is centered
	final public static int RIGHT = 3; // Element is right aligned

	public int getHorizontalAlignment();

	public void setHorizontalAlignment(int n);
}
