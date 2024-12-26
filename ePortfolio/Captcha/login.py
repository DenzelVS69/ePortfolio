import tkinter as tk
from tkinter import messagebox
from captcha import CaptchaGUI  # Import the Captcha class

class LoginGUI:
    def __init__(self, root):
        self.root = root
        self.root.title("Login")
        self.root.geometry("400x300")

        # Simulate stored credentials
        self.credentials = {"user1": "password1", "admin": "adminpass"}
        self.login_attempts = 0
        self.max_attempts = 3

        # Create Login Form
        tk.Label(root, text="Login", font=("Arial", 18)).pack(pady=10)
        tk.Label(root, text="Username:").pack()
        self.username_entry = tk.Entry(root, width=30)
        self.username_entry.pack(pady=5)
        tk.Label(root, text="Password:").pack()
        self.password_entry = tk.Entry(root, width=30, show="*")
        self.password_entry.pack(pady=5)

        tk.Button(root, text="Login", command=self.validate_login).pack(pady=20)

    def validate_login(self):
        """Validate username and password."""
        username = self.username_entry.get()
        password = self.password_entry.get()

        if self.login_attempts >= self.max_attempts:
            messagebox.showerror("Too Many Attempts", "You have exceeded the maximum login attempts.")
            self.root.destroy()
            return

        if username in self.credentials and self.credentials[username] == password:
            self.root.destroy()
            captcha_root = tk.Tk()
            CaptchaGUI(captcha_root)  # Open the CAPTCHA GUI
            captcha_root.mainloop()
        else:
            self.login_attempts += 1
            remaining_attempts = self.max_attempts - self.login_attempts
            messagebox.showerror("Login Failed", f"Invalid credentials. Attempts remaining: {remaining_attempts}")

if __name__ == "__main__":
    root = tk.Tk()
    LoginGUI(root)
    root.mainloop()
