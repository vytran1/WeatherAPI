package com.weatherapi.weatherforecast.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.JwtClaimsSet.Builder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.weatherapi.weatherforecast.clientapp.ClientAppRepository;
import com.weatherapi.weatherforecast.common.ClientApp;

@Configuration
@Profile("production")
public class AuthorizationServerConfig {

	private final RsaKeyProperties rsaKeys;
	
	
	@Value("${app.security.jwt.issuer}")
	private String issueerName;
	
	@Value("${app.security.jwt.access-token.expirations}")
	private int accessTokenExpirationTime;
	
	
	public AuthorizationServerConfig(RsaKeyProperties rsaKeys) {
		super();
		this.rsaKeys = rsaKeys;
	}
	
	
	
	@Bean
	JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
	}
	
	@Bean
	JwtEncoder jwtEncoder() {
		JWK jwk = new RSAKey.Builder(rsaKeys.publicKey()).privateKey(rsaKeys.privateKey()).build();
		JWKSource<SecurityContext> jwtSource = new ImmutableJWKSet<>(new JWKSet(jwk));
		return new NimbusJwtEncoder(jwtSource);
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
		
		return http.build();
	}
	
	@Bean
	RegisteredClientRepository registeredClientRepository(ClientAppRepository clientAppRepository) {
		return new RegisteredClientRepository() {
			
			@Override
			public void save(RegisteredClient registeredClient) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public RegisteredClient findById(String id) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public RegisteredClient findByClientId(String clientId) {
				// TODO Auto-generated method stub
				Optional<ClientApp> result = clientAppRepository.findByClientId(clientId);
				if(result.isEmpty()) {
					return null;
				}
				ClientApp clientApp = result.get();
				
				return RegisteredClient.withId(clientApp.getId().toString())
						.clientName(clientApp.getName())
						.clientId(clientApp.getClientId())
						.clientSecret(clientApp.getClientSecret())
						.scope(clientApp.getRole().toString())
						.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
						.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
						.build();
			}
		};
	}
	
	@Bean
	OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(){
		return (context) -> {
			if(OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
				RegisteredClient registeredClient = context.getRegisteredClient();
				Builder builder = context.getClaims();
				builder.issuer(issueerName);
				builder.expiresAt(Instant.now().plus(accessTokenExpirationTime,ChronoUnit.MINUTES));
				builder.claims((claim) -> {
					claim.put("scope",registeredClient.getScopes());
					claim.put("name",registeredClient.getClientName());
					claim.remove("aud");
				});
			}
		};
	}
	
}
