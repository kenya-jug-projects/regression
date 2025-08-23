package com.kenyajug.regression.controllers;
/*
 * MIT License
 *
 * Copyright (c) 2025 Kenya JUG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
import com.kenyajug.regression.entities.Application;
import com.kenyajug.regression.repository.ApplicationsRepository;
import com.kenyajug.regression.repository.UserRepository;
import com.kenyajug.regression.resources.ApplicationResource;
import com.kenyajug.regression.security.SecurityHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
@Controller
public class ApplicationController {
    private final ApplicationsRepository applicationsRepository;
    private final UserRepository userRepository;
    private final SecurityHelper securityHelper;
    public ApplicationController(ApplicationsRepository applicationsRepository, UserRepository userRepository, SecurityHelper securityHelper) {
        this.applicationsRepository = applicationsRepository;
        this.userRepository = userRepository;
        this.securityHelper = securityHelper;
    }
    @GetMapping("/add/application")
    public String loadApplicationForm(Model model){
        var emptyApplication = new ApplicationResource(UUID.randomUUID().toString(),"","");
        model.addAttribute("application",emptyApplication);
        return "application-form";
    }
    @PostMapping("/add/application")
    public String saveNewApplication(@Valid @ModelAttribute("application") ApplicationResource applicationResource,
                                     BindingResult bindingResult){
        if (bindingResult.hasErrors())
            return "application-form";
        var principal = securityHelper.findAuthenticatedUser();
        var user = userRepository.findByUsername(principal.getUsername()).orElseThrow(() -> new SecurityException("Invalid session, current user is not authenticated"));
        var entity = new Application(
                applicationResource.uuid(),
                applicationResource.name(),
                "1.0",
                applicationResource.runtimeEnvironment(),
                user.uuid(),
                LocalDateTime.now());
        applicationsRepository.save(entity);
        return "redirect:/applications";
    }
    @GetMapping("/applications")
    public String listApplications(Model model){
        var principal = securityHelper.findAuthenticatedUser();
        var user = userRepository.findByUsername(principal.getUsername()).orElseThrow(() -> new SecurityException("Invalid session, current user is not authenticated"));
        var apps = applicationsRepository.findByOwner(user);
        model.addAttribute("apps",apps);
        return "applications-list";
    }
}
