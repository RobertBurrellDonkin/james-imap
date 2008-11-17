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

import org.apache.james.api.imap.ImapCommand;
import org.apache.james.api.imap.ImapConstants;
import org.apache.james.api.imap.ImapMessage;
import org.apache.james.api.imap.message.request.ImapRequest;
import org.apache.james.api.imap.message.response.ImapResponseMessage;
import org.apache.james.api.imap.message.response.imap4rev1.StatusResponseFactory;
import org.apache.james.api.imap.process.ImapProcessor;
import org.apache.james.api.imap.process.ImapSession;
import org.apache.james.imap.message.request.imap4rev1.ListRequest;
import org.apache.james.imap.message.response.imap4rev1.server.ListResponse;
import org.apache.james.imap.processor.base.AbstractMailboxAwareProcessor;
import org.apache.james.imap.processor.base.ImapSessionUtils;
import org.apache.james.mailboxmanager.ListResult;
import org.apache.james.mailboxmanager.MailboxManagerException;
import org.apache.james.mailboxmanager.impl.ListResultImpl;
import org.apache.james.mailboxmanager.manager.MailboxExpression;
import org.apache.james.mailboxmanager.manager.MailboxManager;
import org.apache.james.mailboxmanager.manager.MailboxManagerProvider;

public class ListProcessor extends AbstractMailboxAwareProcessor {

    public ListProcessor(final ImapProcessor next,
            final MailboxManagerProvider mailboxManagerProvider,
            final StatusResponseFactory factory) {
        super(next, mailboxManagerProvider, factory);
    }

    protected boolean isAcceptable(ImapMessage message) {
        return (message instanceof ListRequest);
    }

    protected void doProcess(ImapRequest message, ImapSession session,
            String tag, ImapCommand command, Responder responder) {
        final ListRequest request = (ListRequest) message;
        final String baseReferenceName = request.getBaseReferenceName();
        final String mailboxPatternString = request.getMailboxPattern();
        doProcess(baseReferenceName, mailboxPatternString, session, tag,
                command, responder);
    }

    protected ImapResponseMessage createResponse(boolean noInferior,
            boolean noSelect, boolean marked, boolean unmarked,
            String hierarchyDelimiter, String mailboxName) {
        return new ListResponse(noInferior, noSelect, marked, unmarked,
                hierarchyDelimiter, mailboxName);
    }

    protected final void doProcess(final String baseReferenceName,
            final String mailboxPattern, final ImapSession session,
            final String tag, ImapCommand command, final Responder responder) {
        try {
            String referenceName = baseReferenceName;
            // Should the #user.userName section be removed from names returned?
            final boolean removeUserPrefix;

            final ListResult[] listResults;

            final String user = ImapSessionUtils.getUserName(session);
            final String personalNamespace = ImapConstants.USER_NAMESPACE
                    + ImapConstants.HIERARCHY_DELIMITER_CHAR + user;

            if (mailboxPattern.length() == 0) {
                // An empty mailboxPattern signifies a request for the hierarchy
                // delimiter
                // and root name of the referenceName argument

                String referenceRoot;
                if (referenceName.startsWith(ImapConstants.NAMESPACE_PREFIX)) {
                    // A qualified reference name - get the first element,
                    // and don't remove the user prefix
                    removeUserPrefix = false;
                    int firstDelimiter = referenceName
                            .indexOf(ImapConstants.HIERARCHY_DELIMITER_CHAR);
                    if (firstDelimiter == -1) {
                        referenceRoot = referenceName;
                    } else {
                        referenceRoot = referenceName.substring(0,
                                firstDelimiter);
                    }
                } else {
                    // A relative reference name - need to remove user prefix
                    // from
                    // results.
                    referenceRoot = "";
                    removeUserPrefix = true;

                }

                // Get the mailbox for the reference name.
                listResults = new ListResult[1];
                listResults[0] = ListResultImpl.createNoSelect(referenceRoot,
                        ImapConstants.HIERARCHY_DELIMITER);
            } else {

                // If the mailboxPattern is fully qualified, ignore the
                // reference name.
                if (mailboxPattern.charAt(0) == ImapConstants.NAMESPACE_PREFIX_CHAR) {
                    referenceName = "";
                }

                // If the search pattern is relative, need to remove user prefix
                // from results.
                removeUserPrefix = ((referenceName + mailboxPattern).charAt(0) != ImapConstants.NAMESPACE_PREFIX_CHAR);

                if (removeUserPrefix) {
                    referenceName = personalNamespace + "." + referenceName;
                }

                listResults = doList(session, referenceName, mailboxPattern);
            }

            int prefixLength = personalNamespace.length();

            for (int i = 0; i < listResults.length; i++) {
                final ListResult listResult = listResults[i];
                processResult(responder, removeUserPrefix, prefixLength,
                        listResult);
            }

            okComplete(command, tag, responder);
        } catch (MailboxManagerException e) {
            no(command, tag, responder, e);
        }
    }

    void processResult(final Responder responder,
            final boolean removeUserPrefix, int prefixLength,
            final ListResult listResult) {
        final String delimiter = listResult.getHierarchyDelimiter();
        final String mailboxName = mailboxName(removeUserPrefix, prefixLength,
                listResult);

        final boolean noInferior = listResult.isNoInferiors();
        boolean noSelect = false;
        boolean marked = false;
        boolean unmarked = false;
        switch (listResult.getSelectability()) {
            case ListResult.SELECTABILITY_FLAG_MARKED:
                marked = true;
                break;
            case ListResult.SELECTABILITY_FLAG_UNMARKED:
                unmarked = true;
                break;
            case ListResult.SELECTABILITY_FLAG_NOSELECT:
                noSelect = true;
                break;
        }
        responder.respond(createResponse(noInferior, noSelect, marked,
                unmarked, delimiter, mailboxName));
    }

    private String mailboxName(final boolean removeUserPrefix,
            int prefixLength, final ListResult listResult) {
        final String mailboxName;
        final String name = listResult.getName();
        if (removeUserPrefix) {
            if (name.length() <= prefixLength) {
                mailboxName = "";
            } else {
                mailboxName = name.substring(prefixLength + 1);
            }
        } else {
            mailboxName = name;
        }
        return mailboxName;
    }

    protected final ListResult[] doList(ImapSession session, String base,
            String pattern) throws MailboxManagerException {
        final MailboxManager mailboxManager = getMailboxManager(session);
        final ListResult[] result = mailboxManager.list(new MailboxExpression(
                base, pattern, '*', '%'));
        return result;
    }
}