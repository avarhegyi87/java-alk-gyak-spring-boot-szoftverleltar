package com.example.softwareinventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/nyitolap")
    public String nyitolap() {
        return "index";
    }

    @GetMapping("/admin/home")
    public String admin() {
        return "admin";
    }

    @GetMapping("/telepitesek")
    public String telepitesek() {
        return "telepitesek";
    }

    @GetMapping("/kapcsolat")
    public String kapcsolat() {
        return "kapcsolat";
    }

    @GetMapping("/uzenetek")
    public String uzenetek() {
        return "uzenetek";
    }

    @GetMapping("/regisztral")
    public String greetingForm(Model model) {
        model.addAttribute("reg", new User());
        return "regisztral";
    }

    @Autowired
    private UserRepository userRepo;
    @PostMapping("/regisztral_feldolgoz")
    public String Regisztracio(@Valid @ModelAttribute User user,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("uzenet", "Nem megfelelő formátumú felhasználónév, email cím, vagy jelszó");
            return "reghiba";
        }
        for (User user2 :
                userRepo.findAll()) {
            if (user2.getEmail().equals(user.getEmail())) {
                model.addAttribute("uzenet", "Ez az email már foglalt!");
                return "reghiba";
            }
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = new Role();
        role.setId(2);
        role.setName("ROLE_USER");
        List<Role> roleList = new ArrayList<Role>();
        roleList.add(role);
        user.setSzerepkorok(roleList);
        userRepo.save(user);
        model.addAttribute("id", user.getId());
        return "regjo";
    }
}
