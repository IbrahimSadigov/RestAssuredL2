import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.format.DateTimeParseException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author : Ibrahim Sadigov
 * @mailto : isadigov4638@ada.edu.az
 * @created : 08 July, 2024
 **/
public class Users extends BaseService{


    @Test
    public void listUsers(){
        Response response = requestSpec(LIST_USERS, 200, "get");

        JsonPath jsonPath = response.jsonPath();

        // Assert overall structure and values
        assertThat("Page number is incorrect", jsonPath.getInt("page"), equalTo(2));
        assertThat("Per page count is incorrect", jsonPath.getInt("per_page"), equalTo(6));
        assertThat("Total count is incorrect", jsonPath.getInt("total"), equalTo(12));
        assertThat("Total pages count is incorrect", jsonPath.getInt("total_pages"), equalTo(2));

        // Assert data array size and content
        assertThat("Data array should not be empty", jsonPath.getList("data"), is(not(empty())));
        assertThat("Data array size is incorrect", jsonPath.getList("data"), hasSize(6));

        // Check that each item in the data array has non-null values for key fields
        for (int i = 0; i < jsonPath.getList("data").size(); i++) {
            assertThat("ID should not be null", jsonPath.getInt("data[" + i + "].id"), is(notNullValue()));
            assertThat("Email should not be null", jsonPath.getString("data[" + i + "].email"), is(notNullValue()));
            assertThat("First name should not be null", jsonPath.getString("data[" + i + "].first_name"), is(notNullValue()));
            assertThat("Last name should not be null", jsonPath.getString("data[" + i + "].last_name"), is(notNullValue()));
            assertThat("Avatar URL should not be null", jsonPath.getString("data[" + i + "].avatar"), is(notNullValue()));
        }

        // Assert support section
        assertThat("Support URL is incorrect", jsonPath.getString("support.url"), equalTo("https://reqres.in/#support-heading"));
        assertThat("Support text is incorrect", jsonPath.getString("support.text"), equalTo("To keep ReqRes free, contributions towards server costs are appreciated!"));

        System.out.println("ListUsers assertions passed successfully!");
    }


    @Test
    public void singleUser(){

        int id = getId();

        Response response = requestSpec(SINGLE_USER + id, 200, "get");

        JsonPath jsonPath = response.jsonPath();

        // Assert data object fields
        assertThat("User ID is incorrect", jsonPath.getInt("data.id"), equalTo(id));
        assertThat("User email should not be null", jsonPath.getString("data.email"), is(notNullValue()));
        assertThat("User first name should not be null", jsonPath.getString("data.first_name"), is(notNullValue()));
        assertThat("User last name should not be null", jsonPath.getString("data.last_name"), is(notNullValue()));
        assertThat("User avatar URL should not be null", jsonPath.getString("data.avatar"), is(notNullValue()));

        // Assert support section
        assertThat("Support URL is incorrect", jsonPath.getString("support.url"), equalTo("https://reqres.in/#support-heading"));
        assertThat("Support text is incorrect", jsonPath.getString("support.text"), equalTo("To keep ReqRes free, contributions towards server costs are appreciated!"));

        System.out.println("SingleUser Assertions passed successfully!");
    }


    @Test
    public void createUser(){
        createUserService();
    }

    @Test
    public void updateUser(){

        int id = getId();

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode body = mapper.createObjectNode();

        body.put("name", "morpheus");
        body.put("job", "zion resident");

        Response response = requestSpec(SINGLE_USER + id, body, 200, "put");

        JsonPath jsonPath = response.jsonPath();

        String updatedAt = jsonPath.getString("updatedAt");
        assertThat("UpdatedAt should not be null", updatedAt, is(notNullValue()));
        try {
            Instant.parse(updatedAt);
        } catch (DateTimeParseException e) {
            throw new AssertionError("UpdatedAt is not in the correct format: " + updatedAt);
        }

        System.out.println("UpdateUser Assertions passed successfully!");

    }

    @Test
    public void partialUpdateUser(){
        int id = getId();

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode body = mapper.createObjectNode();

        body.put("name", "morpheus");
        body.put("job", "zion resident");

        Response response = requestSpec(SINGLE_USER + id, body, 200, "patch");

        JsonPath jsonPath = response.jsonPath();

        String updatedAt = jsonPath.getString("updatedAt");
        assertThat("UpdatedAt should not be null", updatedAt, is(notNullValue()));
        try {
            Instant.parse(updatedAt);
        } catch (DateTimeParseException e) {
            throw new AssertionError("UpdatedAt is not in the correct format: " + updatedAt);
        }

        System.out.println("PartialUpdateUser Assertions passed successfully!");
    }

    @Test
    public void deleteUser(){
        int id = getId();

        Response response = requestSpec(SINGLE_USER + id, 204, "delete");

        assertThat("Response status code should be 204", response.getStatusCode(), is(204));

        System.out.println("DeleteUser Assertions passed successfully!");
    }


}
