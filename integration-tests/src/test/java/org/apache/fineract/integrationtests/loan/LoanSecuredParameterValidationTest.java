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

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.fineract.integrationtests.BaseLoanIntegrationTest;
import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.junit.jupiter.api.Test;

public class LoanSecuredParameterValidationTest extends BaseLoanIntegrationTest {

    @Test
    public void test_retrieveLoansWithInvalidSecuredParameter_Returns400() {
        Long clientId = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();

        runAt("01 January 2023", () -> {
            RequestSpecification requestSpec = new RequestSpecBuilder()
                    .setContentType(ContentType.JSON)
                    .build();
            requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
            requestSpec.header("Fineract-Platform-TenantId", "default");

            ResponseSpecification responseSpec400 = new ResponseSpecBuilder()
                    .expectStatusCode(400)
                    .build();

            String response = given()
                    .spec(requestSpec)
                    .queryParam("clientId", clientId)
                    .queryParam("secured", "maybe") // Invalid boolean value
                    .when()
                    .get("/fineract-provider/api/v1/loans")
                    .then()
                    .spec(responseSpec400)
                    .extract()
                    .asString();

            assertTrue(response.contains("error") || response.contains("invalid") || response.contains("400"));
        });
    }

    @Test
    public void test_retrieveLoansWithInvalidSecuredParameterValues() {
        Long clientId = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();

        runAt("01 January 2023", () -> {
            RequestSpecification requestSpec = new RequestSpecBuilder()
                    .setContentType(ContentType.JSON)
                    .build();
            requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
            requestSpec.header("Fineract-Platform-TenantId", "default");

            ResponseSpecification responseSpec400 = new ResponseSpecBuilder()
                    .expectStatusCode(400)
                    .build();

            String[] invalidValues = {"yes", "no", "1", "0", "TRUE", "FALSE", "null", "undefined", "abc"};
            
            for (String invalidValue : invalidValues) {
                given()
                        .spec(requestSpec)
                        .queryParam("clientId", clientId)
                        .queryParam("secured", invalidValue)
                        .when()
                        .get("/fineract-provider/api/v1/loans")
                        .then()
                        .spec(responseSpec400);
            }
        });
    }

    @Test
    public void test_retrieveLoansWithValidSecuredParameterValues() {
        Long clientId = clientHelper.createClient(ClientHelper.defaultClientCreationRequest()).getClientId();

        runAt("01 January 2023", () -> {
            RequestSpecification requestSpec = new RequestSpecBuilder()
                    .setContentType(ContentType.JSON)
                    .build();
            requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
            requestSpec.header("Fineract-Platform-TenantId", "default");

            ResponseSpecification responseSpec200 = new ResponseSpecBuilder()
                    .expectStatusCode(200)
                    .build();

            String[] validValues = {"true", "false"};
            
            for (String validValue : validValues) {
                given()
                        .spec(requestSpec)
                        .queryParam("clientId", clientId)
                        .queryParam("secured", validValue)
                        .when()
                        .get("/fineract-provider/api/v1/loans")
                        .then()
                        .spec(responseSpec200);
            }

            given()
                    .spec(requestSpec)
                    .queryParam("clientId", clientId)
                    .when()
                    .get("/fineract-provider/api/v1/loans")
                    .then()
                    .spec(responseSpec200);
        });
    }
}
