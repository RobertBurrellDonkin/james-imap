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
package org.apache.james.mailbox.maildir.mail.model;

import org.apache.james.mailbox.MailboxPath;
import org.apache.james.mailbox.store.mail.model.Mailbox;

public class MaildirMailbox implements Mailbox<Integer> {

    private Integer id = null;
    private String namespace;
    private String user;
    private String name;
	private long lastUid;
	private long uidValidity;

    public MaildirMailbox(MailboxPath path, long uidValidity, long lastUid) {
        this.namespace = path.getNamespace();
        this.user = path.getUser();
        this.name = path.getName();
        this.uidValidity = uidValidity;
        this.lastUid = lastUid;
    }
    
    public MaildirMailbox(Mailbox<Integer> mailbox) {
        this.id = mailbox.getMailboxId();
        this.namespace = mailbox.getNamespace();
        this.user = mailbox.getUser();
        this.name = mailbox.getName();
        this.uidValidity = mailbox.getUidValidity();
    }

	/*
	 * (non-Javadoc)
	 * @see org.apache.james.mailbox.store.mail.model.Mailbox#consumeUid()
	 */
	public void consumeUid() {
		lastUid++;
	}
	
	public void setMailboxId(Integer id) {
        this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.james.mailbox.store.mail.model.Mailbox#getMailboxId()
	 */
	public Integer getMailboxId() {
	    return id;
	}

    /* (non-Javadoc)
     * @see org.apache.james.mailbox.store.mail.model.Mailbox#getNamespace()
     */
    public String getNamespace() {
        return namespace;
    }

    /* (non-Javadoc)
     * @see org.apache.james.mailbox.store.mail.model.Mailbox#setNamespace(java.lang.String)
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /* (non-Javadoc)
     * @see org.apache.james.mailbox.store.mail.model.Mailbox#getUser()
     */
    public String getUser() {
        return user;
    }

    /* (non-Javadoc)
     * @see org.apache.james.mailbox.store.mail.model.Mailbox#setUser(java.lang.String)
     */
    public void setUser(String user) {
        this.user = user;
    }

	/*
	 * (non-Javadoc)
	 * @see org.apache.james.mailbox.store.mail.model.Mailbox#getName()
	 */
	public String getName() {
		return name;
	}

    /*
     * (non-Javadoc)
     * @see org.apache.james.mailbox.store.mail.model.Mailbox#setName(java.lang.String)
     */
    public void setName(String name) {
        this.name = name;
    }

	/*
	 * (non-Javadoc)
	 * @see org.apache.james.mailbox.store.mail.model.Mailbox#getUidValidity()
	 */
	public long getUidValidity() {
		return uidValidity;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof MaildirMailbox) {
			if (id == ((MaildirMailbox) obj).getMailboxId()) {
				return true;
			}
		}
		return false;
	}

	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
	    final int PRIME = 31;
        int result = 1;
        result = PRIME * result + namespace.hashCode();
        result = PRIME * result + user.hashCode();
        result = PRIME * result + name.hashCode();
        return result;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
	    return namespace + ":" + user + ":" + name;
	}

}
