package cn.edu.zjnu.acm.judge.admin;

import cn.edu.zjnu.acm.judge.domain.Contest;
import cn.edu.zjnu.acm.judge.domain.Problem;
import cn.edu.zjnu.acm.judge.exception.MessageException;
import cn.edu.zjnu.acm.judge.mapper.ContestMapper;
import cn.edu.zjnu.acm.judge.mapper.ProblemMapper;
import cn.edu.zjnu.acm.judge.service.UserDetailService;
import cn.edu.zjnu.acm.judge.util.JudgeUtils;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ProbManagerPageController {

    @Autowired
    private ContestMapper contestMapper;
    @Autowired
    private ProblemMapper problemMapper;

    @RequestMapping(value = "/admin/problems/new", method = {RequestMethod.GET, RequestMethod.HEAD})
    public String newProblem(HttpServletRequest request) {
        UserDetailService.requireAdminLoginned(request);
        request.setAttribute("title", "New Problem");
        request.setAttribute("hint", "Add New problem");
        request.setAttribute("url", "/admin/problems");
        request.setAttribute("method", "POST");
        request.setAttribute("hint2", "Add a Problem");
        Problem problem = Problem
                .builder()
                .memoryLimit(65536)
                .timeLimit(3000)
                .build();
        return finalBlock(problem, request);
    }

    @RequestMapping(value = "/admin/problems/{problemId}/edit", method = {RequestMethod.GET, RequestMethod.HEAD})
    public String probmanagerpage(HttpServletRequest request, @PathVariable("problemId") long problemId) {
        UserDetailService.requireAdminLoginned(request);
        Problem problem = problemMapper.findOne(problemId);
        if (problem == null) {
            throw new MessageException("No such problem, ID:" + problemId);
        }
        problem = problem.toBuilder()
                .description(JudgeUtils.getHtmlFormattedString(problem.getDescription()))
                .input(JudgeUtils.getHtmlFormattedString(problem.getInput()))
                .output(JudgeUtils.getHtmlFormattedString(problem.getOutput()))
                .hint(JudgeUtils.getHtmlFormattedString(problem.getHint()))
                .source(JudgeUtils.getHtmlFormattedString(problem.getSource()))
                .build();

        request.setAttribute("title", "Modify " + problemId);
        request.setAttribute("hint", "Modify problem " + problemId);
        request.setAttribute("url", "/admin/problems/" + problemId);
        request.setAttribute("method", "PUT");
        request.setAttribute("hint2", "Modify problem");
        return finalBlock(problem, request);
    }

    private String finalBlock(Problem problem, HttpServletRequest request) {
        List<Contest> contests = problem.getContest() == null
                ? contestMapper.pending()
                : contestMapper.runningAndScheduling();
        request.setAttribute("problem", problem);
        request.setAttribute("contests", contests);
        return "admin/problems/edit";
    }

}
