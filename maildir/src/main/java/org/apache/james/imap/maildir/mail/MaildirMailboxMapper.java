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

package org.apache.james.imap.maildir.mail;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.transaction.file.TxFileResourceManager;
import org.apache.commons.transaction.file.FileResourceManager.FileResource;
import org.apache.commons.transaction.resource.ResourceException;
import org.apache.james.imap.api.display.HumanReadableText;
import org.apache.james.imap.mailbox.MailboxNotFoundException;
import org.apache.james.imap.mailbox.StorageException;
import org.apache.james.imap.maildir.mail.model.MaildirMailbox;
import org.apache.james.imap.store.mail.MailboxMapper;
import org.apache.james.imap.store.mail.model.Mailbox;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * MailboxMapper which use Maildir layout
 * 
 * 
 */
public class MaildirMailboxMapper extends MaildirTransactionalMapper implements
		MailboxMapper {

	private final static String PREFIX = "james-imap";
	private final XStream xstream = new XStream(new DomDriver());

	public MaildirMailboxMapper(final TxFileResourceManager manager) {
		super(manager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.james.imap.store.mail.MailboxMapper#countMailboxesWithName
	 * (java.lang.String)
	 */
	public long countMailboxesWithName(String name) throws StorageException {
		try {
			FileResource fr = manager.getResource(convertToPath(name));
			if (fr.exists() && fr.isDirectory()) {
				return 1;
			}
			return 0;
		} catch (ResourceException e) {
			throw new StorageException(HumanReadableText.COUNT_FAILED, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.james.imap.store.mail.MailboxMapper#delete(org.apache.james
	 * .imap.store.mail.model.Mailbox)
	 */
	public void delete(Mailbox mailbox) throws StorageException {
		try {
			manager.getResource(getPathForMailbox(mailbox)).delete();
			deleteMailboxId(mailbox.getMailboxId());
		} catch (Exception e) {
			throw new StorageException(HumanReadableText.DELETED_FAILED, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.james.imap.store.mail.MailboxMapper#deleteAll()
	 */
	public void deleteAll() throws StorageException {
		try {
			List<? extends FileResource> mailboxes = manager.getResource(
					manager.getRootPath()).getChildren();
			for (int i = 0; i < mailboxes.size(); i++) {
				mailboxes.get(i).delete();
			}
			manager.getResource(manager.getRootPath()).getChild(PREFIX + "-ids.properties").delete();
		} catch (ResourceException e) {
			throw new StorageException(HumanReadableText.DELETED_FAILED, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.james.imap.store.mail.MailboxMapper#existsMailboxStartingWith
	 * (java.lang.String)
	 */
	public boolean existsMailboxStartingWith(String mailboxName)
			throws StorageException {
		try {
			FileResource fr = manager.getResource(convertToPath(mailboxName));
			if (fr.exists() && fr.isDirectory()) {
				return true;
			}
			return false;

		} catch (ResourceException e) {
			throw new StorageException(HumanReadableText.SEARCH_FAILED, e);
		}
	}

	/**
	 * Convert the give name to the right path
	 * 
	 * @param name
	 * @return path
	 */
	private String convertToPath(String name) {
		return name.substring("#mail".length()).replaceAll("\\.", "/");
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.james.imap.store.mail.MailboxMapper#findMailboxById(long)
	 */
	public Mailbox findMailboxById(long mailboxId) throws StorageException,
			MailboxNotFoundException {

		try {
			String name = convertToPath(getMailboxName(mailboxId));
			MaildirMailbox mailbox = (MaildirMailbox) xstream.fromXML(manager
					.getResource(name).getChild(PREFIX + "-metadata.xml")
					.readStream());
			return mailbox;
		} catch (Exception e) {
			throw new StorageException(HumanReadableText.SEARCH_FAILED, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.james.imap.store.mail.MailboxMapper#findMailboxByName(java.lang.String)
	 */
	public Mailbox findMailboxByName(String name) throws StorageException,
			MailboxNotFoundException {
		try {

			FileResource fr = manager.getResource(convertToPath(name));
			if (fr.exists()) {
				return (MaildirMailbox) xstream.fromXML(fr.getChild(
						PREFIX + "-metadata.xml").readStream());

			}
		} catch (ResourceException e) {
			throw new StorageException(HumanReadableText.SEARCH_FAILED, e);
		}
		throw new MailboxNotFoundException(name);
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.james.imap.store.mail.MailboxMapper#findMailboxWithNameLike(java.lang.String)
	 */
	public List<Mailbox> findMailboxWithNameLike(String name)
			throws StorageException {
		// TODO Auto-generated method stub
		return null;
	}

	private String getMailboxName(long id) throws IOException,
			ResourceException {
		Properties props = new Properties();
		props.load(manager.getResource(manager.getRootPath()).getChild(
				PREFIX + "-ids.properties").readStream());
		return (String) props.get(id);
	}

	private void deleteMailboxId(long id) throws IOException, ResourceException {
		FileResource fr = manager.getResource(manager.getRootPath()).getChild(
				PREFIX + "-ids.properties");
		Properties props = new Properties();
		props.load(fr.readStream());
		props.remove(id);
		props.store(manager.getResource(manager.getRootPath()).getChild(
				PREFIX + "-ids.properties").writeStream(true), "");
	}
	
	private void saveMailboxIdMapping(long id, String name) throws IOException, ResourceException {
		FileResource fr = manager.getResource(manager.getRootPath()).getChild(
				PREFIX + "-ids.properties");
		Properties props = new Properties();
		props.load(fr.readStream());
		props.put(id,name);
		props.store(manager.getResource(manager.getRootPath()).getChild(
				PREFIX + "-ids.properties").writeStream(true), "");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.james.imap.store.mail.MailboxMapper#save(org.apache.james.
	 * imap.store.mail.model.Mailbox)
	 */
	public void save(Mailbox mailbox) throws StorageException {
		try {
			FileResource fr = manager.getResource(getPathForMailbox(mailbox));
			fr.getChild("new").createAsDirectory();
			fr.getChild("cur").createAsDirectory();
			fr.getChild("tmp").createAsDirectory();
			saveMailboxIdMapping(mailbox.getMailboxId(), mailbox.getName());
			xstream.toXML(mailbox,fr.getChild(PREFIX + "-metadata.xml").writeStream(true));
				
		} catch (Exception e) {
			throw new StorageException(HumanReadableText.SAVE_FAILED, e);
		}
	}

	/**
	 * Return the path for the given mailbox
	 * 
	 * @param mailbox
	 * @return path
	 */
	private String getPathForMailbox(Mailbox mailbox) {
		String path = "";
		String name = mailbox.getName();
		String parts[] = name.split("\\.");
		String userParts[] = parts[1].split("@");

		// reverse order to domain, user
		path += constructPath(new String[] { userParts[1], userParts[2] }, 0);
		path += "/Maildir";
		path += constructPath(parts, 2);

		return path;
	}

	/**
	 * Construct path for the given array, starting from start index.
	 * 
	 * @param parts
	 * @param start
	 * @return path
	 */
	private String constructPath(String[] parts, int start) {
		String path = "";
		for (int i = start; i < parts.length; i++) {
			// suffix with "."
			path += "/." + parts[i];
		}
		return path;
	}

}