package cn.edu.zjnu.acm.judge.controller.submission;

import cn.edu.zjnu.acm.judge.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

/**
 * @author zhanhb
 */
@Controller
@RequestMapping(produces = TEXT_HTML_VALUE)
@RequiredArgsConstructor
public class ShowCompileInfoController {

    private final SubmissionService submissionService;

    @GetMapping("showcompileinfo")
    @Secured("ROLE_USER")
    public String showCompileInfo(@RequestParam("solution_id") long submissionId,
            Model model, Authentication authentication) {
        String compileInfo = submissionService.findCompileInfo(submissionId);
        model.addAttribute("compileInfo", compileInfo);
        return "submissions/compile_info";
    }

}
