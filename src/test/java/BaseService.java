import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * @author : Ibrahim Sadigov
 * @mailto : isadigov4638@ada.edu.az
 * @created : 08 July, 2024
 **/
public class BaseService {

    public static final String BASE_URI = "https://reqres.in";
    public static final String CREATE_USER = "/api/users";
    public static final String LIST_USERS = "/api/users?page=2";
    public static final String SINGLE_USER = "/api/users/";

    RequestSpecification spec;

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = BASE_URI;

        spec = new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .addFilters(Arrays.asList(new RequestLoggingFilter(), new ResponseLoggingFilter()))
                .build();

    }

    public Response createUserService(){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode body = mapper.createObjectNode();

        body.put("name", "morpheus");
        body.put("job", "leader");

        Response response = requestSpec(CREATE_USER, body, 201, "post");

        JsonPath jsonPath = response.jsonPath();

        // Assert data fields
        assertThat("User ID should not be null", jsonPath.getString("id"), is(notNullValue()));

        // Assert createdAt field
        String createdAt = jsonPath.getString("createdAt");
        assertThat("CreatedAt should not be null", createdAt, is(notNullValue()));
        try {
            Instant.parse(createdAt);
        } catch (DateTimeParseException e) {
            throw new AssertionError("CreatedAt is not in the correct format: " + createdAt);
        }

        System.out.println("CreateUser Assertions passed successfully!");

        return response;
    }

    public int getId(){
        Response responseCreate = createUserService();

        return responseCreate.jsonPath().getInt("id");
    }

    public Response requestSpec(String URL, int statusCode, String requestType){
        if (requestType.equalsIgnoreCase("get")) {
            return given(spec)
                    .when()
                    .get(URL)
                    .then()
                    .statusCode(statusCode)
                    .extract()
                    .response();
        }else if (requestType.equalsIgnoreCase("delete")) {
            return given(spec)
                    .when()
                    .delete(URL)
                    .then()
                    .statusCode(statusCode)
                    .extract()
                    .response();
        }
        else {
            return null;
        }
    }

    public Response requestSpec(String URL, ObjectNode body, int statusCode, String requestType){
        if (requestType.equalsIgnoreCase("post")){
            return given(spec)
                    .body(body)
                    .when()
                    .post(URL)
                    .then()
                    .statusCode(statusCode)
                    .extract()
                    .response();
        } else if (requestType.equalsIgnoreCase("put")) {
            return given(spec)
                    .body(body)
                    .when()
                    .put(URL)
                    .then()
                    .statusCode(statusCode)
                    .extract()
                    .response();
        } else if (requestType.equalsIgnoreCase("patch")) {
            return given(spec)
                    .body(body)
                    .when()
                    .patch(URL)
                    .then()
                    .statusCode(statusCode)
                    .extract()
                    .response();
        } else {
            return null;
        }
    }











}
