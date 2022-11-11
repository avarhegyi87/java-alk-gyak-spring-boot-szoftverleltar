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

    @GetMapping("/home")
    public String nyitolap() {
        return "index";
    }

    @GetMapping("/admin/home")
    public String admin() {
        return "admin";
    }

    @GetMapping("/installs")
    public String telepitesek() {
        return "installs";
    }

    @GetMapping("/contact_us")
    public String kapcsolat(Model model) {
        model.addAttribute("msg", new Message());
        return "contact_us";
    }

    @GetMapping("/all_messages")
    public String uzenetek(Model model) {
        try {
            SzoftverleltarDbManager manager = new SzoftverleltarDbManager();
            model.addAttribute("message_list",manager.getAllMessages());
            return "all_messages";
        } catch (Exception e) {
            model.addAttribute("error_msg", "Hiba az adatbázis lehívásakor: " + e.getMessage());
            return "all_messages";
        }
    }

    @PostMapping("/send_msg")
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
                return "msg_success";
            } else {
                return "msg_error";
            }

        } catch (Exception e) {
            model.addAttribute("error_msg", e.getMessage());
            return "msg_error";
        }
    }

    @GetMapping("/register")
    public String greetingForm(Model model) {
        model.addAttribute("reg", new User());
        return "register";
    }

    @Autowired
    private UserRepository userRepo;

    @PostMapping("/process_registration")
    public String Regisztracio(@Valid @ModelAttribute User user,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error_msg", "Nem megfelelő formátumú felhasználónév, email cím, vagy jelszó");
            return "reg_error";
        }
        try {
            for (User userCurrent : userRepo.findAll()) {
                if (userCurrent.getEmail().equals(user.getEmail())) {
                    model.addAttribute("error_msg", "Ez az email már foglalt!");
                    return "reg_error";
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
            return "reg_success";
        } catch (Exception e) {
            model.addAttribute("error_msg", e.getMessage());
            return "reg_error";
        }

    }
}
