package com.example.softwareinventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
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

import static com.example.softwareinventory.Message.getTextForMessageType;

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
    public String kapcsolat(Model model) {
        model.addAttribute("msg", new Message());
        return "kapcsolat";
    }

    @GetMapping("/uzenetek")
    public String uzenetek(Model model) {
        model.addAttribute("msg", new Message());
        return "uzenetek";
    }

    @PostMapping("/uzenetkuld")
    public String UzenetKuldes(@Valid @ModelAttribute Message message,
                        Model model,
                        @CurrentSecurityContext(expression = "authentication") Authentication auth,
                               @CurrentSecurityContext(expression = "authentication?.name") String loggInUserName) {
        try {
            SzoftverleltarDbManager manager = new SzoftverleltarDbManager();
            //Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth.isAuthenticated() && loggInUserName != "anonymousUser") {
                message.setUserId(manager.getUserIdFromUserName(loggInUserName));
                message.setUserName(loggInUserName);
            } else {
                message.setUserName("Vendég");
            }
            message.setMessageType(getTextForMessageType(message.getMessageType()));
            if (manager.insertMessage(message)) {
                return "uzenetjo";
            } else {
                return "uzenethiba";
            }

        } catch (Exception e) {
            model.addAttribute("uzenet", e.getMessage());
            return "uzenethiba";
        }
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
        try {
            for (User userCurrent : userRepo.findAll()) {
                if (userCurrent.getEmail().equals(user.getEmail())) {
                    model.addAttribute("uzenet", "Ez az email már foglalt!");
                    return "reghiba";
                }
            }
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            Role role = new Role();
            role.setId(2);
            role.setName("ROLE_USER");
            List<Role> roleList;
            roleList = new ArrayList<Role>();
            roleList.add(role);
            user.setRoles(roleList);
            userRepo.save(user);
            model.addAttribute("id", user.getId());
            return "regjo";
        } catch (Exception e) {
            model.addAttribute("uzenet", e.getMessage());
            return "reghiba";
        }

    }
}
