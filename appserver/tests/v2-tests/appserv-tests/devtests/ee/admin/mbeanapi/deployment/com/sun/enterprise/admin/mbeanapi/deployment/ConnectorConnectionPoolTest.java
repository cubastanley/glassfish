/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.sun.enterprise.admin.mbeanapi.deployment;

import java.util.Map;
import java.util.HashMap;

import com.sun.appserv.management.config.ConnectorConnectionPoolConfig;
import com.sun.appserv.management.config.ConnectorConnectionPoolConfigKeys;


/**
 */
public class ConnectorConnectionPoolTest extends BaseTest
{
    private final Cmd target;

    static final String kName                   = "myConnectorConnectionPool";
    static final String kResourceAdapterName    = "sivajdbcra";
    static final String kTransactionSupport     = "LocalTransaction";
    static final String kConnectionDefinitionName   = "javax.sql.DataSource";

    public ConnectorConnectionPoolTest(final String user, 
        final String password, final String host, final int port, 
        final String name, final String resourceAdapterName, 
        final String connectionDefinitionName)
    {
        final CmdFactory cmdFactory = getCmdFactory();

        final ConnectCmd connectCmd = cmdFactory.createConnectCmd(
                user, password, host, port);

        final CreateConnectorConnectionPoolCmd createCmd = 
                cmdFactory.createCreateConnectorConnectionPoolCmd(name,
                        resourceAdapterName, connectionDefinitionName, 
                        getOptional());

        final DeleteConnectorConnectionPoolCmd deleteCmd = 
                cmdFactory.createDeleteConnectorConnectionPoolCmd(name);

        final PipeCmd p1 = new PipeCmd(connectCmd, createCmd);
        final PipeCmd p2 = new PipeCmd(p1, new VerifyCreateCmd());
        final PipeCmd p3 = new PipeCmd(connectCmd, deleteCmd);

        final CmdChainCmd chainCmd = new CmdChainCmd();
        chainCmd.addCmd(p2);
        chainCmd.addCmd(p3);

        target = chainCmd;
    }

    protected void runInternal() throws Exception
    {
        target.execute();
    }


    public static void main(String[] args) throws Exception
    {
        new ConnectorConnectionPoolTest(
                "admin", "password", "localhost", 8686, 
                kName, kResourceAdapterName, kConnectionDefinitionName).
                    run();
    }

    private Map getOptional()
    {
        final Map optional = new HashMap();
        optional.put(ConnectorConnectionPoolConfigKeys.TRANSACTION_SUPPORT_KEY, 
                kTransactionSupport);
        return optional;
    }

    private final class VerifyCreateCmd implements Cmd, SinkCmd
    {
        private ConnectorConnectionPoolConfig res;

        private VerifyCreateCmd()
        {
        }

        public void setPipedData(Object o)
        {
            res = (ConnectorConnectionPoolConfig)o;
        }

        public Object execute() throws Exception
        {
            System.out.println("Name="+res.getName());
            System.out.println("ResourceAdapterName="+
                    res.getResourceAdapterName());
            System.out.println("ConnectionDefinitionName="+
                    res.getConnectionDefinitionName());
            System.out.println("SteadyPoolSize="+res.getSteadyPoolSize());
            System.out.println("MaxPoolSize="+res.getMaxPoolSize());
            System.out.println("MaxWaitTimeInMillis="+
                    res.getMaxWaitTimeInMillis());
            System.out.println("PoolResizeQuantity="+
                    res.getPoolResizeQuantity());
            System.out.println("IdleTimeoutInSeconds="+
                    res.getIdleTimeoutInSeconds());
            System.out.println("FailAllConnections="+
                    res.getFailAllConnections());
            System.out.println("TransactionSupport="+
                    res.getTransactionSupport());

            return new Integer(0);
        }

    }
}
