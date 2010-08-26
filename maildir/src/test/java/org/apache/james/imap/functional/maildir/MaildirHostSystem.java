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
package org.apache.james.imap.functional.maildir;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.james.imap.api.process.ImapProcessor;
import org.apache.james.imap.encode.main.DefaultImapEncoderFactory;
import org.apache.james.imap.functional.ImapHostSystem;
import org.apache.james.imap.functional.InMemoryUserManager;
import org.apache.james.imap.maildir.MaildirMailboxManager;
import org.apache.james.imap.maildir.MaildirMailboxSessionMapperFactory;
import org.apache.james.imap.maildir.MaildirSubscriptionManager;
import org.apache.james.imap.main.DefaultImapDecoderFactory;
import org.apache.james.imap.processor.main.DefaultImapProcessorFactory;
import org.apache.james.test.functional.HostSystem;

public class MaildirHostSystem extends ImapHostSystem {

    public static final String META_DATA_DIRECTORY = "target/user-meta-data";
    private static final String MAILDIR_HOME = "target/Maildir";
    
    private final MaildirMailboxManager mailboxManager;
    private final InMemoryUserManager userManager;
    private final MaildirMailboxSessionMapperFactory mailboxSessionMapperFactory;
    
    public static HostSystem build() throws Exception { 
        return new MaildirHostSystem();
    }
    
    public MaildirHostSystem() {
        userManager = new InMemoryUserManager();
        mailboxSessionMapperFactory = new MaildirMailboxSessionMapperFactory(MAILDIR_HOME + "/%user");
        MaildirSubscriptionManager sm = new MaildirSubscriptionManager(mailboxSessionMapperFactory);
        mailboxManager = new MaildirMailboxManager(mailboxSessionMapperFactory, userManager);
        
        final ImapProcessor defaultImapProcessorFactory = DefaultImapProcessorFactory.createDefaultProcessor(mailboxManager, sm);
        configure(new DefaultImapDecoderFactory().buildImapDecoder(),
                new DefaultImapEncoderFactory().buildImapEncoder(),
                defaultImapProcessorFactory);
        (new File(MAILDIR_HOME)).mkdirs();
    }
    
    public boolean addUser(String user, String password) throws Exception {
        userManager.addUser(user, password);
        return true;
    }

    @Override
    public void resetData() throws Exception {
        resetUserMetaData();
        try {
        	FileUtils.deleteDirectory(new File(MAILDIR_HOME));
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    public void resetUserMetaData() throws Exception {
        File dir = new File(META_DATA_DIRECTORY);
        if (dir.exists()) {
            FileUtils.deleteDirectory(dir);
        }
        dir.mkdirs();
    }

}