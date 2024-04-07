// CHECKSTYLE:OFF
package server.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.MockMvc;
import server.service.PasswordService;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
public class PasswordControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private PasswordController passwordController;

    @BeforeEach
    public void setup() {
        mockMvc = standaloneSetup(passwordController).build();
    }

    @Test
    public void passwordTest() throws Exception{
        String input = "input";
        when(passwordService.doesPasswordMatch(input)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/auth/matches-password")
                        .param("input", input)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }
}
