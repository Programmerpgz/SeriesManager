package hr.algebra.controllers;

import hr.algebra.exceptions.ValidateException;
import hr.algebra.models.Role;
import hr.algebra.models.User;
import hr.algebra.repository.IUserRepository;
import hr.algebra.repository.RepositoryFactory;
import hr.algebra.utils.AlertUtil;
import hr.algebra.utils.SceneUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController {
    @FXML public Label lblError;
    @FXML private TextField txtName;
    @FXML private TextField txtSurname;
    @FXML private TextField txtEmail;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private final IUserRepository userRepo = RepositoryFactory.users();
    private static final String ERROR_MESSAGE = "Error";

    @FXML
    private void onRegister() {
        String name     = txtName.getText().trim();
        String surname  = txtSurname.getText().trim();
        String email    = txtEmail.getText().trim();
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        log.info("Registration attempt initiated for username: '{}'", username);
        try {
            if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                throw new ValidateException(ERROR_MESSAGE, "Name, surname, email, username and password cannot be empty.");
            }

            String emailValidation = "^.+@.+\\.[a-zA-Z]{2,4}$";

            if (!email.matches(emailValidation)) {
                throw new ValidateException(ERROR_MESSAGE, "Email format is not valid.");
            }

            if (password.length() < 6) {
                throw new ValidateException(ERROR_MESSAGE, "Password must be at least 6 characters long.");
            }
        } catch (ValidateException e) {
            log.warn("Registration form validation failed: {}", e.getMessage());
            AlertUtil.showError(ERROR_MESSAGE, e.getMessage());
            return;
        }

        try {
            if (userRepo.usernameExists(username)) {
                log.warn("Registration rejected: Username '{}' is already taken.", username);
                AlertUtil.showError(ERROR_MESSAGE, "Username " + username + " is already taken.");
                return;
            }
        }catch (Exception e) {
            log.error("Failed to check username existence for '{}': {}", username, e.getMessage(), e);
            AlertUtil.showError(ERROR_MESSAGE, "Failed to check username availability: " + e.getMessage());
            return;
        }

        try {
            User newUser = new User(username, password, name, surname, email, Role.USER);
            userRepo.save(newUser);

            AlertUtil.showInfo("Registration successful.", "Welcome, " + name + "! You can login now!");
            SceneUtil.switchScene(SceneUtil.LOGIN_VIEW, "Login");
        }catch (Exception e) {
            log.error("Error occurred during user registration for username: {}", username, e);
            AlertUtil.showError("Registration Error", "Failed to register user: " + e.getMessage());
        }
    }

    @FXML
    private void onBack() {
        SceneUtil.switchScene(SceneUtil.LOGIN_VIEW, "Login");
    }
}