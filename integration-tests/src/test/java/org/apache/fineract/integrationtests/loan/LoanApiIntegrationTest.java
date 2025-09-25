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
package org.apache.fineract.integrationtests.loan;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.fineract.client.models.GetLoansLoanIdResponse;
import org.apache.fineract.client.models.GetLoansResponse;
import org.apache.fineract.client.models.PostLoanProductsRequest;
import org.apache.fineract.client.models.PostLoanProductsResponse;
import org.apache.fineract.client.models.PostLoansLoanIdResponse;
import org.apache.fineract.client.models.PostLoansRequest;
import org.apache.fineract.client.models.PostLoansResponse;
import org.apache.fineract.integrationtests.BaseLoanIntegrationTest;
import org.apache.fineract.integrationtests.common.ClientHelper;
import org.junit.jupiter.api.Test;

public class LoanApiIntegrationTest extends BaseLoanIntegrationTest {

    @Test
    public void test_retrieveLoansByClientId_Works() {
        AtomicLong createdLoanId = new AtomicLong();
        AtomicLong createdLoanId2 = new AtomicLong();
        Long clientId = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();
        Long clientId2 = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();

        runAt("01 January 2023", () -> {
            // Create Client

            int numberOfRepayments = 3;
            int repaymentEvery = 1;

            // Create Loan Products
            PostLoanProductsRequest product = createOnePeriod30DaysLongNoInterestPeriodicAccrualProduct() //
                    .numberOfRepayments(numberOfRepayments) //
                    .repaymentEvery(repaymentEvery) //
                    .installmentAmountInMultiplesOf(null) //
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS.longValue()) //
                    .interestType(InterestType.DECLINING_BALANCE)//
                    .interestRatePerPeriod(10.0)//
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY)//
                    .interestRecalculationCompoundingMethod(InterestRecalculationCompoundingMethod.NONE)//
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.ADJUST_LAST_UNPAID_PERIOD)//
                    .isInterestRecalculationEnabled(true)//
                    .recalculationRestFrequencyInterval(1)//
                    .recalculationRestFrequencyType(RecalculationRestFrequencyType.DAILY)//
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.REDUCE_EMI_AMOUNT)//
                    .allowPartialPeriodInterestCalcualtion(false)//
                    .disallowExpectedDisbursements(false)//
                    .allowApprovedDisbursedAmountsOverApplied(false)//
                    .overAppliedNumber(null)//
                    .overAppliedCalculationType(null)//
                    .multiDisburseLoan(null);//

            PostLoanProductsResponse loanProductResponse = loanProductHelper.createLoanProduct(product);
            Long loanProductId = loanProductResponse.getResourceId();

            PostLoanProductsRequest product2 = createOnePeriod30DaysLongNoInterestPeriodicAccrualProduct() //
                    .numberOfRepayments(numberOfRepayments) //
                    .repaymentEvery(repaymentEvery) //
                    .installmentAmountInMultiplesOf(null) //
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS.longValue()) //
                    .interestType(InterestType.DECLINING_BALANCE)//
                    .interestRatePerPeriod(10.0)//
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY)//
                    .interestRecalculationCompoundingMethod(InterestRecalculationCompoundingMethod.NONE)//
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.ADJUST_LAST_UNPAID_PERIOD)//
                    .isInterestRecalculationEnabled(true)//
                    .recalculationRestFrequencyInterval(1)//
                    .recalculationRestFrequencyType(RecalculationRestFrequencyType.DAILY)//
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.REDUCE_EMI_AMOUNT)//
                    .allowPartialPeriodInterestCalcualtion(false)//
                    .disallowExpectedDisbursements(false)//
                    .allowApprovedDisbursedAmountsOverApplied(false)//
                    .overAppliedNumber(null)//
                    .overAppliedCalculationType(null)//
                    .multiDisburseLoan(null);//

            PostLoanProductsResponse loanProductResponse2 = loanProductHelper.createLoanProduct(product2);
            Long loanProductId2 = loanProductResponse2.getResourceId();

            // Apply and Approve Loan
            double amount = 5000.0;

            PostLoansRequest applicationRequest = applyLoanRequest(clientId, loanProductId, "01 January 2023", amount, numberOfRepayments)//
                    .repaymentEvery(repaymentEvery)//
                    .interestRatePerPeriod(BigDecimal.valueOf(10.0))//
                    .loanTermFrequency(numberOfRepayments)//
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS)//
                    .loanTermFrequencyType(RepaymentFrequencyType.MONTHS)//
                    .interestType(InterestType.DECLINING_BALANCE)//
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY);//

            PostLoansRequest applicationRequest2 = applyLoanRequest(clientId2, loanProductId2, "01 January 2023", amount,
                    numberOfRepayments)//
                    .repaymentEvery(repaymentEvery)//
                    .interestRatePerPeriod(BigDecimal.valueOf(10.0))//
                    .loanTermFrequency(numberOfRepayments)//
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS)//
                    .loanTermFrequencyType(RepaymentFrequencyType.MONTHS)//
                    .interestType(InterestType.DECLINING_BALANCE)//
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY);//

            PostLoansResponse postLoansResponse = loanTransactionHelper.applyLoan(applicationRequest);
            PostLoansResponse postLoansResponse2 = loanTransactionHelper.applyLoan(applicationRequest2);

            PostLoansLoanIdResponse approvedLoanResult = loanTransactionHelper.approveLoan(postLoansResponse.getResourceId(),
                    approveLoanRequest(amount, "01 January 2023"));

            PostLoansLoanIdResponse approvedLoanResult2 = loanTransactionHelper.approveLoan(postLoansResponse2.getResourceId(),
                    approveLoanRequest(amount, "01 January 2023"));

            Long loanId = approvedLoanResult.getLoanId();
            Long loanId2 = approvedLoanResult2.getLoanId();
            createdLoanId.getAndSet(loanId);
            createdLoanId2.getAndSet(loanId2);

            // disburse Loan
            disburseLoan(loanId, BigDecimal.valueOf(amount), "01 January 2023");
            disburseLoan(loanId2, BigDecimal.valueOf(amount), "01 January 2023");
        });
        runAt("01 February 2023", () -> {
            long loanId = createdLoanId.get();
            GetLoansResponse loansLoanIdResponse = loanTransactionHelper.retrieveAllLoans(null, null, clientId);
            assertThat(loansLoanIdResponse.getPageItems()).isNotNull();
            assertThat(loansLoanIdResponse.getPageItems().size()).isEqualTo(1);
            Long loanIdFromResponse = loansLoanIdResponse.getPageItems().iterator().next().getId();
            assertThat(loanIdFromResponse).isEqualTo(loanId);
        });
    }

    @Test
    public void test_retrieveLoansWithSummary_Works() {
        AtomicLong createdLoanId = new AtomicLong();

        runAt("01 January 2023", () -> {
            // Create Client
            Long clientId = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();

            int numberOfRepayments = 3;
            int repaymentEvery = 1;

            // Create Loan Product
            PostLoanProductsRequest product = createOnePeriod30DaysLongNoInterestPeriodicAccrualProduct() //
                    .numberOfRepayments(numberOfRepayments) //
                    .repaymentEvery(repaymentEvery) //
                    .installmentAmountInMultiplesOf(null) //
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS.longValue()) //
                    .interestType(InterestType.DECLINING_BALANCE)//
                    .interestRatePerPeriod(10.0)//
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY)//
                    .interestRecalculationCompoundingMethod(InterestRecalculationCompoundingMethod.NONE)//
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.ADJUST_LAST_UNPAID_PERIOD)//
                    .isInterestRecalculationEnabled(true)//
                    .recalculationRestFrequencyInterval(1)//
                    .recalculationRestFrequencyType(RecalculationRestFrequencyType.DAILY)//
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.REDUCE_EMI_AMOUNT)//
                    .allowPartialPeriodInterestCalcualtion(false)//
                    .disallowExpectedDisbursements(false)//
                    .allowApprovedDisbursedAmountsOverApplied(false)//
                    .overAppliedNumber(null)//
                    .overAppliedCalculationType(null)//
                    .multiDisburseLoan(null);//

            PostLoanProductsResponse loanProductResponse = loanProductHelper.createLoanProduct(product);
            Long loanProductId = loanProductResponse.getResourceId();

            // Apply and Approve Loan
            double amount = 5000.0;

            PostLoansRequest applicationRequest = applyLoanRequest(clientId, loanProductId, "01 January 2023", amount, numberOfRepayments)//
                    .repaymentEvery(repaymentEvery)//
                    .interestRatePerPeriod(BigDecimal.valueOf(10.0))//
                    .loanTermFrequency(numberOfRepayments)//
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS)//
                    .loanTermFrequencyType(RepaymentFrequencyType.MONTHS)//
                    .interestType(InterestType.DECLINING_BALANCE)//
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY);//

            PostLoansResponse postLoansResponse = loanTransactionHelper.applyLoan(applicationRequest);

            PostLoansLoanIdResponse approvedLoanResult = loanTransactionHelper.approveLoan(postLoansResponse.getResourceId(),
                    approveLoanRequest(amount, "01 January 2023"));

            Long loanId = approvedLoanResult.getLoanId();
            createdLoanId.getAndSet(loanId);

            // disburse Loan
            disburseLoan(loanId, BigDecimal.valueOf(amount), "01 January 2023");
        });
        runAt("01 February 2023", () -> {
            long loanId = createdLoanId.get();
            GetLoansLoanIdResponse loanResponse = loanTransactionHelper.getLoanDetails(loanId);
            GetLoansResponse loansLoanIdResponse = loanTransactionHelper.retrieveAllLoans(loanResponse.getAccountNo(), "summary", null);
            BigDecimal totalUnpaidPayableDueInterest = loansLoanIdResponse.getPageItems().iterator().next().getSummary()
                    .getTotalUnpaidPayableDueInterest();
            assertThat(totalUnpaidPayableDueInterest).isEqualByComparingTo(BigDecimal.valueOf(509.59));
        });
    }

    @Test
    public void test_retrieveLoansWithSummaryWithoutDisbursement_Works() {
        AtomicLong createdLoanId = new AtomicLong();

        runAt("01 January 2023", () -> {
            // Create Client
            Long clientId = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();

            int numberOfRepayments = 3;
            int repaymentEvery = 1;

            // Create Loan Product
            PostLoanProductsRequest product = createOnePeriod30DaysLongNoInterestPeriodicAccrualProduct() //
                    .numberOfRepayments(numberOfRepayments) //
                    .repaymentEvery(repaymentEvery) //
                    .installmentAmountInMultiplesOf(null) //
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS.longValue()) //
                    .interestType(InterestType.DECLINING_BALANCE)//
                    .interestRatePerPeriod(10.0)//
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY)//
                    .interestRecalculationCompoundingMethod(InterestRecalculationCompoundingMethod.NONE)//
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.ADJUST_LAST_UNPAID_PERIOD)//
                    .isInterestRecalculationEnabled(true)//
                    .recalculationRestFrequencyInterval(1)//
                    .recalculationRestFrequencyType(RecalculationRestFrequencyType.DAILY)//
                    .rescheduleStrategyMethod(RescheduleStrategyMethod.REDUCE_EMI_AMOUNT)//
                    .allowPartialPeriodInterestCalcualtion(false)//
                    .disallowExpectedDisbursements(false)//
                    .allowApprovedDisbursedAmountsOverApplied(false)//
                    .overAppliedNumber(null)//
                    .overAppliedCalculationType(null)//
                    .multiDisburseLoan(null);//

            PostLoanProductsResponse loanProductResponse = loanProductHelper.createLoanProduct(product);
            Long loanProductId = loanProductResponse.getResourceId();

            // Apply and Approve Loan
            double amount = 5000.0;

            PostLoansRequest applicationRequest = applyLoanRequest(clientId, loanProductId, "01 January 2023", amount, numberOfRepayments)//
                    .repaymentEvery(repaymentEvery)//
                    .interestRatePerPeriod(BigDecimal.valueOf(10.0))//
                    .loanTermFrequency(numberOfRepayments)//
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS)//
                    .loanTermFrequencyType(RepaymentFrequencyType.MONTHS)//
                    .interestType(InterestType.DECLINING_BALANCE)//
                    .interestCalculationPeriodType(InterestCalculationPeriodType.DAILY);//

            PostLoansResponse postLoansResponse = loanTransactionHelper.applyLoan(applicationRequest);

            PostLoansLoanIdResponse approvedLoanResult = loanTransactionHelper.approveLoan(postLoansResponse.getResourceId(),
                    approveLoanRequest(amount, "01 January 2023"));

            Long loanId = approvedLoanResult.getLoanId();
            createdLoanId.getAndSet(loanId);
        });
        runAt("01 February 2023", () -> {
            long loanId = createdLoanId.get();
            GetLoansLoanIdResponse loanResponse = loanTransactionHelper.getLoanDetails(loanId);
            GetLoansResponse loansLoanIdResponse = loanTransactionHelper.retrieveAllLoans(loanResponse.getAccountNo(), "summary", null);
            assertThat(loansLoanIdResponse.getPageItems()).isNotNull();
            assertThat(loansLoanIdResponse.getPageItems().iterator().next().getSummary()).isNull();
        });
    }

    @Test
    public void test_retrieveLoansWithSecuredParameter_Works() {
        AtomicLong securedLoanId = new AtomicLong();
        AtomicLong unsecuredLoanId = new AtomicLong();
        Long clientId = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();

        runAt("01 January 2023", () -> {
            int numberOfRepayments = 3;
            int repaymentEvery = 1;

            // Create Loan Product
            PostLoanProductsRequest product = createOnePeriod30DaysLongNoInterestPeriodicAccrualProduct()
                    .numberOfRepayments(numberOfRepayments)
                    .repaymentEvery(repaymentEvery)
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS.longValue());

            PostLoanProductsResponse loanProductResponse = loanProductHelper.createLoanProduct(product);
            Long loanProductId = loanProductResponse.getResourceId();

            // Create collateral for secured loan
            Integer collateralId = CollateralManagementHelper.createCollateralProduct(REQUEST_SPEC, RESPONSE_SPEC);
            Integer clientCollateralId = CollateralManagementHelper.createClientCollateral(REQUEST_SPEC, RESPONSE_SPEC,
                    String.valueOf(clientId), collateralId);
            List<HashMap> collaterals = new ArrayList<>();
            addCollaterals(collaterals, clientCollateralId, BigDecimal.valueOf(1));

            // Apply and Approve Secured Loan (with collateral)
            double amount = 5000.0;
            PostLoansRequest securedLoanRequest = applyLoanRequest(clientId, loanProductId, "01 January 2023", amount, numberOfRepayments)
                    .repaymentEvery(repaymentEvery)
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS)
                    .collateral(collaterals);

            PostLoansResponse securedLoanResponse = loanTransactionHelper.applyLoan(securedLoanRequest);
            PostLoansLoanIdResponse approvedSecuredLoan = loanTransactionHelper.approveLoan(securedLoanResponse.getResourceId(),
                    approveLoanRequest(amount, "01 January 2023"));
            securedLoanId.set(approvedSecuredLoan.getLoanId());

            // Apply and Approve Unsecured Loan (without collateral)
            PostLoansRequest unsecuredLoanRequest = applyLoanRequest(clientId, loanProductId, "01 January 2023", amount, numberOfRepayments)
                    .repaymentEvery(repaymentEvery)
                    .repaymentFrequencyType(RepaymentFrequencyType.MONTHS);

            PostLoansResponse unsecuredLoanResponse = loanTransactionHelper.applyLoan(unsecuredLoanRequest);
            PostLoansLoanIdResponse approvedUnsecuredLoan = loanTransactionHelper.approveLoan(unsecuredLoanResponse.getResourceId(),
                    approveLoanRequest(amount, "01 January 2023"));
            unsecuredLoanId.set(approvedUnsecuredLoan.getLoanId());

            disburseLoan(securedLoanId.get(), BigDecimal.valueOf(amount), "01 January 2023");
            disburseLoan(unsecuredLoanId.get(), BigDecimal.valueOf(amount), "01 January 2023");
        });

        runAt("01 February 2023", () -> {
            GetLoansResponse securedLoansResponse = loanTransactionHelper.retrieveAllLoans(null, null, clientId, true);
            assertThat(securedLoansResponse.getPageItems()).isNotNull();
            assertThat(securedLoansResponse.getPageItems().size()).isEqualTo(1);
            assertThat(securedLoansResponse.getPageItems().iterator().next().getId()).isEqualTo(securedLoanId.get());

            GetLoansResponse unsecuredLoansResponse = loanTransactionHelper.retrieveAllLoans(null, null, clientId, false);
            assertThat(unsecuredLoansResponse.getPageItems()).isNotNull();
            assertThat(unsecuredLoansResponse.getPageItems().size()).isEqualTo(1);
            assertThat(unsecuredLoansResponse.getPageItems().iterator().next().getId()).isEqualTo(unsecuredLoanId.get());

            GetLoansResponse allLoansResponse = loanTransactionHelper.retrieveAllLoans(null, null, clientId, null);
            assertThat(allLoansResponse.getPageItems()).isNotNull();
            assertThat(allLoansResponse.getPageItems().size()).isEqualTo(2);
        });
    }

    @Test
    public void test_retrieveLoansWithSecuredParameter_PaginationConsistency() {
        Long clientId = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();
        List<Long> securedLoanIds = new ArrayList<>();
        List<Long> unsecuredLoanIds = new ArrayList<>();

        runAt("01 January 2023", () -> {
            // Create multiple secured and unsecured loans to test pagination
            PostLoanProductsRequest product = createOnePeriod30DaysLongNoInterestPeriodicAccrualProduct();
            PostLoanProductsResponse loanProductResponse = loanProductHelper.createLoanProduct(product);
            Long loanProductId = loanProductResponse.getResourceId();

            // Create collateral
            Integer collateralId = CollateralManagementHelper.createCollateralProduct(REQUEST_SPEC, RESPONSE_SPEC);
            Integer clientCollateralId = CollateralManagementHelper.createClientCollateral(REQUEST_SPEC, RESPONSE_SPEC,
                    String.valueOf(clientId), collateralId);
            List<HashMap> collaterals = new ArrayList<>();
            addCollaterals(collaterals, clientCollateralId, BigDecimal.valueOf(1));

            // Create 3 secured loans
            for (int i = 0; i < 3; i++) {
                PostLoansRequest securedLoanRequest = applyLoanRequest(clientId, loanProductId, "01 January 2023", 1000.0, 1)
                        .collateral(collaterals);
                PostLoansResponse securedLoanResponse = loanTransactionHelper.applyLoan(securedLoanRequest);
                PostLoansLoanIdResponse approvedSecuredLoan = loanTransactionHelper.approveLoan(securedLoanResponse.getResourceId(),
                        approveLoanRequest(1000.0, "01 January 2023"));
                securedLoanIds.add(approvedSecuredLoan.getLoanId());
            }

            // Create 2 unsecured loans
            for (int i = 0; i < 2; i++) {
                PostLoansRequest unsecuredLoanRequest = applyLoanRequest(clientId, loanProductId, "01 January 2023", 1000.0, 1);
                PostLoansResponse unsecuredLoanResponse = loanTransactionHelper.applyLoan(unsecuredLoanRequest);
                PostLoansLoanIdResponse approvedUnsecuredLoan = loanTransactionHelper.approveLoan(unsecuredLoanResponse.getResourceId(),
                        approveLoanRequest(1000.0, "01 January 2023"));
                unsecuredLoanIds.add(approvedUnsecuredLoan.getLoanId());
            }
        });

        runAt("01 February 2023", () -> {
            GetLoansResponse securedLoansResponse = loanTransactionHelper.retrieveAllLoans(null, null, clientId, true);
            assertThat(securedLoansResponse.getPageItems().size()).isEqualTo(3);

            GetLoansResponse unsecuredLoansResponse = loanTransactionHelper.retrieveAllLoans(null, null, clientId, false);
            assertThat(unsecuredLoansResponse.getPageItems().size()).isEqualTo(2);

            GetLoansResponse allLoansResponse = loanTransactionHelper.retrieveAllLoans(null, null, clientId, null);
            assertThat(allLoansResponse.getPageItems().size()).isEqualTo(5);
        });
    }

    @Test
    public void test_retrieveLoansWithSecuredParameter_MultipleCollateralScenarios() {
        Long clientId = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();
        AtomicLong multipleCollateralLoanId = new AtomicLong();

        runAt("01 January 2023", () -> {
            PostLoanProductsRequest product = createOnePeriod30DaysLongNoInterestPeriodicAccrualProduct();
            PostLoanProductsResponse loanProductResponse = loanProductHelper.createLoanProduct(product);
            Long loanProductId = loanProductResponse.getResourceId();

            // Create multiple collateral items
            Integer collateralId1 = CollateralManagementHelper.createCollateralProduct(REQUEST_SPEC, RESPONSE_SPEC);
            Integer collateralId2 = CollateralManagementHelper.createCollateralProduct(REQUEST_SPEC, RESPONSE_SPEC);
            
            Integer clientCollateralId1 = CollateralManagementHelper.createClientCollateral(REQUEST_SPEC, RESPONSE_SPEC,
                    String.valueOf(clientId), collateralId1);
            Integer clientCollateralId2 = CollateralManagementHelper.createClientCollateral(REQUEST_SPEC, RESPONSE_SPEC,
                    String.valueOf(clientId), collateralId2);
            
            List<HashMap> collaterals = new ArrayList<>();
            addCollaterals(collaterals, clientCollateralId1, BigDecimal.valueOf(1));
            addCollaterals(collaterals, clientCollateralId2, BigDecimal.valueOf(1));

            // Create loan with multiple collateral items
            PostLoansRequest loanRequest = applyLoanRequest(clientId, loanProductId, "01 January 2023", 5000.0, 1)
                    .collateral(collaterals);
            PostLoansResponse loanResponse = loanTransactionHelper.applyLoan(loanRequest);
            PostLoansLoanIdResponse approvedLoan = loanTransactionHelper.approveLoan(loanResponse.getResourceId(),
                    approveLoanRequest(5000.0, "01 January 2023"));
            multipleCollateralLoanId.set(approvedLoan.getLoanId());

            disburseLoan(multipleCollateralLoanId.get(), BigDecimal.valueOf(5000.0), "01 January 2023");
        });

        runAt("01 February 2023", () -> {
            GetLoansResponse securedLoansResponse = loanTransactionHelper.retrieveAllLoans(null, null, clientId, true);
            assertThat(securedLoansResponse.getPageItems()).isNotNull();
            assertThat(securedLoansResponse.getPageItems().size()).isEqualTo(1);
            assertThat(securedLoansResponse.getPageItems().iterator().next().getId()).isEqualTo(multipleCollateralLoanId.get());

            GetLoansResponse unsecuredLoansResponse = loanTransactionHelper.retrieveAllLoans(null, null, clientId, false);
            assertThat(unsecuredLoansResponse.getPageItems()).isNotNull();
            assertThat(unsecuredLoansResponse.getPageItems().size()).isEqualTo(0);
        });
    }
}
