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
package org.apache.james.imap.store.mail.model;

import java.util.Date;

import javax.mail.Flags;

/**
 * Links mailbox to member messages.
 */
public interface MailboxMembership {

    public abstract Date getInternalDate();

    public abstract long getMailboxId();

    public abstract int getSize();

    public abstract long getUid();
    
    public abstract Document getDocument();

    public abstract boolean isAnswered();

    public abstract boolean isDeleted();

    public abstract boolean isDraft();

    public abstract boolean isFlagged();

    public abstract boolean isRecent();

    public abstract boolean isSeen();

    /**
     * Sets {@link #isRecent()} to false.
     * A message can only be recent once.
     */
    public abstract void unsetRecent();

    public abstract void setFlags(Flags flags);

    /**
     * Creates a new flags instance populated
     * with the current flag data.
     * @return new instance, not null
     */
    public abstract Flags createFlags();
}
