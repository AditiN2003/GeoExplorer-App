package hb403.geoexplore.UserStorage.controller;

import hb403.geoexplore.UserStorage.entity.User;
import hb403.geoexplore.UserStorage.entity.UserGroup;
import hb403.geoexplore.UserStorage.repository.UserRepository;

import java.util.*;

import hb403.geoexplore.comments.Entity.CommentEntity;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;


    //C of Crudl
    @Operation(summary = "Add a new user to the database")
    @PostMapping(path = "/user/create")
    public @ResponseBody User UserCreate(@RequestBody User newUser) {
        if(newUser != null) {
            // User newestUser = new User(newUser.getName(), newUser.getEmailId(), newUser.getPassword());
            newUser.enforceLocationIO();
            newUser.checkIfAdmin();
            newUser.encryptPassword();
            newUser = userRepository.save(newUser);
            newUser.enforceLocationTable();
            return newUser;
        }
        return null;
    }


    //R of Crudl
    @Operation(summary = "Get a user from the database from its id")
    @GetMapping(path = "/user/{id}")
    public @ResponseBody User getUser(@PathVariable Long id){
        if(id != null) {
            try {
                final User u = userRepository.findById(id).get();
                u.enforceLocationTable();
                return u;
            } catch(Exception e) {

            }
        }
        return null;
    }

    //U of Crudl
    @Operation(summary = "Update a user already in the database by its id")
    @PutMapping(path = "/user/{id}/update")
    public @ResponseBody User updateUser(@PathVariable Long id, @RequestBody User updated) {
        if(id != null && updated != null) {
            User original = userRepository.findById(id).get();
            // User updater = new User(id, updated.getName(), updated.getEmailId(), updated.getPassword());
            updated.setComments(original.getComments());
            updated.enforceLocationIO();
            updated.encryptPassword();
            updated.checkIfAdmin();
            updated.setId(id);
            updated = userRepository.save(updated);
            updated.enforceLocationTable();
            System.out.println("user update ids match before/after?: " + (updated.getId() == id));
            return updated;
        }
        return null;
    }

    // D of Crudl
    @Operation(summary = "Delete a user from the database by its id")
    @DeleteMapping(path = "/user/{id}/delete")
    public @ResponseBody User deleteUser(@PathVariable Long id){
        if(id != null) {
            try {
                User deleted = userRepository.findById(id).get();
                userRepository.deleteById(id);
                deleted.enforceLocationTable();
                return deleted;
            } catch(Exception e) {
                throw e;
            }
        }
        return null;
    }



    //L of Crudl
    @Operation(summary = "Get a list of all the users in the database")
    @GetMapping(path = "/userinfo")
    @ResponseBody List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        for(User u : users) {
            u.enforceLocationTable();
        }
        return users;
    }

    // Get a list of groups that a user is in
    @Operation(summary = "Get the list of usergroups that a user is a member in")
    @GetMapping(path="/user/{id}/groups")
    @ResponseBody Set<UserGroup> getUserGroups(@PathVariable Long id) {
        try {
            return this.getUser(id).getGroups();
        } catch(Exception e) {
            // ...
        }
        return null;
    }


}
