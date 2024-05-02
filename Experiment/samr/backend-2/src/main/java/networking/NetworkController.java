package networking;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/network")
public class NetworkController {

	HashMap<String, User> users = new HashMap<>();
	Graph.DirectedGraph<User> user_graph = new Graph.DirectedGraph<>();


	/** List the currently registered users. */
	@GetMapping(path = "/users")
	public @ResponseBody Collection<User> getUsers() {
		return this.users.values();
	}
	/** Get a user's data by their username -- null if username is not valid. */
	@GetMapping(path = "/users/{uname}")
	public @ResponseBody User getUserByName(@PathVariable String uname) {
		return this.users.get(uname);
	}
	/** Get the user's secret message by logging in. */
	@PostMapping(path = "users/{uname}/login")
	public @ResponseBody String getUserSecret(@PathVariable String uname, @RequestBody String password) {
		if(password != null && uname != null && this.users.containsKey(uname)) {
			final User u = this.users.get(uname);
			if(u.password.equals(password)) {
				return u.secret;
			}
		}
		return "Unable to access secret.";
	}

	/** Add a user to the system. */
	@PostMapping(path = "/users")
	public @ResponseBody String addUser(@RequestBody User user) {
		if(user == null || user.username == null || user.username.isBlank() || this.users.containsKey(user.username)) {
			return String.format("Cannot add user with username [%s] : invalid or already present!", user.username);
		}
		// user.id = this.users.size() + 1;
		this.users.put(user.username, user);
		this.user_graph.add(user);
		return String.format("Successfully added user [%s]!", user.username);
	}


}
