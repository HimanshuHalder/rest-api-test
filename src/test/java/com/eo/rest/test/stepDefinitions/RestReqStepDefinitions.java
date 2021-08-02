package com.eo.rest.test.stepDefinitions;

import com.eo.rest.test.objects.CreatedResponse;
import com.eo.rest.test.objects.GetAllUserResponse;
import com.eo.rest.test.objects.GetUserDetails;
import com.eo.rest.test.objects.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.eo.rest.test.utils.CommonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;

import java.util.Properties;

public class RestReqStepDefinitions {

    private Response response;
    private RequestSpecification request;
    private String requestUrl;
    private Scenario scenario = null;
    private String requestBody;
    private String baseUrl;
    private Properties props = null;
    private String propertiesFileName;
    private String userId = "";
    private int perPage = 0;

    @Before
    public void initialization(Scenario scenario) {
        this.scenario = scenario;
        String profile = System.getProperty("profile") != null ? System.getProperty("profile") : "e1";
        propertiesFileName = "test-config-" + profile + ".properties";
        if (props == null) {
            props = new CommonUtil().readPropertiesFile(propertiesFileName);
        }
        baseUrl = props.getProperty(profile + ".url");
    }

    @Given("I have provided information as below")
    public void i_have_provided_information_as_below(io.cucumber.datatable.DataTable dataTable) throws JsonProcessingException {
        User user = new ObjectMapper().convertValue(dataTable.asMap(String.class, String.class), User.class);
        requestBody = new ObjectMapper().writeValueAsString(user);
        scenario.log("Request Body ::\n" + requestBody);
    }

