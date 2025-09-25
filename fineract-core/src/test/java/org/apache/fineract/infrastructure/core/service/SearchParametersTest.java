/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

public class SearchParametersTest {

    @Test
    public void testSecuredParameterBuilder() {
        SearchParameters searchParams = SearchParameters.builder()
                .secured(true)
                .build();
        assertTrue(searchParams.getSecured());

        searchParams = SearchParameters.builder()
                .secured(false)
                .build();
        assertFalse(searchParams.getSecured());

        searchParams = SearchParameters.builder()
                .build();
        assertNull(searchParams.getSecured());
    }

    @Test
    public void testSecuredParameterWithOtherFields() {
        SearchParameters searchParams = SearchParameters.builder()
                .secured(true)
                .limit(10)
                .offset(0)
                .orderBy("id")
                .sortOrder("ASC")
                .build();
        
        assertTrue(searchParams.getSecured());
        assertEquals(Integer.valueOf(10), searchParams.getLimit());
        assertEquals(Integer.valueOf(0), searchParams.getOffset());
        assertEquals("id", searchParams.getOrderBy());
        assertEquals("ASC", searchParams.getSortOrder());
    }

    @Test
    public void testSecuredParameterToBuilderPattern() {
        SearchParameters original = SearchParameters.builder()
                .secured(true)
                .limit(5)
                .build();
        
        SearchParameters modified = original.toBuilder()
                .secured(false)
                .build();
        
        assertTrue(original.getSecured());
        assertFalse(modified.getSecured());
        assertEquals(Integer.valueOf(5), modified.getLimit());
    }
}
