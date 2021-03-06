/*
 * File: TestClassPredicateTest.java
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

package com.oracle.bedrock.testsupport.junit;

import com.oracle.bedrock.testsupport.junit.*;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@link TestClassPredicate}.
 * <p>
 * Copyright (c) 2016. All Rights Reserved. Oracle Corporation.<br>
 * Oracle is a registered trademark of Oracle Corporation and/or its affiliates.
 *
 * @author Jonathan Knight
 */
public class TestClassPredicateTest
{
    /**
     * Enum description
     */
    public enum DummyEnum {One,
                           Two}


    @Test
    public void shouldEvaluateFalseIfClassIsNull() throws Exception
    {
        TestClassPredicate predicate = new TestClassPredicate();

        assertThat(predicate.test(null), is(false));
    }


    @Test
    public void shouldEvaluateFalseIfClassIsAnnotation() throws Exception
    {
        TestClassPredicate predicate = new TestClassPredicate();

        assertThat(predicate.test(Ignore.class), is(false));
    }


    @Test
    public void shouldEvaluateFalseIfClassIsAnnonymous() throws Exception
    {
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
            }
        };

        TestClassPredicate predicate = new TestClassPredicate();

        assertThat(predicate.test(runnable.getClass()), is(false));
    }


    @Test
    public void shouldEvaluateFalseIfClassIsEnum() throws Exception
    {
        TestClassPredicate predicate = new TestClassPredicate();

        assertThat(predicate.test(DummyEnum.class), is(false));
    }


    @Test
    public void shouldEvaluateFalseIfClassIsInterface() throws Exception
    {
        TestClassPredicate predicate = new TestClassPredicate();

        assertThat(predicate.test(Runnable.class), is(false));
    }


    @Test
    public void shouldEvaluateFalseForAbstractJunit3Test() throws Exception
    {
        TestClassPredicate predicate = new TestClassPredicate();

        assertThat(predicate.test(AbstractJUnit3Test.class), is(false));
    }


    @Test
    public void shouldEvaluateFalseForAbstractJunit3Suite() throws Exception
    {
        TestClassPredicate predicate = new TestClassPredicate();

        assertThat(predicate.test(AbstractJUnit3Suite.class), is(false));
    }


    @Test
    public void shouldEvaluateTrueForJunit3Suite() throws Exception
    {
        TestClassPredicate predicate = new TestClassPredicate();

        assertThat(predicate.test(JUnit3Suite.class), is(true));
    }


    @Test
    public void shouldEvaluateTrueForJunit3Test() throws Exception
    {
        TestClassPredicate predicate = new TestClassPredicate();

        assertThat(predicate.test(JUnit3Test.class), is(true));
    }


    @Test
    public void shouldEvaluateFalseForAbstractJunit4Test() throws Exception
    {
        TestClassPredicate predicate = new TestClassPredicate();

        assertThat(predicate.test(AbstractJUnit4Test.class), is(false));
    }


    @Test
    public void shouldEvaluateFalseForAbstractJunit4RunWithTest() throws Exception
    {
        TestClassPredicate predicate = new TestClassPredicate();

        assertThat(predicate.test(AbstractRunWithAnnotatedTest.class), is(false));
    }


    @Test
    public void shouldEvaluateTrueForJunit4Test() throws Exception
    {
        TestClassPredicate predicate = new TestClassPredicate();

        assertThat(predicate.test(JUnit4Test.class), is(true));
    }


    @Test
    public void shouldEvaluateTrueForJunit4RunWithTest() throws Exception
    {
        TestClassPredicate predicate = new TestClassPredicate();

        assertThat(predicate.test(RunWithAnnotatedTest.class), is(true));
    }


    @Test
    public void shouldEvaluateTrueForAbstractRunWithEnclosedTest() throws Exception
    {
        TestClassPredicate predicate = new TestClassPredicate();

        assertThat(predicate.test(AbstractRunWithEnclosedTest.class), is(true));
    }


    ;
}
