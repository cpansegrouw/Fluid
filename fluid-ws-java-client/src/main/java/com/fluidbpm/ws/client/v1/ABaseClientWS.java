/*
 * Koekiebox CONFIDENTIAL
 *
 * [2012] - [2017] Koekiebox (Pty) Ltd
 * All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property
 * of Koekiebox and its suppliers, if any. The intellectual and
 * technical concepts contained herein are proprietary to Koekiebox
 * and its suppliers and may be covered by South African and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly
 * forbidden unless prior written permission is obtained from Koekiebox.
 */

package com.fluidbpm.ws.client.v1;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.GitDescribe;
import com.fluidbpm.program.api.util.UtilGlobal;
import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;
import com.fluidbpm.program.api.vo.ws.Error;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;

/**
 * Base class for all REST related calls.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see JSONObject
 * @see HttpEntity
 * @see HttpResponse
 * @see HttpClient
 * @see ContentType
 * @see StringEntity
 * @see WS
 */
public abstract class ABaseClientWS {

    //public static final String APPLICATION_JSON_CHARSET_UTF8 = "application/json; charset=UTF-8";

    public static final String CONTENT_TYPE_HEADER = "Content-type";

    //Protected variables used by subclasses...
    protected String endpointUrl = "https://localhost:8443/fluid-ws/";
    protected String serviceTicket;

    private static String EQUALS = "=";
    private static String AMP = "&";

    private static String REGEX_AMP = "\\&";
    private static String REGEX_EQUALS = "\\=";

    public static boolean IS_IN_JUNIT_TEST_MODE = false;

    public static String SYSTEM_PROP_FLUID_TRUST_STORE = "fluid.httpclient.truststore";
    public static String SYSTEM_PROP_FLUID_TRUST_STORE_PASSWORD = "fluid.httpclient.truststore.password";

    private CloseableHttpClient closeableHttpClient;

    /**
     * The HTML Form Name and Value mapping.
     */
    public static class FormNameValue{
        private String name;
        private String value;

        /**
         * Sets the HTML name and value.
         *
         * @param nameParam The HTML name.
         * @param valueParam The HTML value.
         */
        public FormNameValue(String nameParam, String valueParam) {
            this.name = nameParam;
            this.value = valueParam;
            this.value = ABaseClientWS.encodeParam(this.value);
        }

        /**
         * Gets the Form Param Name.
         *
         * @return The Form Param Name.
         */
        public String getName() {
            return this.name;
        }

        /**
         * Gets the Form Param Value.
         *
         * @return The Form Param Value.
         */
        public String getValue() {
            return value;
        }
    }

    /**
     * The HTML Header Name and Value mapping.
     */
    public static class HeaderNameValue{
        private String name;
        private String value;

        /**
         * Sets the HTML Header name and value.
         *
         * @param nameParam The HTML header name.
         * @param valueParam The HTML header value.
         */
        public HeaderNameValue(String nameParam, String valueParam) {
            this.name = nameParam;
            this.value = valueParam;
        }

        /**
         * Gets the Form Param Name.
         *
         * @return The Form Param Name.
         */
        public String getName() {
            return this.name;
        }

        /**
         * Gets the Form Param Value.
         *
         * @return The Form Param Value.
         */
        public String getValue() {
            return value;
        }
    }

    /**
     * The HTTP Method type to use.
     *
     * See: https://en.wikipedia.org/wiki/Hypertext_Transfer_Protocol#Request_methods
     */
    private enum HttpMethod
    {
        GET,
        POST,
        PUT,
        DELETE
    }

    /**
     * Creates a new client and sets the Base Endpoint URL.
     *
     * @param endpointBaseUrlParam URL to base endpoint.
     */
    public ABaseClientWS(String endpointBaseUrlParam) {
        super();

        if(endpointBaseUrlParam == null || endpointBaseUrlParam.trim().isEmpty())
        {
            this.endpointUrl = "https://localhost:8443/fluid-ws/";
        }
        else
        {
            this.endpointUrl = endpointBaseUrlParam;
        }
    }

