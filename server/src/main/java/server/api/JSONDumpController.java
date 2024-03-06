package server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.api.pojo.response_body.EventResponseBody;
import server.service.JSONDumpService;
import server.service.exceptions.ImproperDumpFormatException;

import java.util.List;

@RestController
@RequestMapping("api/v1/admin/jsondump")
public class JSONDumpController {
    private final JSONDumpService jsonDumpService;

    public JSONDumpController(@Autowired JSONDumpService jsonDumpService) {
        this.jsonDumpService = jsonDumpService;
    }

    /**
     * GET api/v1/admin/jsondump
     * Returns a JSON object corresponding to the current state of the server
     */
    @GetMapping("")
    public ResponseEntity<List<EventResponseBody>> getJSONDump(){
        return new ResponseEntity<>(jsonDumpService.createDump(), HttpStatus.OK);
    }

    /**
     * POST api/v1/admin/jsondump with a request body in List<EventDump> format
     * Restores the server to the state in accordance with the passed JSON object
     */
    @PostMapping("")
    public ResponseEntity<String> restoreFromJSONDump(
            @RequestBody List<EventResponseBody> body
    ){
        try {
            jsonDumpService.restoreFromDump(body);
            return new ResponseEntity<>("Restored Successfully", HttpStatus.OK);
        } catch (ImproperDumpFormatException e){
            return new ResponseEntity<>("Improper JSON dump format!", HttpStatus.NOT_MODIFIED);
        } catch (Exception e){
            return new ResponseEntity<>("Unknown Error!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
