/*
 Copyright 2005 Dan Creswell (dan@dancres.org)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 License for the specific language governing permissions and limitations under
 the License.
*/

package org.dancres.blitz.jini.lockmgr;

import java.io.Serializable;

public class LockKey implements Comparable, Serializable {
    private String theClass;
    private Comparable theKey;

    public LockKey(String aClass, Comparable aKey) {
        theClass = aClass;
        theKey = (Comparable) aKey;
    }

    public boolean equals(Object anObject) {
        if (anObject instanceof LockKey) {
            LockKey myOther = (LockKey) anObject;

            if (theClass.equals(myOther.theClass)) {
                return theKey.equals(myOther.theKey);
            }
        }

        return false;
    }

    public int compareTo(Object anObject) {
        LockKey myOther = (LockKey) anObject;

        // Same class requires key compare to differentiate otherwise
        // class is good enough for ordering
        //
        if (theClass.equals(myOther.theClass))
            return theKey.compareTo(myOther.theKey);
        else {
            return theClass.compareTo(myOther.theClass);
        }
    }

    public int hashCode() {
        return theKey.hashCode();
    }

    public String toString() {
        return "LockKey: " + theClass + ", " + theKey;
    }
}