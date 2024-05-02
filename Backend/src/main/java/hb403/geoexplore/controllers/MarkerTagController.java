package hb403.geoexplore.controllers;

import hb403.geoexplore.datatype.MarkerTag;
import hb403.geoexplore.datatype.marker.*;
import hb403.geoexplore.datatype.marker.repository.MarkerTagRepository;

import java.util.*;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class MarkerTagController {
	
	@Autowired
	MarkerTagRepository tag_repo;


	// TODO: enforce unique marker names
	@Operation(summary = "Add a new tag to the database")
	@PostMapping(path = "marker_tags")
	public @ResponseBody MarkerTag addTag(@RequestBody MarkerTag tag) {
		if(tag != null) {
			try {
				tag.setId(-1L);
				return this.tag_repo.save(tag);
			} catch(Exception e) {	// catch for when unique name creation fails

			}
		}
		return null;
	}
	// TODO: enforce unique marker names
	@Operation(summary = "Create a new tag in the database given its name")
	@PostMapping(path = "marker_tags/create")
	public @ResponseBody MarkerTag createTag(@RequestBody String tname) {
		if(tname != null && !tname.isEmpty()) {
			try {
				final MarkerTag t = new MarkerTag();
				t.setName(tname);
				t.setId(-1L);
				return this.tag_repo.save(t);
			} catch(Exception e) {	// see previous method

			}
		}
		return null;
	}

	@Operation(summary = "Get a tag in the database from its id")
	@GetMapping(path = "marker_tags/{id}")
	public @ResponseBody MarkerTag getTagById(@PathVariable Long id) {
		if(id != null) {
			try {
				return this.tag_repo.findById(id).get();
			} catch(Exception e) {

			}
		}
		return null;
	}
	@Operation(summary = "Search for a tag in the database by its name")
	@GetMapping(path = "marker_tags/search/{name}")
	public @ResponseBody MarkerTag searchTagByName(@PathVariable String name) {
		if(name != null) {
			try {
				return this.tag_repo.findByName(name).get();
			} catch(Exception e) {

			}
		}
		return null;
	}

	@Operation(summary = "Update a tag in the database by its id")
	@PutMapping(path = "marker_tags/{id}")
	public @ResponseBody MarkerTag updateTagById(@PathVariable Long id, @RequestBody MarkerTag tag) {
		if(id != null && tag != null) {
			try {
				final MarkerTag l = this.tag_repo.findById(id).get();
				tag.setId(id);
				tag.setTagged_alerts(l.getTagged_alerts());
				tag.setTagged_events(l.getTagged_events());
				tag.setTagged_observations(l.getTagged_observations());
				tag.setTagged_reports(l.getTagged_reports());
				return this.tag_repo.save(tag);
			} catch(Exception e) {

			}
		}
		return null;
	}

	// TODO: ensure all marker tag lists get updated as well
	@Operation(summary = "Delete a tag in the database by its id")
	@DeleteMapping(path = "marker_tags/{id}")
	public @ResponseBody MarkerTag deleteTagById(@PathVariable Long id) {
		if(id != null) {
			try {
				final MarkerTag t = this.getTagById(id);
				this.tag_repo.deleteById(id);
				return t;
			} catch(Exception e) {

			}
		}
		return null;
	}

	@Operation(summary = "Get a list of all tags in the database")
	@GetMapping(path = "marker_tags")
	public @ResponseBody List<MarkerTag> getAllTags() {
		return this.tag_repo.findAll();
	}
	@Operation(summary = "Get a list of all alerts which contain the tag at the given id")
	@GetMapping(path = "marker_tags/{id}/alerts")
	public @ResponseBody Set<AlertMarker> getTagAlerts(@PathVariable Long id) {
		final MarkerTag t = this.getTagById(id);
		if(t != null) {
			return t.getTagged_alerts();
		}
		return null;
	}
	@Operation(summary = "Get a list of all events which contain the tag at the given id")
	@GetMapping(path = "marker_tags/{id}/events")
	public @ResponseBody Set<EventMarker> getTagEvents(@PathVariable Long id) {
		final MarkerTag t = this.getTagById(id);
		if(t != null) {
			return t.getTagged_events();
		}
		return null;
	}
	@Operation(summary = "Get a list of all observations which contain the tag at the given id")
	@GetMapping(path = "marker_tags/{id}/observations")
	public @ResponseBody Set<ObservationMarker> getTagObservations(@PathVariable Long id) {
		final MarkerTag t = this.getTagById(id);
		if(t != null) {
			return t.getTagged_observations();
		}
		return null;
	}
	@Operation(summary = "Get a list of all reports which contain thte tag at the given id")
	@GetMapping(path = "marker_tags/{id}/reports")
	public @ResponseBody Set<ReportMarker> getTagReports(@PathVariable Long id) {
		final MarkerTag t = this.getTagById(id);
		if(t != null) {
			return t.getTagged_reports();
		}
		return null;
	}


}
