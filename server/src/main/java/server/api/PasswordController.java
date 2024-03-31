package server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.service.PasswordService;

@RestController
@RequestMapping("api/v1/admin/auth")
public class PasswordController {

    private final PasswordService passwordService;

    /**
     * Constructs PasswordController with specified PasswordService
     * @param passwordService The PasswordService to be injected
     */
    public PasswordController(@Autowired PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    /**
     * GET api/v1/admin/auth/matches-password
     * Returns whether the given String matches the server's password
     *
     * @param input The entered password
     * @return True iff input matches password, false otherwise.
     *         Returns HttpStatus.OK if successful.
     */
    @GetMapping("/matches-password")
    public ResponseEntity<boolean> matchesPassword(String input) {
        return new ResponseEntity<boolean>(passwordService.doesPasswordMatch(input), HttpStatus.OK);
    }

}
