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

package org.apache.james.imap.store;

import junit.framework.TestCase;

import org.apache.james.imap.store.mail.model.MailboxMembership;

public class MessageRowUtilsTest extends TestCase {    
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testShouldReturnPositiveWhenFirstGreaterThanSecond()
            throws Exception {
        MailboxMembership one = buildMessage(100);
        MailboxMembership two = buildMessage(99);
        assertTrue(MessageRowUtils.getUidComparator().compare(one, two) > 0);
    }

    private MailboxMembership buildMessage(int uid) {
        MessageBuilder builder = new MessageBuilder();
        builder.uid = uid;
        return builder.build();
    }

    public void testShouldReturnNegativeWhenFirstLessThanSecond()
            throws Exception {
        MailboxMembership one = buildMessage(98);
        MailboxMembership two = buildMessage(99);
        assertTrue(MessageRowUtils.getUidComparator().compare(one, two) < 0);
    }

    public void testShouldReturnZeroWhenFirstEqualsSecond() throws Exception {
        MailboxMembership one = buildMessage(90);
        MailboxMembership two = buildMessage(90);
        assertEquals(0, MessageRowUtils.getUidComparator().compare(one, two));
    }
}
