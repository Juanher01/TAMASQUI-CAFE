package com.example.demo.Servicios.dto;

public class CreateCarDTO {

    private Long productId;

    private Integer amount;

    private Long shoppingId;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Long getShoppingId() {
        return shoppingId;
    }

    public void setShoppingId(Long shoppingId) {
        this.shoppingId = shoppingId;
    }

    @Override
    public String toString() {
        return "CreateCarDTO{" +
                "productId=" + productId +
                ", amount=" + amount +
                ", shoppingId=" + shoppingId +
                '}';
    }
}
