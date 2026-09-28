package com.app.novastore.security.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericAuthToken {

    private String token;

    private TokenType tokenType;

    private String refreshToken;

    @JsonProperty("id_token")
    public String getIdToken() {
        return token;
    }
}
