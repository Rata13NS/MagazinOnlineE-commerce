package com.ouronline.store.controllers;

import com.ouronline.store.models.Order;
import com.ouronline.store.models.Product;
import com.ouronline.store.models.User;
import com.ouronline.store.services.IProductServices;
import com.ouronline.store.services.OrderService;
import com.ouronline.store.services.UserService;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "/user")
public class UserController {
    private final UserService userService;
    private final IProductServices productService;
    private final OrderService orderService;

    public UserController(UserService userService, IProductServices productService, OrderService orderService) {
        this.userService = userService;
        this.productService = productService;
        this.orderService = orderService;
    }

    @GetMapping
    @ResponseBody
    public List<User> getUser() {
        return userService.getUser();
    }

    @PostMapping
    @ResponseBody
    public void createUser(@RequestBody User user) {
        userService.createUser(user);
    }

    @PutMapping(path = "{id}")
    @ResponseBody
    public void updateUser(@PathVariable Long id, @RequestBody User user) {
        userService.updateUser(id, user);
    }

    @DeleteMapping(path = "{id}")
    @ResponseBody
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @RequestMapping("/log-in")
    public ModelAndView showLogInPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("logInPage");
        return modelAndView;
    }

    @PostMapping("/log-in")
    public String logIn(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
        try {
            userService.validateExistingUsername(username);
            userService.validatePassword(username, password);
            session.setAttribute("username", username);
            return "redirect:/user/home";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("username", username);
            return "logInPage";
        }
    }

    @RequestMapping("/create-account")
    public String showCreateAccountPage(Model model) {
        model.addAttribute("user", new User());
        return "createAccountPage";
    }

    @PostMapping("/create-account")
    public String createAccount(@ModelAttribute User user,
                                @RequestParam String confirmPassword,
                                @RequestParam(required = false) Boolean newsletterCheckbox,
                                Model model,
                                HttpSession session) {
        try {
            // Validare parolă
            String password = user.getPassword();
            if (!password.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
                model.addAttribute("error", "Password must be at least 8 characters long, include at least one uppercase letter, and one number.");
                return "createAccountPage";
            }

            // Confirmarea parolei
            if (!password.equals(confirmPassword)) {
                model.addAttribute("error", "Passwords do not match!");
                return "createAccountPage";
            }

            // Setăm valoarea pentru newsletter
            if (Boolean.TRUE.equals(newsletterCheckbox)) {
                user.setNewsletter("Da");
            } else {
                user.setNewsletter("Nu");
            }

            user.setStatus("Client");
            userService.createUser(user);
            session.setAttribute("username", user.getUsername());
            return "redirect:/user/home";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "createAccountPage";
        }
    }


    @RequestMapping("/home")
    public String showHomePage(Model model, HttpSession session) {
        // Verifică dacă utilizatorul este autentificat
        String currentUsername = (String) session.getAttribute("username");
        if (currentUsername != null) {
            Optional<User> optionalUser = userService.getUserByUsername(currentUsername);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                model.addAttribute("user", user);
                model.addAttribute("isLoggedIn", true); // Adăugăm un flag pentru autentificare
            }
        } else {
            model.addAttribute("isLoggedIn", false); // Dacă nu este logat
        }

        // Gestionarea coșului de cumpărături
        List<Long> cart = (List<Long>) session.getAttribute("cart");
        int cartCount = (cart != null) ? cart.size() : 0;
        model.addAttribute("cartCount", cartCount); // Adăugăm numărul de produse în model

        return "homePage"; // Returnează view-ul paginii de start
    }

    @RequestMapping("/my-profile")
    public String showMyProfilePage(Model model, HttpSession session) {
        // Verificăm dacă utilizatorul este autentificat
        String currentUsername = (String) session.getAttribute("username");
        if (currentUsername != null) {
            Optional<User> optionalUser = userService.getUserByUsername(currentUsername);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                model.addAttribute("isLoggedIn", true); // Setăm flag-ul pentru autentificare
                model.addAttribute("user", user); // Adăugăm utilizatorul în model

                // Gestionarea coșului de cumpărături
                List<Long> cart = (List<Long>) session.getAttribute("cart");
                int cartCount = (cart != null) ? cart.size() : 0;
                model.addAttribute("cartCount", cartCount); // Numărul de produse din coș

                return "myProfilePage"; // Returnăm pagina de profil
            }
        }

        // Dacă utilizatorul nu este autentificat sau nu există în baza de date
        model.addAttribute("isLoggedIn", false);
        return "redirect:/user/log-in";
    }

    @GetMapping("/my-profile/{id}")
    public String showMyProfilePage(@PathVariable Long id, Model model, HttpSession session) {
        Optional<User> optionalUser = Optional.ofNullable((User) userService.getUserById(id));
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            model.addAttribute("user", user);
            // Gestionarea coșului de cumpărături
            List<Long> cart = (List<Long>) session.getAttribute("cart");
            int cartCount = (cart != null) ? cart.size() : 0;
            model.addAttribute("cartCount", cartCount); // Adăugăm numărul de produse în model
            return "myProfilePage";
        }
        return "errorPage";
    }


    @PostMapping("/apply-changes")
    public String applyProfileChanges(@ModelAttribute User updatedUser, HttpSession session, Model model) {
        String currentUsername = (String) session.getAttribute("username");
        if (currentUsername != null) {
            Optional<User> optionalUser = userService.getUserByUsername(currentUsername);
            if (optionalUser.isPresent()) {
                User existingUser = optionalUser.get();
                try {
                    userService.validateEmailForUpdate(existingUser.getId(), updatedUser.getEmailAdress());
                    existingUser.setEmailAdress(updatedUser.getEmailAdress());
                    userService.updateUserDetails(existingUser);
                    return "redirect:/user/my-profile";
                } catch (IllegalStateException e) {
                    model.addAttribute("error", e.getMessage());
                    model.addAttribute("user", existingUser);
                }
            }
        }
        return "myProfilePage";
    }

    @RequestMapping("/aboutUs")
    public String showAboutUsPage(Model model, HttpSession session) {
        String currentUsername = (String) session.getAttribute("username");
        if (currentUsername != null) {
            Optional<User> optionalUser = userService.getUserByUsername(currentUsername);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                model.addAttribute("user", user);
                model.addAttribute("isLoggedIn", true); // Adăugăm un flag pentru autentificare
            }
        } else {
            model.addAttribute("isLoggedIn", false); // Dacă nu este logat
        }
        // Gestionarea coșului de cumpărături
        List<Long> cart = (List<Long>) session.getAttribute("cart");
        int cartCount = (cart != null) ? cart.size() : 0;
        model.addAttribute("cartCount", cartCount); // Adăugăm numărul de produse în model
        return "aboutUsPage";
    }

    @RequestMapping("/contact")
    public String showContactPage(Model model, HttpSession session) {
        String currentUsername = (String) session.getAttribute("username");
        if (currentUsername != null) {
            Optional<User> optionalUser = userService.getUserByUsername(currentUsername);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                model.addAttribute("user", user);
                model.addAttribute("isLoggedIn", true); // Adăugăm un flag pentru autentificare
            }
        } else {
            model.addAttribute("isLoggedIn", false); // Dacă nu este logat
        }
        // Gestionarea coșului de cumpărături
        List<Long> cart = (List<Long>) session.getAttribute("cart");
        int cartCount = (cart != null) ? cart.size() : 0;
        model.addAttribute("cartCount", cartCount); // Adăugăm numărul de produse în model
        return "contactPage";
    }


    // Metoda pentru log out
    @RequestMapping("/log-out")
    public String logOut(HttpSession session, Model model) {
        // Ștergem atributele din sesiune
        session.removeAttribute("username");
        session.removeAttribute("currentUsername");

        // Invalidează sesiunea
        session.invalidate();

        // Setăm flag-ul isLoggedIn la false și trimitem utilizatorul la pagina de home
        model.addAttribute("isLoggedIn", false);
        return "redirect:/user/home";
    }

    @RequestMapping("/cart")
    public String showCartPage(Model model, HttpSession session) {
        // Verificăm dacă utilizatorul este autentificat
        String currentUsername = (String) session.getAttribute("username");
        if (currentUsername != null) {
            Optional<User> optionalUser = userService.getUserByUsername(currentUsername);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                model.addAttribute("user", user); // Setăm utilizatorul în model
                model.addAttribute("isLoggedIn", true); // Flag-ul pentru autentificare
            } else {
                model.addAttribute("isLoggedIn", false);
            }
        } else {
            model.addAttribute("isLoggedIn", false);
        }

        // Recuperăm produsele din coș
        List<Long> cart = (List<Long>) session.getAttribute("cart");
        List<Product> cartProducts = new ArrayList<>();
        if (cart != null) {
            for (Long productId : cart) {
                Optional<Product> product = productService.getProductById(productId);
                product.ifPresent(cartProducts::add);
            }
        }

        // Setăm produsele din coș în model
        cartProducts.forEach(product -> {
            if (product.getProductImage() != null) {
                product.setProductImageBase64(Base64.getEncoder().encodeToString(product.getProductImage()));
            }
        });
        model.addAttribute("cartProducts", cartProducts);

        // Calcularea prețului total al produselor din coș
        double totalPrice = cartProducts.stream()
                .mapToDouble(product -> Double.parseDouble(product.getProductPrice()))
                .sum();
        model.addAttribute("totalPrice", totalPrice); // Transmitem totalul către Thymeleaf

        // Gestionarea coșului de cumpărături
        int cartCount = (cart != null) ? cart.size() : 0;
        model.addAttribute("cartCount", cartCount); // Adăugăm numărul de produse în model

        return "cartPage";
    }

    @RequestMapping("/orders")
    public String showOrdersPage(Model model, HttpSession session) {
        String currentUsername = (String) session.getAttribute("username");

        if (currentUsername != null) {
            // Obține detaliile utilizatorului conectat
            Optional<User> optionalUser = userService.getUserByUsername(currentUsername);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                model.addAttribute("user", user);
                model.addAttribute("isLoggedIn", true); // Flag pentru autentificare

                // Obține comenzile utilizatorului conectat
                List<Order> userOrders = orderService.getOrdersByUsername(currentUsername);
                model.addAttribute("orders", userOrders); // Transmite comenzile către view
            }
        } else {
            model.addAttribute("isLoggedIn", false); // Dacă utilizatorul nu este logat
            model.addAttribute("orders", new ArrayList<>()); // Nicio comandă
        }

        // Gestionarea coșului de cumpărături
        List<Long> cart = (List<Long>) session.getAttribute("cart");
        int cartCount = (cart != null) ? cart.size() : 0;
        model.addAttribute("cartCount", cartCount); // Transmite numărul de produse din coș

        return "ordersPage"; // Returnează view-ul pentru pagina de comenzi
    }

    @RequestMapping("/admin")
    public String showAdminPage(Model model, HttpSession session) {
        String currentUsername = (String) session.getAttribute("username");
        if (currentUsername != null) {
            Optional<User> optionalUser = userService.getUserByUsername(currentUsername);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                model.addAttribute("user", user);
                model.addAttribute("isLoggedIn", true); // Adăugăm un flag pentru autentificare
            }
        } else {
            model.addAttribute("isLoggedIn", false); // Dacă nu este logat
        }

        // Gestionarea coșului de cumpărături
        List<Long> cart = (List<Long>) session.getAttribute("cart");
        int cartCount = (cart != null) ? cart.size() : 0;
        model.addAttribute("cartCount", cartCount); // Adăugăm numărul de produse în model

        return "adminPage";
    }

    @GetMapping("/big-admin")
    public String showBigAdminPage(Model model, HttpSession session) {
        // Verifică dacă utilizatorul este logat
        String currentUsername = (String) session.getAttribute("username");
        if (currentUsername != null) {
            Optional<User> optionalUser = userService.getUserByUsername(currentUsername);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                model.addAttribute("user", user);
                model.addAttribute("isLoggedIn", true); // Setează flag-ul pentru autentificare
            }
        } else {
            model.addAttribute("isLoggedIn", false); // Setează flag-ul dacă utilizatorul nu este logat
        }

        // Obține produsele din baza de date și le pregătește pentru afișare
        List<Product> products = productService.getAllProducts();
        products.forEach(product -> {
            if (product.getProductImage() != null) {
                String base64Image = Base64.getEncoder().encodeToString(product.getProductImage());
                product.setProductImageBase64(base64Image);
            }
        });
        model.addAttribute("products", products);

        // Gestionarea coșului de cumpărături
        List<Long> cart = (List<Long>) session.getAttribute("cart");
        int cartCount = (cart != null) ? cart.size() : 0;
        model.addAttribute("cartCount", cartCount); // Adăugăm numărul de produse în model

        return "bigAdminPage";
    }
    @GetMapping("/super-admin")
    public String showSuperAdminPage(Model model, HttpSession session) {
        // Verifică dacă utilizatorul este logat
        String currentUsername = (String) session.getAttribute("username");
        if (currentUsername != null) {
            Optional<User> optionalUser = userService.getUserByUsername(currentUsername);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                model.addAttribute("user", user);
                model.addAttribute("isLoggedIn", true); // Setează flag-ul pentru autentificare
            }
        } else {
            model.addAttribute("isLoggedIn", false); // Setează flag-ul dacă utilizatorul nu este logat
        }

        // Obține produsele din baza de date și le pregătește pentru afișare
        List<Product> products = productService.getAllProducts();
        products.forEach(product -> {
            if (product.getProductImage() != null) {
                String base64Image = Base64.getEncoder().encodeToString(product.getProductImage());
                product.setProductImageBase64(base64Image);
            }
        });
        model.addAttribute("products", products);

        // Gestionarea coșului de cumpărături
        List<Long> cart = (List<Long>) session.getAttribute("cart");
        int cartCount = (cart != null) ? cart.size() : 0;
        model.addAttribute("cartCount", cartCount); // Adăugăm numărul de produse în model

        return "superAdminPage";
    }

    @GetMapping("/permissions")
    public String showUserPermissionsPage(Model model, HttpSession session) {
        String currentUsername = (String) session.getAttribute("username");
        if (currentUsername != null) {
            Optional<User> optionalUser = userService.getUserByUsername(currentUsername);
            if (optionalUser.isPresent()) {
                User currentUser = optionalUser.get();
                model.addAttribute("user", currentUser);
                model.addAttribute("isLoggedIn", true);

                // Obține lista tuturor utilizatorilor din baza de date
                List<User> users = userService.getUser();
                model.addAttribute("users", users);

                // Gestionarea coșului de cumpărături
                List<Long> cart = (List<Long>) session.getAttribute("cart");
                int cartCount = (cart != null) ? cart.size() : 0;
                model.addAttribute("cartCount", cartCount); // Adăugăm numărul de produse în model

                return "userPermissionsPage";
            }
        }
        model.addAttribute("isLoggedIn", false);
        return "redirect:/user/log-in";
    }

    @GetMapping("/user-edit/{id}")
    public String showEditUserPage(@PathVariable Long id, Model model, HttpSession session) {
        String currentUsername = (String) session.getAttribute("username");
        if (currentUsername != null) {
            Optional<User> optionalUser = userService.getUserByUsername(currentUsername);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                model.addAttribute("user", user); // Setăm utilizatorul în model
                model.addAttribute("isLoggedIn", true); // Flag-ul pentru autentificare
            } else {
                model.addAttribute("isLoggedIn", false);
            }
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        User user = userService.getUserById(id); // Obține utilizatorul din baza de date
        model.addAttribute("user", user); // Adaugă utilizatorul în model


        return "editUserPage"; // Returnează pagina corectă
    }

    @PostMapping("/user-update")
    public String updateUserDetails(@ModelAttribute User updatedUser) {
        System.out.println("Updating User:");
        System.out.println("ID: " + updatedUser.getId());
        System.out.println("Username: " + updatedUser.getUsername());
        System.out.println("Email: " + updatedUser.getEmailAdress());
        System.out.println("Password: " + updatedUser.getPassword());
        System.out.println("Status: " + updatedUser.getStatus());

        if (updatedUser.getId() == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }

        userService.updateUserByAdmin(updatedUser.getId(), updatedUser);
        return "redirect:/user/permissions";
    }

    @RequestMapping("/order-placement")
    public String showOrderPlacementPage(Model model, HttpSession session) {
        model.addAttribute("order", new Order());
        // Verificăm dacă utilizatorul este autentificat
        String currentUsername = (String) session.getAttribute("username");
        if (currentUsername != null) {
            Optional<User> optionalUser = userService.getUserByUsername(currentUsername);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                model.addAttribute("user", user); // Setăm utilizatorul în model
                model.addAttribute("isLoggedIn", true); // Flag-ul pentru autentificare
            } else {
                model.addAttribute("isLoggedIn", false);
            }
        } else {
            model.addAttribute("isLoggedIn", false);
        }

        // Recuperăm produsele din coș
        List<Long> cart = (List<Long>) session.getAttribute("cart");
        List<Product> cartProducts = new ArrayList<>();
        if (cart != null) {
            for (Long productId : cart) {
                Optional<Product> product = productService.getProductById(productId);
                product.ifPresent(cartProducts::add);
            }
        }

        // Setăm produsele din coș în model
        cartProducts.forEach(product -> {
            if (product.getProductImage() != null) {
                product.setProductImageBase64(Base64.getEncoder().encodeToString(product.getProductImage()));
            }
        });
        model.addAttribute("cartProducts", cartProducts);

        // Calcularea prețului total al produselor din coș
        double totalPrice = cartProducts.stream()
                .mapToDouble(product -> {
                    try {
                        return Double.parseDouble(product.getProductPrice());
                    } catch (NumberFormatException e) {
                        return 0.0; // În cazul în care prețul nu este valid, folosim 0.0
                    }
                })
                .sum();
        model.addAttribute("totalPrice", totalPrice); // Transmitem totalul către Thymeleaf

        // Gestionarea coșului de cumpărături
        int cartCount = (cart != null) ? cart.size() : 0;
        model.addAttribute("cartCount", cartCount); // Adăugăm numărul de produse în model

        return "orderPlacementPage";
    }

    @GetMapping("/orders-admin")
    public String showOrdersAdminPage(Model model, HttpSession session) {
        String currentUsername = (String) session.getAttribute("username");

        if (currentUsername != null) {
            Optional<User> optionalUser = userService.getUserByUsername(currentUsername);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                if ("superadmin".equals(user.getStatus())) {
                    model.addAttribute("user", user);
                    model.addAttribute("isLoggedIn", true);

                    // Filtrarea comenzilor cu statusurile dorite
                    List<Order> filteredOrders = orderService.getAllOrders().stream()
                            .filter(order -> List.of("Registered", "In progress", "Handed to courier",
                                            "Delivered to easybox", "Return", "Order completed")
                                    .contains(order.getOrderStatus()))
                            .collect(Collectors.toList());
                    model.addAttribute("orders", filteredOrders);

                    return "ordersAdminPage";
                }
            }
        }
        model.addAttribute("isLoggedIn", false);
        return "redirect:/user/log-in";
    }
}



