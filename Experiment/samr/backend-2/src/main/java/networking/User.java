package networking;

import java.util.HashSet;
import java.util.Set;

import org.springframework.core.style.ToStringCreator;


public class User {

	protected String username;
	protected String password;
	// protected Integer id;

	protected String secret;


	public User() {
		// this.id = -1;
	}
	public User(String username, String password, String secret) {
		this.username = username;
		this.password = password;
		// this.id = -1;
		this.secret = secret;
	}


	public String getUsername() { return this.username; }
	public void setUsername(String username) { this.username = username; }
	public String getPassword() { return this.password; }
	public void setPassword(String password) { this.password = password; }
	// public Integer getId() { return this.id; }
	public String getSecret() { return this.secret; }
	public void setSecret(String secret) { this.secret = secret; }


	private static String assertPasswordStrength(String pw) {
		final String[] levels = new String[]{ "poor", "moderate", "good", "strong", "powerful", "gigachad" };
		final int i = (pw.length() - 1) / 3;
		return levels[i > 5 ? 5 : i];
	}

	@Override
	public String toString() {
		return new ToStringCreator(this)
			.append("username", this.username)
			.append("password:", assertPasswordStrength(this.password))
			// .append("id", this.id)
			.append("secret", this.secret.isBlank() ? "none" : "classified")
		.toString();
	}

}
