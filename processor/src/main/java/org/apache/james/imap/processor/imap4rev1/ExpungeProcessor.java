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

import org.apache.james.api.imap.ImapCommand;
import org.apache.james.api.imap.ImapMessage;
import org.apache.james.api.imap.display.HumanReadableTextKey;
import org.apache.james.api.imap.message.request.ImapRequest;
import org.apache.james.api.imap.message.response.imap4rev1.StatusResponseFactory;
import org.apache.james.api.imap.process.ImapProcessor;
import org.apache.james.api.imap.process.ImapSession;
import org.apache.james.api.imap.process.SelectedMailbox;
import org.apache.james.imap.mailbox.Mailbox;
import org.apache.james.imap.mailbox.MailboxException;
import org.apache.james.imap.mailbox.MailboxManagerProvider;
import org.apache.james.imap.mailbox.MessageResult;
import org.apache.james.imap.mailbox.util.FetchGroupImpl;
import org.apache.james.imap.mailbox.util.MessageRangeImpl;
import org.apache.james.imap.message.request.imap4rev1.ExpungeRequest;
import org.apache.james.imap.processor.base.AbstractMailboxAwareProcessor;
import org.apache.james.imap.processor.base.ImapSessionUtils;

public class ExpungeProcessor extends AbstractMailboxAwareProcessor {

    public ExpungeProcessor(final ImapProcessor next,
            final MailboxManagerProvider mailboxManagerProvider,
            final StatusResponseFactory factory) {
        super(next, mailboxManagerProvider, factory);
    }

    protected boolean isAcceptable(ImapMessage message) {
        return (message instanceof ExpungeRequest);
    }

    protected void doProcess(ImapRequest message, ImapSession session,
            String tag, ImapCommand command, Responder responder) {
        Mailbox mailbox = getSelectedMailbox(session);
        if (!mailbox.isWriteable()) {
            no(command, tag, responder,
                    HumanReadableTextKey.MAILBOX_IS_READ_ONLY);
        } else {
            try {
                final Iterator it = mailbox.expunge(MessageRangeImpl.all(),
                        FetchGroupImpl.MINIMAL, ImapSessionUtils
                                .getMailboxSession(session));
                final SelectedMailbox mailboxSession = session
                        .getSelected();
                if (mailboxSession != null) {
                    while (it.hasNext()) {
                        final MessageResult result = (MessageResult) it.next();
                        final long uid = result.getUid();
                        mailboxSession.removeRecent(uid);
                    }
                }
                unsolicitedResponses(session, responder, false);
                okComplete(command, tag, responder);
            } catch (MailboxException e) {
                no(command, tag, responder, e);
            }
        }
    }
}
