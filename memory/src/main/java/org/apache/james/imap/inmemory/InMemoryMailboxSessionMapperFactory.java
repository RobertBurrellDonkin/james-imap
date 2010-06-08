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
package org.apache.james.imap.inmemory;

import org.apache.james.imap.inmemory.mail.InMemoryMailboxMapper;
import org.apache.james.imap.inmemory.mail.InMemoryMessageMapper;
import org.apache.james.imap.inmemory.user.InMemorySubscriptionMapper;
import org.apache.james.imap.mailbox.MailboxException;
import org.apache.james.imap.mailbox.MailboxSession;
import org.apache.james.imap.mailbox.SubscriptionException;
import org.apache.james.imap.store.MailboxSessionMapperFactory;
import org.apache.james.imap.store.mail.MailboxMapper;
import org.apache.james.imap.store.mail.MessageMapper;
import org.apache.james.imap.store.transaction.TransactionalMapper;
import org.apache.james.imap.store.user.SubscriptionMapper;

public class InMemoryMailboxSessionMapperFactory extends MailboxSessionMapperFactory<Long> {

    private MailboxMapper<Long> mailboxMapper;
    private MessageMapper<Long> messageMapper;
    private SubscriptionMapper subscriptionMapper;
    
    public InMemoryMailboxSessionMapperFactory() {
        mailboxMapper = new InMemoryMailboxMapper();
        messageMapper = new InMemoryMessageMapper();
        subscriptionMapper = new InMemorySubscriptionMapper();
    }
    
    public MailboxMapper<Long> getMailboxMapper(MailboxSession session) throws MailboxException {
        return mailboxMapper;
    }
    
    @Override
    public MailboxMapper<Long> createMailboxMapper(MailboxSession session) throws MailboxException {
        return null;
    }

    @Override
    public MessageMapper<Long> getMessageMapper(MailboxSession session) throws MailboxException {
        return messageMapper;
    }

    @Override
    public MessageMapper<Long> createMessageMapper(MailboxSession session) throws MailboxException {
        return null;
    }

    @Override
    public SubscriptionMapper getSubscriptionMapper(MailboxSession session) throws SubscriptionException {
        return subscriptionMapper;
    }

    @Override
    public SubscriptionMapper createSubscriptionMapper(MailboxSession session) throws SubscriptionException {
        return null;
    }
    
    public void deleteAll() throws MailboxException {
        final MailboxMapper<Long> mapper = mailboxMapper;
        mapper.execute(new TransactionalMapper.Transaction() {

            public void run() throws MailboxException {
                mapper.deleteAll(); 
            }
            
        });
        ((InMemoryMessageMapper) messageMapper).deleteAll();
        ((InMemorySubscriptionMapper) subscriptionMapper).deleteAll();
    }

}