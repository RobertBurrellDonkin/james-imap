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
package org.apache.james.imap.encode.imap4rev1.server;

import java.io.IOException;

import org.apache.james.api.imap.ImapConstants;
import org.apache.james.api.imap.ImapMessage;
import org.apache.james.imap.encode.ImapEncoder;
import org.apache.james.imap.encode.ImapResponseComposer;
import org.apache.james.imap.encode.base.AbstractChainedImapEncoder;
import org.apache.james.imap.message.response.imap4rev1.server.AbstractListingResponse;
import org.apache.james.imap.message.response.imap4rev1.server.ListResponse;

/**
 * Encoders IMAP4rev1 <code>List</code> responses.
 */
public class ListResponseEncoder extends AbstractChainedImapEncoder {

    public ListResponseEncoder(ImapEncoder next) {
        super(next);
    }

    protected void doEncode(final ImapMessage acceptableMessage,
            final ImapResponseComposer composer) throws IOException {
        final AbstractListingResponse response = (AbstractListingResponse) acceptableMessage;
        ListingEncodingUtils.encodeListingResponse(
                ImapConstants.LIST_RESPONSE_NAME, composer, response);
    }

    protected boolean isAcceptable(ImapMessage message) {
        return (message instanceof ListResponse);
    }

}
