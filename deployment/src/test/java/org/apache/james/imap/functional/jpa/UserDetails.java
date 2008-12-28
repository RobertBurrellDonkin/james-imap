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

package org.apache.james.imap.functional.jpa;

import java.util.Collection;
import java.util.HashSet;

public class UserDetails {
    private final String userName;

    private String password;

    private final Collection subscriptions;

    public UserDetails(final String userName) {
        this.userName = userName;
        this.subscriptions = new HashSet();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection getSubscriptions() {
        return subscriptions;
    }

    public void addSubscription(String subscription) {
        this.subscriptions.add(subscription);
    }

    public void removeSubscription(String mailbox) {
        this.subscriptions.remove(mailbox);
    }
}
