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
package org.apache.james.imap.jcr.mail.model;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.logging.Log;
import org.apache.jackrabbit.JcrConstants;
import org.apache.james.imap.jcr.JCRImapConstants;
import org.apache.james.imap.jcr.Persistent;
import org.apache.james.imap.store.mail.model.Mailbox;


/**
 * JCR implementation of a {@link Mailbox}
 */
public class JCRMailbox implements Mailbox, JCRImapConstants, Persistent{

    public final static String ID_PROPERTY = PROPERTY_PREFIX + "mailboxId";
    public final static String NAME_PROPERTY = PROPERTY_PREFIX + "mailboxName";
    public final static String UIDVALIDITY_PROPERTY = PROPERTY_PREFIX + "mailboxUidValidity";
    public final static String LASTUID_PROPERTY = PROPERTY_PREFIX + "mailboxLastUid";

    private long id = -1;
    private String name;
    private long uidValidity;
    private long lastUid = 0;
    private final Log logger;
    private Node node;
    
    public JCRMailbox(final String name, final long uidValidity, final long lastUid, Log logger) {
        this.name = name;
        this.uidValidity = uidValidity;
        this.lastUid = lastUid;
        this.logger = logger;
    }
    
    public JCRMailbox( final String name, final long uidValidity, Log logger) {
        this.name = name;
        this.uidValidity = uidValidity;
        this.logger = logger;
    }
    
    public JCRMailbox( final Node node, final Log logger) {
        this.node = node;
        this.logger = logger;
    }
    
    
    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.Mailbox#consumeUid()
     */
    public void consumeUid() {
        if (isPersistent()) {
            try {
                long uid = node.getProperty(LASTUID_PROPERTY).getLong();
                uid++;
                node.setProperty(LASTUID_PROPERTY, uid);
            } catch (RepositoryException e) {
                logger.error("Unable to access property " + LASTUID_PROPERTY, e);
            }
        } else {
            lastUid++;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.Mailbox#getLastUid()
     */
    public long getLastUid() {
        if (isPersistent()) {
            try {
                return node.getProperty(LASTUID_PROPERTY).getLong();
            } catch (RepositoryException e) {
                logger.error("Unable to access property " + ID_PROPERTY, e);
            }
        }
        return lastUid;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.Mailbox#getMailboxId()
     */
    public long getMailboxId() {
        if (isPersistent()) {
            try {
                return node.getProperty(ID_PROPERTY).getLong();
            } catch (RepositoryException e) {
                logger.error("Unable to access property " + ID_PROPERTY, e);
            }
        }
        return id;
    }

   
    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.Mailbox#getName()
     */
    public String getName() {
        if (isPersistent()) {
            try {
                return node.getProperty(NAME_PROPERTY).getString();
            } catch (RepositoryException e) {
                logger.error("Unable to access property " + NAME_PROPERTY, e);
            }
        }
        return name;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.Mailbox#getUidValidity()
     */
    public long getUidValidity() {
        if (isPersistent()) {
            try {
                return node.getProperty(UIDVALIDITY_PROPERTY).getLong();
            } catch (RepositoryException e) {
                logger.error("Unable to access property " + UIDVALIDITY_PROPERTY, e);
            }
        }
        return uidValidity;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.store.mail.model.Mailbox#setName(java.lang.String)
     */
    public void setName(String name) {     
        this.name = name;
    }


    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.jcr.Persistent#getNode()
     */
    public Node getNode() {
        return node;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.jcr.Persistent#isPersistent()
     */
    public boolean isPersistent() {
        return node != null;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.jcr.Persistent#merge(javax.jcr.Node)
     */
    public void  merge(Node node) throws RepositoryException {
        node.setProperty(ID_PROPERTY,  getMailboxId());
        node.setProperty(NAME_PROPERTY, getName());
        node.setProperty(UIDVALIDITY_PROPERTY, getUidValidity());
        node.setProperty(LASTUID_PROPERTY, getLastUid());   
        
        this.node = node;
        id = 0;
        lastUid = 0;
        name = null;
        uidValidity = 0;
    }
    
    public String getUUID() {
        if (isPersistent()) {
            try {
                return node.getUUID();
            } catch (RepositoryException e) {
                logger.error("Unable to access property " + JcrConstants.JCR_UUID, e);
            }
        }
        return null;  
    }
}