package hb403.geoexplore;

import hb403.geoexplore.UserStorage.LocationSharing;
import hb403.geoexplore.UserStorage.entity.User;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.web.server.LocalServerPort;	// SBv3


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SamSystemTest {
	
	@LocalServerPort
	int port;

	private static User
		create_test_user = null,
		update_test_user = null;
	private static long test_user_id = -1L;

	@Before
	public void setUp() {

		RestAssured.port = port;
		RestAssured.baseURI = "http://localhost";

	}

	@BeforeClass
	public static void setUpStatic() {
		create_test_user = new User();
		create_test_user.setId(-1L);
		create_test_user.setName("NEW USER (AUTOMATED TEST)");
		create_test_user.setEmailId("email@iastate.edu");
		create_test_user.setPassword("87fdj203dxcjk392");
		create_test_user.setLocation_privacy(LocationSharing.PUBLIC);
		create_test_user.setIo_latitude(10.0);
		create_test_user.setIo_longitude(-10.0);
		create_test_user.setLast_location_update(new Date());

		update_test_user = new User();
		update_test_user.setId(-1L);
		update_test_user.setName("UPDATED USER (AUTOMATED TEST)");
		update_test_user.setEmailId("updated@iastate.edu");
		update_test_user.setPassword("f908fds098f90d");
		update_test_user.setLocation_privacy(LocationSharing.GROUP);
		update_test_user.setIo_latitude(-25.0);
		update_test_user.setIo_longitude(25.0);
		update_test_user.setLast_location_update(new Date());
	}


	private static void assertUsersEqual(User a, User b) {
		assertTrue(		a.getName()					.equals( b.getName() )					);
		assertTrue(		a.getEmailId()				.equals( b.getEmailId() )				);
		assertTrue(		a.getPassword()				.equals( b.getPassword() )				);
		assertEquals(	a.getLocation_privacy(), 	b.getLocation_privacy()					);
		assertEquals(	a.getIo_latitude(), 		b.getIo_latitude()						);
		assertEquals(	a.getIo_longitude(), 		b.getIo_longitude()						);
		System.out.printf("A time: %d, B time: %d\n", a.getLast_location_update().getTime(), b.getLast_location_update().getTime());
		assertTrue(		a.getLast_location_update().getTime() <= b.getLast_location_update().getTime()	);	// these are always a little off... :|
	}

	@Test
	public void A_postUserTest() {

		System.out.printf("Running POST test -- ID is %d\n", test_user_id);

		final Response resp = 
			RestAssured
				.given()
				.contentType("application/json")
				.body(create_test_user)
				.post("/user/create");

		try {
			test_user_id = resp.getBody().as(User.class).getId();
		} catch(Exception e) {
			test_user_id = -1L;	// fail
		}

		assertEquals(200, resp.getStatusCode());
		// System.out.println(this.post_test_response.getBody().asPrettyString());
		final User u = resp.getBody().as(User.class);

		assertTrue(test_user_id >= 0);
		SamSystemTest.assertUsersEqual(create_test_user, u);

	}

	@Test
	public void B_getUserTest() {

		System.out.printf("Running GET test -- ID is %d\n", test_user_id);

		assertTrue(test_user_id >= 0);

		final Response resp =
			RestAssured.get("/user/{id}", test_user_id);

		assertEquals(200, resp.getStatusCode());
		final User u = resp.getBody().as(User.class);

		assertEquals(test_user_id, u.getId());
		SamSystemTest.assertUsersEqual(create_test_user, u);

	}

	@Test
	public void C_updateUserTest() {

		System.out.printf("Running PUT test -- ID is %d\n", test_user_id);

		assertTrue(test_user_id >= 0);

		final Response resp =
			RestAssured
				.given()
				.contentType("application/json")
				.body(update_test_user)
				.put("/user/{id}/update", test_user_id);

		assertEquals(200, resp.getStatusCode());
		// System.out.println(resp.getBody().asPrettyString());
		final User u = resp.getBody().as(User.class);

		assertEquals(test_user_id, u.getId());
		SamSystemTest.assertUsersEqual(update_test_user, u);

	}

	@Test
	public void D_deleteUserTest() {

		System.out.printf("Running DELETE test -- ID is %d\n", test_user_id);

		assertTrue(test_user_id >= 0);

		final Response resp =
			RestAssured.delete("/user/{id}/delete", test_user_id);

		assertEquals(200, resp.getStatusCode());
		final User u = resp.getBody().as(User.class);

		assertEquals(test_user_id, u.getId());
		SamSystemTest.assertUsersEqual(update_test_user, u);

		final Response verify =
			RestAssured.get("/user/{id}", test_user_id);

		assertEquals(200, verify.getStatusCode());
		try {
			final User v = verify.getBody().as(User.class);
			assertEquals(null, v);
		} catch(Exception e) {
			// this is also fine
		}

	}


	// @Test
	// public void testSequential() {
	// 	this.postUserTest();
	// 	this.getUserTest();
	// 	this.updateUserTest();
	// 	this.deleteUserTest();
	// }


}
