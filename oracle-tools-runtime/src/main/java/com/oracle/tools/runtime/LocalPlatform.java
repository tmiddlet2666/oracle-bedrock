/*
 * File: LocalPlatform.java
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * The contents of this file are subject to the terms and conditions of 
 * the Common Development and Distribution License 1.0 (the "License").
 *
 * You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the License by consulting the LICENSE.txt file
 * distributed with this file, or by consulting https://oss.oracle.com/licenses/CDDL
 *
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file LICENSE.txt.
 *
 * MODIFICATIONS:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 */

package com.oracle.tools.runtime;

import com.oracle.tools.io.NetworkHelper;

import com.oracle.tools.runtime.concurrent.RemoteCallable;
import com.oracle.tools.runtime.concurrent.RemoteExecutor;
import com.oracle.tools.runtime.concurrent.RemoteExecutorListener;
import com.oracle.tools.runtime.concurrent.socket.RemoteExecutorClient;
import com.oracle.tools.runtime.concurrent.socket.RemoteExecutorServer;
import com.oracle.tools.runtime.concurrent.socket.SocketBasedRemoteExecutor;

import com.oracle.tools.runtime.java.FluentJavaApplication;
import com.oracle.tools.runtime.java.JavaApplication;
import com.oracle.tools.runtime.java.LocalJavaApplicationBuilder;

import com.oracle.tools.runtime.network.AvailablePortIterator;

import com.oracle.tools.util.FutureCompletionListener;

import java.io.IOException;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import java.util.Enumeration;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The {@link Platform} in which this Java Virtual Machine is running.
 * <p>
 * Copyright (c) 2014. All Rights Reserved. Oracle Corporation.<br>
 * Oracle is a registered trademark of Oracle Corporation and/or its affiliates.
 *
 * @author Jonathan Knight
 * @author Brian Oliver
 */
public class LocalPlatform extends AbstractPlatform
{
    /**
     * The singleton instance of {@link LocalPlatform}.
     */
    private static LocalPlatform  INSTANCE = new LocalPlatform();

    private AvailablePortIterator availablePortIterator;

    /**
     * The {@link InetAddress} of the {@link Platform}.
     */
    private InetAddress address;


    /**
     * Construct a new {@link LocalPlatform}.
     */
    private LocalPlatform()
    {
        super("Local");

        // ----- establish InetAddress of the LocalPlatform -----

        // attempt to use the system property that may have been defined
        // (in the future we may use a PlatformAddress Option to achieve this)
        String addressSystemProperty = System.getProperty("oracletools.runtime.address");

        if (addressSystemProperty == null)
        {
            this.address = NetworkHelper.getFeasibleLocalHost();
        }
        else
        {
            try
            {
                this.address = InetAddress.getByName(addressSystemProperty);
            }
            catch (UnknownHostException e)
            {
                // TODO: log that the specified address can't be resolved, defaulting to the feasible localhost
                this.address = NetworkHelper.getFeasibleLocalHost();
            }
        }

        // ----- establish an AvailablePortIterator for the LocalPlatform ------
        this.availablePortIterator = new AvailablePortIterator(30000, AvailablePortIterator.MAXIMUM_PORT);
    }


    @Override
    public InetAddress getAddress()
    {
        return address;
    }


    /**
     * Obtains the {@link AvailablePortIterator} for the {@link LocalPlatform}.
     *
     * @return the {@link AvailablePortIterator}
     */
    public AvailablePortIterator getAvailablePorts()
    {
        return availablePortIterator;
    }


    @Override
    @SuppressWarnings("unchecked")
    public <A extends Application, B extends ApplicationBuilder<A>> B getApplicationBuilder(Class<A> applicationClass)
    {
        if (JavaApplication.class.isAssignableFrom(applicationClass))
        {
            return (B) new LocalJavaApplicationBuilder();
        }
        else
        {
            return (B) new SimpleApplicationBuilder();
        }
    }


    /**
     * Obtain the singleton instance of the {@link LocalPlatform}.
     *
     * @return the singleton instance of the {@link LocalPlatform}
     */
    public static LocalPlatform getInstance()
    {
        return INSTANCE;
    }