    /**
     * Performs an HTTP request with {@code postfixUrlParam} on {@code httpClientParam}.
     *
     * @param httpClientParam The Apache Http Client to use.
     * @param httpUriRequestParam The Apache URI Request.
     * @param responseHandlerParam The response from the request handler.
     * @param postfixUrlParam URL mapping after the Base endpoint.
     * @return Return body as JSON.
     *
     * @see HttpClient
     * @see HttpUriRequest
     * @see ResponseHandler
     */
    private String executeHttp(
            HttpClient httpClientParam,
            HttpUriRequest httpUriRequestParam,
            ResponseHandler responseHandlerParam,
            String postfixUrlParam)
    {
        try {
            Object returnedObj = httpClientParam.execute(httpUriRequestParam, responseHandlerParam);
            if(returnedObj instanceof String)
            {
                return (String)returnedObj;
            }

            throw new FluidClientException(
                    "Expected 'String' got '"+(
                            (returnedObj == null) ? null:returnedObj.getClass().getName()),
                    FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
        }
        //IO Problem...
        catch (IOException e) {

            if(e instanceof UnknownHostException)
            {
                throw new FluidClientException(
                        "Unable to reach host '"+
                                this.endpointUrl.concat(postfixUrlParam)+"'. "+e.getMessage(),
                        e, FluidClientException.ErrorCode.CONNECT_ERROR);
            }

            if(e instanceof ConnectException)
            {
                throw new FluidClientException(e.getMessage(),
                        e, FluidClientException.ErrorCode.CONNECT_ERROR);
            }

            throw new FluidClientException(
                    e.getMessage(),
                    e, FluidClientException.ErrorCode.IO_ERROR);
        }
    }

    /**
     * Performs an HTTP-GET request with {@code postfixUrlParam}.
     *
     * @param postfixUrlParam URL mapping after the Base endpoint.
     * @return Return body as JSON.
     *
     * @see JSONObject
     */
    public JSONObject getJson(
            String postfixUrlParam) {

        return this.getJson(false, postfixUrlParam);
    }

    /**
     * Performs an HTTP-GET request with {@code postfixUrlParam}.
     *
     * @param skipCheckConnectionValidParam Skip to check if connection to
     *                                      base endpoint is valid.
     * @param postfixUrlParam URL mapping after the Base endpoint.
     *
     * @return Return body as JSON.
     *
     * @see JSONObject
     */
    public JSONObject getJson(
            boolean skipCheckConnectionValidParam,
            String postfixUrlParam) {

        return this.getJson(
                skipCheckConnectionValidParam,
                postfixUrlParam, null);
    }

    /**
     * Performs an HTTP-GET request with {@code postfixUrlParam}.
     *
     * @param skipCheckConnectionValidParam Skip to check if connection to
     *                                      base endpoint is valid.
     * @param postfixUrlParam URL mapping after the Base endpoint.
     * @param headerNameValuesParam The HTTP Headers to include.
     *
     * @return Return body as JSON.
     *
     * @see JSONObject
     */
    public JSONObject getJson(
            boolean skipCheckConnectionValidParam,
            String postfixUrlParam,
            List<HeaderNameValue> headerNameValuesParam) {

        //
        if(!skipCheckConnectionValidParam && !this.isConnectionValid())
        {
            throw new FluidClientException(
                    "Unable to reach service at '"+
                            this.endpointUrl.concat(postfixUrlParam)+"'.",
                    FluidClientException.ErrorCode.CONNECT_ERROR);
        }

        CloseableHttpClient httpclient = this.getClient();

        try {
            HttpGet httpGet = new HttpGet(this.endpointUrl.concat(postfixUrlParam));

            if(headerNameValuesParam != null && !headerNameValuesParam.isEmpty())
            {
                for(HeaderNameValue headerNameVal : headerNameValuesParam)
                {
                    if(headerNameVal.getName() == null || headerNameVal.getName().trim().isEmpty())
                    {
                        continue;
                    }

                    if(headerNameVal.getValue() == null)
                    {
                        continue;
                    }

                    httpGet.setHeader(headerNameVal.getName(), headerNameVal.getValue());
                }
            }

            // Create a custom response handler
            ResponseHandler<String> responseHandler = this.getJsonResponseHandler(
                    this.endpointUrl.concat(postfixUrlParam));

            String responseBody = this.executeHttp(
                    httpclient, httpGet, responseHandler, postfixUrlParam);

            if(responseBody == null || responseBody.trim().isEmpty())
            {
                throw new FluidClientException(
                        "No response data from '"+
                                this.endpointUrl.concat(postfixUrlParam)+"'.",
                        FluidClientException.ErrorCode.IO_ERROR);
            }

            JSONObject jsonOjb = new JSONObject(responseBody);

            if(jsonOjb.isNull(Error.JSONMapping.ERROR_CODE))
            {
                return jsonOjb;
            }

            int errorCode = jsonOjb.getInt(Error.JSONMapping.ERROR_CODE);

            if(errorCode > 0)
            {
                String errorMessage = (jsonOjb.isNull(Error.JSONMapping.ERROR_MESSAGE)
                        ? "Not set":
                        jsonOjb.getString(Error.JSONMapping.ERROR_MESSAGE));

                throw new FluidClientException(errorMessage, errorCode);
            }

            return jsonOjb;
        }
        //
        catch (JSONException jsonExcept)
        {
            throw new FluidClientException(jsonExcept.getMessage(),
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Performs an HTTP-POST request with {@code postfixUrlParam}.
     *
     * @param baseDomainParam The base domain to convert to JSON and POST
     *                        to {@code this} endpoint.
     * @param postfixUrlParam URL mapping after the Base endpoint.
     * @return Return body as JSON.
     *
     * @see JSONObject
     * @see ABaseFluidJSONObject
     */
    protected JSONObject postJson(
            ABaseFluidJSONObject baseDomainParam,
            String postfixUrlParam) {

        //No need to check connection...
        return this.postJson(false, baseDomainParam,postfixUrlParam);
    }

    /**
     * Performs an HTTP-POST request with {@code postfixUrlParam}.
     *
     * @param checkConnectionValidParam Check if connection to base endpoint is valid.
     * @param baseDomainParam The base domain to convert to JSON and POST
     *                        to {@code this} endpoint.
     * @param postfixUrlParam URL mapping after the Base endpoint.
     * @return Return body as JSON.
     *
     * @see JSONObject
     * @see ABaseFluidJSONObject
     */
    protected JSONObject postJson(
            boolean checkConnectionValidParam,
            ABaseFluidJSONObject baseDomainParam,
            String postfixUrlParam) {

        return this.executeJson(
                HttpMethod.POST,
                checkConnectionValidParam,
                baseDomainParam,
                ContentType.APPLICATION_JSON,
                postfixUrlParam);
    }

    /**
     * Performs an HTTP-DELETE request with {@code postfixUrlParam}.
     *
     * @param baseDomainParam The base domain to convert to JSON and DELETE
     *                        to {@code this} endpoint.
     * @param postfixUrlParam URL mapping after the Base endpoint.
     * @return Return body as JSON.
     *
     * @see JSONObject
     */
    protected JSONObject deleteJson(
            ABaseFluidJSONObject baseDomainParam,
            String postfixUrlParam) {

        //No need to check connection...
        return this.deleteJson(false, baseDomainParam, postfixUrlParam);
    }

    /**
     * Performs an HTTP-DELETE request with {@code postfixUrlParam}.
     *
     * @param checkConnectionValidParam Check if connection to base endpoint is valid.
     * @param baseDomainParam The base domain to convert to JSON and DELETE
     *                        to {@code this} endpoint.
     * @param postfixUrlParam URL mapping after the Base endpoint.
     * @return Return body as JSON.
     *
     * @see JSONObject
     */
    protected JSONObject deleteJson(
            boolean checkConnectionValidParam,
            ABaseFluidJSONObject baseDomainParam,
            String postfixUrlParam) {

        return this.executeJson(
                HttpMethod.DELETE,
                checkConnectionValidParam,
                baseDomainParam,
                ContentType.APPLICATION_JSON,
                postfixUrlParam);
    }

    /**
     * Performs an HTTP-POST request with {@code postfixUrlParam} making use of
     * form params as {@code formNameValuesParam}.
     *
     * @param checkConnectionValidParam Check if connection to base endpoint is valid.
     * @param formNameValuesParam The name and value pairs of form data.
     * @param postfixUrlParam URL mapping after the Base endpoint.
     * @return Return body as JSON.
     */
    protected JSONObject postForm(
            boolean checkConnectionValidParam,
            List<FormNameValue> formNameValuesParam,
            String postfixUrlParam) {

        return this.executeForm(
                HttpMethod.POST,
                checkConnectionValidParam,
                formNameValuesParam,
                ContentType.APPLICATION_FORM_URLENCODED,
                postfixUrlParam);
    }

    /**
     * Performs an HTTP-PUT request with {@code postfixUrlParam}.
     *
     * @param checkConnectionValidParam Check if connection to base endpoint is valid.
     * @param baseDomainParam The JSON object to submit to endpoint as PUT.
     * @param postfixUrlParam URL mapping after the Base endpoint.
     * @return Return body as JSON.
     */
    protected JSONObject putJson(
            boolean checkConnectionValidParam,
            ABaseFluidJSONObject baseDomainParam,
            String postfixUrlParam) {

        return this.executeJson(
                HttpMethod.PUT,
                checkConnectionValidParam,
                baseDomainParam,
                ContentType.APPLICATION_JSON,
                postfixUrlParam);
    }

    /**
     * Performs an HTTP-PUT request with {@code postfixUrlParam}.
     *
     * @param baseDomainParam The JSON object to submit to endpoint as PUT.
     * @param postfixUrlParam URL mapping after the Base endpoint.
     * @return Return body as JSON.
     */
    protected JSONObject putJson(
            ABaseFluidJSONObject baseDomainParam,
            String postfixUrlParam) {

        //Create without connection check...
        return this.putJson(false, baseDomainParam, postfixUrlParam);
    }

    /**
     * Submit a JSON based HTTP request body with JSON as a response.
     *
     * @param httpMethodParam The HTTP method to use.
     * @param checkConnectionValidParam Check if connection to base endpoint is valid.
     * @param baseDomainParam The object to convert to JSON and submit as {@code httpMethodParam}.
     * @param contentTypeParam The Mime / Content type to submit as.
     * @param postfixUrlParam URL mapping after the Base endpoint.
     * @return Return body as JSON.
     *
     * @see HttpMethod
     * @see JSONObject
     * @see ContentType
     * @see ABaseFluidJSONObject
     */
    protected JSONObject executeJson(
            HttpMethod httpMethodParam,
            boolean checkConnectionValidParam,
            ABaseFluidJSONObject baseDomainParam,
            ContentType contentTypeParam,
            String postfixUrlParam) {

        //Validate that something is set.
        if(baseDomainParam == null)
        {
            throw new FluidClientException("No JSON body to post.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        String bodyJsonString = baseDomainParam.toJsonObject().toString();

        return this.executeString(httpMethodParam,checkConnectionValidParam,
                bodyJsonString, contentTypeParam, postfixUrlParam);
    }

    /**
     * Submit a HTML Form based HTTP request body with JSON as a response.
     *
     * @param httpMethodParam The HTTP method to use.
     * @param checkConnectionValidParam Check if connection to base endpoint is valid.
     * @param formNameValuesParam The Form name and value pairs.
     * @param contentTypeParam The Mime / Content type to submit as.
     * @param postfixUrlParam URL mapping after the Base endpoint.
     *
     * @return Return body as JSON.
     *
     * @see HttpMethod
     * @see JSONObject
     * @see ContentType
     * @see ABaseFluidJSONObject
     */
    protected JSONObject executeForm(
            HttpMethod httpMethodParam,
            boolean checkConnectionValidParam,
            List<FormNameValue> formNameValuesParam,
            ContentType contentTypeParam,
            String postfixUrlParam) {

        //
        if(formNameValuesParam == null || formNameValuesParam.isEmpty())
        {
            throw new FluidClientException("No 'Name and Value' body to post.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        StringBuilder strBuilder = new StringBuilder();

        for(FormNameValue nameValue : formNameValuesParam)
        {
            if(nameValue.getName() == null || nameValue.getName().trim().isEmpty())
            {
                continue;
            }

            if(nameValue.getValue() == null)
            {
                continue;
            }

            strBuilder.append(nameValue.getName());
            strBuilder.append(EQUALS);
            strBuilder.append(nameValue.getValue());
            strBuilder.append(AMP);
        }

        String bodyJsonString = strBuilder.toString();
        bodyJsonString = bodyJsonString.substring(0, bodyJsonString.length() - 1);

        return this.executeString(httpMethodParam,checkConnectionValidParam,
                bodyJsonString, contentTypeParam, postfixUrlParam);
    }

    /**
     * Submit the {@code stringParam} as HTTP request body with JSON as a response.
     *
     * @param httpMethodParam The HTTP method to use.
     * @param checkConnectionValidParam Check if connection to base endpoint is valid.
     * @param stringParam The Text to submit.
     * @param contentTypeParam The Mime / Content type to submit as.
     * @param postfixUrlParam URL mapping after the Base endpoint.
     *
     * @return Return body as JSON.
     *
     * @see HttpMethod
     * @see JSONObject
     * @see ContentType
     * @see ABaseFluidJSONObject
     */
    protected JSONObject executeString(
            HttpMethod httpMethodParam,
            boolean checkConnectionValidParam,
            String stringParam,
            ContentType contentTypeParam,
            String postfixUrlParam) {

        if(stringParam == null || stringParam.isEmpty())
        {
            throw new FluidClientException("No JSON body to post.",
                    FluidClientException.ErrorCode.FIELD_VALIDATE);
        }

        //Check connection...
        if(!checkConnectionValidParam && !this.isConnectionValid())
        {
            throw new FluidClientException(
                    "Unable to reach service at '"+
                            this.endpointUrl.concat(postfixUrlParam)+"'.",
                    FluidClientException.ErrorCode.CONNECT_ERROR);
        }

        CloseableHttpClient httpclient = this.getClient();

        String responseBody = null;

        try {
            HttpUriRequest uriRequest = null;

            //POST...
            if(httpMethodParam == HttpMethod.POST)
            {
                //When its html Form Data...
                if(contentTypeParam == ContentType.APPLICATION_FORM_URLENCODED)
                {
                    RequestBuilder builder = RequestBuilder.post().setUri(
                            this.endpointUrl.concat(postfixUrlParam));

                    builder = this.addParamsToBuildFromString(builder,stringParam);

                    uriRequest = builder.build();
                }
                //JSON or any other...
                else
                {
                    uriRequest = new HttpPost(this.endpointUrl.concat(postfixUrlParam));
                }

                uriRequest.setHeader(CONTENT_TYPE_HEADER, contentTypeParam.toString());
            }
            //PUT...
            else if(httpMethodParam == HttpMethod.PUT)
            {
                if(contentTypeParam == ContentType.APPLICATION_FORM_URLENCODED)
                {
                    RequestBuilder builder = RequestBuilder.put().setUri(
                            this.endpointUrl.concat(postfixUrlParam));

                    builder = this.addParamsToBuildFromString(builder, stringParam);
                    uriRequest = builder.build();
                }
                else
                {
                    uriRequest = new HttpPut(this.endpointUrl.concat(postfixUrlParam));
                    uriRequest.setHeader(CONTENT_TYPE_HEADER, contentTypeParam.toString());
                }
            }
            //DELETE...
            else if(httpMethodParam == HttpMethod.DELETE)
            {
                uriRequest = new HttpDelete(this.endpointUrl.concat(postfixUrlParam));
                uriRequest.setHeader(CONTENT_TYPE_HEADER, contentTypeParam.toString());
            }

            //Check that the URI request is set.
            if(uriRequest == null)
            {
                throw new FluidClientException(
                        "URI Request is not set for HTTP Method '"+httpMethodParam+"'.",
                        FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
            }

            //When HttpEntity Enclosing Request Base...
            if(uriRequest instanceof HttpEntityEnclosingRequestBase)
            {
                HttpEntity httpEntity = new StringEntity(stringParam, contentTypeParam);
                ((HttpEntityEnclosingRequestBase)uriRequest).setEntity(httpEntity);
            }

            // Create a custom response handler
            ResponseHandler<String> responseHandler = this.getJsonResponseHandler(
                    this.endpointUrl.concat(postfixUrlParam));

            responseBody = this.executeHttp(httpclient, uriRequest,
                    responseHandler, postfixUrlParam);

            if(responseBody == null || responseBody.trim().isEmpty())
            {
                throw new FluidClientException(
                        "No response data from '"+
                                this.endpointUrl.concat(postfixUrlParam)+"'.",
                        FluidClientException.ErrorCode.IO_ERROR);
            }

            JSONObject jsonOjb = new JSONObject(responseBody);
            if(jsonOjb.isNull(Error.JSONMapping.ERROR_CODE))
            {
                return jsonOjb;
            }

            int errorCode = jsonOjb.getInt(Error.JSONMapping.ERROR_CODE);
            if(errorCode > 0)
            {
                String errorMessage = (jsonOjb.isNull(Error.JSONMapping.ERROR_MESSAGE)
                        ? "Not set":
                        jsonOjb.getString(Error.JSONMapping.ERROR_MESSAGE));

                throw new FluidClientException(errorMessage, errorCode);
            }

            return jsonOjb;
        }
        //Invalid JSON Body...
        catch (JSONException jsonExcept)
        {
            if(responseBody != null && !responseBody.trim().isEmpty())
            {
                throw new FluidClientException(
                        jsonExcept.getMessage() + "\n Response Body is: \n\n" +
                                responseBody,
                        jsonExcept, FluidClientException.ErrorCode.JSON_PARSING);
            }

            throw new FluidClientException(
                    jsonExcept.getMessage(),
                    jsonExcept, FluidClientException.ErrorCode.JSON_PARSING);
        }
        //Fluid Client Exception...
        catch (FluidClientException fluidClientExcept)
        {
            throw fluidClientExcept;
        }
        //Other Exceptions...
        catch (Exception otherExcept)
        {
            throw new FluidClientException(otherExcept.getMessage(),
                    otherExcept, FluidClientException.ErrorCode.ILLEGAL_STATE_ERROR);
        }
    }

    /**
     * Add params to the {@code builderParam} and returns {@code builderParam}.
     *
     * @param builderParam Possible existing builder.
     * @param formDataToAddParam Form Data as Text.
     * @return Apache HTTP commons request builder.
     */
    private RequestBuilder addParamsToBuildFromString(
            RequestBuilder builderParam,
            String formDataToAddParam)
    {
        String[] nameValuePairs = formDataToAddParam.split(REGEX_AMP);

        if(nameValuePairs.length > 0)
        {
            for(String nameValuePair : nameValuePairs)
            {
                String[] nameValuePairArr = nameValuePair.split(REGEX_EQUALS);
                if(nameValuePairArr.length > 1)
                {
                    String name = nameValuePairArr[0];
                    String value = nameValuePairArr[1];

                    builderParam = builderParam.addParameter(name, value);
                }
            }
        }

        return builderParam;
    }

    /**
     * Get a text based response handler used mainly for JSON.
     *
     * @param urlCalledParam The url called.
     * @return String based response handler.
     */
    private ResponseHandler<String> getJsonResponseHandler(final String urlCalledParam)
    {
        // Create a custom response handler
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

            /**
             * Process the {@code responseParam} and return text if valid.
             *
             * @param responseParam The HTTP response from the server.
             * @return Text response.
             * @throws IOException If there are any communication or I/O problems.
             */
            public String handleResponse(final HttpResponse responseParam) throws IOException {

                int status = responseParam.getStatusLine().getStatusCode();
                if (status == 404) {
                    throw new FluidClientException(
                            "Endpoint for Service not found. URL ["+
                                    urlCalledParam+"].",
                            FluidClientException.ErrorCode.CONNECT_ERROR);
                }
                //
                else if (status >= 200 && status < 300) {
                    HttpEntity entity = responseParam.getEntity();

                    String responseJsonString = (entity == null) ? null:
                            EntityUtils.toString(entity);

                    return responseJsonString;
                }
                //Bad Request... Server Side Error meant for client...
                else if (status == 400) {
                    HttpEntity entity = responseParam.getEntity();

                    String responseJsonString = (entity == null) ? null :
                            EntityUtils.toString(entity);

                    return responseJsonString;
                }
                //
                else {
                    HttpEntity entity = responseParam.getEntity();

                    String responseString = (entity != null) ?
                            EntityUtils.toString(entity) : null;

                    throw new FluidClientException(
                            "Unexpected response status: " + status+". "
                            +responseParam.getStatusLine().getReasonPhrase()+". \nResponse Text ["+
                                    responseString+"]",
                            FluidClientException.ErrorCode.IO_ERROR);
                }
            }
        };

        return responseHandler;
    }

    /**
     * Translates a string into {@code application/x-www-form-urlencoded}
     * format using a specific encoding scheme. This method uses the
     * supplied encoding scheme to obtain the bytes for unsafe
     * characters.
     *
     * @param textParam The text to URL encode.
     * @return Encoded text from {@code textParam}.
     *
     * @see URLEncoder#encode(String, String)
     */
    public static String encodeParam(String textParam)
    {
        if(textParam == null)
        {
            return null;
        }

        try {
            return URLEncoder.encode(textParam,"UTF-8");
        }
        //
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Performs a HTTP Get against the connection test Web Service to
     * confirm whether the connection is valid.
     *
     * @return Whether the connection is valid or not.
     */
    public boolean isConnectionValid()
    {
        //Init the session to get the salt...
        try{
            this.getJson(true, WS.Path.Test.Version1.testConnection());
        }
        //
        catch (FluidClientException flowJobExcept)
        {
            if(flowJobExcept.getErrorCode() == FluidClientException.ErrorCode.CONNECT_ERROR)
            {
                return false;
            }

            throw flowJobExcept;
        }

        return true;
    }

    /**
     * Inspects the {@code baseDomainParam} to confirm whether
     * the base domain is of type {@code Error}.
     *
     * @param baseDomainParam The domain object to inspect.
     * @return Whether the {@code baseDomainParam} is of type {@code Error} or error code is greater than 0.
     *
     * @see ABaseFluidJSONObject
     * @see Error
     */
    protected boolean isError(ABaseFluidJSONObject baseDomainParam)
    {
        if(baseDomainParam == null)
        {
            return false;
        }

        //Must be subclass of error and error code greater than 0...
        if(baseDomainParam instanceof Error && ((Error)baseDomainParam).getErrorCode() > 0)
        {
            return true;
        }

        return false;
    }

    /**
     * Gets the service ticket.
     *
     * @return The service ticket.
     */
    public String getServiceTicket() {
        return this.serviceTicket;
    }

    /**
     * Sets the service ticket.
     *
     * @param serviceTicketParam The service ticket.
     */
    public void setServiceTicket(String serviceTicketParam) {
        this.serviceTicket = serviceTicketParam;
    }

    /**
     * Checks whether the {@code textParam} is {@code null} or empty.
     *
     * @param textParam The text to check.
     * @return Whether the {@code textParam} is empty.
     */
    protected final boolean isEmpty(String textParam) {
        return (textParam == null) ? true : textParam.trim().isEmpty();
    }

    /**
     * Retrieves the `git describe` outcome.
     *
     * @return The version of the Fluid API.
     */
    public String getFluidAPIVersion()
    {
        return GitDescribe.GIT_DESCRIBE;
    }

    /**
     * Creates a new Http client.
     *
     * If part of a test run, the Http client will accept
     * self signed certificates.
     *
     * See flag {@code IS_IN_JUNIT_TEST_MODE}.En
     *
     * @return CloseableHttpClient that may or may not accept
     * self signed certificates.
     *
     * @since v1.1
     */
    private CloseableHttpClient getClient()
    {
        if(this.closeableHttpClient != null)
        {
            return this.closeableHttpClient;
        }

        //Only accept self signed certificate if in Junit test case.
        String pathToFluidTrustStore = this.getPathToFluidSpecificTrustStore();
        //Test mode...
        if(IS_IN_JUNIT_TEST_MODE || pathToFluidTrustStore != null)
        {
            SSLContextBuilder builder = new SSLContextBuilder();

            try {
                //builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
                if(pathToFluidTrustStore == null)
                {
                    builder.loadTrustMaterial(new SSLTrustAll());
                }
                else
                {
                    String password = this.getFluidSpecificTrustStorePassword();
                    if(password == null)
                    {
                        password = UtilGlobal.EMPTY;
                    }

                    if(IS_IN_JUNIT_TEST_MODE)
                    {
                        builder.loadTrustMaterial(
                                new File(pathToFluidTrustStore),
                                password.toCharArray(),
                                new SSLTrustAll());
                    }
                    else
                    {
                        builder.loadTrustMaterial(
                                new File(pathToFluidTrustStore),
                                password.toCharArray());
                    }
                }

                SSLContext sslContext = builder.build();

                this.closeableHttpClient = HttpClients.custom().setSSLSocketFactory(
                        new SSLConnectionSocketFactory(sslContext)).build();
            }
            //Changed for Java 1.6 compatibility...
            catch (NoSuchAlgorithmException e) {

                throw new FluidClientException(
                        "NoSuchAlgorithm: Unable to load self signed trust material. "+e.getMessage(),
                        e, FluidClientException.ErrorCode.CRYPTOGRAPHY);
            } catch (KeyManagementException e) {

                throw new FluidClientException(
                        "KeyManagement: Unable to load self signed trust material. "+e.getMessage(), e,
                        FluidClientException.ErrorCode.CRYPTOGRAPHY);
            } catch (KeyStoreException e) {

                throw new FluidClientException(
                        "KeyStore: Unable to load self signed trust material. "+e.getMessage(), e,
                        FluidClientException.ErrorCode.CRYPTOGRAPHY);
            } catch (CertificateException e) {

                throw new FluidClientException(
                        "Certificate: Unable to load self signed trust material. "+e.getMessage(), e,
                        FluidClientException.ErrorCode.CRYPTOGRAPHY);
            } catch (IOException ioError) {

                throw new FluidClientException(
                        "IOError: Unable to load self signed trust material. "+ioError.getMessage(), ioError,
                        FluidClientException.ErrorCode.CRYPTOGRAPHY);
            }
        }
        //Default HTTP Client...
        else
        {
            this.closeableHttpClient = HttpClients.createDefault();
        }

        return this.closeableHttpClient;
    }

    /**
     * Retrieves the system property for the Fluid specific trust store.
     *
     * @return The {@code fluid.httpclient.truststore} system property value.
     *
     * @see System
     * @see java.util.Properties
     */
    private String getPathToFluidSpecificTrustStore()
    {
        String fluidSystemTrustStore =
                System.getProperty(SYSTEM_PROP_FLUID_TRUST_STORE);

        if(fluidSystemTrustStore == null || fluidSystemTrustStore.trim().isEmpty())
        {
            return null;
        }

        File certFile = new File(fluidSystemTrustStore);

        if(certFile.exists() && certFile.isFile())
        {
            return fluidSystemTrustStore;
        }

        return null;
    }

    /**
     * Retrieves the system property for the Fluid specific trust store password.
     *
     * @return The {@code fluid.httpclient.truststore.password} system property value.
     *
     * @see System
     * @see java.util.Properties
     */
    private String getFluidSpecificTrustStorePassword()
    {
        return System.getProperty(SYSTEM_PROP_FLUID_TRUST_STORE_PASSWORD);
    }

    /**
     * If the HTTP Client is set, this will
     * close and clean any connections that needs to be closed.
     *
     * @since v1.1
     */
    public void closeAndClean()
    {
        CloseConnectionRunnable closeConnectionRunnable =
                new CloseConnectionRunnable(this);

        Thread closeConnThread = new Thread(
                closeConnectionRunnable,"Close ABaseClientWS Connection");
        closeConnThread.start();
    }

    /**
     * Close the SQL and ElasticSearch Connection, but not in
     * a separate {@code Thread}.
     */
    protected void closeConnectionNonThreaded()
    {
        if(this.closeableHttpClient != null)
        {
            try {
                this.closeableHttpClient.close();
            }
            //
            catch (IOException e) {

                throw new FluidClientException(
                        "Unable to close Http Client connection. "+
                                e.getMessage(),
                        e, FluidClientException.ErrorCode.IO_ERROR);
            }
        }

        this.closeableHttpClient = null;
    }

    /**
     * Trust all SSL type connections.
     */
    private static final class SSLTrustAll implements TrustStrategy
    {
        /**
         *
         * @param x509Certificates List of X509 certificates.
         * @param stringParam Text.
         * @return {@code true}, always trusted.
         * @throws CertificateException If there is a cert problem.
         */
        @Override
        public boolean isTrusted(X509Certificate[] x509Certificates, String stringParam) throws CertificateException {
            return true;
        }
    }

    /**
     * Utility class to close the connection in a thread.
     */
    private static class CloseConnectionRunnable implements Runnable{

        private ABaseClientWS baseClientWS;

        /**
         * The resource to close.
         *
         * @param aBaseClientWSParam Base utility to close.
         */
        public CloseConnectionRunnable(ABaseClientWS aBaseClientWSParam) {
            this.baseClientWS = aBaseClientWSParam;
        }

        /**
         * Performs the threaded operation.
         */
        @Override
        public void run() {

            if(this.baseClientWS == null)
            {
                return;
            }

            this.baseClientWS.closeConnectionNonThreaded();
        }
    }
}
