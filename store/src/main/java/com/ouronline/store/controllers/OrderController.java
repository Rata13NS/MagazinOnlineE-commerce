package com.ouronline.store.controllers;

import com.ouronline.store.models.Order;
import com.ouronline.store.models.Product;
import com.ouronline.store.models.User;
import com.ouronline.store.repositories.OrderRepository;
import com.ouronline.store.repositories.ProductRepository;
import com.ouronline.store.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    @Autowired
    public OrderController(OrderRepository orderRepository, ProductRepository productRepository, UserService userService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.productRepository = productRepository;
    }

    @GetMapping("/place")
    public String getOrderPlacementPage(Model model, HttpSession session) {
        // Verificăm dacă utilizatorul este autentificat
        String currentUsername = (String) session.getAttribute("username");
        if (currentUsername != null) {
            Optional<User> optionalUser = userService.getUserByUsername(currentUsername);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                model.addAttribute("user", user);
                model.addAttribute("isLoggedIn", true); // Flag pentru utilizator autentificat
            }
        } else {
            model.addAttribute("isLoggedIn", false); // Flag pentru utilizator neautentificat
        }

        // Gestionarea coșului de cumpărături
        List<Long> cart = (List<Long>) session.getAttribute("cart");
        int cartCount = (cart != null) ? cart.size() : 0;
        model.addAttribute("cartCount", cartCount);

        // Inițializăm un obiect gol pentru comandă
        model.addAttribute("order", new Order());

        return "orderPlacementPage";
    }

    @PostMapping("/place")
    public String placeOrder(@ModelAttribute Order order,
                             @RequestParam("orderTotalPrice") Double totalPrice,
                             Model model, HttpSession session) {

        // Logare pentru debugging
        System.out.println("Received Order Total Price: " + totalPrice);

        // Validare pentru total price
        if (totalPrice == null || totalPrice <= 0) {
            model.addAttribute("errorMessage", "Invalid total price. Please try again.");
            return "orderPlacementPage";
        }
        order.setOrderTotalPrice(totalPrice);

        // Validare tip de plată
        String paymentType = order.getOrderPaymentType();
        if (paymentType == null || paymentType.trim().isEmpty()) {
            model.addAttribute("errorMessage", "Payment type is required.");
            return "orderPlacementPage";
        }

        // Setează detaliile comenzii
        order.setOrderData(LocalDate.now());
        order.setOrderPlacementHour(LocalTime.now());
        order.setOrderStatus("Registered");

        // Verifică utilizatorul autentificat
        String currentUsername = (String) session.getAttribute("username");
        if (currentUsername != null) {
            Optional<User> optionalUser = userService.getUserByUsername(currentUsername);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                order.setOrderUsername(user.getUsername());
                order.setEmailAddress(user.getEmailAdress());
                model.addAttribute("user", user);
                model.addAttribute("isLoggedIn", true);
            }
        } else {
            model.addAttribute("isLoggedIn", false);
        }

        // Obține coșul de cumpărături din sesiune
        List<Long> productIds = (List<Long>) session.getAttribute("cart");
        if (productIds != null) {
            List<Product> cartProducts = productIds.stream()
                    .map(productId -> productRepository.findById(productId).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            order.setOrderProductsNumber(String.valueOf(cartProducts.size()));
            order.setOrderProductsId(cartProducts.stream()
                    .map(product -> String.valueOf(product.getProductId()))
                    .collect(Collectors.joining(",")));
            order.setOrderProductsName(cartProducts.stream()
                    .map(Product::getProductName)
                    .collect(Collectors.joining(",")));
        }

        // VALIDARE SUPLIMENTARĂ: Verificarea câmpurilor de livrare în funcție de tipul de livrare
        List<String> errors = new ArrayList<>();

        if ("Easybox".equals(order.getOrderDeliveryType())) {
            if (order.getOrderDeliveryEasyboxAdress() == null || order.getOrderDeliveryEasyboxAdress().trim().isEmpty()) {
                errors.add("Please enter the Easybox address.");
            }
        } else if ("Courier".equals(order.getOrderDeliveryType())) {
            if (order.getOrderDeliveryCity() == null || order.getOrderDeliveryCity().trim().isEmpty()) {
                errors.add("Please enter the city for delivery.");
            }
            if (order.getOrderDeliveryStreet() == null || order.getOrderDeliveryStreet().trim().isEmpty()) {
                errors.add("Please enter the street for delivery.");
            }
            if (order.getOrderDeliveryNumber() == null || order.getOrderDeliveryNumber().trim().isEmpty()) {
                errors.add("Please enter the number for delivery.");
            }
        } else {
            errors.add("Invalid delivery type selected.");
        }

        // Dacă există erori, returnează pagina cu mesajele de eroare
        if (!errors.isEmpty()) {
            model.addAttribute("order", order); // Menține datele deja completate
            model.addAttribute("errorMessage", String.join(" ", errors));
            return "orderPlacementPage";
        }

        // Salvează comanda și obține ID-ul
        Order savedOrder = orderRepository.save(order);
        Long orderId = savedOrder.getOrderId();

        // Logare pentru confirmare
        System.out.println("Order saved with ID: " + orderId + " and Total Price: " + savedOrder.getOrderTotalPrice());

        // Mesaj de confirmare
        model.addAttribute("confirmationMessage", "Your order " + orderId + " has been registered. Thank you!");

        // Golește coșul
        session.setAttribute("cart", null);

        return "orderPlacementPage";
    }

    @GetMapping("/order-details/{orderId}")
    public String getOrderDetails(@PathVariable Long orderId, Model model, HttpSession session) {
        String currentUsername = (String) session.getAttribute("username");
        if (currentUsername == null) {
            return "redirect:/user/log-in"; // Redirecționează la log-in dacă utilizatorul nu este autentificat
        }

        Optional<User> optionalUser = userService.getUserByUsername(currentUsername);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("user", user);
            model.addAttribute("status", user.getStatus());
        } else {
            model.addAttribute("isLoggedIn", false);
        }


        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            if (!order.getOrderUsername().equals(currentUsername)) {
                return "redirect:/user/orders";
            }

            // Setăm detalii despre comandă
            model.addAttribute("order", order);
            model.addAttribute("placementTime", order.getOrderPlacementHour().toString().substring(0, 5));

            // Obține produsele și pregătește imaginile pentru afișare
            String[] productIds = order.getOrderProductsId().split(",");
            List<Product> products = Arrays.stream(productIds)
                    .map(id -> productRepository.findById(Long.parseLong(id)).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            products.forEach(product -> {
                if (product.getProductImage() != null) {
                    product.setProductImageBase64(Base64.getEncoder().encodeToString(product.getProductImage()));
                }
            });

            model.addAttribute("products", products);

            // Adresă de livrare
            if ("Easybox".equals(order.getOrderDeliveryType())) {
                model.addAttribute("deliveryAddress", order.getOrderDeliveryEasyboxAdress());
            } else if ("Courier".equals(order.getOrderDeliveryType())) {
                String fullAddress = String.format("%s, %s, %s, %s, %s %s",
                        order.getOrderDeliveryCity(),
                        order.getOrderDeliverySector(),
                        order.getOrderDeliveryStreet(),
                        order.getOrderDeliveryNumber(),
                        order.getOrderDeliveryBlock(),
                        order.getOrderDeliveryApartmentNumber());
                model.addAttribute("deliveryAddress", fullAddress);
            }

            return "clientOrderPage";
        }

        return "redirect:/user/orders";
    }

    @GetMapping("/admin/order-details/{orderId}")
    public String getAdminOrderDetails(@PathVariable Long orderId, Model model, HttpSession session) {
        // Verificăm autentificarea utilizatorului
        String currentUsername = (String) session.getAttribute("username");
        if (currentUsername == null) {
            return "redirect:/user/log-in"; // Redirecționează la log-in dacă utilizatorul nu este autentificat
        }

        Optional<User> optionalUser = userService.getUserByUsername(currentUsername);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("user", user);
            model.addAttribute("status", user.getStatus());
        } else {
            model.addAttribute("isLoggedIn", false);
        }

        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            model.addAttribute("order", order);

            // Informațiile despre client
            model.addAttribute("userName", order.getOrderClientName());
            model.addAttribute("userSirName", order.getOrderClientSirName());
            model.addAttribute("userUsername", order.getOrderUsername());
            model.addAttribute("userEmail", order.getEmailAddress());

            // Produsele din comandă
            String[] productIds = order.getOrderProductsId().split(",");
            List<Product> products = Arrays.stream(productIds)
                    .map(id -> productRepository.findById(Long.parseLong(id)).orElse(null))
                    .filter(Objects::nonNull)
                    .peek(product -> {
                        if (product.getProductImage() != null) {
                            product.setProductImageBase64(Base64.getEncoder().encodeToString(product.getProductImage()));
                        }
                    })
                    .collect(Collectors.toList());
            model.addAttribute("products", products);

            // Lista statusurilor
            List<String> statuses = List.of(
                    "Registered",
                    "In progress",
                    "Handed to courier",
                    "Delivered to easybox",
                    "Return",
                    "Order completed"
            );
            model.addAttribute("statuses", statuses);

            return "adminOrderPage";
        }

        return "redirect:/orders/admin";
    }

    @PostMapping("/admin/update-order-status")
    public String updateOrderStatus(@RequestParam Long orderId, @RequestParam String orderStatus) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setOrderStatus(orderStatus);
            orderRepository.save(order);
        }
        return "redirect:/orders/admin/order-details/" + orderId;
    }
}