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

public class LockStatus implements Serializable {
    private static final int FAILED_STATE = -1;
    private static final int SUCCEED_STATE = -2;

    static final LockStatus FAILED = new LockStatus(FAILED_STATE, null);

    private Serializable theResource;
    private int theStatus;

    private LockStatus(int aStatus, Serializable aResource) {
        theStatus = aStatus;
        theResource = aResource;
    }

    public LockStatus(Serializable aResource) {
        theStatus = SUCCEED_STATE;
        theResource = aResource;
    }

    boolean didSucceed() {
        return (theStatus != FAILED_STATE);
    }

    Serializable getResource() {
        return theResource;
    }
}