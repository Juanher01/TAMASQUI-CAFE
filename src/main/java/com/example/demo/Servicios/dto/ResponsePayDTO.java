package com.example.demo.Servicios.dto;

public class ResponsePayDTO {

    private Data data;

    private String type;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ResponsePayDTO{" +
                "data=" + data +
                ", type='" + type + '\'' +
                '}';
    }

    public static class Data{

        private String id;

        public String getId() {return id;}

        public void setId(String id) {this.id = id;}
    }
}




