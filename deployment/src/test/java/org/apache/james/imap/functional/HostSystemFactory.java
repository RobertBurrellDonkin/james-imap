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

package org.apache.james.imap.functional;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.james.imap.encode.main.DefaultImapEncoderFactory;
import org.apache.james.imap.main.DefaultImapDecoderFactory;
import org.apache.james.imap.mock.MailboxManagerProviderSingleton;
import org.apache.james.imap.processor.main.DefaultImapProcessorFactory;
import org.apache.james.mailboxmanager.mock.TorqueMailboxManagerProviderSingleton;
import org.apache.james.test.functional.imap.HostSystem;

public class HostSystemFactory {

    public static final String META_DATA_DIRECTORY = "target/user-meta-data";

    public static void resetUserMetaData() throws Exception {

        File dir = new File(META_DATA_DIRECTORY);
        if (dir.exists()) {
            FileUtils.deleteDirectory(dir);
        }
        dir.mkdirs();
    }

    public static HostSystem createStandardImap() throws Exception {

        ExperimentalHostSystem host = TorqueMailboxManagerProviderSingleton.host;
        final DefaultImapProcessorFactory defaultImapProcessorFactory = new DefaultImapProcessorFactory();
        resetUserMetaData();
        defaultImapProcessorFactory.configure(MailboxManagerProviderSingleton
                .getMailboxManagerProviderInstance());
        host.configure(new DefaultImapDecoderFactory().buildImapDecoder(),
                new DefaultImapEncoderFactory().buildImapEncoder(),
                defaultImapProcessorFactory.buildImapProcessor(),
                new ExperimentalHostSystem.Resetable() {

                    public void reset() throws Exception {
                        MailboxManagerProviderSingleton.reset();
                        resetUserMetaData();
                    }

                });
        return host;
    }
}
