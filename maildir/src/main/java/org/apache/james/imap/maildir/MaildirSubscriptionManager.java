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
package org.apache.james.imap.maildir;

import org.apache.james.imap.mailbox.MailboxSession;
import org.apache.james.imap.maildir.user.model.MaildirSubscription;
import org.apache.james.imap.store.StoreSubscriptionManager;
import org.apache.james.imap.store.user.model.Subscription;

public class MaildirSubscriptionManager extends StoreSubscriptionManager<Integer> {

    public MaildirSubscriptionManager(MaildirMailboxSessionMapperFactory mf) {
        super(mf);
    }

    @Override
    protected Subscription createSubscription(MailboxSession session, String mailbox) {
        return new MaildirSubscription(session.getUser().getUserName(), mailbox);
    }

}