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
package org.apache.james.imap.inmemory.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.james.imap.api.MailboxPath;
import org.apache.james.imap.inmemory.mail.model.InMemoryMailbox;
import org.apache.james.imap.mailbox.MailboxNotFoundException;
import org.apache.james.imap.mailbox.StorageException;
import org.apache.james.imap.store.mail.MailboxMapper;
import org.apache.james.imap.store.mail.model.Mailbox;
import org.apache.james.imap.store.transaction.NonTransactionalMapper;

public class InMemoryMailboxMapper extends NonTransactionalMapper implements MailboxMapper<Long> {
    
    private static final int INITIAL_SIZE = 128;
    private final Map<Long, InMemoryMailbox> mailboxesById;
    private final char delimiter;

    public InMemoryMailboxMapper(char delimiter) {
        mailboxesById = new ConcurrentHashMap<Long, InMemoryMailbox>(INITIAL_SIZE);
        this.delimiter = delimiter;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.MailboxMapper#delete(org.apache.james.imap.store.mail.model.Mailbox)
     */
    public void delete(Mailbox<Long> mailbox) throws StorageException {
        mailboxesById.remove(mailbox.getMailboxId());
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.MailboxMapper#deleteAll()
     */
    public void deleteAll() throws StorageException {
        mailboxesById.clear();
    }


    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.MailboxMapper#findMailboxById(java.lang.Object)
     */
    public Mailbox<Long> findMailboxById(Long mailboxId) throws StorageException, MailboxNotFoundException {
        Mailbox<Long> mailbox = mailboxesById.get(mailboxesById);
        if (mailbox == null) {
            throw new MailboxNotFoundException(mailboxId);
        } else {
            return mailbox;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.MailboxMapper#findMailboxByName(java.lang.String)
     */
    public synchronized Mailbox<Long> findMailboxByPath(MailboxPath path) throws StorageException, MailboxNotFoundException {
        Mailbox<Long> result = null;
        for (final InMemoryMailbox mailbox:mailboxesById.values()) {
            MailboxPath mp = new MailboxPath(mailbox.getNamespace(), mailbox.getUser(), mailbox.getName());
            if (mp.equals(path)) {
                result = mailbox;
                break;
            }
        }
        if (result == null) {
            throw new MailboxNotFoundException(path);
        } else {
            return result;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.MailboxMapper#findMailboxWithNameLike(java.lang.String)
     */
    public List<Mailbox<Long>> findMailboxWithPathLike(MailboxPath path) throws StorageException {
        final String regex = path.getName().replace("%", ".*");
        List<Mailbox<Long>> results = new ArrayList<Mailbox<Long>>();
        for (final InMemoryMailbox mailbox:mailboxesById.values()) {
            if (mailbox.getName().matches(regex)) {
                results.add(mailbox);
            }
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.MailboxMapper#save(org.apache.james.imap.store.mail.model.Mailbox)
     */
    public void save(Mailbox<Long> mailbox) throws StorageException {
        mailboxesById.put(mailbox.getMailboxId(), (InMemoryMailbox) mailbox);
    }

    /**
     * Do nothing
     */
    public void endRequest() {
        // Do nothing
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.MailboxMapper#hasChildren(org.apache.james.imap.store.mail.model.Mailbox)
     */
    public boolean hasChildren(Mailbox<Long> mailbox) throws StorageException,
            MailboxNotFoundException {
        String mailboxName = mailbox.getName() + delimiter;
        for (final InMemoryMailbox box:mailboxesById.values()) {
            if (box.getName().startsWith(mailboxName)) {
                return true;
            }
        }
        return false;
    }
    
}
