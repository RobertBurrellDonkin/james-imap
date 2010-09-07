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
package org.apache.james.mailbox.maildir;

import org.apache.james.mailbox.MailboxException;
import org.apache.james.mailbox.MailboxPath;
import org.apache.james.mailbox.MailboxSession;
import org.apache.james.mailbox.maildir.mail.model.MaildirMailbox;
import org.apache.james.mailbox.store.Authenticator;
import org.apache.james.mailbox.store.MailboxSessionMapperFactory;
import org.apache.james.mailbox.store.MapperStoreMessageManager;
import org.apache.james.mailbox.store.StoreMailboxManager;
import org.apache.james.mailbox.store.mail.MailboxMapper;
import org.apache.james.mailbox.store.mail.model.Mailbox;
import org.apache.james.mailbox.util.MailboxEventDispatcher;

public class MaildirMailboxManager extends StoreMailboxManager<Integer> {

    public MaildirMailboxManager(
            MailboxSessionMapperFactory<Integer> mailboxSessionMapperFactory,
            Authenticator authenticator) {
        super(mailboxSessionMapperFactory, authenticator);
    }

    @Override
    protected MapperStoreMessageManager<Integer> createMessageManager(MailboxEventDispatcher dispatcher,
            Mailbox<Integer> mailboxEntiy, MailboxSession session) throws MailboxException {
        return new MaildirMessageManager(mailboxSessionMapperFactory, dispatcher, mailboxEntiy);
    }

    @Override
    protected void doCreateMailbox(MailboxPath mailboxPath, MailboxSession session)
            throws MailboxException {
        final Mailbox<Integer> mailbox = new MaildirMailbox(mailboxPath, randomUidValidity(), 0);
        final MailboxMapper<Integer> mapper = mailboxSessionMapperFactory.getMailboxMapper(session);
        mapper.save(mailbox);
    }

}