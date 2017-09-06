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
package cn.edu.zjnu.acm.judge.service;

import cn.edu.zjnu.acm.judge.domain.Submission;
import cn.edu.zjnu.acm.judge.exception.MessageException;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 *
 * @author zhanhb
 */
@Service
public class ContestOnlyService {

    private static final String KEY = "value";
    private Cache cache;

    @Autowired
    public void setCacheManager(CacheManager cacheManager) {
        cache = cacheManager.getCache("contest-only");
    }

    public Long getContestOnly() {
        return cache.get(KEY, Long.class);
    }

    public void setContestOnly(Long contestOnly) {
        if (contestOnly == null) {
            cache.evict(KEY);
        } else {
            cache.put(KEY, contestOnly);
        }
    }

    public void checkSubmit(HttpServletRequest request, Long contest, long problemId) {
        Long contestOnly = getContestOnly();
        if (contestOnly == null) {
            return;
        }
        if (UserDetailService.isAdminLoginned(request)) {
            return;
        }
        if (!Objects.equals(contest, contestOnly)) {
            throw new MessageException("onlinejudge.contest.only.submit", HttpStatus.BAD_REQUEST);
        }
    }

    public void checkRegister() {
        Long contestOnly = getContestOnly();
        if (contestOnly == null) {
            return;
        }
        throw new MessageException("onlinejudge.contest.only.register", HttpStatus.BAD_REQUEST);
    }

    public void checkViewSource(HttpServletRequest request, @NonNull Submission submission) {
        if (!canViewSource(request, submission)) {
            throw new MessageException("onlinejudge.contest.only.view.souree", HttpStatus.BAD_REQUEST);
        }
    }

    public boolean canViewSource(HttpServletRequest request, Submission submission) {
        Long contestOnly = getContestOnly();
        if (contestOnly == null) {
            return true;
        }
        if (UserDetailService.isAdminLoginned(request)) {
            return true;
        }
        Long contestId = submission.getContest();
        if (contestId == null) {
            return false;
        }
        return Objects.equals(contestId, contestOnly);
    }

}