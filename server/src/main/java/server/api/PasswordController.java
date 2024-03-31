package server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.service.PasswordService;

@RestController
@RequestMapping("api/v1/admin")
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
     * GET api/v1/admin
     * Returns a randomly generated password to be used by admin
     *
     * @return A ResponseEntity containing the randomly generated password if successful.
     *         Returns HttpStatus.OK if successful.
     */
    @GetMapping("")
    public ResponseEntity<char[]> getPassword(){
        char[] password = passwordService.generatePassword(20);
        // Clears the password from memory after it's been used:
        passwordService.clearPassword(password);
        return new ResponseEntity<>(password, HttpStatus.OK);
    }
}
