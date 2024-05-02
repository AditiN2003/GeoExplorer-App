package org.springframework.samples.petclinic.pet;

import org.springframework.core.style.ToStringCreator;
import org.hibernate.annotations.*;
import jakarta.persistence.*;
import jakarta.persistence.Table;


@Entity
@Table(name = "pets")
public class Pet {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@NotFound(action = NotFoundAction.IGNORE)
	public Integer id;

	@Column(name = "name")
	@NotFound(action = NotFoundAction.IGNORE)
	public String name;

	@Column(name = "nickname")
	@NotFound(action = NotFoundAction.IGNORE)
	public String nickname;

	@Column(name = "type")
	@NotFound(action = NotFoundAction.IGNORE)
	public String type;

	@Column(name = "age")
	@NotFound(action = NotFoundAction.IGNORE)
	public Integer age;

	@Column(name = "owner_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public Integer owner_id;


	public Pet() {}
	public Pet(int id, String name, String nickname, String type, int age, int owner_id) {
		this.id = id;
		this.name = name;
		this.nickname = nickname;
		this.type = type;
		this.age = age;
		this.owner_id = owner_id;
	}

	public Integer getId() { return this.id; }
	public void setId(Integer id) { this.id = id; }
	public String getName() { return this.name; }
	public void setName(String n) { this.name = n; }
	public String getNickname() { return this.nickname; }
	public void setNickname(String nn) { this.nickname = nn; }
	public String getType() { return this.type; }
	public void setType(String type) { this.type = type; }
	public Integer getAge() { return this.age; }
	public void setAge(Integer age) { this.age = age; }
	public Integer getOwnerId() { return this.owner_id; }
	public void setOwnerId(Integer oi) { this.owner_id = oi; }

	@Override
	public String toString() {
		return new ToStringCreator(this)
			.append("id", this.id)
			.append("name", this.name)
			.append("nickname", this.nickname)
			.append("type", this.type)
			.append("age", this.age)
			.append("ownerId", this.owner_id)
		.toString();
	}


}
