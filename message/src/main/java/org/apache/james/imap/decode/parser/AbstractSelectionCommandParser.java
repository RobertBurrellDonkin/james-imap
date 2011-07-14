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
package org.apache.james.imap.decode.parser;

import org.apache.james.imap.api.ImapCommand;
import org.apache.james.imap.api.ImapMessage;
import org.apache.james.imap.api.process.ImapSession;
import org.apache.james.imap.decode.DecodingException;
import org.apache.james.imap.decode.ImapRequestLineReader;
import org.apache.james.imap.decode.ImapRequestLineReader.CharacterValidator;
import org.apache.james.imap.decode.base.AbstractImapCommandParser;
import org.apache.james.imap.message.request.AbstractMailboxSelectionRequest;

public abstract class AbstractSelectionCommandParser extends AbstractImapCommandParser{
    private final static byte[] CONDSTORE = "(CONDSTORE)".getBytes();

    public AbstractSelectionCommandParser(ImapCommand command) {
        super(command);
    }
    


    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.james.imap.decode.base.AbstractImapCommandParser#decode(org
     * .apache.james.imap.api.ImapCommand,
     * org.apache.james.imap.decode.ImapRequestLineReader, java.lang.String,
     * org.apache.james.imap.api.process.ImapSession)
     */
    protected ImapMessage decode(ImapCommand command, ImapRequestLineReader request, String tag, ImapSession session) throws DecodingException {
        final String mailboxName = request.mailbox();
        boolean condstore = false;

        char c = Character.UNASSIGNED;
        try {
            c = request.nextWordChar();
        } catch (DecodingException e) {

        }
        if (c == '(') {
            request.consumeWord(new CharacterValidator() {
                int pos = 0;
                @Override
                public boolean isValid(char chr) {
                    if (pos > CONDSTORE.length) {
                        return false;
                    } else {
                        return ImapRequestLineReader.cap(chr) == CONDSTORE[pos++];
                    }
                }
            });
            condstore = true;
            
        }
       
        request.eol();
        final ImapMessage result = createRequest(command, mailboxName, condstore, tag);
        return result;
    }
    
    protected abstract AbstractMailboxSelectionRequest createRequest(ImapCommand command, String mailboxName, boolean condstore, String tag);
}