    /**
     * Perform and output diagnostics concerning the {@link LocalPlatform}.
     *
     * @param arguments
     */
    public static void main(String[] arguments) throws UnknownHostException, SocketException
    {
        LocalPlatform platform = LocalPlatform.getInstance();

        System.out.println("---------------------------------------");
        System.out.println("Oracle Tools: LocalPlatform Diagnostics");
        System.out.println("---------------------------------------");

        System.out.println("Platform Name       : " + platform.getName());
        System.out.println("Address             : " + platform.getAddress());
        System.out.println("Available Ports     : " + platform.getAvailablePorts());
        System.out.println("Options             : " + platform.getOptions());

        System.out.println();
        System.out.println("---------------------------------");
        System.out.println("Oracle Tools: Network Diagnostics");
        System.out.println("---------------------------------");
        System.out.println("LocalHost Address   : " + InetAddress.getLocalHost());
        System.out.println("Loopback Address    : " + InetAddress.getLoopbackAddress());
        System.out.println("Prefer IPv4         : "
                           + System.getProperty(FluentJavaApplication.JAVA_NET_PREFER_IPV4_STACK, "(undefined)"));
        System.out.println("Prefer IPv6         : "
                           + System.getProperty(FluentJavaApplication.JAVA_NET_PREFER_IPV6_STACK, "(undefined)"));

        System.out.println();
        System.out.println("Network Interfaces:");
        System.out.println("-------------------");

        for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            interfaces.hasMoreElements(); )
        {
            NetworkInterface networkInterface = interfaces.nextElement();

            System.out.println("Display Name        : " + networkInterface.getDisplayName() + " ("
                               + networkInterface.getName() + ") #" + networkInterface.getIndex());
            System.out.println("Is Active?          : " + networkInterface.isUp());
            System.out.println("Is Loopback?        : " + networkInterface.isLoopback());
            System.out.println("Is Virtual?         : " + networkInterface.isVirtual());
            System.out.println("Is Point-To-Point?  : " + networkInterface.isPointToPoint());
            System.out.println("Supports Multicast? : " + networkInterface.supportsMulticast());
            System.out.print("Hardware Address    : ");

            byte[] hardwareAddress = networkInterface.getHardwareAddress();

            if (hardwareAddress == null)
            {
                System.out.println("(undefined)");
            }
            else
            {
                for (int i = 0; i < hardwareAddress.length; i++)
                {
                    System.out.print((i > 0 ? ":" : "")
                                     + Integer.toString((hardwareAddress[i] & 0xff) + 0x100, 16).substring(1));
                }

                System.out.println();
            }

            System.out.print("InetAddresses       : ");

            for (Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                inetAddresses.hasMoreElements(); )
            {
                InetAddress inetAddress = inetAddresses.nextElement();

                System.out.print(inetAddress + (inetAddresses.hasMoreElements() ? ", " : ""));
            }

            System.out.println();

            System.out.println();
        }

        // establish a remote executor server locally to test connectivity

        try (RemoteExecutorServer server = new RemoteExecutorServer();)
        {
            System.out.println("Network Connectivity Tests:");
            System.out.println("---------------------------");

            server.addListener(new RemoteExecutorListener()
            {
                @Override
                public void onOpened(RemoteExecutor executor)
                {
                    if (executor instanceof SocketBasedRemoteExecutor)
                    {
                        SocketBasedRemoteExecutor socketBasedExecutor = (SocketBasedRemoteExecutor) executor;

                        System.out.println("[Server]: Connection Opened (for client executor #"
                                           + socketBasedExecutor.getExecutorId() + ")");
                    }
                }

                @Override
                public void onClosed(RemoteExecutor executor)
                {
                    if (executor instanceof SocketBasedRemoteExecutor)
                    {
                        SocketBasedRemoteExecutor socketBasedExecutor = (SocketBasedRemoteExecutor) executor;

                        System.out.println("[Server]: Connection Closed (for client executor #"
                                           + socketBasedExecutor.getExecutorId() + ")\n");
                    }
                }
            });

            InetAddress serverAddress = server.open();

            System.out.println("Established Server Socket Listening at : " + serverAddress + ":" + server.getPort());
            System.out.println();

            connectWith(InetAddress.getLoopbackAddress(), server.getPort(), "InetAddress.getLoopbackAddress()");

            connectWith(InetAddress.getLocalHost(), server.getPort(), "InetAddress.getLocalHost()");

            connectWith(platform.getAddress(), server.getPort(), "LocalPlatform.getAddress()");
        }
        catch (Exception e)
        {
        }
    }


    private static void connectWith(InetAddress inetAddress,
                                    int         port,
                                    String      description)
    {
        try (RemoteExecutorClient client = new RemoteExecutorClient(inetAddress, port))
        {
            System.out.println("Connection Validation For : " + description);
            System.out.println("---------------------------");
            System.out.println("[Client]: Establishing Client Socket on : " + inetAddress + ":" + port);
            client.open();

            FutureCompletionListener<String> future = new FutureCompletionListener<>();

            System.out.println("[Client]: Sending Client Request to Server...");

            client.submit(new PingPong(description), future);

            System.out.println("[Client]: Waiting for Server Response...");

            String value = future.get(15, TimeUnit.SECONDS);

            if (description.equals(value))
            {
                System.out.println("[Client]: Server successfully executed Client Request");
            }
            else
            {
                System.out.println("[Client]: Server failed to execute Client Request.  Returned [" + value
                                   + "] instead of [" + description + "]");
            }

            System.out.println("[Client]: Closing Client Socket on : " + inetAddress + ":" + port);

            System.out.println();
        }
        catch (ExecutionException e)
        {
            System.out.println("[Client]: Server failed to execute the Client Request:");
            e.printStackTrace();
        }
        catch (TimeoutException e)
        {
            System.out.println("[Client]: Server failed to execute the Client Request in the required timeout");
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            System.out.println("[Client]: Client was interrupted waiting for the Server Response");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            System.out.println("[Client]: Failed to connect to: " + description);
            e.printStackTrace();
        }
    }


    /**
     * A simple {@link RemoteCallable} to test remote execution submission.
     */
    public static class PingPong implements RemoteCallable<String>
    {
        /**
         * A value to return to the caller.
         */
        private String value;


        /**
         * Constructs a {@link PingPong} {@link RemoteCallable}.
         *
         * @param value  the value to return
         */
        public PingPong(String value)
        {
            this.value = value;
        }


        @Override
        public String call() throws Exception
        {
            System.out.println("[Server]: Executing Client Request <" + value + ">");

            return value;
        }
    }
}
