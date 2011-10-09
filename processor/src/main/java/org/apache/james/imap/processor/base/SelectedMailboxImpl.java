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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.mail.Flags;
import javax.mail.Flags.Flag;

import org.apache.james.imap.api.ImapSessionUtils;
import org.apache.james.imap.api.process.ImapSession;
import org.apache.james.imap.api.process.SelectedMailbox;
import org.apache.james.mailbox.MailboxException;
import org.apache.james.mailbox.MailboxListener;
import org.apache.james.mailbox.MailboxManager;
import org.apache.james.mailbox.MailboxPath;
import org.apache.james.mailbox.MailboxSession;
import org.apache.james.mailbox.MessageRange;
import org.apache.james.mailbox.MessageResult;
import org.apache.james.mailbox.MessageResultIterator;
import org.apache.james.mailbox.UpdatedFlags;

/**
 * Default implementation of {@link SelectedMailbox}
 */
public class SelectedMailboxImpl implements SelectedMailbox, MailboxListener{

    private final Set<Long> recentUids;

    private boolean recentUidRemoved;

    private MailboxManager mailboxManager;

    private MailboxPath path;

    private ImapSession session;
    

    private final static Flags FLAGS = new Flags();
    static {
        FLAGS.add(Flags.Flag.ANSWERED);
        FLAGS.add(Flags.Flag.DELETED);
        FLAGS.add(Flags.Flag.DRAFT);
        FLAGS.add(Flags.Flag.FLAGGED);
        FLAGS.add(Flags.Flag.SEEN);
    }
    
    private final long sessionId;
    private Set<Long> flagUpdateUids;
    private Flags.Flag uninterestingFlag;
    private Set<Long> expungedUids;

    private boolean isDeletedByOtherSession = false;
    private boolean sizeChanged = false;
    private boolean silentFlagChanges = false;
    private Flags applicableFlags;
    private boolean applicableFlagsChanged;
    
    private SortedMap<Integer, Long> msnToUid;

    private SortedMap<Long, Integer> uidToMsn;

    private long highestUid = 0;

    private int highestMsn = 0;
    
    public SelectedMailboxImpl(final MailboxManager mailboxManager, final ImapSession session, final MailboxPath path) throws MailboxException {
        recentUids = new TreeSet<Long>();
        recentUidRemoved = false;
        this.session = session;
        this.sessionId = ImapSessionUtils.getMailboxSession(session).getSessionId();
        flagUpdateUids = new TreeSet<Long>();
        expungedUids = new TreeSet<Long>();
        uninterestingFlag = Flags.Flag.RECENT;
        this.mailboxManager = mailboxManager;
        
        // Ignore events from our session
        setSilentFlagChanges(true);
        this.path = path;
        this.session = session;

        msnToUid = new TreeMap<Integer, Long>();
        uidToMsn = new TreeMap<Long, Integer>();

        
        init();
    }
 

    private synchronized void init() throws MailboxException {
        MailboxSession mailboxSession = ImapSessionUtils.getMailboxSession(session);
        
        mailboxManager.addListener(path, this, mailboxSession);

        MessageResultIterator messages = mailboxManager.getMailbox(path, mailboxSession).getMessages(MessageRange.all(), FetchGroupImpl.MINIMAL, mailboxSession);
        Flags applicableFlags = new Flags(FLAGS);
        while(messages.hasNext()) {
            MessageResult mr = messages.next();
            applicableFlags.add(mr.getFlags());
            add(mr.getUid());
        }
        
      
        // \RECENT is not a applicable flag in imap so remove it from the list
        applicableFlags.remove(Flags.Flag.RECENT);
        this.applicableFlags = applicableFlags;
    }

    private void add(int msn, long uid) {
        if (uid > highestUid) {
            highestUid = uid;
        }
        msnToUid.put(msn, uid);
        uidToMsn.put(uid, msn);
    }

    /**
     * Expunge the message with the given uid
     * 
     * @param uid
     */
    private void expunge(final long uid) {
        final int msn = msn(uid);
        remove(msn, uid);
        final List<Integer> renumberMsns = new ArrayList<Integer>(msnToUid.tailMap(msn + 1).keySet());
        for (final Integer msnInteger : renumberMsns) {
            int aMsn = msnInteger.intValue();
            long aUid = uid(aMsn);
            remove(aMsn, aUid);
            add(aMsn - 1, aUid);
        }
        highestMsn--;
    }

    private void remove(int msn, long uid) {
        uidToMsn.remove(uid);
        msnToUid.remove(msn);
    }

    /**
     * Add the give uid
     * 
     * @param uid
     */
    private void add(long uid) {
        if (!uidToMsn.containsKey(uid)) {
            highestMsn++;
            add(highestMsn, uid);
        }
    }

