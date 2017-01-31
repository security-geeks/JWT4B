package app;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.impl.JWTParser;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.Header;
import com.auth0.jwt.interfaces.Payload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/* 
 * This Class is implemented separately to get raw access to the content of the Tokens. 
 * The JWTDecoder class cannot be extended because it is final
 */

public class CustomJWTToken extends JWT {

	private String headerJson;
	private String payloadJson;
	private Header header;
	private Payload payload;
	private byte[] signature;
	private String token;

	public CustomJWTToken(String token) {
		this.token = token;
		final String[] parts = splitToken(token);
		final JWTParser converter = new JWTParser();

		try {
			headerJson = StringUtils.newStringUtf8(Base64.decodeBase64(parts[0]));
			payloadJson = StringUtils.newStringUtf8(Base64.decodeBase64(parts[1]));
		} catch (NullPointerException e) {
			throw new JWTDecodeException("The UTF-8 Charset isn't initialized.", e);
		}
		header = converter.parseHeader(headerJson);
		payload = converter.parsePayload(payloadJson);
		signature = Base64.decodeBase64(parts[2]);

	}

	public CustomJWTToken(String headerJson, String payloadJson, String signature) {
		this.headerJson = headerJson;
		this.payloadJson = payloadJson;
		this.signature = Base64.decodeBase64(signature);
	}

	public String getHeaderJson() {
		return headerJson;
	}

	public String getPayloadJson() {
		return payloadJson;
	}

	public JsonNode getHeaderJsonNode() {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readTree(getHeaderJson());
		} catch (IOException e) {
			return null;
		}
	}
	
	public void setSignature(Algorithm algorithm){ 
		 byte[] contentBytes = String.format("%s.%s", b64(getHeaderJson()), b64(getPayloadJson())).getBytes(StandardCharsets.UTF_8);
		 signature = algorithm.sign(contentBytes);
	}

	public JsonNode getPayloadJsonNode() {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readTree(getPayloadJson());
		} catch (IOException e) {
			return null;
		}
	}

	public void setHeaderJson(String headerJson) {
		this.headerJson = headerJson;
	}

	public void setPayloadJson(String payloadJson) {
		this.payloadJson = payloadJson;
	}

	public void setHeaderJsonNode(JsonNode headerPayloadJson) {
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			this.headerJson = objectMapper.writeValueAsString(headerPayloadJson);
		} catch (JsonProcessingException e) {
		}

	}

	public void setPayloadJsonNode(JsonNode payloadJsonNode) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			this.payloadJson = objectMapper.writeValueAsString(payloadJsonNode);
		} catch (JsonProcessingException e) {
		}
	}

	@Override
	public String getToken() {
		String content = String.format("%s.%s", b64(getHeaderJson()), b64(getPayloadJson()));

		String signatureEncoded = Base64.encodeBase64URLSafeString(this.signature);

		return String.format("%s.%s", content, signatureEncoded);
	}

	private String b64(String input) { 
		return Base64.encodeBase64URLSafeString(input.getBytes(StandardCharsets.UTF_8));
	}
	
	@Override
	public List<String> getAudience() {
		int fail = 2 / 0;
		return null;

	}

	@Override
	public Claim getClaim(String arg0) {
		int fail = 2 / 0;
		return null;
	}

	@Override
	public Map<String, Claim> getClaims() {
		int fail = 2 / 0;
		return null;
	}

	@Override
	public Date getExpiresAt() {
		int fail = 2 / 0;
		return null;
	}

	@Override
	public String getId() {
		int fail = 2 / 0;
		return null;
	}

	@Override
	public Date getIssuedAt() {
		int fail = 2 / 0;
		return null;
	}

	@Override
	public String getIssuer() {
		int fail = 2 / 0;
		return null;
	}

	@Override
	public Date getNotBefore() {
		int fail = 2 / 0;
		return null;
	}

	@Override
	public String getSubject() {
		int fail = 2 / 0;
		return null;
	}

	@Override
	public String getAlgorithm() {
		return getHeaderJsonNode().get("alg").asText();
	}

	@Override
	public String getContentType() {
		return getHeaderJsonNode().get("typ").asText();
	}

	@Override
	public Claim getHeaderClaim(String arg0) {
		int fail = 2 / 0;
		return null;
	}

	@Override
	public String getKeyId() {
		int fail = 2 / 0;
		return null;
	}

	@Override
	public String getType() {
		int fail = 2 / 0;
		return null;
	}

	@Override
	public String getSignature() {
		return Base64.encodeBase64URLSafeString(this.signature);
	}
	
	public void setSignature(String signature) { 
		this.signature = Base64.decodeBase64(signature);
	}

	// method copied from
	// https://github.com/auth0/java-jwt/blob/9148ca20adf679721591e1d012b7c6b8c4913d75/lib/src/main/java/com/auth0/jwt/TokenUtils.java#L14
	// Cannot be reused, it's visibility is protected.
	static String[] splitToken(String token) throws JWTDecodeException {
		String[] parts = token.split("\\.");
		if (parts.length == 2 && token.endsWith(".")) {
			// Tokens with alg='none' have empty String as Signature.
			parts = new String[] { parts[0], parts[1], "" };
		}
		if (parts.length != 3) {
			throw new JWTDecodeException(
					String.format("The token was expected to have 3 parts, but got %s.", parts.length));
		}
		return parts;
	}
}
