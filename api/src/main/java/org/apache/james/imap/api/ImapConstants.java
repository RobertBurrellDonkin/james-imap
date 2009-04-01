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

package org.apache.james.imap.api;

public interface ImapConstants {
    // Basic response types
    String OK = "OK";

    String NO = "NO";

    String BAD = "BAD";

    String BYE = "BYE";

    String UNTAGGED = "*";

    String SP = " ";

    public static final String NIL = "NIL";

    public static final String UID = "UID";

    public static final String MIME_HEADER_CONTENT_LOCATION = "Content-Location";

    public static final String MIME_HEADER_CONTENT_MD5 = "Content-MD5";

    public static final String MIME_HEADER_CONTENT_LANGUAGE = "Content-Language";

    public static final String[] EMPTY_STRING_ARRAY = {};

    public static final byte BYTE_OPENING_PARENTHESIS = 0x28;

    public static final byte[] BYTES_OPENING_PARENTHESIS = { BYTE_OPENING_PARENTHESIS };

    public static final byte BYTE_CLOSING_PARENTHESIS = 0x29;

    public static final byte[] BYTES_CLOSING_PARENTHESIS = { BYTE_CLOSING_PARENTHESIS };

    public static final byte BYTE_SP = 0x20;

    public static final byte[] BYTES_SPACE = { BYTE_SP };

    public static final byte BYTE_DQUOTE = 0x22;

    public static final byte BYTE_BACK_SLASH = 0x5C;

    public static final byte BYTE_QUESTION = 0x3F;

    public static final byte[] BYTES_DQUOTE = { BYTE_DQUOTE };

    public static final byte BYTE_OPEN_SQUARE_BRACKET = 0x5B;

    public static final byte[] BYTES_OPEN_SQUARE_BRACKET = { BYTE_OPEN_SQUARE_BRACKET };

    public static final byte BYTE_CLOSE_SQUARE_BRACKET = 0x5D;

    public static final byte[] BYTES_CLOSE_SQUARE_BRACKET = { BYTE_CLOSE_SQUARE_BRACKET };

    public static final byte BYTE_OPEN_BRACE = 0x7B;

    public static final byte[] BYTES_OPEN_BRACE = { BYTE_OPEN_BRACE };

    public static final byte BYTE_CLOSE_BRACE = 0x7D;

    public static final byte[] BYTES_CLOSE_BRACE = { BYTE_CLOSE_BRACE };

    public static final byte[] BYTES_LINE_END = { 0x0D, 0x0A };

    public static final char OPENING_PARENTHESIS = '(';

    public static final char CLOSING_PARENTHESIS = ')';

    public static final char OPENING_SQUARE_BRACKET = '[';

    public static final char CLOSING_SQUARE_BRACKET = ']';

    public static final char SP_CHAR = ' ';

    public static final char DQUOTE = '\"';

    String VERSION = "IMAP4rev1";

    String SUPPORTS_LITERAL_PLUS = "LITERAL+";
    
    public static final String SUPPORTS_NAMESPACES = "NAMESPACE";

    String USER_NAMESPACE = "#mail";

    char HIERARCHY_DELIMITER_CHAR = '.';

    final char NAMESPACE_PREFIX_CHAR = '#';

    String HIERARCHY_DELIMITER = String.valueOf(HIERARCHY_DELIMITER_CHAR);

    final String NAMESPACE_PREFIX = String.valueOf(NAMESPACE_PREFIX_CHAR);

    String INBOX_NAME = "INBOX";

    public String MIME_TYPE_TEXT = "TEXT";

    public String MIME_TYPE_MULTIPART = "MULTIPART";

    public String MIME_SUBTYPE_PLAIN = "PLAIN";

    public String MIME_TYPE_MESSAGE = "MESSAGE";

    public String MIME_SUBTYPE_RFC822 = "RFC822";

    // RFC822 CONSTANTS:
    // TODO: Consider switching to standard case
    public String RFC822_BCC = "Bcc";

    public String RFC822_CC = "Cc";

    public String RFC822_FROM = "From";

    public String RFC822_DATE = "Date";

    public String RFC822_SUBJECT = "Subject";

    public String RFC822_TO = "To";

    public String RFC822_SENDER = "Sender";

    public String RFC822_REPLY_TO = "Reply-To";

    public String RFC822_IN_REPLY_TO = "In-Reply-To";

    public String RFC822_MESSAGE_ID = "Message-ID";

    public static final String NAMESPACE_COMMAND_NAME = "NAMESPACE";

    public static final char BACK_SLASH = '\\';

    public static final String STATUS_UNSEEN = "UNSEEN";

    public static final String STATUS_UIDVALIDITY = "UIDVALIDITY";

    public static final String STATUS_UIDNEXT = "UIDNEXT";

    public static final String STATUS_RECENT = "RECENT";

    public static final String STATUS_MESSAGES = "MESSAGES";

    public static final String UNSUBSCRIBE_COMMAND_NAME = "UNSUBSCRIBE";

    public static final String UID_COMMAND_NAME = "UID";

    public static final String SUBSCRIBE_COMMAND_NAME = "SUBSCRIBE";

    public static final String STORE_COMMAND_NAME = "STORE";

    public static final String STATUS_COMMAND_NAME = "STATUS";

    public static final String SELECT_COMMAND_NAME = "SELECT";

    public static final String SEARCH_COMMAND_NAME = "SEARCH";

    public static final String RENAME_COMMAND_NAME = "RENAME";

    public static final String NOOP_COMMAND_NAME = "NOOP";

    public static final String LSUB_COMMAND_NAME = "LSUB";

    public static final String LOGOUT_COMMAND_NAME = "LOGOUT";

    public static final String LOGIN_COMMAND_NAME = "LOGIN";

    public static final String LIST_COMMAND_NAME = "LIST";

    public static final String FETCH_COMMAND_NAME = "FETCH";

    public static final String EXPUNGE_COMMAND_NAME = "EXPUNGE";

    public static final String EXAMINE_COMMAND_NAME = "EXAMINE";

    public static final String DELETE_COMMAND_NAME = "DELETE";

    public static final String CREATE_COMMAND_NAME = "CREATE";

    public static final String COPY_COMMAND_NAME = "COPY";

    public static final String CLOSE_COMMAND_NAME = "CLOSE";

    public static final String CHECK_COMMAND_NAME = "CHECK";

    public static final String CAPABILITY_COMMAND_NAME = "CAPABILITY";

    public static final String AUTHENTICATE_COMMAND_NAME = "AUTHENTICATE";

    public static final String APPEND_COMMAND_NAME = "APPEND";

    public static final String LIST_RESPONSE_NAME = "LIST";

    public static final String LSUB_RESPONSE_NAME = "LSUB";

    public static final String SEARCH_RESPONSE_NAME = "SEARCH";

    public static final String NAME_ATTRIBUTE_NOINFERIORS = "\\Noinferiors";

    public static final String NAME_ATTRIBUTE_NOSELECT = "\\Noselect";

    public static final String NAME_ATTRIBUTE_MARKED = "\\Marked";

    public static final String NAME_ATTRIBUTE_UNMARKED = "\\Unmarked";

    public static final String PS_TEXT = "TEXT";

    public static final String PS_HEADER = "HEADER";

    public static final String PS_MIME = "MIME";

    public static final String FETCH_RFC822 = "RFC822";

    public static final String FETCH_RFC822_HEADER = "RFC822.HEADER";

    public static final String FETCH_RFC822_TEXT = "RFC822.TEXT";

    public static final String FETCH_BODY_STRUCTURE = "BODYSTRUCTURE";

    public static final String FETCH_BODY = "BODY";
}
