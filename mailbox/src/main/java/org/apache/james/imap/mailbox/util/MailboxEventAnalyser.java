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

package org.apache.james.imap.mailbox.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.Flags;

import org.apache.james.imap.mailbox.MailboxListener;

public class MailboxEventAnalyser implements MailboxListener {

    private boolean isDeletedByOtherSession = false;

    private boolean sizeChanged = false;

    private boolean silentFlagChanges = false;

    private final long sessionId;

    private final Set<Long> flagUpdateUids;

    private final Flags.Flag uninterestingFlag;

    private final Set<Long> expungedUids;

    public MailboxEventAnalyser(final long sessionId) {
        super();
        this.sessionId = sessionId;
        flagUpdateUids = new TreeSet<Long>();
        expungedUids = new TreeSet<Long>();
        uninterestingFlag = Flags.Flag.RECENT;
    }

    public void event(Event event) {
        final long eventSessionId = event.getSessionId();
        if (event instanceof MessageEvent) {
            final MessageEvent messageEvent = (MessageEvent) event;
            final long uid = messageEvent.getSubjectUid();
            if (messageEvent instanceof Added) {
                sizeChanged = true;
            } else if (messageEvent instanceof FlagsUpdated) {
                FlagsUpdated updated = (FlagsUpdated) messageEvent;
                if (interestingFlags(updated)
                        && (sessionId != eventSessionId || !silentFlagChanges)) {
                    final Long uidObject = new Long(uid);
                    flagUpdateUids.add(uidObject);
                }
            } else if (messageEvent instanceof Expunged) {
                final Long uidObject = new Long(uid);
                expungedUids.add(uidObject);
            }
        } else if (event instanceof MailboxDeletionEvent) {
            if (eventSessionId != sessionId) {
                isDeletedByOtherSession = true;
            }
        }
    }

    private boolean interestingFlags(FlagsUpdated updated) {
        final boolean result;
        final Iterator it = updated.flagsIterator();
        if (it.hasNext()) {
            final Flags.Flag flag = (Flags.Flag) it.next();
            if (flag.equals(uninterestingFlag)) {
                result = false;
            } else {
                result = true;
            }
        } else {
            result = false;
        }
        return result;
    }

    public void reset() {
        sizeChanged = false;
        flagUpdateUids.clear();
        expungedUids.clear();
        isDeletedByOtherSession = false;
    }

    /**
     * Are flag changes from current session ignored?
     * 
     * @return true if any flag changes from current session will be ignored,
     *         false otherwise
     */
    public final boolean isSilentFlagChanges() {
        return silentFlagChanges;
    }

    /**
     * Sets whether changes from current session should be ignored.
     * 
     * @param silentFlagChanges
     *            true if any flag changes from current session should be
     *            ignored, false otherwise
     */
    public final void setSilentFlagChanges(boolean silentFlagChanges) {
        this.silentFlagChanges = silentFlagChanges;
    }

    /**
     * Has the size of the mailbox changed?
     * 
     * @return true if new messages have been added, false otherwise
     */
    public final boolean isSizeChanged() {
        return sizeChanged;
    }

    /**
     * Is the mailbox deleted?
     * 
     * @return true when the mailbox has been deleted by another session, false
     *         otherwise
     */
    public final boolean isDeletedByOtherSession() {
        return isDeletedByOtherSession;
    }

    public Collection<Long> flagUpdateUids() {
        return Collections.unmodifiableSet(flagUpdateUids);
    }

    public Collection<Long> expungedUids() {
        return Collections.unmodifiableSet(expungedUids);
    }

    public boolean hasExpungedUids() {
        return !expungedUids.isEmpty();
    }
}