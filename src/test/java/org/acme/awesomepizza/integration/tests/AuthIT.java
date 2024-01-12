package org.acme.awesomepizza.integration.tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class AuthIT {

    @Autowired private MockMvc mockMvc;

    @Test
    void should_successfully_login() throws Exception {
        this.mockMvc.perform(
                    multipart("/v1/auth/login")
                        .param("username", "admin")
                        .param("password", "admin")
                )
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(header().exists("awesome-pizza-jwt"))
                .andExpect(cookie().exists("awesome-pizza-jwt"));
    }

    @Test
    void should_return_401_for_bad_credentials() throws Exception {
        this.mockMvc.perform(
                    multipart("/v1/auth/login")
                            .param("username", "invalid")
                            .param("password", "invalid")
                ).andDo(print())
                .andExpect(status().is(401));
    }
}
