package com.taskmanager.controller;

import com.taskmanager.dto.RegisterDto;
import com.taskmanager.exception.DuplicateResourceException;
import com.taskmanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null) model.addAttribute("errorMsg", "Username sau parola incorecta.");
        if (logout != null) model.addAttribute("logoutMsg", "V-ati deconectat cu succes.");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerDto") RegisterDto dto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (!dto.passwordsMatch()) {
            result.rejectValue("confirmPassword", "error.registerDto", "Parolele nu se potrivesc.");
        }

        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.register(dto);
            redirectAttributes.addFlashAttribute("successMsg", "Cont creat cu succes! Va puteti autentifica.");
            return "redirect:/auth/login";
        } catch (DuplicateResourceException e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "auth/register";
        }
    }
}
