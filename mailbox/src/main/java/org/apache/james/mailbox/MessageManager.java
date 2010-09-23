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

package org.apache.james.mailbox;

import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.Flags;

import org.apache.james.mailbox.MessageResult.FetchGroup;


/**
 * Interface which represent a Mailbox
 * 
 * A {@link MessageManager} should be valid for the whole {@link MailboxSession}
 *
 */
public interface MessageManager {

    /**
     * Return the count 
     * 
     * @param mailboxSession
     * @return count
     * @throws MailboxException
     */
    long getMessageCount(MailboxSession mailboxSession) throws MailboxException;

    /**
     * Return if the Mailbox is writable
     * 
     * @param session
     * @return writable
     */
    boolean isWriteable(MailboxSession session);

    /**
     * Searches for messages matching the given query. The result must be ordered 
     * @param mailboxSession not null
     * @return uid iterator
     * @throws UnsupportedCriteriaException
     *             when any of the search parameters are not supported by this
     *             mailbox
     * @throws MailboxException when search fails for other reasons
     */
    Iterator<Long> search(SearchQuery searchQuery, MailboxSession mailboxSession) throws MailboxException;

    /**
     * Expunges messages in the given range from this mailbox.
     * @param set not null
     * @param mailboxSession not null
     * @return 
     * @throws MailboxException
     *             if anything went wrong
     */
    Iterator<Long> expunge(MessageRange set, MailboxSession mailboxSession) throws MailboxException;

    /**
     * Sets flags on messages within the given range.
     * The new flags are returned for each message altered.
     * 
     * @param flags
     *            Flags to be set
     * @param value
     *            true = set, false = unset
     * @param replace
     *            replace all Flags with this flags, value has to be true
     * @param set
     *            the range of messages
     * @param mailboxSession not null
     * @return new flags indexed by UID
     * @throws MailboxException
     */
    Map<Long, Flags> setFlags(Flags flags, boolean value, boolean replace,
            MessageRange set, MailboxSession mailboxSession) throws MailboxException;

    /**
     * Appends a message to this mailbox.
     * @param internalDate the time of addition to be set, not null
     * @param mailboxSession not null
     * @param isRecent true when the message should be marked recent,
     * false otherwise
     * @param flags optionally set these flags on created message, 
     * or null when no additional flags should be set
     * @return uid for the newly added message
     * @throws MailboxException when message cannot be appended
     */
    long appendMessage(InputStream msgIn, Date internalDate, MailboxSession mailboxSession, 
            boolean isRecent, Flags flags) throws MailboxException;

    /**
     * Gets messages in the given range.
     * @param set
     * @param mailboxSession not null
     * @return MessageResult with the fields defined by FetchGroup
     * @throws MailboxException
     */
    Iterator<MessageResult> getMessages(MessageRange set, FetchGroup fetchGroup, 
            MailboxSession mailboxSession) throws MailboxException;
    
    /**
     * Gets current meta data for the mailbox.
     * Consolidates common calls together to allow improved performance.
     * The meta-data returned should be immutable and represent the current state
     * of the mailbox.
     * @param resetRecent true when recent flags should be reset,
     * false otherwise
     * @param mailboxSession context, not null
     * @param fetchGroup describes which optional data should be returned
     * @return meta data, not null
     * @throws MailboxException
     */
    MetaData getMetaData(boolean resetRecent, MailboxSession mailboxSession, MessageManager.MetaData.FetchGroup fetchGroup) throws MailboxException;
    
    /**
     * Meta data about the current state of the mailbox.
     */
    public interface MetaData {
        
        /**
         * Describes the optional data types.
         */
        public enum FetchGroup {
            NO_UNSEEN, UNSEEN_COUNT, FIRST_UNSEEN
        };
        
        /**
         * Gets the UIDs of recent messages. 
         * @return the uids flagged RECENT in this mailbox, 
         */
        public List<Long> getRecent();

        /**
         * Gets the number of recent messages.
         * @return the number of messages flagged RECENT in this mailbox
         */
        public long countRecent();
        
        /**
         * Gets the flags which can be stored by this mailbox.
         * @return Flags that can be stored
         */
        Flags getPermanentFlags();
        
        /**
         * Gets the UIDVALIDITY.
         * @return UIDVALIDITY
         */
        long getUidValidity();
        

        /**
         * Gets the next UID predicted. The returned UID is not guaranteed to be the 
         * one that is assigned to the next message. Its only guaranteed that it will be 
         * at least equals or bigger then the value
         * 
         * @param mailboxSession not null
         * @return the uid that will be assigned to the next appended message
         */
        long getUidNext();
        
        /**
         * Gets the number of messages that this mailbox contains.
         * @return number of messages contained
         */
        long getMessageCount();
        
        /**
         * Gets the number of unseen messages contained in this mailbox.
         * This is an optional property.
         * @return number of unseen messages contained 
         * or zero when this optional data has not been requested
         * @see FetchGroup#UNSEEN_COUNT
         */
        long getUnseenCount();
        
        /**
         * Gets the UID of the first unseen message.
         * This is an optional property.
         * @return uid of the first unseen message,
         * or null when there are no unseen messages
         * @see FetchGroup#FIRST_UNSEEN
         */
        Long getFirstUnseen();
        
        /**
         * Is this mailbox writable?
         * @return true if read-write, 
         * false if read only
         */
        boolean isWriteable();

    }
}
