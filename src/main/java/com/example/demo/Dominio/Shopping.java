package com.example.demo.Dominio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "shopping")
public class Shopping implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;   //llave primaria

    private LocalDateTime date;

    private String state;

    private BigDecimal totalPrice;

    private Long paymentId;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")  //clave foranea
    private User user;

    @JsonIgnoreProperties(value = {/*"shopping",*/ "hibernateLazyInitializer", "handler"})
    @OneToMany(mappedBy = "shopping", cascade = CascadeType.ALL)
    private List<Cart> carts;   // elementos del carrito de la compra

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<Cart> getCarts() {
        return carts;
    }

    public void setCarts(List<Cart> carts) {
        this.carts = carts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shopping)) return false;
        return id != null && id.equals(((Shopping)o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Shopping{" +
                "id=" + id +
                ", date=" + date +
                ", state='" + state + '\'' +
                ", totalPrice=" + totalPrice +
                ", paymentId=" + paymentId +
                ", user=" + user +
                ", carts=" + carts +
                '}';
    }

}
