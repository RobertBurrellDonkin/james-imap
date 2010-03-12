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
package org.apache.james.imap.jcr.mail;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.logging.Log;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.util.Text;
import org.apache.james.imap.api.display.HumanReadableText;
import org.apache.james.imap.jcr.JCRImapConstants;
import org.apache.james.imap.jcr.JCRUtils;
import org.apache.james.imap.jcr.mail.model.JCRMailbox;
import org.apache.james.imap.mailbox.MailboxNotFoundException;
import org.apache.james.imap.mailbox.StorageException;
import org.apache.james.imap.store.mail.MailboxMapper;
import org.apache.james.imap.store.mail.model.Mailbox;
import org.apache.james.imap.store.transaction.NonTransactionalMapper;

/**
 * JCR implementation of a MailboxMapper
 * 
 * 
 */
public class JCRMailboxMapper extends NonTransactionalMapper implements MailboxMapper,JCRImapConstants {

    private final static String WILDCARD = "*";
    private final Session session;
    private final String PATH = PROPERTY_PREFIX + "mailboxes";
    private Log logger;

    public JCRMailboxMapper(final Session session, final Log logger) {
        this.session = session;
        this.logger = logger;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.james.imap.store.mail.MailboxMapper#countMailboxesWithName
     * (java.lang.String)
     */
    public long countMailboxesWithName(String name) throws StorageException {
        String nodeName = Text.unescapeIllegalJcrChars(name);

        try {
            boolean found = session.getRootNode().hasNode(JCRUtils.createPath(PATH, nodeName));
            if (found) {
                return 1;
            }
        } catch (PathNotFoundException e) {
            // not found
        } catch (RepositoryException e) {
            throw new StorageException(HumanReadableText.COUNT_FAILED, e);
        }
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.james.imap.store.mail.MailboxMapper#delete(org.apache.james
     * .imap.store.mail.model.Mailbox)
     */
    public void delete(Mailbox mailbox) throws StorageException {
        String nodeName = Text.unescapeIllegalJcrChars(mailbox.getName());
        try {
            session.getRootNode().getNode(JCRUtils.createPath(PATH, nodeName)).remove();
        } catch (PathNotFoundException e) {
            // mailbox does not exists..
        } catch (RepositoryException e) {
            throw new StorageException(HumanReadableText.DELETED_FAILED, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.imap.store.mail.MailboxMapper#deleteAll()
     */
    public void deleteAll() throws StorageException {
        try {
            session.getRootNode().getNode(PATH).remove();
            session.save();

        } catch (PathNotFoundException e) {
            // nothing todo
        } catch (RepositoryException e) {
            throw new StorageException(HumanReadableText.DELETED_FAILED, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.james.imap.store.mail.MailboxMapper#existsMailboxStartingWith
     * (java.lang.String)
     */
    public boolean existsMailboxStartingWith(String mailboxName) throws StorageException {
        try {
            return session.getRootNode().getNodes(JCRUtils.createPath(PATH,  mailboxName) + WILDCARD).hasNext();
        } catch (RepositoryException e) {
            throw new StorageException(HumanReadableText.SEARCH_FAILED, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.imap.store.mail.MailboxMapper#findMailboxById(long)
     */
    public Mailbox findMailboxById(long mailboxId) throws StorageException, MailboxNotFoundException {
        /*
        try {
            QueryManager manager = session.getWorkspace().getQueryManager();
            String queryString = JCRUtils.createPath(session.getRootNode().getPath(), PATH) + "//element(" + mailboxId + ")," + JCRMailbox.ID_PROPERTY + ")";
            Query query = manager.createQuery(queryString, Query.XPATH);

            NodeIterator nodes = query.execute().getNodes();
            if (nodes.hasNext() == false) {
                throw new MailboxNotFoundException(mailboxId);
            } else {
                return new JCRMailbox(nodes.nextNode(),logger);
            }
        } catch (PathNotFoundException e) {
            throw new MailboxNotFoundException(mailboxId);
        } catch (RepositoryException e) {
            throw new StorageException(HumanReadableText.SEARCH_FAILED, e);
        }
        */
        throw new StorageException(HumanReadableText.UNSUPPORTED,null);
    }

    public Mailbox findMailboxByUUID(String uuid) throws StorageException, MailboxNotFoundException {
        try {
            return new JCRMailbox(session.getNodeByUUID(uuid),logger);
        } catch (PathNotFoundException e) {
            throw new MailboxNotFoundException(uuid);
        } catch (RepositoryException e) {
            throw new StorageException(HumanReadableText.SEARCH_FAILED, e);
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.james.imap.store.mail.MailboxMapper#findMailboxByName(java
     * .lang.String)
     */
    public Mailbox findMailboxByName(String name) throws StorageException, MailboxNotFoundException {
        try {
            Node node = session.getRootNode().getNode(JCRUtils.createPath(PATH, name));
            return new JCRMailbox(node, logger);
        } catch (PathNotFoundException e) {
            throw new MailboxNotFoundException(name);
        } catch (RepositoryException e) {
            throw new StorageException(HumanReadableText.SEARCH_FAILED, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.james.imap.store.mail.MailboxMapper#findMailboxWithNameLike
     * (java.lang.String)
     */
    public List<Mailbox> findMailboxWithNameLike(String name) throws StorageException {
        List<Mailbox> mailboxList = new ArrayList<Mailbox>();
        try {
            NodeIterator it = session.getRootNode().getNodes(PATH + NODE_DELIMITER + WILDCARD + name + WILDCARD);
            while (it.hasNext()) {
                mailboxList.add(new JCRMailbox(it.nextNode(), logger));
            }
        } catch (PathNotFoundException e) {
            // nothing todo
        } catch (RepositoryException e) {
            throw new StorageException(HumanReadableText.SEARCH_FAILED, e);
        }
        return mailboxList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.james.imap.store.mail.MailboxMapper#save(org.apache.james.
     * imap.store.mail.model.Mailbox)
     */
    public void save(Mailbox mailbox) throws StorageException {
        String nodePath = JCRUtils.createPath(PATH,mailbox.getName());
        try {
            Node node;
            if (session.getRootNode().hasNode(nodePath)) {
                node = session.getRootNode().getNode(PATH);
            } else {
                node = session.getRootNode().addNode(PATH, JcrConstants.MIX_REFERENCEABLE);
            }
            ((JCRMailbox)mailbox).merge(node);
            session.save();
        } catch (RepositoryException e) {
            throw new StorageException(HumanReadableText.SAVE_FAILED, e);
        }
    }
    
    public Mailbox consumeNextUid(String uuid) throws StorageException, MailboxNotFoundException {

        JCRMailbox mailbox = (JCRMailbox) findMailboxByUUID(uuid);
        mailbox.consumeUid();
        try {
            session.save();
        } catch (PathNotFoundException e) {
            throw new MailboxNotFoundException(uuid);
        } catch (RepositoryException e) {
            throw new StorageException(HumanReadableText.SAVE_FAILED, e);
        }
        return mailbox;

    }

}
