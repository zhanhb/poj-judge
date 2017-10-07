/*
 * Copyright 2016 ZJNU ACM.
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
package cn.edu.zjnu.acm.judge.service;

import cn.edu.zjnu.acm.judge.data.form.BestSubmissionForm;
import cn.edu.zjnu.acm.judge.domain.Contest;
import cn.edu.zjnu.acm.judge.domain.Submission;
import cn.edu.zjnu.acm.judge.mapper.ContestMapper;
import cn.edu.zjnu.acm.judge.mapper.SubmissionMapper;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 *
 * @author zhanhb
 */
@Service
public class SubmissionService {

    @Autowired
    private ContestMapper contestMapper;
    @Autowired
    private SubmissionMapper submissionMapper;

    public boolean canView(HttpServletRequest request, Submission submission) {
        if (UserDetailsServiceImpl.isAdminLoginned(request)) {
            return true;
        }
        // TODO cast to Authentication
        if (UserDetailsServiceImpl.isUser((Authentication) request.getUserPrincipal(), submission.getUser())) {
            return true;
        }
        boolean sourceBrowser = UserDetailsServiceImpl.isSourceBrowser(request);
        if (sourceBrowser) {
            Long contestId = submission.getContest();
            if (contestId == null) {
                return true;
            }
            Contest contest = contestMapper.findOne(contestId);
            return contest == null || contest.isEnded();
        }
        return false;
    }

    public Page<Submission> bestSubmission(Long contestId, long problemId, Pageable pageable, long total) {
        BestSubmissionForm form = BestSubmissionForm.builder().contestId(contestId).problemId(problemId).build();
        List<Submission> bestSubmissions = submissionMapper.bestSubmission(form, pageable);
        return new PageImpl<>(bestSubmissions, pageable, total);
    }

}
