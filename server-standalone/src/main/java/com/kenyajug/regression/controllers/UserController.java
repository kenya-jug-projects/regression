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
import com.kenyajug.regression.entities.User;
import com.kenyajug.regression.repository.UserRepository;
import com.kenyajug.regression.resources.PasswordUpdateResource;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
@Controller
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @GetMapping("/user/form")
    public String launchUserUpdateForm(Model model){
        var admin = userRepository.findByUsername("admin@regression.com").orElseThrow(() -> new SecurityException("Security Error! Missing admin account"));
        var passwordUpdateResource = new PasswordUpdateResource(admin.password(),"","");
        model.addAttribute("password", passwordUpdateResource);
        return "user-form";
    }
    @PostMapping("/admin/update")
    public String updateAdminPassword(Model model, @ModelAttribute("password") @Valid PasswordUpdateResource passwordUpdateResource, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            model.addAttribute("password", passwordUpdateResource);
            return "user-form";
        }
        if (!passwordUpdateResource.confirmPassword().equals(passwordUpdateResource.newPassword())) {
            bindingResult.rejectValue("confirmPassword","Passwords do not match","The new passwords you entered don’t match");
            model.addAttribute("password",passwordUpdateResource);
            return "user-form";
        }
        var admin = userRepository.findByUsername("admin@regression.com").orElseThrow(() -> new SecurityException("Security Error! Missing admin account"));
        var user = new User(
                admin.uuid(),
                admin.username(),
                passwordEncoder.encode(passwordUpdateResource.newPassword()),
                admin.roles_list_json(),
                admin.created_at());
        userRepository.updateById(admin.uuid(), user);
        return "redirect:/";
    }
}
