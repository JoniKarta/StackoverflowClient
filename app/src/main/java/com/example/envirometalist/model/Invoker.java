package com.example.envirometalist.model;

public class Invoker {
    private String email;

    public Invoker() {
        super();
    }

    public Invoker(String email) {
        super();
        this.email = email;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    @Override
    public String toString() {
        return "Invoker [email=" + email + "]";
    }

}
