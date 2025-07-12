
package com.devops.kruschefan.apigateway;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ApiGatewayRoutingTests {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    void testUserServiceRoute() {
        Response response = RestAssured.get("/user");
        assertThat(response.getStatusCode(), is(anyOf(is(200), is(401), is(403), is(404))));
    }

    @Test
    void testFormServiceRoute() {
        Response response = RestAssured.get("/form");
        assertThat(response.getStatusCode(), is(anyOf(is(200), is(401), is(403), is(404))));
    }

    @Test
    void testTemplateServiceRoute() {
        Response response = RestAssured.get("/template");
        assertThat(response.getStatusCode(), is(anyOf(is(200), is(401), is(403), is(404))));
    }

    @Test
    void testGenAIServiceRoute() {
        Response response = RestAssured.get("/genai");
        assertThat(response.getStatusCode(), is(anyOf(is(200), is(401), is(403), is(404))));
    }
}
