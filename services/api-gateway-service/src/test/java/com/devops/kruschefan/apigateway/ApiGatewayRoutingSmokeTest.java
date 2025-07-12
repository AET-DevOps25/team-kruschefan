package com.devops.kruschefan.apigateway;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ApiGatewayRoutingSmokeTest {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    void testUserServiceRoute() {
        assertDoesNotThrow(() -> {
            Response response = RestAssured.get("/user");
            System.out.println("User Service Status: " + response.getStatusCode());
        });
    }

    @Test
    void testFormServiceRoute() {
        assertDoesNotThrow(() -> {
            Response response = RestAssured.get("/form");
            System.out.println("Form Service Status: " + response.getStatusCode());
        });
    }

    @Test
    void testTemplateServiceRoute() {
        assertDoesNotThrow(() -> {
            Response response = RestAssured.get("/template");
            System.out.println("Template Service Status: " + response.getStatusCode());
        });
    }

    @Test
    void testGenAIServiceRoute() {
        assertDoesNotThrow(() -> {
            Response response = RestAssured.get("/genai");
            System.out.println("GenAI Service Status: " + response.getStatusCode());
        });
    }
}
