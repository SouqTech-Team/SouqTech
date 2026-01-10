package org.stand.springbootecommerce.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.stand.springbootecommerce.dto.request.AuthenticationRequest;
import org.stand.springbootecommerce.dto.request.RegisterRequest;
import org.stand.springbootecommerce.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthenticationSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private ResultActions resultActions;

    @Given("Le système est prêt pour une nouvelle inscription")
    public void systemReady() {
        userRepository.deleteAll();
    }

    @Given("Un utilisateur existe avec l'email {string} et le mot de passe {string}")
    public void userExists(String email, String password) throws Exception {
        userRepository.deleteAll();
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name("Cucumber")
                .surname("Tester")
                .email(email)
                .password(password)
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());
    }

    @When("J me fais une inscription avec le nom {string}, le prénom {string} et l'email {string}")
    public void register(String name, String surname, String email) throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .name(name)
                .surname(surname)
                .email(email)
                .password("password123")
                .build();

        resultActions = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    @When("Je tente de me connecter avec l'email {string} et le mot de passe {string}")
    public void login(String email, String password) throws Exception {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email(email)
                .password(password)
                .build();

        resultActions = mockMvc.perform(post("/api/v1/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    @Then("Le code de réponse de l'inscription doit être {int}")
    public void verifyRegisterStatusCode(int statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @Then("Le code de réponse doit être {int}")
    public void verifyStatusCode(int statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @And("Une réponse contenant un token JWT doit être retournée")
    public void verifyJwtToken() throws Exception {
        resultActions.andExpect(jsonPath("$.token").exists());
    }
}
