let captchaId = null;
let token = null;

async function loadCaptcha() {
  const response = await fetch("http://localhost:8080/api/captcha");
  const data = await response.json();
  captchaId = data.id;
  document.getElementById("captchaQuestion").textContent = data.question;
}

async function loadProfile() {
  const response = await fetch("http://localhost:8080/api/profile", {
    method: "GET",
    headers: {
      Authorisation: token,
    },
  });

  if (response.ok) {
    const data = await response.json();

    document.getElementById("profileFirstName").value = data.first_name;
    document.getElementById("profileLastName").value = data.last_name;
    document.getElementById("profileEmail").value = data.email;
  }
}

async function logout() {
  const response = await fetch("http://localhost:8080/api/logout", {
    method: "POST",
    headers: {
      Authorisation: token,
    },
  });

  if (response.ok) {
    token = null;
    showSection("login");
    document.getElementById("loginForm").reset();
    document.getElementById("passwordForm").reset();
    document.getElementById("profileForm").reset();
    alert("Logout successful!");
  } else {
    alert("Logout failed!");
  }
}

loadCaptcha();

function showSection(section) {
  if (section === "register") {
    document.getElementById("register").hidden = false;
    document.getElementById("login").hidden = true;
    document.getElementById("profile").hidden = true;
  } else if (section === "login") {
    document.getElementById("register").hidden = true;
    document.getElementById("login").hidden = false;
    document.getElementById("profile").hidden = true;
  } else if (section === "profile") {
    if (token === null) {
      alert("Please log in first!");
      showSection("login");
      return;
    }

    document.getElementById("register").hidden = true;
    document.getElementById("login").hidden = true;
    document.getElementById("profile").hidden = false;

    loadProfile();
  }
}

document
  .getElementById("registerForm")
  .addEventListener("submit", async function (event) {
    event.preventDefault();

    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const email = document.getElementById("registerEmail").value;
    const password = document.getElementById("registerPassword").value;
    const captchaAnswer = document.getElementById("captchaAnswer").value;

    const response = await fetch("http://localhost:8080/api/register", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        first_name: firstName,
        last_name: lastName,
        email: email,
        password: password,
        captcha_id: captchaId,
        captcha_answer: captchaAnswer,
      }),
    });

    if (response.ok) {
      alert("Registration successful! Please log in.");
      document.getElementById("registerForm").reset();
      showSection("login");
    } else {
      alert("Registration failed!");
    }
  });

document
  .getElementById("loginForm")
  .addEventListener("submit", async function (event) {
    event.preventDefault();

    const email = document.getElementById("loginEmail").value;
    const password = document.getElementById("loginPassword").value;

    const response = await fetch("http://localhost:8080/api/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email: email,
        password: password,
      }),
    });

    if (response.ok) {
      const data = await response.json();
      token = data.token;
      showSection("profile");
    } else {
      alert("Login failed!");
    }
  });

document
  .getElementById("profileForm")
  .addEventListener("submit", async function (event) {
    event.preventDefault();

    const firstName = document.getElementById("profileFirstName").value;
    const lastName = document.getElementById("profileLastName").value;
    const email = document.getElementById("profileEmail").value;

    const response = await fetch("http://localhost:8080/api/profile", {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorisation: token,
      },
      body: JSON.stringify({
        first_name: firstName,
        last_name: lastName,
        email: email,
        change_password: false,
      }),
    });

    if (response.ok) {
      alert("Profile updated successfully!");
    } else {
      alert("Profile update failed!");
    }
  });

document
  .getElementById("passwordForm")
  .addEventListener("submit", async function (event) {
    event.preventDefault();

    const currentPassword = document.getElementById("currentPassword").value;
    const newPassword = document.getElementById("newPassword").value;

    const response = await fetch("http://localhost:8080/api/profile", {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorisation: token,
      },
      body: JSON.stringify({
        first_name: document.getElementById("profileFirstName").value,
        last_name: document.getElementById("profileLastName").value,
        email: document.getElementById("profileEmail").value,
        change_password: true,
        current_password: currentPassword,
        new_password: newPassword,
      }),
    });

    if (response.ok) {
      alert("Password changed successfully!");
      document.getElementById("passwordForm").reset();
    } else {
      alert("Password change failed!");
    }
  });