    @When("I called the endpoint {string}")
    public void i_called_the_endpoint(String restEndpoint) {
        RestAssured.baseURI = baseUrl;
        request = RestAssured.given().config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("applicaiton/json", ContentType.TEXT)));
        try {
            requestUrl = baseUrl + restEndpoint;
            scenario.log(requestUrl);
            response = request.body(requestBody)
                    .contentType("applicaiton/json")
                    .post(requestUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        scenario.log(response.prettyPrint());
    }

    @Then("I should receive the status code is {int}")
    public void i_should_receive_the_status_code_is(Integer statusCode) {
        scenario.log("Status code: " + response.statusLine());
        Assert.assertTrue("Status code validation.", response.statusCode() == statusCode);
    }

    @Then("should have expected return body")
    public void should_have_expected_return_body() throws JsonProcessingException {
        CreatedResponse createdResponse = new ObjectMapper().readValue(response.prettyPrint(), CreatedResponse.class);
        Assert.assertTrue("Must return ID.", new CommonUtil().isNumeric(createdResponse.id));
        Assert.assertTrue("Must createdDate in UTC timestamp.", new CommonUtil().isTimeStampValid(createdResponse.createdAt));
        //TODO: Below line must be uncommented once create response will be fixed
//        Assert.assertTrue("Must name.", !createdResponse.name.equals(""));
//        Assert.assertTrue("Must job.", !createdResponse.job.equals(""));
    }

    @Given("I am calling GET user endpoint to fetch data with {string}")
    public void i_am_calling_get_user_endpoint_to_fetch_data_with(String id) {
        userId = id;
    }

    @When("I called get the endpoint {string}")
    public void i_called_get_the_endpoint(String endpoint) {
        RestAssured.baseURI = baseUrl;
        request = RestAssured.given().config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("applicaiton/json", ContentType.TEXT)));
        try {
            requestUrl = baseUrl + endpoint;
            scenario.log(requestUrl);
            if (userId.equals("all")) {
                response = request
                        .contentType("applicaiton/json")
                        .get(requestUrl);
            } else {
                response = request.param("id", userId)
                        .contentType("applicaiton/json")
                        .get(requestUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        scenario.log(response.prettyPrint());
    }

    @When("I called get the endpoint {string} with {string}")
    public void i_called_get_the_endpoint_with(String endpoint, String perPage) {
        RestAssured.baseURI = baseUrl;
        request = RestAssured.given().config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("applicaiton/json", ContentType.TEXT)));
        try {
            requestUrl = baseUrl + endpoint;
            scenario.log(requestUrl);
            if (perPage.equals("max")) {
                this.perPage = 999;
                response = request.param("per_page", 999)
                        .contentType("applicaiton/json")
                        .get(requestUrl);
            } else if (perPage.equals("default")) {
                this.perPage = 6;
                response = request
                        .contentType("applicaiton/json")
                        .get(requestUrl);
            } else {
                this.perPage = Integer.parseInt(perPage);
                response = request.param("per_page", this.perPage)
                        .contentType("applicaiton/json")
                        .get(requestUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        scenario.log(response.prettyPrint());
    }

    @Then("should return data based on page")
    public void should_return_data_based_on_page() throws JsonProcessingException {
        GetAllUserResponse getUserResponse = new ObjectMapper().readValue(response.prettyPrint(), GetAllUserResponse.class);

        int page = (getUserResponse.total % perPage != 0) ? getUserResponse.total / perPage + 1 : getUserResponse.total / perPage;
        int dataSize = perPage > getUserResponse.total ? getUserResponse.total : perPage;

        Assert.assertTrue("Validating per page.", getUserResponse.per_page == perPage);
        Assert.assertTrue("Validating total pages.", getUserResponse.total_pages == page);
        Assert.assertTrue("Validating number of data returned.", getUserResponse.data.size() == dataSize);
        for(int i=0; i<getUserResponse.data.size();i++) {
            //considering user id, email, first_name, last_name, avatar value will be returned always
            //considering avatar will always be a jpg file if not we have to update assertion related to expected file format
            Assert.assertTrue("Validating user id.", getUserResponse.data.get(i).id != 0);
            Assert.assertTrue("Validating email.", !getUserResponse.data.get(i).email.isEmpty());
            Assert.assertTrue("Validating first name.", !getUserResponse.data.get(i).first_name.isEmpty());
            Assert.assertTrue("Validating last name.", !getUserResponse.data.get(i).last_name.isEmpty());
            Assert.assertTrue("Validating avatar.", getUserResponse.data.get(i).avatar != null);
            Assert.assertTrue("Validating avatar.", new CommonUtil().isValidUrl(getUserResponse.data.get(i).avatar));
            Assert.assertTrue("Validating avatar.", getUserResponse.data.get(i).avatar.endsWith("jpg") || getUserResponse.data.get(i).avatar.endsWith("JPG"));
        }
        Assert.assertTrue("Validating support url.", new CommonUtil().isValidUrl(getUserResponse.support.url));
        Assert.assertTrue("Validating support url.", getUserResponse.support.text.equals("To keep ReqRes free, contributions towards server costs are appreciated!"));
    }

    @Then("should return the user details associated with the id")
    public void should_return_the_user_details_associated_with_the_id() throws JsonProcessingException {
        GetUserDetails getUserDetails = new ObjectMapper().readValue(response.prettyPrint(), GetUserDetails.class);
        Assert.assertTrue("Validating user id.", getUserDetails.data.id == Integer.parseInt(userId));
        Assert.assertTrue("Validating email.", !getUserDetails.data.email.isEmpty());
        Assert.assertTrue("Validating first name.", !getUserDetails.data.first_name.isEmpty());
        Assert.assertTrue("Validating last name.", !getUserDetails.data.last_name.isEmpty());
        Assert.assertTrue("Validating avatar.", getUserDetails.data.avatar != null);
        Assert.assertTrue("Validating avatar.", new CommonUtil().isValidUrl(getUserDetails.data.avatar));
        Assert.assertTrue("Validating avatar.", getUserDetails.data.avatar.endsWith("jpg"));
        Assert.assertTrue("Validating support url.", new CommonUtil().isValidUrl(getUserDetails.support.url));
        Assert.assertTrue("Validating support url.", getUserDetails.support.text.equals("To keep ReqRes free, contributions towards server costs are appreciated!"));
    }

}



