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

package org.apache.james.imap.encode;

import static org.junit.Assert.*;

import org.apache.james.imap.api.ImapCommand;
import org.apache.james.imap.api.ImapConstants;
import org.apache.james.imap.api.display.HumanReadableText;
import org.apache.james.imap.api.message.response.StatusResponse;
import org.apache.james.imap.encode.ImapEncoder;
import org.apache.james.imap.encode.ImapResponseComposer;
import org.apache.james.imap.encode.StatusResponseEncoder;
import org.apache.james.imap.encode.base.ImapResponseComposerImpl;
import org.apache.james.imap.encode.main.DefaultLocalizer;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class StatusResponseEncoderTest {

    private static final String COMMAND = "COMMAND";

    private static final String TAG = "TAG";

    private static final HumanReadableText KEY = new HumanReadableText(
            "KEY", "TEXT");

    MockImapResponseWriter writer;

    ImapResponseComposer response;

    ImapEncoder mockNextEncoder;

    StatusResponse mockStatusResponse;

    StatusResponseEncoder encoder;

    ImapCommand stubCommand;

    private Mockery context = new JUnit4Mockery();

    @Before
    public void setUp() throws Exception {
        writer = new MockImapResponseWriter();
        response = new ImapResponseComposerImpl(writer);
        mockNextEncoder = context.mock(ImapEncoder.class);
        mockStatusResponse = context.mock(StatusResponse.class);
        encoder = new StatusResponseEncoder(mockNextEncoder, new DefaultLocalizer());
        stubCommand = ImapCommand.anyStateCommand(COMMAND);
    }


    @Test
    public void testTaggedOkCode() throws Exception {
        execute(StatusResponse.Type.OK, StatusResponse.ResponseCode.alert(),
                KEY, TAG);
        assertEquals(8, this.writer.operations.size());
        assertEquals(new MockImapResponseWriter.TagOperation(TAG),
                writer.operations.get(0));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(
                ImapConstants.OK), writer.operations.get(1));
        assertEquals(MockImapResponseWriter.BracketOperation.openSquare(),
                writer.operations.get(2));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(
                StatusResponse.ResponseCode.alert().getCode()),
                writer.operations.get(3));
        assertEquals(MockImapResponseWriter.BracketOperation.closeSquare(),
                writer.operations.get(4));
        assertEquals(new MockImapResponseWriter.CommandNameOperation(COMMAND),
                writer.operations.get(5));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(KEY
                .getDefaultValue()), writer.operations.get(6));
        assertEquals(new MockImapResponseWriter.EndOperation(),
                writer.operations.get(7));
    }

    @Test
    public void testTaggedOkNoCode() throws Exception {
        execute(StatusResponse.Type.OK, null, KEY, TAG);
        assertEquals(5, this.writer.operations.size());
        assertEquals(new MockImapResponseWriter.TagOperation(TAG),
                writer.operations.get(0));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(
                ImapConstants.OK), writer.operations.get(1));
        assertEquals(new MockImapResponseWriter.CommandNameOperation(COMMAND),
                writer.operations.get(2));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(KEY
                .getDefaultValue()), writer.operations.get(3));
        assertEquals(new MockImapResponseWriter.EndOperation(),
                writer.operations.get(4));
    }

    @Test
    public void testTaggedBadCode() throws Exception {
        execute(StatusResponse.Type.BAD, StatusResponse.ResponseCode.alert(),
                KEY, TAG);
        assertEquals(8, this.writer.operations.size());
        assertEquals(new MockImapResponseWriter.TagOperation(TAG),
                writer.operations.get(0));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(
                ImapConstants.BAD), writer.operations.get(1));
        assertEquals(MockImapResponseWriter.BracketOperation.openSquare(),
                writer.operations.get(2));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(
                StatusResponse.ResponseCode.alert().getCode()),
                writer.operations.get(3));
        assertEquals(MockImapResponseWriter.BracketOperation.closeSquare(),
                writer.operations.get(4));
        assertEquals(new MockImapResponseWriter.CommandNameOperation(COMMAND),
                writer.operations.get(5));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(KEY
                .getDefaultValue()), writer.operations.get(6));
        assertEquals(new MockImapResponseWriter.EndOperation(),
                writer.operations.get(7));
    }

    @Test
    public void testTaggedBadNoCode() throws Exception {
        execute(StatusResponse.Type.BAD, null, KEY, TAG);
        assertEquals(5, this.writer.operations.size());
        assertEquals(new MockImapResponseWriter.TagOperation(TAG),
                writer.operations.get(0));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(
                ImapConstants.BAD), writer.operations.get(1));
        assertEquals(new MockImapResponseWriter.CommandNameOperation(COMMAND),
                writer.operations.get(2));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(KEY
                .getDefaultValue()), writer.operations.get(3));
        assertEquals(new MockImapResponseWriter.EndOperation(),
                writer.operations.get(4));
    }

    @Test
    public void testTaggedNoCode() throws Exception {
        execute(StatusResponse.Type.NO, StatusResponse.ResponseCode.alert(),
                KEY, TAG);
        assertEquals(8, this.writer.operations.size());
        assertEquals(new MockImapResponseWriter.TagOperation(TAG),
                writer.operations.get(0));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(
                ImapConstants.NO), writer.operations.get(1));
        assertEquals(MockImapResponseWriter.BracketOperation.openSquare(),
                writer.operations.get(2));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(
                StatusResponse.ResponseCode.alert().getCode()),
                writer.operations.get(3));
        assertEquals(MockImapResponseWriter.BracketOperation.closeSquare(),
                writer.operations.get(4));
        assertEquals(new MockImapResponseWriter.CommandNameOperation(COMMAND),
                writer.operations.get(5));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(KEY
                .getDefaultValue()), writer.operations.get(6));
        assertEquals(new MockImapResponseWriter.EndOperation(),
                writer.operations.get(7));
    }

    @Test
    public void testTaggedNoNoCode() throws Exception {
        execute(StatusResponse.Type.NO, null, KEY, TAG);
        assertEquals(5, this.writer.operations.size());
        assertEquals(new MockImapResponseWriter.TagOperation(TAG),
                writer.operations.get(0));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(
                ImapConstants.NO), writer.operations.get(1));
        assertEquals(new MockImapResponseWriter.CommandNameOperation(COMMAND),
                writer.operations.get(2));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(KEY
                .getDefaultValue()), writer.operations.get(3));
        assertEquals(new MockImapResponseWriter.EndOperation(),
                writer.operations.get(4));
    }

    @Test
    public void testUntaggedOkCode() throws Exception {
        execute(StatusResponse.Type.OK, StatusResponse.ResponseCode.alert(),
                KEY, null);
        assertEquals(7, this.writer.operations.size());
        assertEquals(new MockImapResponseWriter.UntaggedOperation(),
                writer.operations.get(0));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(
                ImapConstants.OK), writer.operations.get(1));
        assertEquals(MockImapResponseWriter.BracketOperation.openSquare(),
                writer.operations.get(2));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(
                StatusResponse.ResponseCode.alert().getCode()),
                writer.operations.get(3));
        assertEquals(MockImapResponseWriter.BracketOperation.closeSquare(),
                writer.operations.get(4));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(KEY
                .getDefaultValue()), writer.operations.get(5));
        assertEquals(new MockImapResponseWriter.EndOperation(),
                writer.operations.get(6));
    }

    @Test
    public void testUntaggedOkNoCode() throws Exception {
        execute(StatusResponse.Type.OK, null, KEY, null);
        assertEquals(4, this.writer.operations.size());
        assertEquals(new MockImapResponseWriter.UntaggedOperation(),
                writer.operations.get(0));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(
                ImapConstants.OK), writer.operations.get(1));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(KEY
                .getDefaultValue()), writer.operations.get(2));
        assertEquals(new MockImapResponseWriter.EndOperation(),
                writer.operations.get(3));
    }

    @Test
    public void testUntaggedBadCode() throws Exception {
        execute(StatusResponse.Type.BAD, StatusResponse.ResponseCode.alert(),
                KEY, null);
        assertEquals(7, this.writer.operations.size());
        assertEquals(new MockImapResponseWriter.UntaggedOperation(),
                writer.operations.get(0));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(
                ImapConstants.BAD), writer.operations.get(1));
        assertEquals(MockImapResponseWriter.BracketOperation.openSquare(),
                writer.operations.get(2));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(
                StatusResponse.ResponseCode.alert().getCode()),
                writer.operations.get(3));
        assertEquals(MockImapResponseWriter.BracketOperation.closeSquare(),
                writer.operations.get(4));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(KEY
                .getDefaultValue()), writer.operations.get(5));
        assertEquals(new MockImapResponseWriter.EndOperation(),
                writer.operations.get(6));
    }

    @Test
    public void testUntaggedBadNoCode() throws Exception {
        execute(StatusResponse.Type.BAD, null, KEY, null);
        assertEquals(4, this.writer.operations.size());
        assertEquals(new MockImapResponseWriter.UntaggedOperation(),
                writer.operations.get(0));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(
                ImapConstants.BAD), writer.operations.get(1));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(KEY
                .getDefaultValue()), writer.operations.get(2));
        assertEquals(new MockImapResponseWriter.EndOperation(),
                writer.operations.get(3));
    }

    @Test
    public void testUntaggedNoCode() throws Exception {
        execute(StatusResponse.Type.NO, StatusResponse.ResponseCode.alert(),
                KEY, null);
        assertEquals(7, this.writer.operations.size());
        assertEquals(new MockImapResponseWriter.UntaggedOperation(),
                writer.operations.get(0));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(
                ImapConstants.NO), writer.operations.get(1));
        assertEquals(MockImapResponseWriter.BracketOperation.openSquare(),
                writer.operations.get(2));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(
                StatusResponse.ResponseCode.alert().getCode()),
                writer.operations.get(3));
        assertEquals(MockImapResponseWriter.BracketOperation.closeSquare(),
                writer.operations.get(4));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(KEY
                .getDefaultValue()), writer.operations.get(5));
        assertEquals(new MockImapResponseWriter.EndOperation(),
                writer.operations.get(6));
    }

    @Test
    public void testUntaggedNoNoCode() throws Exception {
        execute(StatusResponse.Type.NO, null, KEY, null);
        assertEquals(4, this.writer.operations.size());
        assertEquals(new MockImapResponseWriter.UntaggedOperation(),
                writer.operations.get(0));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(
                ImapConstants.NO), writer.operations.get(1));
        assertEquals(new MockImapResponseWriter.TextMessageOperation(KEY
                .getDefaultValue()), writer.operations.get(2));
        assertEquals(new MockImapResponseWriter.EndOperation(),
                writer.operations.get(3));
    }

    private void execute(StatusResponse.Type type,
            StatusResponse.ResponseCode code, HumanReadableText key,
            String tag) throws Exception {
        configure(type, code, key, tag);
        compose();
    }

    
    private void compose() throws Exception {
        encoder.doEncode(mockStatusResponse, response, new FakeImapSession());
    }

    private void configure(final StatusResponse.Type type,
            final StatusResponse.ResponseCode code, final HumanReadableText key,
            final String tag) {
        context.checking(new Expectations() {{
            oneOf(mockStatusResponse).getServerResponseType();will(returnValue(type));
            oneOf(mockStatusResponse).getTag();will(returnValue(tag));
            oneOf(mockStatusResponse).getTextKey();will(returnValue(key));
            oneOf(mockStatusResponse).getResponseCode();will(returnValue(code));
            if (tag == null) {
                oneOf(mockStatusResponse).getCommand();will(returnValue(null));
            } else {
                oneOf(mockStatusResponse).getCommand();will(returnValue(stubCommand));
            }
        }});
    }
}
