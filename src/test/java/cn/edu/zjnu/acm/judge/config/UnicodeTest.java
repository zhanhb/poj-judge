/*
 * Copyright 2017 ZJNU ACM.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.edu.zjnu.acm.judge.config;

import cn.edu.zjnu.acm.judge.Application;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author zhanhb
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class UnicodeTest {

    @Autowired
    private DataSource dataSource;

    @Test
    public void testUnicode() throws SQLException {
        int x = 0x1f602;
        char[] toChars = Character.toChars(x);
        String laughCry = new String(toChars);

        try (Connection connection = dataSource.getConnection()) {
            connection.prepareStatement("CREATE TABLE `test_table1`(`id` INT NOT NULL, `value` LONGTEXT NULL, PRIMARY KEY (`id`) ) COLLATE='utf8mb4_general_ci'").execute();
            try {
                try (PreparedStatement ps = connection.prepareStatement("insert into test_table1(id,value)values(1,?)")) {
                    ps.setBytes(1, laughCry.getBytes(StandardCharsets.UTF_8));
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = connection.prepareStatement("insert into test_table1(id,value)values(2,?)")) {
                    ps.setString(1, laughCry);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = connection.prepareStatement("select value from test_table1 where id in(1,2)");
                        ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        assertEquals(laughCry, rs.getString(1));
                    }
                }
            } finally {
                connection.prepareStatement("DROP TABLE `test_table1`").execute();
            }
        }
    }

}