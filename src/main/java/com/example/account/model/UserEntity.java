package com.example.account.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String username;
    private String password;
    private String nickname;
    
    public UserEntity() {}
    
    public UserEntity(String username, String password, String nickname){
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }
}
