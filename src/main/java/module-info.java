module calve23.moduleoptimizer {
    requires javafx.controls;
    requires javafx.fxml;
    requires tess4j;
    requires opencv;
    requires java.desktop;
    requires java.logging;
    requires com.github.kwhat.jnativehook;


    opens calve23.moduleoptimizer to javafx.fxml;
    exports calve23.moduleoptimizer;
}