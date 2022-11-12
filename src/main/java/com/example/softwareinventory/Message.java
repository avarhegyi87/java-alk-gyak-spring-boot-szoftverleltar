package com.example.softwareinventory;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.*;
import java.sql.Time;

public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "felhasznalo_id", nullable = true)
    private Integer userId;

    @Column(name = "felhasznalo_nev", nullable = true)
    private String userName;

    @Column(name = "ido")
    private Time time;

    @NotNull
    @Pattern(regexp = "(^(install|report_error|other_msg)$)")
    @Column(name = "uzenet_tipus")
    private String messageType;

    @NotNull
    @Size(max=255)
    @Column(name = "uzenet", nullable = false)
    private String messageText;

    public Integer getId() {
        return id;
    }

    public static String getTextForMessageType(String value) {
        switch (value) {
            case "install":return "Telepítés kérése";
            case "report_error":return "Hibabejelentés";
            default:return "Egyéb üzenet";
        }
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}
