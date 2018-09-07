/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.tf.timer.spi;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class TimerFactoryTest {
    private String tfName = "TFTF";
    private String tfDescription = "The TimerFactorySuite TimerFactory";
    private TimerFactory tf;

    private String timer1Name = "t1";
    private String timer1Description = "Timer one";
    private Timer t1;

    private String timer2Name = "t2";
    private String timer2Description = "Timer two";
    private Timer t2;

    private String timerGroup1Name = "tg1";
    private String timerGroup1Description = "TimerGroup one";
    private TimerGroup tg1;

    private static final int EXPECTED_NUM_TIMERS = 2;

    // Remember, the factory is also a TimerGroup!
    private static final int EXPECTED_NUM_TIMER_GROUPS = 2;

    private static final int EXPECTED_NUM_IDS =
            EXPECTED_NUM_TIMERS + EXPECTED_NUM_TIMER_GROUPS;


    @Before
    public void setUp() {
        tf = TimerFactoryBuilder.make(tfName, tfDescription);
        t1 = tf.makeTimer(timer1Name, timer1Description);
        t2 = tf.makeTimer(timer2Name, timer2Description);
        tg1 = tf.makeTimerGroup(timerGroup1Name, timerGroup1Description);
    }

    @After
    public void tearDown() {
        TimerFactoryBuilder.destroy(tf);
    }

    @Test()
    public void testNumberOfIds() {
        Assert.assertEquals(EXPECTED_NUM_IDS, tf.numberOfIds());
    }

    @Test()
    public void testGetControllable1() {
        int id = t1.id();
        Controllable con = tf.getControllable(id);
        Assert.assertTrue(con instanceof Timer);
        Timer timer = Timer.class.cast(con);
        Assert.assertEquals(t1, timer);
    }

    @Test()
    public void testGetControllable2() {
        int id = t2.id();
        Controllable con = tf.getControllable(id);
        Assert.assertTrue(con instanceof Timer);
        Timer timer = Timer.class.cast(con);
        Assert.assertEquals(t2, timer);
    }

    @Test()
    public void testGetControllable3() {
        int id = tg1.id();
        Controllable con = tf.getControllable(id);
        Assert.assertTrue(con instanceof TimerGroup);
        TimerGroup tg = TimerGroup.class.cast(con);
        Assert.assertEquals(tg1, tg);
    }

    @Test()
    public void testMakeLogEventHandler() {
        String name = "LogEventHandler1";
        TimerEventHandler h1 = tf.makeLogEventHandler(name);
        Assert.assertEquals(h1.name(), name);
        try {
            tf.makeLogEventHandler(name);
            Assert.fail("Should throw an exception");
        } catch (IllegalArgumentException exc) {
            // this is correct
        } catch (Throwable thr) {
            Assert.fail("Unexpected exception " + thr);
        }
        try {
            tf.makeStatsEventHandler(name);
            Assert.fail("Should throw an exception");
        } catch (IllegalArgumentException exc) {
            // this is correct
        } catch (Throwable thr) {
            Assert.fail("Unexpected exception " + thr);
        }
        tf.removeTimerEventHandler(h1);
        h1 = tf.makeLogEventHandler(name);
        tf.removeTimerEventHandler(h1);
        h1 = tf.makeStatsEventHandler(name);
    }

    @Test()
    public void testTimers() {
        Map<String, ? extends Timer> tmap = tf.timers();
        Assert.assertEquals(tmap.size(), EXPECTED_NUM_TIMERS);
        Timer x1 = tmap.get(timer1Name);
        Assert.assertEquals(x1, t1);
        Timer x2 = tmap.get(timer2Name);
        Assert.assertEquals(x2, t2);
    }

    @Test()
    public void testTimerGroups() {
        Map<String, ? extends TimerGroup> tmap = tf.timerGroups();
        Assert.assertEquals(tmap.size(), EXPECTED_NUM_TIMER_GROUPS);
        TimerGroup x1 = tmap.get(timerGroup1Name);
        Assert.assertEquals(x1, tg1);
        TimerGroup x2 = tmap.get(tfName);
        Assert.assertEquals(x2, tf);
    }

    @Test()
    public void testMakeController() {
        String name = "Controller1";
        TimerEventController controller = tf.makeController(name);
        Assert.assertEquals(controller.name(), name);
        try {
            tf.makeController(name);
            Assert.fail("Should throw an exception");
        } catch (IllegalArgumentException exc) {
            // this is correct
        } catch (Throwable thr) {
            Assert.fail("Unexpected exception " + thr);
        }
        tf.removeController(controller);
        controller = tf.makeController(name);
    }

    // enabledSet and activeSet are tested in the ActivationSuite
}
