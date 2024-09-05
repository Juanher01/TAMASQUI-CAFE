package com.example.demo.Configuracion;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application", ignoreInvalidFields = true)
public class ApplicationProperties {
    private final Security security = new Security();

    //mercado pago
    private final MercadoPago mercadoPago = new MercadoPago();

    public MercadoPago getMercadoPago() {
        return mercadoPago;
    }

    public Security getSecurity() {
        return security;
    }

    public static class Security {

        private final Authentication authentication = new Authentication();

        public Authentication getAuthentication() {
            return authentication;
        }

        // Clase estática que maneja la configuración de seguridad.
        public static class Authentication {

            private final Jwt jwt = new Jwt();

            public Jwt getJwt(){
                return jwt;
            }

            //Clase estatica que maneja la configuracion de jwt
            public static class Jwt {

                private String base64secret = "";

                private long tokenValidityInSeconds = 1800;

                public String getBase64secret() {
                    return base64secret;
                }

                private String secret = "";

                public String getSecret() {
                    return secret;
                }

                public void setSecret(String secret) {
                    this.secret = secret;
                }

                public void setBase64secret(String base64secret) {
                    this.base64secret = base64secret;
                }

                public long getTokenValidityInSeconds() {
                    return tokenValidityInSeconds;
                }

                public void setTokenValidityInSeconds(long tokenValidityInSeconds) {
                    this.tokenValidityInSeconds = tokenValidityInSeconds;
                }
            }
        }
    }

    //clase estatica que maneja la configuración de mercado pago.
    public static class MercadoPago {
        private String accessToken;
        public String getAccessToken() {
            return accessToken;
        }
        public void setAccessToken(String accessToken){
            this.accessToken = accessToken;
        }
    }

}
