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
package cn.edu.zjnu.acm.judge.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

/**
 *
 * @author zhanhb
 */
@Controller
@RequestMapping(value = "/admin", produces = TEXT_HTML_VALUE)
@Secured("ROLE_ADMIN")
public class ManagerController {

    @GetMapping({
        "/",
        "/problems",
        "/problems/add",
        "/problems/{id}",
        "/problems/{id}/view/{lang}",
        "/problems/{id}/edit",
        "/problems/{id}/edit/{lang}",
        "/contests",
        "/contests/add",
        "/contests/{id}",
        "/contests/{id}/edit",
        "/accounts",
        "/accounts/import",
        "/system/index"
    })
    public String manager() {
        return "manager";
    }

    @GetMapping
    public String index(HttpServletRequest request) {
        String query = request.getQueryString();
        if (StringUtils.isEmpty(query)) {
            return "redirect:/admin/";
        } else {
            return "redirect:/admin/?" + query;
        }
    }

}