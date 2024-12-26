import os
import random
import tkinter as tk
from tkinter import messagebox
from PIL import Image, ImageTk

class CaptchaGUI:
    def __init__(self, root):
        self.root = root
        self.root.title("CAPTCHA Verification")
        self.root.geometry("500x550")

        # Path to the 'static' folder
        self.static_folder = os.path.join(os.path.dirname(__file__), "static")

        # Define sets of images and correct responses
        self.image_sets = {
            "set1": [os.path.join(self.static_folder, f"set1_image{i}.jpg") for i in range(1, 5)],
            "set2": [os.path.join(self.static_folder, f"set2_image{i}.jpg") for i in range(1, 5)],
            "set3": [os.path.join(self.static_folder, f"set3_image{i}.jpg") for i in range(1, 5)],
        }

        self.correct_responses = {
            "set1": os.path.join(self.static_folder, "set1_image1.jpg"),
            "set2": os.path.join(self.static_folder, "set2_image1.jpg"),
            "set3": os.path.join(self.static_folder, "set3_image4.jpg"),
        }

        self.set_order = list(self.image_sets.keys())
        random.shuffle(self.set_order)  # Randomize the order of sets
        self.current_set_index = 0  # Start with the first set in the randomized order

        self.captcha_frame = None
        self.show_captcha()

    def show_captcha(self):
        """Display the CAPTCHA images for the user to solve."""
        if self.captcha_frame:
            self.captcha_frame.destroy()  # Clear previous CAPTCHA frame

        self.captcha_frame = tk.Frame(self.root)
        self.captcha_frame.pack(pady=20)

        tk.Label(self.captcha_frame, text="Which one is different?", font=("Arial", 14)).pack(pady=10)

        # Get the current set of images and shuffle them
        current_set = self.set_order[self.current_set_index]
        image_pool = self.image_sets[current_set]
        correct_image = self.correct_responses[current_set]
        random.shuffle(image_pool)

        # Create a grid frame for images
        grid_frame = tk.Frame(self.captcha_frame)
        grid_frame.pack()

        # Display images in a 2x2 grid
        row, col = 0, 0
        for img_file in image_pool:
            try:
                img = Image.open(img_file)
                img = img.resize((200, 200))  # Resize for display
                photo = ImageTk.PhotoImage(img)

                btn = tk.Button(grid_frame, image=photo, command=lambda f=img_file: self.validate_captcha(f, correct_image))
                btn.image = photo  # Keep reference to avoid garbage collection
                btn.grid(row=row, column=col, padx=10, pady=10)

                col += 1
                if col > 1:  # Move to the next row after 2 images
                    col = 0
                    row += 1
            except Exception as e:
                print(f"Error loading image {img_file}: {e}")

    def validate_captcha(self, selected_image, correct_image):
        """Validate if the user clicked the correct image."""
        if selected_image == correct_image:
            messagebox.showinfo("Success", "CAPTCHA Verified! Login Successful.")
            self.captcha_frame.destroy()
            self.show_success_screen()
        else:
            messagebox.showerror("Error", "Incorrect CAPTCHA. Try again.")
            self.switch_to_next_set()
            self.show_captcha()

    def switch_to_next_set(self):
        """Switch to the next set of images."""
        self.current_set_index = (self.current_set_index + 1) % len(self.set_order)

    def show_success_screen(self):
        """Display success message."""
        success_frame = tk.Frame(self.root)
        success_frame.pack(pady=100)
        tk.Label(success_frame, text="Welcome! You have successfully verified the CAPTCHA.", font=("Arial", 12)).pack(pady=15)
        tk.Button(success_frame, text="Exit", command=self.root.quit).pack(pady=10)
