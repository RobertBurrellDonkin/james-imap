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

package org.apache.james.imap.processor;

import static org.apache.james.imap.api.ImapConstants.SUPPORTS_ENABLE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.james.imap.api.ImapCommand;
import org.apache.james.imap.api.message.response.StatusResponseFactory;
import org.apache.james.imap.api.process.ImapProcessor;
import org.apache.james.imap.api.process.ImapSession;
import org.apache.james.imap.message.request.EnableRequest;
import org.apache.james.imap.message.response.EnableResponse;
import org.apache.james.mailbox.MailboxManager;

public class EnableProcessor extends AbstractMailboxProcessor<EnableRequest> implements CapabilityImplementingProcessor {

    private final List<PermitEnableCapabilityProcessor> capabilities = new ArrayList<PermitEnableCapabilityProcessor>();
    private final static String ENABLED_CAPABILITIES = "ENABLED_CAPABILITIES";
    
    public EnableProcessor(final ImapProcessor next, final MailboxManager mailboxManager, final StatusResponseFactory factory, final List<PermitEnableCapabilityProcessor> capabilities) {
        this(next, mailboxManager, factory);
        this.capabilities.addAll(capabilities);

    }

    public EnableProcessor(final ImapProcessor next, final MailboxManager mailboxManager, final StatusResponseFactory factory) {
        super(EnableRequest.class, next, mailboxManager, factory);
    }


    /*
     * (non-Javadoc)
     * @see org.apache.james.imap.processor.AbstractMailboxProcessor#doProcess(org.apache.james.imap.api.message.request.ImapRequest, org.apache.james.imap.api.process.ImapSession, java.lang.String, org.apache.james.imap.api.ImapCommand, org.apache.james.imap.api.process.ImapProcessor.Responder)
     */
    protected void doProcess(EnableRequest request, ImapSession session, String tag, ImapCommand command, Responder responder) {
        List<String> caps = request.getCapabilities();
        Set<String> enabledCaps = new HashSet<String>();
        for (int i = 0; i < caps.size(); i++) {
            String cap = caps.get(i);
            for (int a = 0; a < capabilities.size(); a++) {
                if (capabilities.get(a).getPermitEnableCapabilities(session).contains(cap)) {
                    enabledCaps.add(cap);
                }
            }
        }
        getEnabledCapabilities(session).addAll(enabledCaps);
        
        responder.respond(new EnableResponse(new ArrayList<String>(enabledCaps)));
        
        unsolicitedResponses(session, responder, false);
        okComplete(command, tag, responder);
    }

   
    /**
     * Add a {@link PermitEnableCapabilityProcessor} which can be enabled
     * 
     * @param implementor
     */
    public void addProcessor(PermitEnableCapabilityProcessor implementor) {
        capabilities.add(implementor);
    }

    @SuppressWarnings("unchecked")
    public static Set<String> getEnabledCapabilities(ImapSession session) {
        Set<String> caps = (Set<String>) session.getAttribute(ENABLED_CAPABILITIES);
        
        if (caps == null) {
            caps = new HashSet<String>();
            session.setAttribute(ENABLED_CAPABILITIES, caps);
        } 
        return caps;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.james.imap.processor.CapabilityImplementingProcessor#
     * getImplementedCapabilities(org.apache.james.imap.api.process.ImapSession)
     */
    public List<String> getImplementedCapabilities(ImapSession session) {
        final List<String> capabilities = new ArrayList<String>();
        capabilities.add(SUPPORTS_ENABLE);
    
        return capabilities;
    }

}