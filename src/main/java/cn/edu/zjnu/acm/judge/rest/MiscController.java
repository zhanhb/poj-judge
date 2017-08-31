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
package cn.edu.zjnu.acm.judge.rest;

import cn.edu.zjnu.acm.judge.config.JudgeConfiguration;
import cn.edu.zjnu.acm.judge.data.form.SystemInfoForm;
import cn.edu.zjnu.acm.judge.mapper.UserProblemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 *
 * @author zhanhb
 */
@RequestMapping(value = "/api/misc", produces = APPLICATION_JSON_VALUE)
@RestController
@Secured("ROLE_ADMIN")
public class MiscController {

    @Autowired
    private UserProblemMapper userProblemMapper;
    @Autowired
    private JudgeConfiguration judgeConfiguration;

    @PostMapping("fix")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void fix() {
        userProblemMapper.init();
        userProblemMapper.updateProblems();
        userProblemMapper.updateUsers();
    }

    @PutMapping("systemInfo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setSystemInfo(@RequestBody SystemInfoForm json) {
        String info = json.getInfo();
        boolean pureText = json.isPureText();
        SystemInfoForm systemInfo = new SystemInfoForm();
        if (StringUtils.isEmptyOrWhitespace(info)) {
            systemInfo.setPureText(true);
        } else {
            systemInfo.setPureText(pureText);
            systemInfo.setInfo(info.trim());
        }
        judgeConfiguration.setSystemInfo(systemInfo);
    }

    @GetMapping("systemInfo")
    public SystemInfoForm systemInfo() {
        SystemInfoForm systemInfo = judgeConfiguration.getSystemInfo();
        if (systemInfo == null) {
            systemInfo = new SystemInfoForm();
            systemInfo.setPureText(true);
            return systemInfo;
        }
        if (StringUtils.isEmptyOrWhitespace(systemInfo.getInfo())) {
            systemInfo.setPureText(true);
        }
        return systemInfo;
    }

}