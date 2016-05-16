package edu.sfsu.napkin.api;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.auth.oauth.OAuthSigner;
import com.google.api.client.http.GenericUrl;

import edu.sfsu.napkin.Recipe;

/** Constructs a request URL w/params & builds oauth headers if necessary **/
public abstract class RequestBuilder {
    protected String baseUrl;
    protected String authString;
    protected boolean oauthRequired;
    protected TreeMap<String, String> oauthParams;
    protected OAuthSigner signingKey;

    public RequestBuilder() {
        oauthRequired = false;
        baseUrl = "";
        authString = "";
    }

    /**
     * Builds a valid URL from the given params
     * @param endpoint The endpoint to use for the url. i.e /api/search/
     * @param params A map with URL parameters as keys, and their values as values
     * @return A valid URL based off of the parameters & endpoint
     * @throws UnsupportedEncodingException
     */
    protected String buildQueryString(String endpoint, Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder query = new StringBuilder(baseUrl).append(endpoint).append("?");

        for (Map.Entry kv: params.entrySet()) {
            query.append(kv.getKey()).append("=").append(URLEncoder.encode(kv.getValue().toString(), "UTF-8")).append("&");
        }

        query.deleteCharAt(query.length() - 1);
        return query.toString();
    }

    /**
     * Builds a valid URL for the given ID
     * @param endpoint The endpoint to use for the url. i.e /api/search/
     * @param id An ID for the specific resource to be queried
     * @return A valid URL based off of the ID & endpoint
     * @throws UnsupportedEncodingException
     */
    protected String buildQueryString(String endpoint, String id) throws UnsupportedEncodingException {
        StringBuilder query = new StringBuilder(baseUrl).append(endpoint)
                                                        .append("/")
                                                        .append(id)
                                                        .append("?")
                                                        .append(authString);

        return query.toString();
    }

    /**
     * Used for building a valid header for OAuth APIs
     * @param httpMethod The HTTP method to use
     * @param url The URL to use
     * @return A valid header
     * @throws UnsupportedEncodingException
     * @throws GeneralSecurityException
     */
    public String buildOauthHeader(String httpMethod, String url) throws UnsupportedEncodingException, GeneralSecurityException {
        OAuthParameters oauthClientParams = new OAuthParameters();
        GenericUrl genericUrl = new GenericUrl(url);

        oauthClientParams.signatureMethod = oauthParams.get("oauth_signature_method");
        oauthClientParams.consumerKey = oauthParams.get("oauth_consumer_key");
        oauthClientParams.token = oauthParams.get("oauth_token");
        oauthClientParams.version = oauthParams.get("oauth_version");
        oauthClientParams.signer = signingKey;
        oauthClientParams.computeNonce();
        oauthClientParams.computeTimestamp();
        oauthClientParams.computeSignature(httpMethod, genericUrl);

        return oauthClientParams.getAuthorizationHeader();
    }
}
