package com.example.account.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JwtRequest implements Serializable{
    private static final long serialVersionUID = 5926468583005150707L;

    private String username;
    private String password;
    private String nickname;
}
