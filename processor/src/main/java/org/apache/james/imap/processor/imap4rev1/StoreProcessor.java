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

package org.apache.james.imap.processor.imap4rev1;

import java.util.Iterator;

import javax.mail.Flags;

import org.apache.james.api.imap.ImapCommand;
import org.apache.james.api.imap.ImapMessage;
import org.apache.james.api.imap.message.IdRange;
import org.apache.james.api.imap.message.request.ImapRequest;
import org.apache.james.api.imap.message.response.imap4rev1.StatusResponseFactory;
import org.apache.james.api.imap.process.ImapProcessor;
import org.apache.james.api.imap.process.ImapSession;
import org.apache.james.api.imap.process.SelectedMailbox;
import org.apache.james.imap.mailbox.Mailbox;
import org.apache.james.imap.mailbox.MailboxException;
import org.apache.james.imap.mailbox.MailboxManagerProvider;
import org.apache.james.imap.mailbox.MailboxSession;
import org.apache.james.imap.mailbox.MessageRange;
import org.apache.james.imap.mailbox.MessageResult;
import org.apache.james.imap.mailbox.MessageResult.FetchGroup;
import org.apache.james.imap.mailbox.util.FetchGroupImpl;
import org.apache.james.imap.mailbox.util.MessageRangeImpl;
import org.apache.james.imap.message.request.imap4rev1.StoreRequest;
import org.apache.james.imap.message.response.imap4rev1.FetchResponse;
import org.apache.james.imap.processor.base.ImapSessionUtils;

public class StoreProcessor extends AbstractMailboxProcessor {

    private static final FetchGroup STORE_FETCH_GROUP = FetchGroupImpl.FLAGS;

    public StoreProcessor(final ImapProcessor next, final MailboxManagerProvider mailboxManagerProvider,
            final StatusResponseFactory factory) {
        super(next, mailboxManagerProvider, factory);
    }

    protected boolean isAcceptable(ImapMessage message) {
        return (message instanceof StoreRequest);
    }

    protected void doProcess(ImapRequest message, ImapSession session,
            String tag, ImapCommand command, Responder responder) {
        final StoreRequest request = (StoreRequest) message;
        final IdRange[] idSet = request.getIdSet();
        final Flags flags = request.getFlags();
        final boolean useUids = request.isUseUids();
        final boolean silent = request.isSilent();
        final boolean isSignedPlus = request.isSignedPlus();
        final boolean isSignedMinus = request.isSignedMinus();

        final boolean replace;
        final boolean value;
        if (isSignedMinus) {
            value = false;
            replace = false;
        } else if (isSignedPlus) {
            value = true;
            replace = false;
        } else {
            replace = true;
            value = true;
        }
        try {
            final Mailbox mailbox = getSelectedMailbox(session);
            for (int i = 0; i < idSet.length; i++) {
                final long lowVal;
                final long highVal;
                final SelectedMailbox selected = session.getSelected();
                if (useUids) {
                    lowVal = idSet[i].getLowVal();
                    highVal = idSet[i].getHighVal();
                } else {
                    lowVal = selected.uid((int) idSet[i].getLowVal());
                    highVal = selected.uid((int) idSet[i].getHighVal());
                }
                final MessageRange messageSet = MessageRangeImpl.uidRange(
                        lowVal, highVal);
                final MailboxSession mailboxSession = ImapSessionUtils
                        .getMailboxSession(session);
                final Iterator it = mailbox.setFlags(flags, value, replace,
                        messageSet, STORE_FETCH_GROUP, mailboxSession);
                if (!silent) {
                    while (it.hasNext()) {
                        final MessageResult result = (MessageResult) it.next();
                        final long uid = result.getUid();
                        final int msn = selected.msn(uid);
                        final Flags resultFlags = result.getFlags();
                        final Long resultUid;
                        if (useUids) {
                            resultUid = new Long(uid);
                        } else {
                            resultUid = null;
                        }
                        if (selected.isRecent(uid)) {
                            resultFlags.add(Flags.Flag.RECENT);
                        }
                        final FetchResponse response = new FetchResponse(msn,
                                resultFlags, resultUid, null, null, null, null,
                                null, null);
                        responder.respond(response);
                    }
                }
            }
            final boolean omitExpunged = (!useUids);
            unsolicitedResponses(session, responder, omitExpunged, useUids);
            okComplete(command, tag, responder);
        } catch (MailboxException e) {
            no(command, tag, responder, e);
        }
    }
}
