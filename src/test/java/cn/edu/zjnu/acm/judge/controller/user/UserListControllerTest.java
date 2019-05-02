package cn.edu.zjnu.acm.judge.controller.user;

import cn.edu.zjnu.acm.judge.Application;
import cn.edu.zjnu.acm.judge.service.MockDataService;
import cn.edu.zjnu.acm.judge.util.JudgeUtils;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest(classes = Application.class)
@Transactional
@WebAppConfiguration
public class UserListControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private MockDataService mockDataService;

    /**
     * Test of userList method, of class UserListController.
     *
     * @see UserListController#userList(HttpServletRequest, Pageable)
     * @see JudgeUtils#sequence(long, long)
     */
    @Test
    public void testUserList() throws Exception {
        log.info("userList");
        MvcResult result = mvc.perform(get("/userlist"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn();
        for (int i = 0; i < 105; i++) {
            mockDataService.user();
        }
        result = mvc.perform(get("/userlist"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn();
    }

}
