package com.example.demo.Servicios.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public class CheckoutDTO {

    @JsonProperty("shopId")
    private Long shopId;
    private String status;

    private Long paymentId;

    public CheckoutDTO() {

    }

    public CheckoutDTO(Long shopId, String status, Long paymentId){
        this.shopId = shopId;
        this.status = status;
        this.paymentId = paymentId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CheckoutDTO{" +
                "shopId=" + shopId +
                ", status='" + status + '\'' +
                ", paymentId=" + paymentId +
                '}';
    }
}