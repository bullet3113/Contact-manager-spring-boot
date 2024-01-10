package com.smartcontactmanagaer.SCMApplication.Controller;

import com.smartcontactmanagaer.SCMApplication.Entity.User;
import com.smartcontactmanagaer.SCMApplication.Repository.UserRepository;
import com.smartcontactmanagaer.SCMApplication.misc.Message;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
public class HomeController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About");
        return "about";
    }

    @RequestMapping("/signup")
    public String signup(Model model, HttpSession session) {
        model.addAttribute("user", new User());
        model.addAttribute("title", "Signup");
        session.invalidate();
        return "signup";
    }

    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Login");
        return "login";
    }

    // user registration handleer
    @RequestMapping(value = "/do-register", method = RequestMethod.POST)
    public String registerUser(@Valid @RequestParam("profileImage") MultipartFile file, @ModelAttribute User user, BindingResult result, @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model, HttpSession session) {
//        System.out.println(user);
//        System.out.println(agreement);

        try {
            if(!agreement) {
                System.out.println("Please check T&C");
                throw new Exception("Please check T&C");
            }

            if(result.hasErrors()) {
                System.out.println("ERROR " + result.getAllErrors());
                session.invalidate();
                model.addAttribute("user", user);
                return "signup";
            }

            if(file.isEmpty()) {
                user.setImageUrl("contact.png");
//                throw new Exception("Invalid File");
            } else {
                // storing the file to images folder in static and adding the filename in database
                user.setImageUrl(file.getOriginalFilename());

                File saveFile = new ClassPathResource("static/img").getFile();
                System.out.println(saveFile.getAbsolutePath());

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                System.out.println(path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

            user.setRole("ROLE_USER");
            user.setActive(true);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            model.addAttribute("user", new User());
            session.setAttribute("message", new Message("Successfully Registered!", "alert-success"));
            System.out.println(user);
            return "signup";
        } catch (Exception e) {
            Message message = new Message(e.getMessage(), "alert-danger");
            model.addAttribute("user", user);
            session.setAttribute("message", message);
            return "signup";
        }


    }
}
