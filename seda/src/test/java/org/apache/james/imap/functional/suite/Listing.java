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

import org.apache.james.imap.functional.FrameworkForAuthenticatedState;
import org.apache.james.test.functional.HostSystem;

public class Listing extends FrameworkForAuthenticatedState {

    public Listing(HostSystem system) throws Exception {
        super(system);
    }

    public void testListPlusUS() throws Exception {
        scriptTest("ListPlus", Locale.US);
    }
    
    public void testListPercentWildcardUS() throws Exception {
        scriptTest("ListPercentWildcard", Locale.US);
    }

    public void testListPlusKOREA() throws Exception {
        scriptTest("ListPlus", Locale.KOREA);
    }
    
    public void testListPercentWildcardKOREA() throws Exception {
        scriptTest("ListPercentWildcard", Locale.KOREA);
    }
    
    public void testListPlusITALY() throws Exception {
        scriptTest("ListPlus", Locale.ITALY);
    }
    
    public void testListPercentWildcardITALY() throws Exception {
        scriptTest("ListPercentWildcard", Locale.ITALY);
    }
}
