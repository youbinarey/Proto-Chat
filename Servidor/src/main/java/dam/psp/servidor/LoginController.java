package dam.psp.servidor;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import net.synedra.validatorfx.Validator;

public class LoginController {
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;

    private Validator validator;

    @FXML
    public void initialize() {
        validator = new Validator();

        // Validación para el usuario (no vacío)
        validator.createCheck()
                .dependsOn("usuario", txtUsuario.textProperty())
                .withMethod(c -> {
                    String usuario = (String) c.get("usuario");

                    if (usuario.isEmpty()) {
                        c.error("El usuario no puede estar vacío");
                    }
                })
                .decorates(txtUsuario) // Resalta el campo si hay error
                .immediate(); // Valida en tiempo real

        // Validación para la contraseña (mínimo 6 caracteres)
        validator.createCheck()
                .dependsOn("password", txtPassword.textProperty())
                .withMethod(c -> {
                    String password = (String) c.get("password");
                    if (password.length() < 6) {
                        c.error("La contraseña debe tener al menos 6 caracteres");
                    }
                })
                .decorates(txtPassword)
                .immediate();

        // Deshabilitar el botón hasta que no haya errores
        btnLogin.disableProperty().bind(validator.containsErrorsProperty());
    }
}
