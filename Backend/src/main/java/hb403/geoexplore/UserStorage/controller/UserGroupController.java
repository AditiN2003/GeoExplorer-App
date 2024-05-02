package hb403.geoexplore.UserStorage.controller;

import java.util.*;

import hb403.geoexplore.UserStorage.repository.*;
import hb403.geoexplore.datatype.marker.ObservationMarker;
import hb403.geoexplore.datatype.marker.repository.ObservationRepository;
import io.swagger.v3.oas.annotations.Operation;
import hb403.geoexplore.UserStorage.entity.*;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;


@RestController
public class UserGroupController {

	@Autowired
	UserGroupRepository group_repo;
	@Autowired
	UserRepository user_repo;
	@Autowired
	ObservationRepository observationRepository;

	
	@Operation(summary = "Add a new usergroup to the database")
	@PostMapping(path = "user/groups")
	public @ResponseBody UserGroup addGroup(@RequestBody UserGroup group) {		// adds a group with full UserGroup serialized data
		if(group != null) {
			group.setId(-1L);
			return this.group_repo.save(group);
		}
		return null;
	}
	@Operation(summary = "Create a new usergroup in the database by simply providing a name")
	@PostMapping(path = "user/groups/create")
	public @ResponseBody UserGroup createGroup(@RequestBody String name) {		// creates and adds a group just from a name
		if(name != null && !name.isEmpty()) {
			final UserGroup g = new UserGroup();
			g.setTitle(name);
			g.setId(-1L);
			return this.group_repo.save(g);
		}
		return null;
	}

	@Operation(summary = "Get a usergroup in the database from its id")
	@GetMapping(path = "user/groups/{id}")
	public @ResponseBody UserGroup getGroupById(@PathVariable Long id) {
		if(id != null) {
			try{
				return this.group_repo.findById(id).get();
			} catch(Exception e) {
				// ...
			}
		}
		return null;
	}

	@Operation(summary = "Update a usergroup already in the database by its id")
	@PutMapping(path = "user/groups/{id}")
	public @ResponseBody UserGroup updateGroupById(@PathVariable Long id, @RequestBody UserGroup group) {
		if(id != null && group != null) {
			group.setId(id);
			return this.group_repo.save(group);
		}
		return null;
	}

	@Operation(summary = "Delete a usergroup from the database by its id")
	@DeleteMapping(path = "user/groups/{id}")	// TODO: I think that deleting a group deletes all the users as well :(
	public @ResponseBody UserGroup deleteGroupById(@PathVariable Long id) {
		if(id != null) {
			try {
				final UserGroup g = this.getGroupById(id);
				this.group_repo.deleteById(id);
				return g;
			} catch(Exception e) {
				// ...
			}
		}
		return null;
	}

	@Operation(summary = "Get a list of all the usergroups in the database")
	@GetMapping(path = "user/groups")
	public @ResponseBody List<UserGroup> getAllGroups() {
		return this.group_repo.findAll();
	}

	@Operation(summary = "Add a user already in the database to a usergroup already in the database - both by their respective ids")
	@PostMapping(path = "user/groups/{group_id}/members")
	public @ResponseBody UserGroup addMemberToGroupById(@PathVariable Long group_id, @RequestBody Long user_id) {
		if(group_id != null && user_id != null) {
			try {
				final UserGroup g = this.group_repo.findById(group_id).get();
				final User u = this.user_repo.findById(user_id).get();
				if(g.getMembers().add(u)) {	// if successful add
					u.getGroups().add(g);
				} else {
					return g;	// maybe should return null to signify fail?
				}
				return this.group_repo.save(g);
			} catch(Exception e) {
				System.out.println(e);
			}
		}
		return null;
	}
	@Operation(summary = "Deletes a user from the userGroup without deleting them from the user repo")
	@DeleteMapping(path = "user/usergroups/{group_id}/{user_id}/")
	public @ResponseBody String deleteUser(@PathVariable Long group_id, @PathVariable Long user_id){
		try {
			UserGroup tempGroup = group_repo.findById(group_id).get();
			for(User u : tempGroup.getMembers()) {
				if (u.getId().equals(user_id)) {
					tempGroup.getMembers().remove(u);
					break;
				}
			}
			group_repo.save(tempGroup);
		}
		catch (Exception e){
			System.out.println(e);
		}
		return "successfully deleted user from group";
	}

	@Operation(summary = "Get a list of all the members in a user group")
	@GetMapping(path = "user/usergroups/{group_id}/memberlist")
	public @ResponseBody ArrayList<String> listGroupMembersById(@PathVariable long group_id){
		ArrayList<String> emailIds = new ArrayList<String>();
		try {
			UserGroup temp = this.group_repo.findById(group_id).get();
			Set<User> allMembers = temp.getMembers();
			allMembers.forEach(user  ->{
				emailIds.add(user.getEmailId());
			});
		}
		catch (Exception e){
			System.out.println(e);
		}
		return emailIds;
	}

	@Operation(summary = "adds to a filter for groups to make it so they only see the observations \"reposted\" by the group")
	@PutMapping(path = "user/userGroups/{group_id}/{observation_id}")
	public @ResponseBody String forFilter(@PathVariable Long group_id, @PathVariable Long observation_id){
		ObservationMarker tempObs = observationRepository.getById(observation_id);
		UserGroup tempGroup = group_repo.getById(group_id);
		tempGroup.addToObservations(tempObs);
		tempObs.addToPertainsGroup(tempGroup);
		group_repo.save(tempGroup);
		observationRepository.save(tempObs);
		return "added Observation " + tempObs.getId() + "to filter for group " + tempGroup.getTitle();
	}


	@Operation(summary = "gets number of group members to display under name on group page if there is one?")
	@GetMapping(path = "user/usergroup/{group_id}/num")
	public @ResponseBody int memberCount(@PathVariable long group_id){
        return group_repo.getById(group_id).getMembers().size();
	}


	// find by name?


}
