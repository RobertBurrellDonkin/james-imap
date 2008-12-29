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

package org.apache.james.imap.functional.suite;

import java.util.Locale;

import org.apache.james.imap.functional.FrameworkForSelectedStateBase;
import org.apache.james.test.functional.HostSystem;

public class FetchBodyStructure extends FrameworkForSelectedStateBase {

    public FetchBodyStructure(HostSystem system) {
        super(system);
    }

    public void testFetchFetchSimpleBodyStructureUS() throws Exception {
        scriptTest("FetchSimpleBodyStructure", Locale.US);
    }

    public void testFetchFetchSimpleBodyStructureKOREA() throws Exception {
        scriptTest("FetchSimpleBodyStructure", Locale.KOREA);
    }

    public void testFetchFetchSimpleBodyStructureITALY() throws Exception {
        scriptTest("FetchSimpleBodyStructure", Locale.ITALY);
    }

    public void testFetchFetchMultipartBodyStructureUS() throws Exception {
        scriptTest("FetchMultipartBodyStructure", Locale.US);
    }

    public void testFetchFetchMultipartBodyStructureKOREA() throws Exception {
        scriptTest("FetchMultipartBodyStructure", Locale.KOREA);
    }

    public void testFetchFetchMultipartBodyStructureITALY() throws Exception {
        scriptTest("FetchMultipartBodyStructure", Locale.ITALY);
    }

    public void testFetchStructureEmbeddedUS() throws Exception {
        scriptTest("FetchStructureEmbedded", Locale.US);
    }

    public void testFetchStructureEmbeddedITALY() throws Exception {
        scriptTest("FetchStructureEmbedded", Locale.ITALY);
    }

    public void testFetchStructureEmbeddedKOREA() throws Exception {
        scriptTest("FetchStructureEmbedded", Locale.KOREA);
    }

    public void testFetchStructureComplexUS() throws Exception {
        scriptTest("FetchStructureComplex", Locale.US);
    }

    public void testFetchStructureComplexITALY() throws Exception {
        scriptTest("FetchStructureComplex", Locale.ITALY);
    }

    public void testFetchStructureComplexKOREA() throws Exception {
        scriptTest("FetchStructureComplex", Locale.KOREA);
    }
}