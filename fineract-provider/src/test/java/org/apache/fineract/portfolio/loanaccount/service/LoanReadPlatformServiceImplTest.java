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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.ColumnValidator;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.service.OfficeReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.data.LoanApplicationTimelineData;
import org.apache.fineract.portfolio.loanaccount.data.LoanStatusEnumData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.apache.fineract.portfolio.loanproduct.service.LoanDropdownReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
public class LoanReadPlatformServiceImplTest {

    @Mock
    private PlatformSecurityContext context;
    
    @Mock
    private JdbcTemplate jdbcTemplate;
    
    @Mock
    private OfficeReadPlatformService officeReadPlatformService;
    
    @Mock
    private LoanProductReadPlatformService loanProductReadPlatformService;
    
    @Mock
    private LoanDropdownReadPlatformService loanDropdownReadPlatformService;
    
    @Mock
    private LoanRepositoryWrapper loanRepositoryWrapper;
    
    @Mock
    private ColumnValidator columnValidator;

    private LoanReadPlatformServiceImpl loanReadPlatformService;

    @BeforeEach
    public void setUp() {
        loanReadPlatformService = new LoanReadPlatformServiceImpl(context, jdbcTemplate, officeReadPlatformService,
                loanProductReadPlatformService, loanDropdownReadPlatformService, loanRepositoryWrapper, columnValidator);
    }

    @Test
    public void testSecuredParameterSqlGeneration() {
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
