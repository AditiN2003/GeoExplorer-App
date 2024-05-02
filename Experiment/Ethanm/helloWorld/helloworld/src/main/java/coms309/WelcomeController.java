package coms309;

import org.springframework.web.bind.annotation.*;

import java.util.Random;
import java.util.ArrayList;

@RestController
class WelcomeController {

    int numStored;
    ArrayList<String> storedHazards= new ArrayList<String>();
    ArrayList<String> storedLocations= new ArrayList<String>();
    int iterator;



    @GetMapping("/")
    public String welcome() {
        return "Hello and welcome to COMS 309. This Page will store names for you. Key: {} = variable\n\r" +
                "To add to the hazard you encountered simply add it to the url using /{hazard}\n\r" +
                "To view your hazards add /hazards/inspect to the base url\n\r" +
                "To add which city you encountered the hazard in add /locate/{cityName} to the url\n\r" +
                "To Clear all your hazards add /clear to the base url\n\r" +
                "To view instructions again go back to the base url";
    }

    //This is a put class that updates the arraylist in order to store more data.
    @PutMapping("/{hazard}")
    public String welcome(@PathVariable String hazard) {
        storedHazards.add(hazard);
        return "Hazard \"" + hazard + "\" added to the list";
    }
    @GetMapping("/hazards/inspect")
    public String ArrayListView(){
        for (int i = 0; i < storedHazards.size();i++){
            System.out.println("Hazard: \"" + storedHazards.get(i) + "\" at location \"" + storedLocations.get(i) + "\".");
        }
        return "full list completed";
    }

    @GetMapping("/locate/{cityName}")
    public String AddLocation(@PathVariable String cityName){
        storedLocations.add(cityName);
        return "Added " + storedHazards.get(iterator) + "To location " + cityName;
    }

    @DeleteMapping("/clear")
    public String ArrayListClear(){
        storedHazards.clear();

        return "List has been cleared";
    }

}
