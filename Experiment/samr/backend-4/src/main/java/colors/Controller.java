package colors;

import java.util.*;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/generator")
public class Controller {

	HashMap<Integer, String> pages = new HashMap<>();


	protected static String formatHtmlPageColor(String color, String innertext) {
		return String.format("<!DOCTYPE html><html><body style=\"background-color:%s;\">%s</body></html>", color, innertext);
	}
	protected static String formatHtmlPageColor(String color) { return formatHtmlPageColor(color, ""); }
	protected static String formatHtmlPageColor(int r, int g, int b) {
		r %= 255;
		g %= 255;
		b %= 255;
		return formatHtmlPageColor(String.format("rgb(%d,%d,%d)", r, g, b));
	}

	
	@GetMapping(path = "/{color}")
	public @ResponseBody String createColorPage(@PathVariable String color) {
		return formatHtmlPageColor(color);
	}
	@GetMapping(path = "/random")
	public @ResponseBody String getRandomColorPage() {
		final Random r = new Random();
		return formatHtmlPageColor(
			r.nextInt(255),
			r.nextInt(255),
			r.nextInt(255)
		);
	}
	@GetMapping(path = "/saved/{id}")
	public @ResponseBody String getStoredPage(@PathVariable Integer id) {
		if(this.pages.containsKey(id)) {
			return formatHtmlPageColor(this.pages.get(id));
		}
		return "page id is not valid :(";
	}

	@GetMapping(path = "/{color}/save")
	public @ResponseBody String saveColoredPage(@PathVariable String color) {
		return this.saveColoredPage(color, color.hashCode());
	}
	@PostMapping(path = "/{color}")
	public @ResponseBody String saveColoredPage(@PathVariable String color, @RequestBody Integer id) {
		if(id == null || this.pages.containsKey(id)) {
			return "failed to save page -- invalid id :(";
		}
		this.pages.putIfAbsent(id, color);
		return formatHtmlPageColor(color, String.format("saved page color to id %d! :)", id));
	}
	@PutMapping(path = "/{color}")
	public @ResponseBody String updateIdMapping(@PathVariable String color, @RequestBody Integer id) {
		if(id == null) {
			return "failed to save page -- id could not be parsed :(";
		}
		this.pages.put(id, color);
		return formatHtmlPageColor(color, String.format("updated page color for id %d! :)", id));
	}
	@DeleteMapping(path = "/saved/{id}")
	public @ResponseBody String deleteStoredPage(@PathVariable Integer id) {
		if(id != null && this.pages.containsKey(id)) {
			String color = this.pages.remove(id);
			return formatHtmlPageColor(color, String.format("successfully deleted page for id %d.", id));
		}
		return "falied to delete page -- id not valid.";
	}


}
