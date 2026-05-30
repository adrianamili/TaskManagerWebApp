package com.taskmanager.controller;

import com.taskmanager.service.TagService;
import com.taskmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final TagService tagService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("users", userService.findAll(PageRequest.of(0, 10)));
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "createdAt") String sortBy,
                        @RequestParam(defaultValue = "desc") String direction,
                        Model model) {
        Sort sort = direction.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        model.addAttribute("users", userService.findAll(pageable));
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        return "admin/users";
    }

    @PostMapping("/users/{id}/promote")
    public String promote(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.promoteToAdmin(id);
        redirectAttributes.addFlashAttribute("successMsg", "User promovat la ADMIN.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/deactivate")
    public String deactivate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deactivate(id);
        redirectAttributes.addFlashAttribute("successMsg", "User dezactivat.");
        return "redirect:/admin/users";
    }

    @GetMapping("/tags")
    public String tags(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        model.addAttribute("tags", tagService.findAll(pageable));
        model.addAttribute("currentPage", page);
        return "admin/tags";
    }

    @PostMapping("/tags/new")
    public String createTag(@RequestParam String name,
                            @RequestParam(required = false) String color,
                            @RequestParam(required = false) String description,
                            RedirectAttributes redirectAttributes) {
        tagService.create(name, color, description);
        redirectAttributes.addFlashAttribute("successMsg", "Tag creat cu succes!");
        return "redirect:/admin/tags";
    }

    @PostMapping("/tags/{id}/delete")
    public String deleteTag(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        tagService.delete(id);
        redirectAttributes.addFlashAttribute("successMsg", "Tag sters.");
        return "redirect:/admin/tags";
    }
}
