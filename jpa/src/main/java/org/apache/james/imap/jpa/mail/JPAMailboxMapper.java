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

package org.apache.james.imap.jpa.mail;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import org.apache.james.imap.api.display.HumanReadableTextKey;
import org.apache.james.imap.jpa.mail.model.JPAMailbox;
import org.apache.james.imap.mailbox.MailboxNotFoundException;
import org.apache.james.imap.mailbox.StorageException;
import org.apache.james.imap.store.mail.MailboxMapper;
import org.apache.james.imap.store.mail.model.Mailbox;

/**
 * Data access management for mailbox.
 */
public abstract class JPAMailboxMapper extends Mapper implements MailboxMapper {

    private static final char SQL_WILDCARD_CHAR = '%';
    
    public JPAMailboxMapper(EntityManager entityManager) {
        super(entityManager);
    }

    /**
     * @see org.apache.james.imap.jpa.mail.MailboxMapper#hasChildren
     */
    public boolean hasChildren(String mailboxName, char hierarchyDeliminator) throws StorageException {
        
        final String name = mailboxName + hierarchyDeliminator + SQL_WILDCARD_CHAR; 
        final Long numberOfChildMailboxes = (Long) entityManager.createNamedQuery("countMailboxesWithNameLike").setParameter("nameParam", name).getSingleResult();
        return numberOfChildMailboxes != null && numberOfChildMailboxes > 0;
    }



    /**
     * @see org.apache.james.imap.jpa.mail.MailboxMapper#save(org.apache.james.imap.jpa.mail.model.JPAMailbox)
     */
    public void save(Mailbox mailbox) throws StorageException {
        try {
            entityManager.persist(mailbox);
        } catch (PersistenceException e) {
            throw new StorageException(HumanReadableTextKey.SAVE_FAILED, e);
        } 
    }

    /**
     * @see org.apache.james.imap.jpa.mail.MailboxMapper#findMailboxByName(java.lang.String)
     */
    public Mailbox findMailboxByName(String name) throws StorageException, MailboxNotFoundException {
        try {
            return (JPAMailbox) entityManager.createNamedQuery("findMailboxByName").setParameter("nameParam", name).getSingleResult();
        } catch (NoResultException e) {
            throw new MailboxNotFoundException(name);
            
        } catch (PersistenceException e) {
            throw new StorageException(HumanReadableTextKey.SEARCH_FAILED, e);
        } 
    }

    /**
     * @see org.apache.james.imap.jpa.mail.MailboxMapper#delete(org.apache.james.imap.jpa.mail.model.JPAMailbox)
     */
    public void delete(Mailbox mailbox) throws StorageException {
        try {  
            entityManager.remove(mailbox);
        } catch (PersistenceException e) {
            throw new StorageException(HumanReadableTextKey.DELETED_FAILED, e);
        } 
    }

    /**
     * @see org.apache.james.imap.jpa.mail.MailboxMapper#findMailboxWithNameLike(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<Mailbox> findMailboxWithNameLike(String name) throws StorageException {
        try {
            return entityManager.createNamedQuery("findMailboxWithNameLike").setParameter("nameParam", name).getResultList();
        } catch (PersistenceException e) {
            throw new StorageException(HumanReadableTextKey.SEARCH_FAILED, e);
        } 
    }

    /**
     * @see org.apache.james.imap.jpa.mail.MailboxMapper#deleteAll()
     */
    public void deleteAll() throws StorageException {
        try {
            entityManager.createNamedQuery("deleteAll").executeUpdate();
        } catch (PersistenceException e) {
            throw new StorageException(HumanReadableTextKey.DELETED_FAILED, e);
        } 
    }

    /**
     * @see org.apache.james.imap.jpa.mail.MailboxMapper#countMailboxesWithName(java.lang.String)
     */
    public long countMailboxesWithName(String name) throws StorageException {
        try {
            return (Long) entityManager.createNamedQuery("countMailboxesWithName").setParameter("nameParam", name).getSingleResult();
        } catch (PersistenceException e) {
            throw new StorageException(HumanReadableTextKey.COUNT_FAILED, e);
        } 
    }

    /**
     * @see org.apache.james.imap.jpa.mail.MailboxMapper#findMailboxById(long)
     */
    public Mailbox findMailboxById(long mailboxId) throws StorageException, MailboxNotFoundException  {
        try {
            return (JPAMailbox) entityManager.createNamedQuery("findMailboxById").setParameter("idParam", mailboxId).getSingleResult();
        } catch (NoResultException e) {
            throw new MailboxNotFoundException(mailboxId);   
        } catch (PersistenceException e) {
            throw new StorageException(HumanReadableTextKey.SEARCH_FAILED, e);
        } 
    }

    /**
     * @see org.apache.james.imap.jpa.mail.MailboxMapper#consumeNextUid(long)
     */
    public Mailbox consumeNextUid(long mailboxId) throws StorageException, MailboxNotFoundException {
        try {
            return doConsumeNextUid(mailboxId);
        } catch (NoResultException e) {
            throw new MailboxNotFoundException(mailboxId);
        } catch (PersistenceException e) {
            throw new StorageException(HumanReadableTextKey.COMSUME_UID_FAILED, e);
        } 
    }

    /** Locking is required and is implementation specific */
    protected abstract JPAMailbox doConsumeNextUid(long mailboxId) throws PersistenceException;
}
