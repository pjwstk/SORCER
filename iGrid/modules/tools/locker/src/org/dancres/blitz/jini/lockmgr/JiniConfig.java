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

import java.io.IOException;

import net.jini.discovery.LookupDiscovery;

import net.jini.core.discovery.LookupLocator;

import net.jini.discovery.LookupDiscoveryManager;

import net.jini.jeri.BasicILFactory;
import net.jini.jeri.ProxyTrustILFactory;
import net.jini.jeri.BasicJeriExporter;

import net.jini.jeri.tcp.TcpServerEndpoint;

import net.jini.export.Exporter;

import net.jini.core.entry.Entry;

/**
   Holds configuration for JINI related aspects of the Replica Group.
 */
public class JiniConfig {
    public static final long DEFAULT_TIMEOUT = 10000;
    public static final String DEFAULT_MCASTADDR = "228.3.11.76";
    public static final int DEFAULT_MCASTPORT = 12345;
    public static final String DEFAULT_LOCKGROUP = "jinilock1";
    public static final int DEFAULT_TTL = 15;

    public static final JiniConfig DEFAULT =
        new JiniConfig(LookupDiscovery.ALL_GROUPS, null,
                       new Entry[0]);

    private String[] theGroups;
    private LookupLocator[] theLocators;
    private Entry[] theAttrs;

    private Exporter theExporter;

    private LookupDiscoveryManager theLDM;

    private long theOpTimeout;

    private String theMcastAddr;
    private int theMcastPort;
    private int theTTL;

    private String theLockGroup;
    
    public JiniConfig(String[] aGroups, LookupLocator[] aLocators,
                      Entry[] anAttrs,
                      String aLockGroup) {

        this(aGroups, aLocators, anAttrs,
             new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
                                   new BasicILFactory(null, null),
                                   false, true), DEFAULT_TIMEOUT,
             DEFAULT_MCASTADDR, DEFAULT_MCASTPORT, DEFAULT_TTL, aLockGroup);
    }

    public JiniConfig(String[] aGroups,
                      LookupLocator[] aLocators,
                      Entry[] anAttrs) {
        this(aGroups, aLocators, anAttrs,
             new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
                                   new BasicILFactory(null, null),
                                   false, true), DEFAULT_TIMEOUT,
             DEFAULT_MCASTADDR, DEFAULT_MCASTPORT, DEFAULT_TTL,
             DEFAULT_LOCKGROUP);
    }

    /**
       @param aGroups determines the JINI groups in which the node will
       advertise it's Replicant proxy.
       @param aLocators determines specific lookup services in which the
       node will advertise it's Replicant proxy.
       @param anAttrs the Entry attributes to use for registration
       @param anExporter determines the form of transport used by the
       Replicant proxy.  Limited configuration options only as of this moment
       because <code>ServerProxyTrust</code> is not implemented.
       @param aTimeout is the time in which a network operation between nodes
       should complete.
       @param aMcastAddr is the multicast address to use for comms
       @param aMcastPort is the multicast port to use for comms
       @param aTTL is the ttl which should be placed on multicast packets
       @param aLockGroup can be used to create separate groups of co-operating
       LockMgrs.  Ensure you label the registered proxy's in some way such
       as adding an Entry attribute or changing the LUS group so that you can
       differentiate.
     */
    public JiniConfig(String[] aGroups,
                      LookupLocator[] aLocators,
                      Entry[] anAttrs,
                      Exporter anExporter, long aTimeout,
                      String aMcastAddr, int aMcastPort, int aTTL,
                      String aLockGroup) {
        theGroups = aGroups;
        theLocators = aLocators;
        theExporter = anExporter;
        theOpTimeout = aTimeout;
        theMcastAddr = aMcastAddr;
        theMcastPort = aMcastPort;
        theTTL = aTTL;
        theLockGroup = aLockGroup;
        theAttrs = anAttrs;
    }

    public String getMcastAddr() {
        return theMcastAddr;
    }

    public String getMcastPort() {
        return Integer.toString(theMcastPort);
    }

    public String getMcastTTL() {
        return Integer.toString(theTTL);
    }

    public long getOpTimeout() {
        return theOpTimeout;
    }

    public String getLockGroup() {
        return theLockGroup;
    }

    public String[] getJiniGroups() {
        return theGroups;
    }

    public LookupLocator[] getJiniLocators() {
        return theLocators;
    }

    public Entry[] getJiniAttrs() {
        return theAttrs;
    }
    
    public Exporter getExporter() {
        return theExporter;
    }

    public synchronized LookupDiscoveryManager getLDM() throws IOException {
        if (theLDM == null) {
            theLDM =
                new LookupDiscoveryManager(getJiniGroups(),
                                           getJiniLocators(),
                                           null);
        }

        return theLDM;
    }

    public synchronized void stopServices() {
        if (theLDM != null)
            theLDM.terminate();
    }
}