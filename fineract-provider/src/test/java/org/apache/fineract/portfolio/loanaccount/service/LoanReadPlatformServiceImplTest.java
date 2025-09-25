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
package org.apache.fineract.portfolio.loanaccount.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for secured parameter functionality in LoanReadPlatformService.
 * Note: Full service testing requires 32 dependencies, so we focus on testing
 * the SearchParameters integration which is the core of the secured filtering logic.
 * End-to-end functionality is covered by integration tests.
 */
public class LoanReadPlatformServiceImplTest {

    @Test
    public void testSecuredParameterInSearchParameters() {
        SearchParameters securedTrueParams = SearchParameters.builder()
                .secured(true)
                .build();
        
        assertTrue(securedTrueParams.getSecured());

        SearchParameters securedFalseParams = SearchParameters.builder()
                .secured(false)
                .build();
        
        assertFalse(securedFalseParams.getSecured());

        SearchParameters noSecuredParams = SearchParameters.builder()
                .build();
        
        assertTrue(noSecuredParams.getSecured() == null);
    }

    @Test
    public void testSecuredParameterWithOtherSearchCriteria() {
        SearchParameters complexParams = SearchParameters.builder()
                .secured(true)
                .clientId(123L)
                .status("ACTIVE")
                .limit(10)
                .offset(0)
                .build();
        
        assertTrue(complexParams.getSecured());
        assertTrue(complexParams.getClientId().equals(123L));
        assertTrue(complexParams.getStatus().equals("ACTIVE"));
        assertTrue(complexParams.getLimit().equals(10));
        assertTrue(complexParams.getOffset().equals(0));
    }
}
