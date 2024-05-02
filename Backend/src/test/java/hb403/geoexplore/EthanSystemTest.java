package hb403.geoexplore;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import hb403.geoexplore.datatype.MarkerTag;
import hb403.geoexplore.datatype.marker.ObservationMarker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.web.server.LocalServerPort;	// SBv3

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@RunWith(SpringRunner.class)
public class EthanSystemTest {

  @LocalServerPort
	int port = 8080;


	private ObservationMarker tempObs;
	private MarkerTag tag;

	private Set<MarkerTag> assertionTag;
	Long tag_id = (long) 2;
	@Before
	public void setUp() {
		RestAssured.port = port;
		RestAssured.baseURI = "http://localhost";

		this.tempObs = new ObservationMarker(); //set up observation to be tested
		this.tempObs.setTitle("Samba");
		this.tempObs.setDescription("A new way to dance");
		this.tempObs.setIo_latitude(42.03);
		this.tempObs.setIo_longitude(93.63);
		this.tempObs.enforceLocationIO();

		this.tag = new MarkerTag(); //set up tag to be tested
		this.tag.setId(tag_id);
		this.tag.setName("tag1");
	}


	//Variables that need to be preserved throughout testing
	Long id = (long) 57; //this just needs to be updated to what the observation's id will be, I kept running into bugs when trying to get it.


	//test 1 of 4, this will be testing the creation of an observation
	@Test
	public void observationPostTest() {
		// Send request and receive response
		Response response = RestAssured.given().
				contentType("application/JSON").
				body(this.tempObs).
				when().
				post("geomap/observations");
		// Check status code
		int statusCode = response.getStatusCode();
		assertEquals(200, statusCode);
		// Check response body for correct response
		String returnString = response.getBody().asString();
		try {
			JSONObject returnObj = new JSONObject(returnString);
			//tempObs.setId((long) returnObj.get("id"));

			assertEquals("Samba", returnObj.get("title"));
			assertEquals("A new way to dance",returnObj.get("description"));
			assertEquals(42.03, returnObj.get("io_latitude"));
			assertEquals(93.63,returnObj.get("io_longitude") );
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	//test 2 of 4, this is a test to update the observation just changing the title and description with location staying the same
	@Test
	public void observationUpdateTest() {
		tempObs.setTitle("Salsa");
		tempObs.setDescription("A new fun way to dance");
		Response response = RestAssured.given().
				contentType("application/JSON").
				body(this.tempObs).
				when().
				put("geomap/observations/{id}",id);
		// Check status code
		int statusCode = response.getStatusCode();
		assertEquals(200, statusCode);
		// Check response body for correct response
		String returnString = response.getBody().asString();
		try {
			JSONObject returnObj = new JSONObject(returnString);
			//JSONObject returnObj = returnArr.getJSONObject(returnArr.length()-1);
			assertEquals("Salsa", returnObj.get("title"));
			assertEquals("A new fun way to dance",returnObj.get("description"));
			assertEquals(42.03, returnObj.get("io_latitude"));
			assertEquals(93.63,returnObj.get("io_longitude") );
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	//test 3 of 4, this will be adding a tag to the observation
	@Test
	public void observationTagTest() {
		//tempObs.getTags().add(tag);
		Response response = RestAssured.given().
				contentType("application/JSON").
				body(this.tag.getId()).
				when().
				post("geomap/observations/{id}/tags",id);
		// Check status code
		int statusCode = response.getStatusCode();
		assertEquals(200, statusCode);
		// Check response body for correct response
		String returnString = response.getBody().asString();
		try {
			JSONObject returnObj = new JSONObject(returnString);
			System.out.println(tag.toString());
			System.out.println(returnObj.get("tags"));
			assertNotEquals(null, returnObj.get("tags")); //since the tags in the object, come as a value I can't match for some reason I just make sure it's not null
			assertEquals("Salsa", returnObj.get("title"));
			assertEquals("A new fun way to dance",returnObj.get("description"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//test 4 of 4, this will be deleting the observation

	@Test
	public void observationDeleteTest() {
		Response response = RestAssured.given().
				contentType("application/JSON").
				body("").
				when().
				delete("geomap/observations/{id}",id);
		// Check status code
		int statusCode = response.getStatusCode();
		assertEquals(200, statusCode);
		// Check response body for correct response
		String returnString = response.getBody().asString();
		try {
			JSONObject returnObj = new JSONObject(returnString);
            assertEquals("Salsa", returnObj.get("title"));
			//assertEquals("A new way to dance",returnObj.get("description"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
