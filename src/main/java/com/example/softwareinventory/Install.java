package com.example.softwareinventory;

import javax.persistence.*;
import java.sql.Date;

public class Install {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    //@Index()
    @Column(name = "gepid")
    private Integer computerId;

    @Column(name = "szoftverid")
    private Integer softwareId;

    @Column(name = "verzio")
    private String version;

    @Column(name = "datum", nullable = true)
    private Date date;

    @ManyToOne
    @JoinColumn(
            name = "GEPID", referencedColumnName = "ID",
            insertable = false, updatable = false
    )
    private Comp computer;

    @ManyToOne
    @JoinColumn(
            name = "SZOFTVERID", referencedColumnName = "ID",
            insertable = false, updatable = false
    )
    private Software software;

    private Integer installCount_Notebook;

    private Integer installCount_Desktop;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getComputerId() {
        return computerId;
    }

    public void setComputerId(Integer computerId) {
        this.computerId = computerId;
    }

    public Integer getSoftwareId() {
        return softwareId;
    }

    public void setSoftwareId(Integer softwareId) {
        this.softwareId = softwareId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Comp getComputer() {
        return computer;
    }

    public void setComputer(Comp computer) {
        this.computer = computer;
    }

    public Software getSoftware() {
        return software;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }

    public Integer getInstallCount_Notebook() {
        return installCount_Notebook;
    }

    public void setInstallCount_Notebook(Integer installCount_Notebook) {
        this.installCount_Notebook = installCount_Notebook;
    }

    public Integer getInstallCount_Desktop() {
        return installCount_Desktop;
    }

    public void setInstallCount_Desktop(Integer installCount_Desktop) {
        this.installCount_Desktop = installCount_Desktop;
    }
}
