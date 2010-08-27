/*
 * This file is part of the OpenNMS(R) Application.
 *
 * OpenNMS(R) is Copyright (C) 2010 The OpenNMS Group, Inc.  All rights reserved.
 * OpenNMS(R) is a derivative work, containing both original code, included code and modified
 * code that was published under the GNU General Public License. Copyrights for modified
 * and included code are below.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * Copyright (C) 2010 The OpenNMS Group, Inc.  All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * For more information contact:
 *      OpenNMS Licensing       <license@opennms.org>
 *      http://www.opennms.org/
 *      http://www.opennms.com/
 */
package org.opennms.netmgt.dao.castor;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.opennms.netmgt.config.datacollection.DatacollectionConfig;
import org.opennms.netmgt.config.datacollection.DatacollectionGroup;
import org.opennms.netmgt.config.datacollection.SnmpCollection;
import org.opennms.test.ConfigurationTestUtils;
import org.opennms.test.mock.MockLogAppender;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * DataCollectionConfigParserTest
 * 
 * @author <a href="mail:agalue@opennms.org">Alejandro Galue</a>
 */
public class DataCollectionConfigParserTest {

    private static final int resourceTypesCount = 69;
    private static final int systemDefCount = 126;
    private static final int groupsCount = 183;
    
    @Before
    public void setUp() {
        MockLogAppender.setupLogging();
    }
    
    @After
    public void tearDown() {
        MockLogAppender.assertNoWarningsOrGreater();
    }

    @Test
    public void testLoadWithEmptyConfig() throws Exception {
        // Configure XML repository
        File configFile = ConfigurationTestUtils.getFileForConfigFile("datacollection-config.xml");
        File configFolder = new File(configFile.getParentFile(), "datacollection");
        System.err.println(configFolder.getAbsolutePath());
        // Assert.assertTrue(configFolder.isDirectory());

        // Create DatacollectionConfig
        DatacollectionConfig config = new DatacollectionConfig();
        SnmpCollection collection = new SnmpCollection();
        collection.setName("default");
        config.addSnmpCollection(collection);

        // Validate default datacollection content
        Assert.assertEquals(0, collection.getIncludeCollectionCount());
        Assert.assertEquals(0, collection.getResourceTypeCount()); 
        Assert.assertNull(collection.getSystems());
        Assert.assertNull(collection.getGroups());

        /* this test is only valid when there are subdirectory configs
        // Create Parser
        DataCollectionConfigParser parser = new DataCollectionConfigParser(configFolder.getAbsolutePath());
        parser.parse(config);

        // Validate Parser
        DatacollectionGroup globalContainer = parser.getGlobalContainer();
        Assert.assertEquals(resourceTypesCount, globalContainer.getResourceTypeCount());
        Assert.assertEquals(systemDefCount, globalContainer.getSystemDefCount());
        Assert.assertEquals(groupsCount, globalContainer.getGroupCount());

        // Validate SNMP Collection
        Assert.assertEquals(0, collection.getResourceTypeCount()); 
        Assert.assertEquals(0, collection.getSystems().getSystemDefCount());
        Assert.assertEquals(0, collection.getGroups().getGroupCount());
        */
    }

    @Test
    public void testLoadWithOnlyExternalReferences() throws Exception {
        // Configure XML repository
        File configFile = ConfigurationTestUtils.getFileForConfigFile("datacollection-config.xml");
        File configFolder = new File(configFile.getParentFile(), "datacollection");
        System.err.println(configFolder.getAbsolutePath());
        // Assert.assertTrue(configFolder.isDirectory());

        // Create DatacollectionConfig
        Resource resource = new FileSystemResource(configFile);
        DatacollectionConfig config = CastorUtils.unmarshal(DatacollectionConfig.class, resource);

        // Validate default datacollection content
        SnmpCollection collection = config.getSnmpCollection(0);
        // in 1.9 this is 42
        Assert.assertEquals(0, collection.getIncludeCollectionCount());
        // in 1.9 this is 0
        Assert.assertEquals(69, collection.getResourceTypeCount()); 
        // these are only null in the default 1.9 config
        // Assert.assertNull(collection.getSystems());
        // Assert.assertNull(collection.getGroups());

        // Create Parser
        DataCollectionConfigParser parser = new DataCollectionConfigParser(configFolder.getAbsolutePath());
        parser.parse(config);

        // Validate Parser
        DatacollectionGroup globalContainer = parser.getGlobalContainer();
        Assert.assertEquals(resourceTypesCount, globalContainer.getResourceTypeCount());
        Assert.assertEquals(systemDefCount, globalContainer.getSystemDefCount());
        Assert.assertEquals(groupsCount, globalContainer.getGroupCount());

        // Validate SNMP Collection
        Assert.assertEquals(resourceTypesCount, collection.getResourceTypeCount()); 
        Assert.assertEquals(systemDefCount, collection.getSystems().getSystemDefCount());
        // this is 144 in 1.9
        Assert.assertEquals(183, collection.getGroups().getGroupCount()); // Unused groups will be discarted
    }

}
