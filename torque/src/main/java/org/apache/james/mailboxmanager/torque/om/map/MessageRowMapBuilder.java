/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/
package org.apache.james.mailboxmanager.torque.om.map;

import java.util.Date;

import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.map.ColumnMap;
import org.apache.torque.map.DatabaseMap;
import org.apache.torque.map.MapBuilder;
import org.apache.torque.map.TableMap;

/**
 * 
 * 
 * This class was autogenerated by Torque on:
 * 
 * [Sun Dec 09 17:45:09 GMT 2007]
 * 
 * @deprecated Torque implementation will get removed in the next release
 */
@Deprecated()
public class MessageRowMapBuilder implements MapBuilder {
    /**
     * The name of this class
     */
    public static final String CLASS_NAME = "org.apache.james.mailboxmanager.torque.om.map.MessageRowMapBuilder";

    /**
     * The database map.
     */
    private DatabaseMap dbMap = null;

    /**
     * Tells us if this DatabaseMapBuilder is built so that we don't have to
     * re-build it every time.
     * 
     * @return true if this DatabaseMapBuilder is built
     */
    public boolean isBuilt() {
        return (dbMap != null);
    }

    /**
     * Gets the databasemap this map builder built.
     * 
     * @return the databasemap
     */
    public DatabaseMap getDatabaseMap() {
        return this.dbMap;
    }

    /**
     * The doBuild() method builds the DatabaseMap
     * 
     * @throws TorqueException
     */
    public synchronized void doBuild() throws TorqueException {
        if (isBuilt()) {
            return;
        }
        dbMap = Torque.getDatabaseMap("mailboxmanager");

        dbMap.addTable("message");
        TableMap tMap = dbMap.getTable("message");
        tMap.setJavaName("MessageRow");
        tMap
                .setOMClass(org.apache.james.mailboxmanager.torque.om.MessageRow.class);
        tMap
                .setPeerClass(org.apache.james.mailboxmanager.torque.om.MessageRowPeer.class);
        tMap.setDescription("");
        tMap.setPrimaryKeyMethod("none");

        ColumnMap cMap = null;

        // ------------- Column: mailbox_id --------------------
        cMap = new ColumnMap("mailbox_id", tMap);
        cMap.setType(new Long(0));
        cMap.setTorqueType("BIGINT");
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(true);
        cMap.setNotNull(true);
        cMap.setJavaName("MailboxId");
        cMap.setAutoIncrement(false);
        cMap.setProtected(false);
        cMap.setDescription("Mailbox Id");
        cMap.setInheritance("false");
        cMap.setForeignKey("mailbox", "mailbox_id");
        cMap.setPosition(1);
        tMap.addColumn(cMap);
        // ------------- Column: uid --------------------
        cMap = new ColumnMap("uid", tMap);
        cMap.setType(new Long(0));
        cMap.setTorqueType("BIGINT");
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(true);
        cMap.setNotNull(true);
        cMap.setJavaName("Uid");
        cMap.setAutoIncrement(false);
        cMap.setProtected(false);
        cMap.setDescription("the last used uid (default 0)");
        cMap.setInheritance("false");
        cMap.setPosition(2);
        tMap.addColumn(cMap);
        // ------------- Column: internal_date --------------------
        cMap = new ColumnMap("internal_date", tMap);
        cMap.setType(new Date());
        cMap.setTorqueType("DATE");
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
        cMap.setNotNull(false);
        cMap.setJavaName("InternalDate");
        cMap.setAutoIncrement(false);
        cMap.setProtected(false);
        cMap.setDescription("internal date");
        cMap.setInheritance("false");
        cMap.setPosition(3);
        tMap.addColumn(cMap);
        // ------------- Column: size --------------------
        cMap = new ColumnMap("size", tMap);
        cMap.setType(new Integer(0));
        cMap.setTorqueType("INTEGER");
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
        cMap.setNotNull(false);
        cMap.setJavaName("Size");
        cMap.setAutoIncrement(false);
        cMap.setProtected(false);
        cMap.setDescription("message size");
        cMap.setInheritance("false");
        cMap.setPosition(4);
        tMap.addColumn(cMap);
        tMap.setUseInheritance(false);
    }
}
