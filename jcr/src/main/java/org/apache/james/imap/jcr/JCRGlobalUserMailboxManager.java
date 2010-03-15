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
package org.apache.james.imap.jcr;

import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.james.imap.api.display.HumanReadableText;
import org.apache.james.imap.jcr.mail.JCRMailboxMapper;
import org.apache.james.imap.mailbox.MailboxException;
import org.apache.james.imap.mailbox.MailboxSession;
import org.apache.james.imap.store.Authenticator;
import org.apache.james.imap.store.StoreMailbox;
import org.apache.james.imap.store.Subscriber;
import org.apache.james.imap.store.mail.MailboxMapper;
import org.apache.james.imap.store.mail.model.Mailbox;
import org.apache.james.imap.store.transaction.TransactionalMapper;

/**
 * JCR based MailboxManager which use the same username and password to obtain a
 * JCR Session for every MailboxSession
 * 
 * 
 */
public class JCRGlobalUserMailboxManager extends JCRMailboxManager {

    private final String username;
    private final char[] password; 

    public JCRGlobalUserMailboxManager(final Authenticator authenticator, final Subscriber subscriber, final Repository repository, final String workspace, final String username, final String password) {
        super(authenticator, subscriber, repository, workspace);

        this.username = username;
        if (password != null) {
        	this.password = password.toCharArray();
        } else {
        	this.password = new char[0];
        }
    }


    public void deleteEverything() throws MailboxException {
        Session session = getSession(null);
        final MailboxMapper mapper = new JCRMailboxMapper(session,getLog());
        mapper.execute(new TransactionalMapper.Transaction() {

            public void run() throws MailboxException {
                mapper.deleteAll(); 
                mailboxes.clear();
            }
            
        });
        session.logout();
    }
    
    @Override
    protected StoreMailbox createMailbox(Mailbox mailboxRow, MailboxSession session) {
        JCRMailbox mailbox = new JCRGlobalMailbox((org.apache.james.imap.jcr.mail.model.JCRMailbox) mailboxRow, session, getRepository(), getWorkspace(), username, password, getLog());
        return mailbox;
    }
    
    @Override
    protected Session getSession(MailboxSession s) throws MailboxException {
        try {
            return getRepository().login(new SimpleCredentials(username, password), getWorkspace());
        } catch (LoginException e) {
            throw new MailboxException(HumanReadableText.INVALID_LOGIN, e);
        } catch (NoSuchWorkspaceException e) {
            throw new MailboxException(HumanReadableText.GENERIC_FAILURE_DURING_PROCESSING, e);
        } catch (RepositoryException e) {
            throw new MailboxException(HumanReadableText.GENERIC_FAILURE_DURING_PROCESSING, e);

        }
    }

}
