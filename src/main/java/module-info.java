module Test{
    requires javafx.controls;
    requires javafx.fxml;
    requires  javafx.graphics;
    requires itextpdf;
    requires java.sql;

    requires org.json;
    requires spring.security.core;


    requires java.net.http;
    requires java.mail;
    requires org.apache.commons.text;
    opens controllers;
    opens models;
    opens services;
    opens test;
        }