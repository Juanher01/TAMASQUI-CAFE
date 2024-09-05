package com.example.demo.Dominio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "cart")
public class Cart implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer amount;

    private BigDecimal unitePrice;

    private BigDecimal totalPrice;

    @JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id") // llave foranea tabla cart
    private Item item;  // item asociado al carrito

    @JsonIgnoreProperties(value = {"user", "carts", "hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_id") //llave foranea tabla cart
    private Shopping shopping;  //compra asociada al carrito

    public void getCart(Cart cart) {
        this.id = cart.getId();
        this.item = cart.getItem();
        this.shopping = cart.getShopping();
        this.amount = cart.getAmount();
        this.totalPrice = cart.getTotalPrice();
        this.unitePrice = cart.getUnitePrice();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getUnitePrice() {
        return unitePrice;
    }

    public void setUnitePrice(BigDecimal unitePrice) {
        this.unitePrice = unitePrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Shopping getShopping() {
        return shopping;
    }

    public void setShopping(Shopping shopping) {
        this.shopping = shopping;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cart)) return false;
        return id != null && id.equals(((Cart)o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", amount=" + amount +
                ", unitePrice=" + unitePrice +
                ", totalPrice=" + totalPrice +
                ", item=" + item +
                ", shopping=" + shopping +
                '}';
    }
}
