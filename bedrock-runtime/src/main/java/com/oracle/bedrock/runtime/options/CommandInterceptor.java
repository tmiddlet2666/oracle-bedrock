/*
 * File: CommandInterceptor.java
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

package com.oracle.bedrock.runtime.options;

import com.oracle.bedrock.Option;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * An Option that can be used to intercept and modify a command before is is executed
 * by a platform.
 * <p>
 * Copyright (c) 2018. All Rights Reserved. Oracle Corporation.<br>
 * Oracle is a registered trademark of Oracle Corporation and/or its affiliates.
 *
 * @author Jonathan Knight
 */
public interface CommandInterceptor extends Option
{
    /**
     * Intercept a command prior to execution.
     *
     *
     * @param executableName    the name of the command to execute
     * @param arguments         the arguments to the command
     * @param envVariables      the environment variables
     * @param workingDirectory  the working directory
     *                          
     * @return  the modified command to actually execute
     */
    String onExecute(String executableName, List<String> arguments, Properties envVariables, File workingDirectory);
}
