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
package org.apache.james.mailbox.maildir.user;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.james.mailbox.SubscriptionException;
import org.apache.james.mailbox.maildir.user.model.MaildirSubscription;
import org.apache.james.mailbox.store.transaction.NonTransactionalMapper;
import org.apache.james.mailbox.store.user.SubscriptionMapper;
import org.apache.james.mailbox.store.user.model.Subscription;

public class MaildirSubscriptionMapper extends NonTransactionalMapper implements SubscriptionMapper {

    private static final String PATH_USER = "%user";
    private static final String FILE_SUBSCRIPTION = "subscriptions";
    private final String maildirLocation;
    
    public MaildirSubscriptionMapper(String maildirLocation) {
        this.maildirLocation = maildirLocation;
    }
    
    /* 
     * (non-Javadoc)
     * @see org.apache.james.mailbox.store.user.SubscriptionMapper#delete(org.apache.james.mailbox.store.user.model.Subscription)
     */
    public void delete(Subscription subscription) throws SubscriptionException {
     // TODO: we need some kind of file locking here
        Set<String> subscriptionNames = readSubscriptionsForUser(subscription.getUser());
        boolean changed = subscriptionNames.remove(subscription.getMailbox());
        if (changed) {
            try {
                writeSubscriptions(new File(createFolderNameFromUser(subscription.getUser())), subscriptionNames);
            } catch (IOException e) {
                throw new SubscriptionException(e);
            }
        }
    }

    /* 
     * (non-Javadoc)
     * @see org.apache.james.mailbox.store.user.SubscriptionMapper#findSubscriptionsForUser(java.lang.String)
     */
    public List<Subscription> findSubscriptionsForUser(String user) throws SubscriptionException {
        Set<String> subscriptionNames = readSubscriptionsForUser(user);
        ArrayList<Subscription> subscriptions = new ArrayList<Subscription>();
        for (String subscription : subscriptionNames) {
            subscriptions.add(new MaildirSubscription(user, subscription));
        }
        return subscriptions;
    }

    /* 
     * (non-Javadoc)
     * @see org.apache.james.mailbox.store.user.SubscriptionMapper#findMailboxSubscriptionForUser(java.lang.String, java.lang.String)
     */
    public Subscription findMailboxSubscriptionForUser(String user, String mailbox) throws SubscriptionException {
        File userRoot = new File(createFolderNameFromUser(user));
        Set<String> subscriptionNames;
        try {
            subscriptionNames = readSubscriptions(userRoot);
        } catch (IOException e) {
            throw new SubscriptionException(e);
        }
        if (subscriptionNames.contains(mailbox))
            return new MaildirSubscription(user, mailbox);
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.mailbox.store.user.SubscriptionMapper#save(org.apache.james.mailbox.store.user.model.Subscription)
     */
    public void save(Subscription subscription) throws SubscriptionException {
        // TODO: we need some kind of file locking here
        Set<String> subscriptionNames = readSubscriptionsForUser(subscription.getUser());
        boolean changed = subscriptionNames.add(subscription.getMailbox());
        if (changed) {
            try {
                writeSubscriptions(new File(createFolderNameFromUser(subscription.getUser())), subscriptionNames);
            } catch (IOException e) {
                throw new SubscriptionException(e);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.mailbox.store.transaction.TransactionalMapper#endRequest()
     */
    public void endRequest() {
        // nothing to do
    }
    
    /**
     * Get the folder name of the folder containing the user's mailboxes 
     * @param user The user to get the folder for
     * @return The name of the folder
     */
    private String createFolderNameFromUser(String user) {
        return maildirLocation.replace(PATH_USER, user);
    }
    
    /**
     * Read the subscriptions for a particular user
     * @param user The user to get the subscriptions for
     * @return A Set of names of subscribed mailboxes of the user
     * @throws SubscriptionException
     */
    private Set<String> readSubscriptionsForUser(String user) throws SubscriptionException { 
        File userRoot = new File(createFolderNameFromUser(user));
        Set<String> subscriptionNames;
        try {
            subscriptionNames = readSubscriptions(userRoot);
        } catch (IOException e) {
            throw new SubscriptionException(e);
        }
        return subscriptionNames;
    }

    /**
     * Read the names of the mailboxes which are subscribed from the specified folder
     * @param mailboxFolder The folder which contains the subscription file
     * @return A Set of names of subscribed mailboxes
     * @throws IOException
     */
    private Set<String> readSubscriptions(File mailboxFolder) throws IOException {
        File subscriptionFile = new File(mailboxFolder, FILE_SUBSCRIPTION);
        HashSet<String> subscriptions = new HashSet<String>();
        if (!subscriptionFile.exists()) {
            return subscriptions;
        }
        FileReader fileReader = new FileReader(subscriptionFile);
        BufferedReader reader = new BufferedReader(fileReader);
        String subscription;
        while ((subscription = reader.readLine()) != null)
            if (!subscription.equals(""))
                subscriptions.add(subscription);
        reader.close();
        fileReader.close();
        return subscriptions;
    }
    
    /**
     * Write the set of mailbox names into the subscriptions file in the specified folder
     * @param mailboxFolder Folder which contains the subscriptions file
     * @param subscriptions Set of names of subscribed mailboxes
     * @throws IOException
     */
    private void writeSubscriptions(File mailboxFolder, final Set<String> subscriptions) throws IOException {
        List<String> sortedSubscriptions = new ArrayList<String>(subscriptions);
        Collections.sort(sortedSubscriptions);
        File subscriptionFile = new File(mailboxFolder, FILE_SUBSCRIPTION);
        if (!subscriptionFile.exists())
            subscriptionFile.createNewFile();
        FileWriter fileWriter = new FileWriter(subscriptionFile);
        PrintWriter writer = new PrintWriter(fileWriter);
        for (String subscription : sortedSubscriptions)
            writer.println(subscription);
        writer.close();
        fileWriter.close();
    }

}
