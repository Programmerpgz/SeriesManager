package hr.algebra.controllers;

import hr.algebra.models.Role;
import hr.algebra.models.User;
import hr.algebra.repository.IUserRepository;
import hr.algebra.repository.RepositoryFactory;
import hr.algebra.utils.AlertUtil;
import hr.algebra.utils.SceneUtil;
import hr.algebra.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final IUserRepository userRepo = RepositoryFactory.users();
    private static final String ERROR_MESSAGE = "Error";

    @FXML
    private void onLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            log.warn("Error: Username or password field was empty.");
            AlertUtil.showError(ERROR_MESSAGE,"Please enter your username and password.");
            return;
        }
        try {
            Optional<User> userOpt = userRepo.getByUsername(username);

            if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(password)) {
                log.warn("Login failed for user '{}': Invalid credentials.", username);
                AlertUtil.showError("Login Failed", "Invalid username or password.");

                txtPassword.clear();
                return;
            }

            User user = userOpt.get();
            SessionManager.login(user);
            log.info("User '{}' logged in with role '{}'", user.getFullName(), user.getRole());

            if (user.getRole() == Role.ADMINISTRATOR) {
                log.info("Redirecting admin user '{}' to Admin Dashboard.", username);
                SceneUtil.switchScene(SceneUtil.ADMIN_DASHBOARD, "Admin Maintenance");
            } else {
                log.info("Redirecting regular user '{}' to Series Catalog.", username);
                SceneUtil.switchScene(SceneUtil.MAIN_VIEW, "Series Catalog");
            }
        } catch (Exception e){
            log.error("Error occurred during login process", e);
            AlertUtil.showError(ERROR_MESSAGE, "Error occurred while trying to login");
        }
    }

    @FXML
    private void onRegister() {
        try {
            SceneUtil.switchScene(SceneUtil.REGISTER_VIEW, "Registration");
        }catch (Exception e){
            log.error("Failed to switch to registration scene", e);
            AlertUtil.showError(ERROR_MESSAGE, "Failed to switch to open registration form");
        }
    }
}

