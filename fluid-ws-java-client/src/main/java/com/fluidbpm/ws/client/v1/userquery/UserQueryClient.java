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

package com.fluidbpm.ws.client.v1.userquery;

import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.vo.item.FluidItemListing;
import com.fluidbpm.program.api.vo.userquery.UserQuery;
import com.fluidbpm.program.api.vo.userquery.UserQueryListing;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.ABaseClientWS;

/**
 * Java Web Service Client for {@code UserQuery} related actions.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see JSONObject
 * @see WS.Path.UserQuery
 * @see UserQuery
 */
public class UserQueryClient extends ABaseClientWS {

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param endpointBaseUrlParam URL to base endpoint.
     * @param serviceTicketParam The Server issued Service Ticket.
     */
    public UserQueryClient(
            String endpointBaseUrlParam,
            String serviceTicketParam) {
        super(endpointBaseUrlParam);

        this.setServiceTicket(serviceTicketParam);
    }

    /**
     * Creates a new {@code UserQuery}.
     *
     * @param userQueryParam The {@code UserQuery} to create.
     * @return The Created User Query.
     *
     * @see com.fluidbpm.program.api.vo.userquery.UserQuery
     */
    public UserQuery createUserQuery(UserQuery userQueryParam)
    {
        if(userQueryParam != null && this.serviceTicket != null)
        {
            userQueryParam.setServiceTicket(this.serviceTicket);
        }

        return new UserQuery(this.putJson(
                userQueryParam,
                WS.Path.UserQuery.Version1.userQueryCreate()));
    }

    /**
     * Updates an existing {@code UserQuery}.
     *
     * @param userQueryParam The {@code UserQuery} to update.
     * @return The Updated UserQuery.
     *
     * @see UserQuery
     */
    public UserQuery updateUserQuery(UserQuery userQueryParam)
    {
        if(userQueryParam != null && this.serviceTicket != null)
        {
            userQueryParam.setServiceTicket(this.serviceTicket);
        }

        return new UserQuery(this.postJson(
                userQueryParam,
                WS.Path.UserQuery.Version1.userQueryUpdate()));
    }

    /**
     * Deletes the {@code UserQuery} provided.
     * Id must be set on the {@code UserQuery}.
     *
     * @param userQueryToDeleteParam The UserQuery to Delete.
     * @return The deleted UserQuery.
     */
    public UserQuery deleteUserQuery(
            UserQuery userQueryToDeleteParam)
    {
        if(userQueryToDeleteParam != null && this.serviceTicket != null)
        {
            userQueryToDeleteParam.setServiceTicket(this.serviceTicket);
        }

        return new UserQuery(this.postJson(userQueryToDeleteParam,
                WS.Path.UserQuery.Version1.userQueryDelete()));
    }

    /**
     * Deletes the {@code UserQuery} provided.
     * Id must be set on the {@code UserQuery}.
     *
     * @param userQueryToDeleteParam The UserQuery to Delete.
     * @param forcefullyDeleteParam Delete the UserQuery forcefully.
     * @return The deleted UserQuery.
     */
    public UserQuery deleteUserQuery(
            UserQuery userQueryToDeleteParam,
            boolean forcefullyDeleteParam)
    {
        if(userQueryToDeleteParam != null && this.serviceTicket != null)
        {
            userQueryToDeleteParam.setServiceTicket(this.serviceTicket);
        }

        return new UserQuery(this.postJson(userQueryToDeleteParam,
                WS.Path.UserQuery.Version1.userQueryDelete(forcefullyDeleteParam)));
    }

    /**
     * Retrieves User Query information for the provided {@code userQueryIdParam}.
     *
     * @param userQueryIdParam The ID of the {@code UserQuery} to retrieve info for.
     *
     * @return UserQuery information.
     *
     * @see UserQuery
     */
    public UserQuery getUserQueryById(Long userQueryIdParam)
    {
        UserQuery userQueryToGetInfoFor = new UserQuery();
        userQueryToGetInfoFor.setId(userQueryIdParam);

        if(this.serviceTicket != null)
        {
            userQueryToGetInfoFor.setServiceTicket(this.serviceTicket);
        }

        try {
            return new UserQuery(this.postJson(
                    userQueryToGetInfoFor, WS.Path.UserQuery.Version1.getById()));
        }
        //
        catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Retrieves all user query information.
     *
     * @return UserQuery information.
     *
     * @see UserQueryListing
     */
    public UserQueryListing getAllUserQueries()
    {
        UserQuery userQueryToGetInfoFor = new UserQuery();

        if(this.serviceTicket != null)
        {
            userQueryToGetInfoFor.setServiceTicket(this.serviceTicket);
        }

        try {
            return new UserQueryListing(this.postJson(
                    userQueryToGetInfoFor, WS.Path.UserQuery.Version1.getAllUserQueries()));
        }
        //
        catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }

    /**
     * Executes the {@code UserQuery} {@code queryToExecuteParam}
     * and returns the result information.
     *
     * @param queryToExecuteParam The UserQuery to execute.
     *
     * @return The UserQuery result.
     *
     * @see FluidItemListing
     */
    public FluidItemListing executeUserQuery(
            UserQuery queryToExecuteParam)
    {
        return this.executeUserQuery(queryToExecuteParam, true);
    }
    
    /**
     * Executes the {@code UserQuery} {@code queryToExecuteParam}
     * and returns the result information.
     *
     * @param queryToExecuteParam The UserQuery to execute.
     * @param populateAncestorIdParam - Whether the ancestor id should be populated (when applicable).
     *
     * @return The UserQuery result.
     *
     * @see FluidItemListing
     */
    public FluidItemListing executeUserQuery(
            UserQuery queryToExecuteParam,
            boolean populateAncestorIdParam)
    {
        if(this.serviceTicket != null && queryToExecuteParam != null)
        {
            queryToExecuteParam.setServiceTicket(this.serviceTicket);
        }

        try {
            return new FluidItemListing(this.postJson(
                    queryToExecuteParam, WS.Path.UserQuery.Version1.executeUserQuery(
                            populateAncestorIdParam)));
        }
        //
        catch (JSONException jsonExcept) {
            throw new FluidClientException(jsonExcept.getMessage(),
                    FluidClientException.ErrorCode.JSON_PARSING);
        }
    }
}
