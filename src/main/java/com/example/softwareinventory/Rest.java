package com.example.softwareinventory;
import javax.persistence.*;
@Entity
@Table(name="phones")
public class Rest {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)	// AUTO_INCREMENT
    private Long id;

    private String brand;

    private String model;

    private Long price;

    private Long year;


    public Rest() {
    }

    public Rest(Long id, String brand, String model, Long price, Long year) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.price = price;
        this.year = year;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }
}
