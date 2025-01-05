document.getElementById("createAccountButton").addEventListener("click", function() {
    let username = document.getElementById("setUsernameInput").value;
    let password = document.getElementById("setPasswordInput").value;
    let confirmPassword = document.getElementById("confirmPasswordInput").value;
    let phoneNumber = document.getElementById("setPhoneInput").value;
    let emailAdress = document.getElementById("setEmailInput").value;
    let firstName = document.getElementById("setFirstNameInput").value;
    let lastName = document.getElementById("setLastNameInput").value;
    let age = document.getElementById("setAgeInput").value;
    let profession = document.getElementById("setProfessionInput").value;

    if (password !== confirmPassword) {
        alert("Passwords do not match!");
        return;
    }

    let person = {
        username: username,
        password: password,
        phoneNumber: phoneNumber,
        emailAdress: emailAdress,
        firstName: firstName,
        lastName: lastName,
        age: age,
        profession: profession,
        status: "voter",
        bibliography: "",
        motto: "",
        nrOfVotes: 0
    };

    fetch("/persons", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(person)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Network response was not ok");
            }
            return response.json();
        })
        .then(data => {
            console.log("Account created successfully:", data);
            localStorage.setItem("username", username);
            window.location.href = "homePage";
        })
        .catch(error => {
            console.error("Error:", error);
            alert("An error occurred while creating the account!");
        });
});

document.getElementById("logInButton").addEventListener("click", function() {
    let username = document.getElementById("logInUsernameInput").value;
    let password = document.getElementById("logInPasswordInput").value;

    let credentials = {
        username: username,
        password: password
    };

    fetch("/persons/log-in", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(credentials)
    })
        .then(response => {
            if (response.ok) {
                localStorage.setItem("username", username);
                window.location.href = "homePage";
            }
        })
        .catch(error => {
            console.error("Error:", error);
            alert("An error occurred while logging in!");
        });
});

function redirectToMyProfilePage() {
    window.location.href = "/user/my-profile";
}
function redirectToHomePage() {
    window.location.href = "/user/home";
}
function redirectToCreateAccountPage() {
    window.location.href = "/user/create-account";
}
function redirectToShopPage() {
    window.location.href = "/product/shop";
}
function redirectToAboutUsPage() {
    window.location.href = "/user/aboutUs";
}
function redirectToContactPage() {
    window.location.href = "/user/contact";
}
function redirectToLogInPage() {
    window.location.href = "/user/log-in";
}
function redirectToCartPage(){
    window.location.href = "/user/cart";
}
function redirectToOrdersPage(){
    window.location.href = "/user/orders"
}
function redirectToClothingShopPage(){
    window.location.href = "/product/clothing"
}
function redirectToArtworksShopPage(){
    window.location.href = "/product/artworks"
}
function redirectToStickersShopPage(){
    window.location.href = "/product/stickers"
}
function redirectToCustomItShopPage(){
    window.location.href = "/product/customIt"
}
function redirectToAdminPage(){
    window.location.href = "/user/admin"
}
function redirectToBigAdminPage(){
    window.location.href = "/user/big-admin"
}
function redirectToSuperAdminPage(){
    window.location.href = "/user/super-admin"
}
function redirectToEditProductPage(productId) {
    window.location.href = "/product/edit/" + productId;
}
function redirectToOrderPlacementPage(){
    window.location.href = "/user/order-placement";
}

