package com.todolist.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todolist.model.User;
import com.todolist.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setup() {

    }

    @Test
    public void getAllUser() throws Exception {
        User user1 = new User("getUser1@mail.com", "getUser1", "getUser1", "getUser1", LocalDate.parse("2000-06-11"));
        userRepository.save(user1);
        User user2 = new User("getUser2@mail.com", "getUser2", "getUser2", "getUser2", LocalDate.parse("2000-06-11"));
        userRepository.save(user2);
        this.mockMvc.perform(get("/user"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn();
    }

    @Test
    public void getUserById() throws Exception {
        User user = new User("getById@mail.com", "getById", "getById", "getById", LocalDate.parse("2000-06-11"));
        user = userRepository.save(user);
        long userId = user.getId();
        this.mockMvc.perform(get("/user/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("getById@mail.com"))
                .andExpect(jsonPath("$.firstname").value("getById"))
                .andExpect(jsonPath("$.lastname").value("getById"))
                .andExpect(jsonPath("$.password").value("getById"))
                .andReturn();
    }

    @Test
    public void createNewValidUser() throws Exception {
        User newUser = new User("post@mail.com", "post", "post", "post1234", LocalDate.parse("2000-06-11"));
        this.mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("post@mail.com"))
                .andExpect(jsonPath("$.firstname").value("post"))
                .andExpect(jsonPath("$.lastname").value("post"))
                .andExpect(jsonPath("$.password").value("post1234"));
    }

    @Test
    public void updateUser() throws Exception {
        User user = new User("test@mail.com", "test", "test", "test1234", LocalDate.parse("2000-06-11"));
        user = userRepository.save(user);
        long userId = user.getId();
        User modifUser = new User("update@mail.com", "update", "update", "update123", LocalDate.parse("2000-06-11"));
        this.mockMvc.perform(put("/user/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifUser))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("update@mail.com"))
                .andExpect(jsonPath("$.firstname").value("update"))
                .andExpect(jsonPath("$.lastname").value("update"))
                .andExpect(jsonPath("$.password").value("update123"));
    }

    @Test
    public void deleteUser() throws Exception {
        User user = new User("delete@mail.com", "delete", "delete", "delete123", LocalDate.parse("2000-06-11"));
        user = userRepository.save(user);
        long userId = user.getId();
        this.mockMvc.perform(delete("/user/{id}", userId))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();
    }
}