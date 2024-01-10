package com.smartcontactmanagaer.SCMApplication.Controller;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.smartcontactmanagaer.SCMApplication.Entity.Contact;
import com.smartcontactmanagaer.SCMApplication.Entity.User;
import com.smartcontactmanagaer.SCMApplication.Repository.ContactRepository;
import com.smartcontactmanagaer.SCMApplication.Repository.UserRepository;
import com.smartcontactmanagaer.SCMApplication.misc.Message;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ContactRepository contactRepository;

    // method for adding common data to all endpoints
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String userEmail = principal.getName();
        User user = userRepository.findByEmail(userEmail);
        model.addAttribute("user", user);
        System.out.println(user);
    }

    @RequestMapping("/index")
    public String userDashboard(Model model, Principal principal) {
        model.addAttribute("title", "Dashboard");
        return "normal/user_dashboard";
    }

    // open add contact form handler
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model, HttpSession session) {

        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
//        session.invalidate();
        return "normal/add_contact";
    }

    // add contact to database handler
    @PostMapping("/process-contact")
    public String addContact(@Valid @RequestParam("profileImage") MultipartFile file, @ModelAttribute Contact contact, BindingResult result, @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model, HttpSession session, Principal principal) {

        try {
            if(!agreement) {
                throw new Exception("Please check T&C");
            }

            String userEmail = principal.getName();
            User user = userRepository.findByEmail(userEmail);
//            user.getContactList().add(contact);
//            userRepository.save(user);

            // processing and uploading file
            if(file.isEmpty()) {
                contact.setImageUrl("contact.png");
//                throw new Exception("Invalid File");
            } else {
                // storing the file to images folder in static and adding the filename in database
                contact.setImageUrl(file.getOriginalFilename());

                File saveFile = new ClassPathResource("static/img").getFile();
                System.out.println(saveFile.getAbsolutePath());

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                System.out.println(path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

            contact.setUser(user);
            contactRepository.save(contact);
            System.out.println(contact);
            model.addAttribute("contact", new Contact());
            session.setAttribute("message", new Message("Contact Added Successfully", "alert-success"));
            return "normal/add_contact";
        } catch (Exception e) {
            model.addAttribute("contact", contact);
            session.setAttribute("message", new Message(e.getMessage(), "alert-danger"));
            return "normal/add_contact";
        }

    }

    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") int page, Model model, Principal principal) {
        model.addAttribute("title", "All Contacts");
        String userEmail = principal.getName();
        // getting user
        User user = userRepository.findByEmail(userEmail);
        int userId = user.getUId();

        // creating pageable object
        Pageable pageable = PageRequest.of(page, 5);

        // getting all the contacts of currrent user
        Page<Contact> contactPage = contactRepository.findContactsByUser(userId, pageable);

        model.addAttribute("contacts", contactPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contactPage.getTotalPages());

        return "normal/show_contacts";
    }

    @GetMapping("/view-contact/{contactId}")
    public String singleContact(@PathVariable("contactId") int contactId, Model model, Principal principal) {
        Contact contact = contactRepository.findById(contactId).orElse(null);
        User user = userRepository.findByEmail(principal.getName());
        if(contact.getUser().getUId() != user.getUId()) return "normal/unauthorized";
        model.addAttribute("title", contact.getName());
        model.addAttribute("contact", contact);
        return "normal/single_contact";
    }

    @GetMapping("/edit-contact/{contactId}")
    public String openEditContact(@PathVariable("contactId") int contactId, Model model, Principal principal) {

        Contact contact = contactRepository.findById(contactId).orElse(null);
        User user = userRepository.findByEmail(principal.getName());
        if(contact.getUser().getUId() != user.getUId()) return "normal/unauthorized";
        model.addAttribute("title", "Edit Contact");
        model.addAttribute("contact", contact);

        return "normal/edit_contact";
    }

    @PostMapping("/process-contact-edit")
    public String editContact(@Valid @RequestParam("profileImage") MultipartFile file, @ModelAttribute Contact contact, BindingResult result, @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model, HttpSession session, Principal principal) {
        try {
            if(!agreement) {
                throw new Exception("Please check T&C");
            }

            Contact currContact = contactRepository.findByEmail(contact.getEmail());
            currContact.setName(contact.getName());
            currContact.setNickName(contact.getNickName());
            currContact.setPhone(contact.getPhone());
            currContact.setEmail(contact.getEmail());
            currContact.setDescription(contact.getDescription());
            currContact.setWork(contact.getWork());
            // processing and uploading file
            if(file.isEmpty()) {
//                currContact.setImageUrl(contact.getImageUrl());
//                throw new Exception("Invalid File");
            } else {
                // getting profile image to delete
                String imgUrl = currContact.getImageUrl();
                File prevFile = new ClassPathResource("static/img").getFile();
                System.out.println(prevFile.getAbsolutePath());
                Path prevFilePath = Paths.get(prevFile.getAbsolutePath() + File.separator + imgUrl);
                System.out.println(Files.deleteIfExists(prevFilePath));

                // storing the file to images folder in static and adding the filename in database
                currContact.setImageUrl(file.getOriginalFilename());

                File saveFile = new ClassPathResource("static/img").getFile();
                System.out.println(saveFile.getAbsolutePath());

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

            contactRepository.save(currContact);
            System.out.println(contact);
            session.setAttribute("message", new Message("Contact Edited Successfully", "alert-success"));
            model.addAttribute("title", currContact.getName());
            model.addAttribute("contact", currContact);
            return "normal/single_contact";
        } catch (Exception e) {
            model.addAttribute("contact", contact);
            session.setAttribute("message", new Message(e.getMessage(), "alert-danger"));
            return "normal/edit_contact";
        }
    }

    @GetMapping("/delete-contact/{contactId}")
    public String deleteContact(@PathVariable("contactId") int contactId, Principal principal) {
        Contact contact = contactRepository.findById(contactId).orElse(null);
        User user = userRepository.findByEmail(principal.getName());
        if(contact.getUser().getUId() != user.getUId()) return "unauthorized";

        // getting profile image to delete
        String imgUrl = contact.getImageUrl();
        System.out.println(imgUrl);
//        Path path = Paths.get("static/img");
//        Path file = path.resolve(imgUrl);

        try {
            File saveFile = new ClassPathResource("static/img").getFile();
            System.out.println(saveFile.getAbsolutePath());
            Path filePath = Paths.get(saveFile.getAbsolutePath() + File.separator + imgUrl);
            System.out.println(Files.deleteIfExists(filePath));
        } catch (Exception e) {

        }
        contactRepository.delete(contact);
        return "redirect:/user/show-contacts/0";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("title", "Profile");
        return "normal/user_profile";
    }
}
