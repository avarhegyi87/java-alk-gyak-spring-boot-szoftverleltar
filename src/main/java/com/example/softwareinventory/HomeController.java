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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import static com.example.softwareinventory.Message.getTextForMessageType;
import static java.lang.Long.parseLong;

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
    public String telepitesek(Model model) {
        try {
            SzoftverleltarDbManager manager = new SzoftverleltarDbManager();
            model.addAttribute("all_installs", manager.getInstallStats());
        } catch (Exception e) {
            model.addAttribute("error_msg", e.getMessage());
        }
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
                               BindingResult bindingResult,
                               Model model,
                               @CurrentSecurityContext(expression = "authentication") Authentication auth,
                               @CurrentSecurityContext(expression = "authentication?.name") String loggInUserName) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error_msg", "Túl hosszú az üzenet, vagy nem lett kiválasztva üzenettípus.");
            return "msg_error";
        }
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
                model.addAttribute("error_msg", "Az üzenetet nem sikerült elküldeni az adatbázisba.");
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

    @PostMapping("/registration_processing")
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
                    model.addAttribute("error_msg", "Már regisztrálta ezt az email címet!");
                    return "reg_error";
                }
                if (userCurrent.getUsername().equals(user.getUsername())) {
                    model.addAttribute("error_msg","Ez a felhasználónév már foglalt. Válasszon másikat!");
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
            System.out.println(user.getUsername());
            userRepo.save(user);

            model.addAttribute("id", user.getId());
            return "reg_success";
        } catch (Exception e) {
            model.addAttribute("error_msg", e.getMessage());
            return "reg_error";
        }

    }

    @GetMapping("/rest/visual")
    public static String Rest(Model model) throws ClientProtocolException, IOException {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://localhost:1000/rest");
        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = "";
        List<Rest> sorok = new ArrayList<Rest>();

        String adat = "";
        while ((line = rd.readLine()) != null) {
            adat = line;

        }
        String delimiter = ",\\{";
        adat = adat.replaceAll("[\\[\\]]", "");
        String[] adattomb = adat.split(delimiter);


        for (String data : adattomb) {
            String valami;
            if (!data.contains("{")) {
                valami = "{" + data;
            } else {
                valami = data;
            }
            System.out.println(valami);
            JSONObject jsonObject = new JSONObject(valami);
            List<String> jsontmb = new ArrayList<String>();
            Rest r = new Rest();
            jsonObject.keySet().forEach(keyStr ->
            {
                Object keyvalue = jsonObject.get(keyStr);
                jsontmb.add(String.valueOf(keyvalue));
            });
            System.out.println(jsontmb);
            r.setId(parseLong(jsontmb.get(3)));
            r.setBrand(jsontmb.get(4));
            r.setModel(jsontmb.get(2));
            r.setPrice(parseLong(jsontmb.get(1)));
            r.setYear(parseLong(jsontmb.get(0)));
            sorok.add(r);
        }
        model.addAttribute("usr", sorok);
        return "rest";
    }


}
