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
 */
import com.kenyajug.regression.entities.LogsDataSource;
import com.kenyajug.regression.repository.ApplicationsRepository;
import com.kenyajug.regression.repository.LogsDataSourceRepository;
import com.kenyajug.regression.repository.UserRepository;
import com.kenyajug.regression.security.SecurityHelper;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import java.time.LocalDateTime;
import java.util.UUID;
@Controller
public class DatasourceController {
    private final ApplicationsRepository applicationsRepository;
    private final UserRepository userRepository;
    private final SecurityHelper securityHelper;
    private final LogsDataSourceRepository logsDataSourceRepository;
    public DatasourceController(ApplicationsRepository applicationsRepository, UserRepository userRepository, SecurityHelper securityHelper, LogsDataSourceRepository logsDataSourceRepository) {
        this.applicationsRepository = applicationsRepository;
        this.userRepository = userRepository;
        this.securityHelper = securityHelper;
        this.logsDataSourceRepository = logsDataSourceRepository;
    }
    @GetMapping("/add/data/source")
    public String datasourceForm(Model model) {
        var emptyDateSource = new LogsDataSource(UUID.randomUUID().toString(),"","","", LocalDateTime.now(),"");
        var principal = securityHelper.findAuthenticatedUser();
        var user = userRepository.findByUsername(principal.getUsername()).orElseThrow(() -> new SecurityException("Invalid session, current user is not authenticated"));
        var apps = applicationsRepository.findByOwner(user);
        model.addAttribute("datasource",emptyDateSource);
        model.addAttribute("applications",apps);
        return "data-source-form";
    }
    @PostMapping("/add/data/source")
    public String saveNewDataSource(@Valid @ModelAttribute("datasource") LogsDataSource logsDataSource,
                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "data-source-form";
        logsDataSourceRepository.save(logsDataSource);
        return "redirect:/";
    }
    @GetMapping("/data/sources")
    public String listDataSources(Model model){
        var datasourceList = logsDataSourceRepository.findAll();
        model.addAttribute("logsDatasourceList",datasourceList);
        return "datasource-list";
    }
}
