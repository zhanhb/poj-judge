package cn.edu.zjnu.acm.judge.controller.api;

import cn.edu.zjnu.acm.judge.Application;
import cn.edu.zjnu.acm.judge.domain.Language;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Transactional
@WebAppConfiguration
public class LanguageControllerTest {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        mvc = webAppContextSetup(context).build();
    }

    /**
     * Test of findAll method, of class LanguageController.
     * {@link LanguageController#findAll()}
     */
    @Test
    public void testFindAll() throws Exception {
        System.out.println("findAll");
        MvcResult result = mvc.perform(get("/api/languages"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    /**
     * Test of save method, of class LanguageController.
     * {@link LanguageController#save(Language)}
     */
    @Test
    public void testSave() throws Exception {
        System.out.println("save");
        Language request = null;
        MvcResult result = mvc.perform(post("/api/languages")
                .content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    /**
     * Test of findOne method, of class LanguageController.
     * {@link LanguageController#findOne(long)}
     */
    @Test
    public void testFindOne() throws Exception {
        System.out.println("findOne");
        long id = 0;
        MvcResult result = mvc.perform(get("/api/languages/{id}", id))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    /**
     * Test of update method, of class LanguageController.
     * {@link LanguageController#update(long, Language)}
     */
    @Test
    public void testUpdate() throws Exception {
        System.out.println("update");
        long id = 0;
        Language request = null;
        MvcResult result = mvc.perform(put("/api/languages/{id}", id)
                .content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    /**
     * Test of delete method, of class LanguageController.
     * {@link LanguageController#delete(long)}
     */
    @Test
    public void testDelete() throws Exception {
        System.out.println("delete");
        long id = 0;
        MvcResult result = mvc.perform(delete("/api/languages/{id}", id))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

}
