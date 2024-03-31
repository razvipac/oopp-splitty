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

    /**
     * Constructs a JSONDumpController with the specified JSONDumpService.
     *
     * @param jsonDumpService The JSONDumpService instance to be injected.
     */
    public JSONDumpController(@Autowired JSONDumpService jsonDumpService) {
        this.jsonDumpService = jsonDumpService;
    }

    /**
     * GET api/v1/admin/jsondump
     * Returns a JSON object corresponding to the current state of the server
     *
     * @return A ResponseEntity containing a list of EventResponseBody objects if successful.
     *         Returns HttpStatus.OK if successful.
     */
    @GetMapping("")
    public ResponseEntity<List<EventResponseBody>> getJSONDump(){
        return new ResponseEntity<>(jsonDumpService.createDump(), HttpStatus.OK);
    }

    /**
     * POST api/v1/admin/jsondump with a request body in List<EventDump> format
     * Restores the server to the state in accordance with the passed JSON object
     *
     * @param body The request body in List<EventResponseBody> format.
     * @return A ResponseEntity with a status message.
     *         Returns "Restored Successfully" if the restoration is successful (HttpStatus.OK).
     *         Returns "Improper JSON dump format!" if the JSON dump format
     *         is improper (HttpStatus.NOT_MODIFIED).
     *         Returns "Unknown Error!" if an unknown error occurs
     *         (HttpStatus.INTERNAL_SERVER_ERROR).
     */
//    @PostMapping("")
//    public ResponseEntity<String> restoreFromJSONDump(
//            @RequestBody List<EventResponseBody> body
//    ){
//        try {
//            jsonDumpService.restoreFromDump(body);
//            return new ResponseEntity<>("Restored Successfully", HttpStatus.OK);
//        } catch (ImproperDumpFormatException e){
//            return new ResponseEntity<>("Improper JSON dump format!", HttpStatus.NOT_MODIFIED);
//        } catch (Exception e){
//            return new ResponseEntity<>("Unknown Error!", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
    @PostMapping("")
    public ResponseEntity<String> restoreFromJSONDump(
            @RequestBody EventResponseBody body
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
