module hr.algebra {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.naming;
    requires java.desktop;
    requires org.slf4j;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.xml;
    requires com.fasterxml.jackson.annotation;
    requires org.postgresql.jdbc;

    exports hr.algebra.main;
    exports hr.algebra.controllers;
    exports hr.algebra.models;
    exports hr.algebra.utils;

    opens hr.algebra.utils to com.fasterxml.jackson.databind, javafx.fxml;
    opens hr.algebra.models to com.fasterxml.jackson.databind, javafx.base, javafx.fxml;
    opens hr.algebra.controllers to javafx.fxml;
    opens hr.algebra.main to javafx.fxml;
}