    /**
     * @see org.apache.james.mailbox.MailboxListener#event(org.apache.james.mailbox.MailboxListener.Event)
     */


    /**
     * @see SelectedMailbox#getFirstUid()
     */
    @Override
    public synchronized long getFirstUid() {
        if (uidToMsn.isEmpty()) {
            return -1;
        } else {
            return uidToMsn.firstKey();
        }
    }

    /**
     * @see SelectedMailbox#getLastUid()
     */
    @Override
    public synchronized long getLastUid() {
        if (uidToMsn.isEmpty()) {
            return -1;
        } else {
            return uidToMsn.lastKey();
        }
    }


    @Override
    public synchronized void deselect() {
        uidToMsn.clear();
        msnToUid.clear();
        flagUpdateUids.clear();

        uninterestingFlag = null;
        expungedUids.clear();
        recentUids.clear();
        MailboxSession mailboxSession = ImapSessionUtils.getMailboxSession(session);

        try {
            mailboxManager.removeListener(path, this, mailboxSession);
        } catch (MailboxException e) {
            if (session.getLog().isInfoEnabled()) {
                session.getLog().info("Unable to remove listener " + this + " from mailbox while closing it", e);
            }
        }

    }

   
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.imap.api.process.SelectedMailbox#removeRecent(long)
     */
    @Override
    public synchronized  boolean removeRecent(long uid) {
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
    public synchronized boolean addRecent(long uid) {
        return recentUids.add(uid);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.imap.api.process.SelectedMailbox#getRecent()
     */
    @Override
    public synchronized Collection<Long> getRecent() {
        checkExpungedRecents();
        return new ArrayList<Long>(recentUids);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.imap.api.process.SelectedMailbox#recentCount()
     */
    @Override
    public synchronized int recentCount() {
        checkExpungedRecents();
        return recentUids.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.imap.api.process.SelectedMailbox#getPath()
     */
    @Override
    public synchronized MailboxPath getPath() {
        return path;
    }

    private void checkExpungedRecents() {
        for (final long uid : expungedUids()) {
            removeRecent(uid);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.imap.api.process.SelectedMailbox#isRecent(long)
     */
    @Override
    public synchronized boolean isRecent(long uid) {
        return recentUids.contains(uid);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.james.imap.api.process.SelectedMailbox#isRecentUidRemoved()
     */
    @Override
    public synchronized boolean isRecentUidRemoved() {
        return recentUidRemoved;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.james.imap.api.process.SelectedMailbox#resetRecentUidRemoved()
     */
    @Override
    public synchronized void resetRecentUidRemoved() {
        recentUidRemoved = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.imap.api.process.SelectedMailbox#resetEvents()
     */
    public synchronized void resetEvents() {
        sizeChanged = false;
        flagUpdateUids.clear();
        isDeletedByOtherSession = false;
        applicableFlagsChanged = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.james.imap.api.process.SelectedMailbox#remove(java.lang.Long)
     */
    @Override
    public synchronized  int remove(Long uid) {
        final int result = msn(uid);
        expunge(uid);
        return result;
    }



    private boolean interestingFlags(UpdatedFlags updated) {
        boolean result;
        final Iterator<Flags.Flag> it = updated.systemFlagIterator();
        if (it.hasNext()) {
            final Flags.Flag flag = it.next();
            if (flag.equals(uninterestingFlag)) {
                result = false;
            } else {
                result = true;
            }
        } else {
            result = false;
        }
        // See if we need to check the user flags
        if (result == false) {
            final Iterator<String> userIt = updated.userFlagIterator();
            result = userIt.hasNext();
        }
        return result;
    }

    
    @Override
    public synchronized void resetExpungedUids() {
        expungedUids.clear();
    }

    /**
     * Are flag changes from current session ignored?
     * 
     * @return true if any flag changes from current session will be ignored,
     *         false otherwise
     */
    public synchronized final boolean isSilentFlagChanges() {
        return silentFlagChanges;
    }

    /**
     * Sets whether changes from current session should be ignored.
     * 
     * @param silentFlagChanges
     *            true if any flag changes from current session should be
     *            ignored, false otherwise
     */
    public synchronized final void setSilentFlagChanges(boolean silentFlagChanges) {
        this.silentFlagChanges = silentFlagChanges;
    }

    /**
     * Has the size of the mailbox changed?
     * 
     * @return true if new messages have been added, false otherwise
     */
    @Override
    public synchronized final boolean isSizeChanged() {
        return sizeChanged;
    }

    /**
     * Is the mailbox deleted?
     * 
     * @return true when the mailbox has been deleted by another session, false
     *         otherwise
     */
    @Override
    public synchronized final boolean isDeletedByOtherSession() {
        return isDeletedByOtherSession;
    }

    /**
     * Return a unmodifiable {@link Collection} of uids which have updated flags
     * 
     * @return uids
     */
    @Override
    public synchronized Collection<Long> flagUpdateUids() {
        // copy the TreeSet to fix possible
        // java.util.ConcurrentModificationException
        // See IMAP-278
        return Collections.unmodifiableSet(new TreeSet<Long>(flagUpdateUids));
        
    }

    /**
     * Return a unmodifiable {@link Collection} of uids that where expunged
     * 
     * @return uids
     */
    @Override
    public synchronized Collection<Long> expungedUids() {
        // copy the TreeSet to fix possible
        // java.util.ConcurrentModificationException
        // See IMAP-278
        return Collections.unmodifiableSet(new TreeSet<Long>(expungedUids));
        
    }




    @Override
    public synchronized Flags getApplicableFlags() {
        return applicableFlags;
    }

    @Override
    public synchronized boolean hasNewApplicableFlags() {
        return applicableFlagsChanged;
    }

    @Override
    public synchronized void resetNewApplicableFlags() {
        applicableFlagsChanged = false;
    }

    @Override
    public synchronized void event(Event event) {

        // Check if the event was for the mailbox we are observing
        if (event.getMailboxPath().equals(getPath())) {
            final long eventSessionId = event.getSession().getSessionId();
            if (event instanceof MessageEvent) {
                final MessageEvent messageEvent = (MessageEvent) event;
                // final List<Long> uids = messageEvent.getUids();
                if (messageEvent instanceof Added) {
                    sizeChanged = true;
                    final List<Long> uids = ((Added) event).getUids();
                    for (int i = 0; i < uids.size(); i++) {
                        add(uids.get(i));
                    }
                } else if (messageEvent instanceof FlagsUpdated) {
                    FlagsUpdated updated = (FlagsUpdated) messageEvent;
                    List<UpdatedFlags> uFlags = updated.getUpdatedFlags();
                    if (sessionId != eventSessionId || !silentFlagChanges) {

                        for (int i = 0; i < uFlags.size(); i++) {
                            UpdatedFlags u = uFlags.get(i);

                            if (interestingFlags(u)) {

                                flagUpdateUids.add(u.getUid());

                            }
                        }
                    }

                    SelectedMailbox sm = session.getSelected();
                    if (sm != null) {
                        // We need to add the UID of the message to the recent
                        // list if we receive an flag update which contains a
                        // \RECENT flag
                        // See IMAP-287
                        List<UpdatedFlags> uflags = updated.getUpdatedFlags();
                        for (int i = 0; i < uflags.size(); i++) {
                            UpdatedFlags u = uflags.get(i);
                            Iterator<Flag> flags = u.systemFlagIterator();

                            while (flags.hasNext()) {
                                if (Flag.RECENT.equals(flags.next())) {
                                    MailboxPath path = sm.getPath();
                                    if (path != null && path.equals(event.getMailboxPath())) {
                                        sm.addRecent(u.getUid());
                                    }
                                }
                            }
                          

                        }
                    }
                    
                    int size = applicableFlags.getUserFlags().length;
                    FlagsUpdated updatedF = (FlagsUpdated) messageEvent;
                    List<UpdatedFlags> flags = updatedF.getUpdatedFlags();

                    for (int i = 0; i < flags.size(); i++) {
                        applicableFlags.add(flags.get(i).getNewFlags());

                    }

                    // \RECENT is not a applicable flag in imap so remove it
                    // from the list
                    applicableFlags.remove(Flags.Flag.RECENT);

                    if (size < applicableFlags.getUserFlags().length) {
                        applicableFlagsChanged = true;
                    }
                    
                    
                } else if (messageEvent instanceof Expunged) {
                    expungedUids.addAll(messageEvent.getUids());
                    
                }
            } else if (event instanceof MailboxDeletion) {
                if (eventSessionId != sessionId) {
                    isDeletedByOtherSession = true;
                }
            } else if (event instanceof MailboxRenamed) {
                final MailboxRenamed mailboxRenamed = (MailboxRenamed) event;
                path = mailboxRenamed.getNewPath();
            }
        }
    }

    @Override
    public synchronized int msn(long uid) {
        Integer msn = uidToMsn.get(uid);
        if (msn != null) {
            return msn.intValue();
        } else {
            return SelectedMailbox.NO_SUCH_MESSAGE;
        }
    }

    @Override
    public synchronized long uid(int msn) {
        if (msn == -1) {
            return SelectedMailbox.NO_SUCH_MESSAGE;
        }
        Long uid = msnToUid.get(msn);
        if (uid != null) {
            return uid.longValue();
        } else {
            return SelectedMailbox.NO_SUCH_MESSAGE;
        }
    }

    @Override
    public synchronized long existsCount() {
        return uidToMsn.size();
    }
    

}
