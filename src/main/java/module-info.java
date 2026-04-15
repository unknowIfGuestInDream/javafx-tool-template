module com.tlcsdm.fxtemplate {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.management;
    requires java.prefs;

    requires atlantafx.base;
    requires com.dlsc.preferencesfx;
    requires org.slf4j;

    exports com.tlcsdm.fxtemplate;
    opens com.tlcsdm.fxtemplate.controller to javafx.fxml;
}
