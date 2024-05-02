package coms309.people;

import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.HashMap;




/**
 * Controller used to showcase Create and Read from a LIST
 *
 * @author Vivek Bengre
 */

@RestController
public class variablesqureSize {

    ArrayList<square> squares = new ArrayList<square>();

    // Note that there is only ONE instance of PeopleController in 
    // Springboot system.


    //CRUDL (create/read/update/delete/list)
    // use POST, GET, PUT, DELETE, GET methods for CRUDL

    // THIS IS THE LIST OPERATION
    // gets all the people in the list and returns it in JSON format
    // This controller takes no input. 
    // Springboot automatically converts the list to JSON format 
    // in this case because of @ResponseBody
    // Note: To LIST, we use the GET method
    @PostMapping("/square/{length}/{width}")
    public String Square(@PathVariable Integer length ,@PathVariable Integer width) {
        int tempLength = length.intValue(), tempWidth = width.intValue();
        square temp = new square(tempLength, tempWidth);
        ArrayList<Character> printed= temp.printsquare();
        return printed + "\nSquare has length \"" + length + "\" and width \"" + width + "\"";
    }



}

