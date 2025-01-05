package com.ouronline.store.controllers;

import com.ouronline.store.models.Product;
import com.ouronline.store.models.User;
import com.ouronline.store.services.IProductServices;
import com.ouronline.store.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/product")
public class ProductController {

    private final IProductServices productService;
    private final UserService userService;

    @Autowired
    public ProductController(IProductServices productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @PostMapping("/add-product")
    public String addProduct(@RequestParam("productName") String productName,
                             @RequestParam("productDescription") String productDescription,
                             @RequestParam("productDimensions") String productDimensions,
                             @RequestParam("productWeight") String productWeight,
                             @RequestParam("productPrice") String productPrice,
                             @RequestParam("productCategory") String productCategory,
                             @RequestParam("productImage") MultipartFile productImage) {

        Product product = new Product();
        product.setProductName(productName);
        product.setProductDescription(productDescription);
        product.setProductDimensions(productDimensions);
        product.setProductWeight(productWeight);
        product.setProductPrice(productPrice);
        product.setProductCategory(productCategory);

        try {
            // Asigură-te că imaginea este convertită corect în byte[]
            product.setProductImage(productImage.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/admin?error=true";
        }

        productService.saveProduct(product);
        return "redirect:/product/shop"; // Redirecționare către pagina shop după salvare
    }

    @GetMapping("/shop")
    public String showShopPage(Model model, HttpSession session) {
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

        return "shopPage";
    }

    @GetMapping("/details/{productId}")
    public String showProductDetailsPage(@PathVariable Long productId, Model model, HttpSession session) {
        // Verificăm dacă utilizatorul este logat
        String currentUsername = (String) session.getAttribute("username");
        if (currentUsername != null) {
            Optional<User> optionalUser = userService.getUserByUsername(currentUsername);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                model.addAttribute("user", user);
                model.addAttribute("isLoggedIn", true); // Setăm flag-ul pentru autentificare
            }
        } else {
            model.addAttribute("isLoggedIn", false); // Dacă nu este logat
        }

        // Obținem produsul după ID
        Optional<Product> optionalProduct = productService.getProductById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setProductImageBase64(Base64.getEncoder().encodeToString(product.getProductImage()));
            model.addAttribute("product", product);
        } else {
            return "redirect:/product/shop"; // Dacă produsul nu există, redirecționează la pagina de shop
        }

        // Gestionarea coșului de cumpărături
        List<Long> cart = (List<Long>) session.getAttribute("cart");
        int cartCount = (cart != null) ? cart.size() : 0;
        model.addAttribute("cartCount", cartCount); // Adăugăm numărul de produse în model

        return "productPage"; // Redirecționare la pagina de detalii a produsului
    }

    @GetMapping("/clothing")
    public String showClothingPage(Model model, HttpSession session) {
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

        List<Product> products = productService.getProductsByCategory("Clothing");
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

        return "clothingShopPage";
    }

    @GetMapping("/artworks")
    public String showArtworksPage(Model model, HttpSession session) {
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

        List<Product> products = productService.getProductsByCategory("Artworks");
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

        return "artworksShopPage";
    }

    @GetMapping("/stickers")
    public String showStickersPage(Model model, HttpSession session) {
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

        List<Product> products = productService.getProductsByCategory("Stickers");
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

        return "stickersShopPage";
    }

    @GetMapping("/customIt")
    public String showCustomItPage(Model model, HttpSession session) {
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

        List<Product> products = productService.getProductsByCategory("CustomIt");
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

        return "customItShopPage";
    }

    @DeleteMapping("/delete/{productId}")
    @ResponseBody
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        System.out.println("Deleting product with ID: " + productId); // Linie de debug pentru a verifica apelul
        try {
            productService.deleteProductById(productId);
            return ResponseEntity.ok("Product deleted successfully");
        } catch (Exception e) {
            e.printStackTrace(); // Linie de debug pentru a vedea erorile
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting product");
        }
    }

    @GetMapping("/edit/{productId}")
    public String showEditProductPage(@PathVariable Long productId, Model model, HttpSession session) {
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
            model.addAttribute("isLoggedIn", false); // Dacă utilizatorul nu este logat
        }

        // Obține produsul după ID
        Optional<Product> optionalProduct = productService.getProductById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setProductImageBase64(Base64.getEncoder().encodeToString(product.getProductImage()));
            model.addAttribute("product", product);
        } else {
            return "redirect:/product/shop"; // Dacă produsul nu există, redirecționează la pagina de shop
        }

        // Gestionarea coșului de cumpărături
        List<Long> cart = (List<Long>) session.getAttribute("cart");
        int cartCount = (cart != null) ? cart.size() : 0;
        model.addAttribute("cartCount", cartCount); // Adăugăm numărul de produse în model

        return "editProductPage"; // Returnează pagina pentru editarea produsului
    }

    @PostMapping("/update")
    public String updateProduct(@RequestParam("productId") Long productId,
                                @RequestParam("productName") String productName,
                                @RequestParam("productDescription") String productDescription,
                                @RequestParam("productDimensions") String productDimensions,
                                @RequestParam("productWeight") String productWeight,
                                @RequestParam("productPrice") String productPrice,
                                @RequestParam("productCategory") String productCategory,
                                @RequestParam(value = "productImage", required = false) MultipartFile productImage) {
        Optional<Product> optionalProduct = productService.getProductById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();

            // Actualizează câmpurile produsului
            product.setProductName(productName);
            product.setProductDescription(productDescription);
            product.setProductDimensions(productDimensions);
            product.setProductWeight(productWeight);
            product.setProductPrice(productPrice);
            product.setProductCategory(productCategory);

            // Dacă există o imagine nouă, actualizează imaginea
            if (productImage != null && !productImage.isEmpty()) {
                try {
                    product.setProductImage(productImage.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                    return "redirect:/product/edit/" + productId + "?error=true";
                }
            }

            // Salvează modificările
            productService.saveProduct(product);
        }

        return "redirect:/product/shop"; // Redirecționează la pagina de shop după actualizare
    }

    @PostMapping("/add-to-cart")
    @ResponseBody
    public ResponseEntity<String> addToCart(@RequestParam Long productId, HttpSession session) {
        // Extragem coșul din sesiune
        List<Long> cart = (List<Long>) session.getAttribute("cart");

        // Dacă nu există, creăm unul nou
        if (cart == null) {
            cart = new ArrayList<>();
        }

        // Adăugăm produsul în coș
        cart.add(productId);

        // Actualizăm sesiunea
        session.setAttribute("cart", cart);

        return ResponseEntity.ok("Product added to cart successfully!");
    }

    @PostMapping("/remove-from-cart")
    @ResponseBody
    public ResponseEntity<String> removeFromCart(@RequestParam Long productId, HttpSession session) {
        List<Long> cart = (List<Long>) session.getAttribute("cart");

        if (cart != null) {
            cart.remove(productId);
            session.setAttribute("cart", cart);
        }

        return ResponseEntity.ok("Product removed from cart.");
    }

    @GetMapping("/somePage")
    public String somePage(HttpSession session, Model model) {
        // Preluarea coșului de cumpărături din sesiune
        List<Long> cart = (List<Long>) session.getAttribute("cart");

        // Calcularea numărului de produse din coș
        int cartCount = (cart != null) ? cart.size() : 0;

        // Adăugarea valorii în model pentru Thymeleaf
        model.addAttribute("cartCount", cartCount);

        // Returnează pagina
        return "somePage"; // Înlocuiește cu numele paginii tale
    }
}

