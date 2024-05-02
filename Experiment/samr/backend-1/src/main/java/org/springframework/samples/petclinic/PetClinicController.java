package org.springframework.samples.petclinic;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.pet.Pet;
import org.springframework.samples.petclinic.pet.PetRepository;


@RestController
public class PetClinicController {
	
	@Autowired
	protected OwnerRepository owners_repo;
	@Autowired
	protected PetRepository pets_repo;

	protected HashMap<Integer, HashSet<Pet>> owners_to_pets = new HashMap<>();


	protected void addOwner(Owner o) {
		if(o != null) {
			this.owners_to_pets.put(
				this.owners_repo.save(o).id,
				new HashSet<>()
			);
		}
	}

/** Owners */

	/** Create a new owner */
	@PostMapping(path = "/owners/create")
	public @ResponseBody String saveOwner(Owner owner) {
		if(owner == null) return "Failed to add owner -- invalid object!";
		Owner n = owners_repo.save(owner);
		owners_to_pets.put(n.id, new HashSet<>());
		return String.format("Successfully saved owner %s to id %d!", n.firstName, n.id);
	}
	/** Saves pre-generated dummy owners to the repo */
	@GetMapping(path = "/owners/generate")
	public @ResponseBody String createDummyData() {
		this.addOwner( new Owner(0, "John", "Doe", "404 Not found", "some numbers") );
		this.addOwner( new Owner(0, "Jane", "Doe", "Its a secret", "you wish") );
		this.addOwner( new Owner(0, "Some", "Pleb", "Right next to the Library", "515-345-41213") );
		this.addOwner( new Owner(0, "Chad", "Champion", "Reddit memes corner", "420-420-4200") );
		return "Successfully saved dummy data!";
	}

	/** List all the owners */
	@GetMapping(path = "/owners")
	public @ResponseBody List<Owner> getAllOwners() {
		return owners_repo.findAll();
	}
	/** Get an owner's data (by owner id) */
	@GetMapping(path = "/owners/{ownerId}")
	public @ResponseBody Optional<Owner> findOwnerById(@PathVariable("ownerId") Integer id) {
		if(id == null) return Optional.empty();
		return owners_repo.findById(id);
	}
	/** Get a list of all the pets that an owner owns */
	@GetMapping(path = "/owners/{ownerId}/pets")
	public @ResponseBody List<Pet> findPetsByOwnerId(@PathVariable("ownerId") Integer id) {
		if(id != null) {
			HashSet<Pet> pets = owners_to_pets.get(id);
			if(pets != null) {
				return new ArrayList<>(pets);
			}
		}
		return null;
	}



/** Pets */

	/** Create a pet and attach to owner if owner id is valid */
	@PostMapping(path = "/pets/create")
	public @ResponseBody String savePet(@RequestBody Pet pet) {
		if(pet == null) return "Failed to save pet -- invalid object!";
		Pet p = pets_repo.save(pet);
		if(p.owner_id != null) {
			Optional<Owner> owner = owners_repo.findById(p.owner_id);
			if(owner.isPresent() && this.owners_to_pets.get(owner.get().id).add(p)) {
				return String.format("Successfully saved pet %s with id %d [owner: %s]", p.name, p.id, owner.get().firstName);
			}
		}
		return String.format("Successfully saved pet %s with id %d [no owner assigned].", p.name, p.id);
	}

	/** Get a list of all pets */
	@GetMapping(path = "/pets")
	public @ResponseBody List<Pet> allPets() {
		return pets_repo.findAll();
	}
	/** Get a pet by it's id */
	@GetMapping( path = "pets/{petId}")
	public @ResponseBody Optional<Pet> findPetById(@PathVariable("petId") Integer id) {
		if(id == null) return Optional.empty();
		return pets_repo.findById(id);
	}
	/** Get a pet's owner */
	@GetMapping(path = "pets/{petId}/owner")
	public @ResponseBody Optional<Owner> findOwnerByPetId(@PathVariable("petId") Integer id) {
		if(id != null) {
			Optional<Pet> pet = pets_repo.findById(id);
			if(pet.isPresent()) {
				return this.findOwnerById(pet.get().owner_id);
			}
		}
		return Optional.empty();
	}


}
