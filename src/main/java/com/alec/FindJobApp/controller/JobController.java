package com.alec.FindJobApp.controller;

import com.alec.FindJobApp.model.JobPost;
import com.alec.FindJobApp.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class JobController {

    @Autowired
    private JobService service;

    @GetMapping({"/", "home"})
    public String home() {
        return "home";
    }

    @GetMapping("addjob")
    public String addJob() {
        return "addjob";
    }

    @PostMapping("handleForm")
    public String handleForm(JobPost jobPost) {
        service.addJob(jobPost);
        return "success";
    }

    @GetMapping("viewalljobs")
    public String viewJobs(Model m) {
        List<JobPost> jobs = service.getAllJobs();
        m.addAttribute("jobPosts", jobs);
        return "viewalljobs";
    }

    // GET mapping to load the edit form with existing job data
    @GetMapping("editjob")
    public String editJob(@RequestParam int id, Model m) {
        JobPost job = service.getJob(id);
        m.addAttribute("jobPost", job);
        return "editjob";
    }

    // POST mapping to handle the update
    @PostMapping("updatejob")
    public String updateJob(JobPost jobPost) {
        service.updateJob(jobPost);
        return "redirect:/viewalljobs";
    }

    // GET mapping to delete a job
    @GetMapping("deletejob")
    public String deleteJob(@RequestParam int id) {
        service.deleteJob(id);
        return "redirect:/viewalljobs";
    }
}