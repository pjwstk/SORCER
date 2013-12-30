/*
 * Copyright 2005 Sun Microsystems, Inc.
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
package org.jini.rio.tools.ant;

/**
 */
public class Argument extends Object {
    /** Holds value of property name. */
    private String name;

    /** Creates new Argument */
    public Argument() {
    }

    public Argument(String name) {
        this.name = name;
    }

    /** 
     * Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return(name);
    }

    /** 
     * Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }
}