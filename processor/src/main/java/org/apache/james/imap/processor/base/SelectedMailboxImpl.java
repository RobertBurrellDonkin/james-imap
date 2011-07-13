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

package org.apache.james.imap.processor.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.Flags;

import org.apache.james.imap.api.ImapSessionUtils;
import org.apache.james.imap.api.process.ImapSession;
import org.apache.james.imap.api.process.SelectedMailbox;
import org.apache.james.mailbox.MailboxException;
import org.apache.james.mailbox.MailboxManager;
import org.apache.james.mailbox.MailboxPath;
import org.apache.james.mailbox.MailboxSession;

/**
 * Default implementation of {@link SelectedMailbox}
 */
public class SelectedMailboxImpl implements SelectedMailbox {

    private final MailboxEventAnalyser events;

    private final UidToMsnConverter converter;

    private final Set<Long> recentUids;

    private boolean recentUidRemoved;

    private final boolean condstore;

    public SelectedMailboxImpl(final MailboxManager mailboxManager, final Iterator<Long> uids, final Flags applicableFlags, final ImapSession session, final MailboxPath path, final boolean condstore) throws MailboxException {
        recentUids = new TreeSet<Long>();
        recentUidRemoved = false;
        MailboxSession mailboxSession = ImapSessionUtils.getMailboxSession(session);
        events = new MailboxEventAnalyser(session, path, applicableFlags);
        // Ignore events from our session
        events.setSilentFlagChanges(true);
        mailboxManager.addListener(path, events, mailboxSession);
        converter = new UidToMsnConverter(session, uids);
        mailboxManager.addListener(path, converter, mailboxSession);
        this.condstore = condstore;
    }

    /**
     * @see org.apache.james.imap.api.process.SelectedMailbox#deselect()
     */
    public void deselect() {
        converter.close();
        events.close();
        recentUids.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.imap.api.process.SelectedMailbox#isSizeChanged()
     */
    public boolean isSizeChanged() {
        return events.isSizeChanged();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.imap.api.process.SelectedMailbox#msn(long)
     */
    public int msn(long uid) {
        return converter.getMsn(uid);
    }

    /**
     * Is the mailbox deleted?
     * 
     * @return true when the mailbox has been deleted by another session, false
     *         otherwise
     */
    public boolean isDeletedByOtherSession() {
        return events.isDeletedByOtherSession();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.imap.api.process.SelectedMailbox#uid(int)
     */
    public long uid(int msn) {
        return converter.getUid(msn);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.imap.api.process.SelectedMailbox#removeRecent(long)
     */
    public boolean removeRecent(long uid) {
        final boolean result = recentUids.remove(uid);
        if (result) {
            recentUidRemoved = true;
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.imap.api.process.SelectedMailbox#addRecent(long)
     */
    public boolean addRecent(long uid) {
        return recentUids.add(uid);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.imap.api.process.SelectedMailbox#getRecent()
     */
    public Collection<Long> getRecent() {
        checkExpungedRecents();
        return new ArrayList<Long>(recentUids);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.imap.api.process.SelectedMailbox#recentCount()
     */
    public int recentCount() {
        checkExpungedRecents();
        return recentUids.size();
    }

    public long existsCount() {
        return converter.getCount();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.imap.api.process.SelectedMailbox#getPath()
     */
    public MailboxPath getPath() {
        return events.getMailboxPath();
    }

    private void checkExpungedRecents() {
        for (final long uid : events.expungedUids()) {
            removeRecent(uid);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.imap.api.process.SelectedMailbox#isRecent(long)
     */
    public boolean isRecent(long uid) {
        return recentUids.contains(uid);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.james.imap.api.process.SelectedMailbox#isRecentUidRemoved()
     */
    public boolean isRecentUidRemoved() {
        return recentUidRemoved;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.james.imap.api.process.SelectedMailbox#resetRecentUidRemoved()
     */
    public void resetRecentUidRemoved() {
        recentUidRemoved = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.imap.api.process.SelectedMailbox#resetEvents()
     */
    public void resetEvents() {
        events.reset();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.imap.api.process.SelectedMailbox#expungedUids()
     */
    public Collection<Long> expungedUids() {
        return events.expungedUids();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.james.imap.api.process.SelectedMailbox#remove(java.lang.Long)
     */
    public int remove(Long uid) {
        final int result = msn(uid);
        converter.expunge(uid);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.imap.api.process.SelectedMailbox#flagUpdateUids()
     */
    public Collection<Long> flagUpdateUids() {
        return events.flagUpdateUids();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.imap.api.process.SelectedMailbox#getFirstUid()
     */
    public long getFirstUid() {
        return converter.getFirstUid();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.imap.api.process.SelectedMailbox#getLastUid()
     */
    public long getLastUid() {
        return converter.getLastUid();
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.api.process.SelectedMailbox#resetExpungedUids()
     */
    public void resetExpungedUids() {
        events.resetExpungedUids();
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.api.process.SelectedMailbox#getApplicableFlags()
     */
    public Flags getApplicableFlags() {
        return events.getApplicableFlags();
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.api.process.SelectedMailbox#hasNewApplicableFlags()
     */
    public boolean hasNewApplicableFlags() {
        return events.hasNewApplicableFlags();
    }
    
    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.api.process.SelectedMailbox#resetNewApplicableFlags()
     */
    public void resetNewApplicableFlags() {
        events.resetNewApplicableFlags();
    }

    @Override
    public boolean getCondstore() {
        return condstore;
    }
}