function redirectToOrdersAdminPage() {
    window.location.href = "/user/orders-admin";
}
function deleteProduct(productId) {
    fetch(`/product/delete/${productId}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.ok) {
                alert("Product deleted successfully");
                window.location.reload(); // Reîncarcă pagina pentru a reflecta schimbările
            } else {
                alert("Failed to delete product");
                console.error("Failed to delete product. Status:", response.status);
            }
        })
        .catch(error => console.error("Error:", error));
}

function addToCart(productId) {
    fetch('/product/add-to-cart', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: `productId=${productId}`
    })
        .then(response => {
            if (response.ok) {
                alert("Product added to cart successfully!");
            } else {
                alert("Failed to add product to cart.");
            }
        })
        .catch(error => console.error('Error:', error));
}

function removeFromCart(productId) {
    fetch('/product/remove-from-cart', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: `productId=${productId}`
    })
        .then(response => {
            if (response.ok) {
                alert("Product removed from cart.");
                location.reload(); // Reîncărcăm pagina pentru a actualiza coșul
            } else {
                alert("Failed to remove product from cart.");
            }
        })
        .catch(error => console.error('Error:', error));
}

document.addEventListener('DOMContentLoaded', () => {
    // Exclusivitate pentru Delivery Type
    const deliveryOptions = document.querySelectorAll('.delivery-option');
    deliveryOptions.forEach(option => {
        option.addEventListener('change', () => {
            deliveryOptions.forEach(otherOption => {
                if (otherOption !== option) {
                    otherOption.checked = false; // Deselectează celelalte opțiuni
                }
            });
        });
    });

    // Exclusivitate pentru Payment Options (va fi gestionat din updatePaymentOptions)
    // Astfel, nu este nevoie să adăugăm evenimente suplimentare aici
});

// Funcția pentru afișarea corectă a secțiunilor din Delivery Information
function toggleDeliveryInfo() {
    const easyboxChecked = document.getElementById('deliveryTypeEasybox').checked;
    const courierChecked = document.getElementById('deliveryTypeCourier').checked;

    const deliveryInfo = document.getElementById('deliveryInfo');
    const easyboxAddress = document.getElementById('easyboxAddress');
    const fullAddress = document.getElementById('fullAddress');

    // Ascunde totul by default
    deliveryInfo.style.display = 'none';
    easyboxAddress.style.display = 'none';
    fullAddress.style.display = 'none';

    // Elimină atributul 'required' de pe toate câmpurile de adresă
    document.getElementById('easyboxAddressInput').required = false;
    document.getElementById('orderDeliveryCity').required = false;
    document.getElementById('orderDeliveryStreet').required = false;
    document.getElementById('orderDeliveryNumber').required = false;

    if (easyboxChecked) {
        deliveryInfo.style.display = 'block';
        easyboxAddress.style.display = 'block';
        document.getElementById('easyboxAddressInput').required = true;
    } else if (courierChecked) {
        deliveryInfo.style.display = 'block';
        fullAddress.style.display = 'block';
        document.getElementById('orderDeliveryCity').required = true;
        document.getElementById('orderDeliveryStreet').required = true;
        document.getElementById('orderDeliveryNumber').required = true;
    }
}

// Funcția pentru actualizarea opțiunilor de plată
function updatePaymentOptions() {
    const paymentOptions = document.getElementById('paymentOptions');
    paymentOptions.innerHTML = ''; // Resetează opțiunile

    const easyboxChecked = document.getElementById('deliveryTypeEasybox').checked;
    const courierChecked = document.getElementById('deliveryTypeCourier').checked;

    if (easyboxChecked) {
        paymentOptions.innerHTML = `
            <div class="form-check">
                <input class="form-check-input payment-option" type="radio" id="paymentCardEasybox" name="orderPaymentType" value="Card at Easybox" required>
                <label class="form-check-label" for="paymentCardEasybox">Card at Easybox</label>
            </div>
            <div class="form-check">
                <input class="form-check-input payment-option" type="radio" id="paymentOnlineCardEasybox" name="orderPaymentType" value="Online Card" required>
                <label class="form-check-label" for="paymentOnlineCardEasybox">Online Card</label>
            </div>
        `;
    } else if (courierChecked) {
        paymentOptions.innerHTML = `
            <div class="form-check">
                <input class="form-check-input payment-option" type="radio" id="paymentCashCourier" name="orderPaymentType" value="Cash" required>
                <label class="form-check-label" for="paymentCashCourier">Cash</label>
            </div>
            <div class="form-check">
                <input class="form-check-input payment-option" type="radio" id="paymentOnlineCardCourier" name="orderPaymentType" value="Online Card" required>
                <label class="form-check-label" for="paymentOnlineCardCourier">Online Card</label>
            </div>
        `;
    }

    // Adaugă evenimente de exclusivitate pentru noile opțiuni de plată
    const paymentOptionElements = document.querySelectorAll('.payment-option');
    paymentOptionElements.forEach(option => {
        option.addEventListener('change', () => {
            paymentOptionElements.forEach(otherOption => {
                if (otherOption !== option) {
                    otherOption.checked = false; // Deselectează celelalte opțiuni de plată
                }
            });
        });
    });
}

document.getElementById('orderPlacementForm').addEventListener('submit', function (event) {
    // Verifică dacă o metodă de livrare este selectată
    const deliveryType = document.querySelector('input[name="orderDeliveryType"]:checked');
    if (!deliveryType) {
        alert("Please select a delivery type.");
        event.preventDefault();
        return;
    }

    // Verifică câmpurile în funcție de tipul de livrare
    if (deliveryType.value === "Easybox") {
        const easyboxAddress = document.getElementById('easyboxAddressInput').value.trim();
        if (easyboxAddress === "") {
            alert("Please enter the Easybox address.");
            event.preventDefault();
            return;
        }
    } else if (deliveryType.value === "Courier") {
        const city = document.getElementById('orderDeliveryCity').value.trim();
        const street = document.getElementById('orderDeliveryStreet').value.trim();
        const number = document.getElementById('orderDeliveryNumber').value.trim();
        if (city === "" || street === "" || number === "") {
            alert("Please enter City, Street, and Number for delivery.");
            event.preventDefault();
            return;
        }
    }

    // Verifică dacă o metodă de plată este selectată
    const paymentType = document.querySelector('input[name="orderPaymentType"]:checked');
    if (!paymentType) {
        alert("Please select a payment option.");
        event.preventDefault();
        return;
    }

    // Dacă toate validările sunt trecute, permite trimiterea formularului
});

function filterTable() {
    const filter = document.getElementById("filterStatus").value.toLowerCase();
    const search = document.getElementById("searchInput").value.toLowerCase();
    const rows = document.querySelectorAll("#ordersTable tbody tr");

    rows.forEach(row => {
        const status = row.querySelector("td[data-status]").innerText.toLowerCase();
        const textContent = row.innerText.toLowerCase();
        row.style.display = (textContent.includes(search) && (filter === "all" || status === filter)) ? "" : "none";
    });
}

function searchUserPermissions() {
    const search = document.getElementById("searchUserInput").value.toLowerCase();
    const rows = document.querySelectorAll("#userPermissionsTable tbody tr");

    rows.forEach(row => {
        const textContent = row.innerText.toLowerCase();
        row.style.display = textContent.includes(search) ? "" : "none";
    });
}

function updateStatus(orderId) {
    const newStatus = document.getElementById("statusDropdown").value;
    fetch(`/orders/admin/update-order-status`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `orderId=${orderId}&newStatus=${encodeURIComponent(newStatus)}`
    })
        .then(response => response.text())
        .then(data => alert(data))
        .catch(error => console.error('Error:', error));
}

document.querySelector("form").addEventListener("submit", function (event) {
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    // Validarea parolei
    const passwordRegex = /^(?=.*[A-Z])(?=.*\d).{8,}$/;
    if (!passwordRegex.test(password)) {
        event.preventDefault();
        alert("Password must be at least 8 characters long, include at least one uppercase letter, and one number.");
        return;
    }

    // Validarea confirmării parolei
    if (password !== confirmPassword) {
        event.preventDefault();
        alert("Passwords do not match!");
        return;
    }
});