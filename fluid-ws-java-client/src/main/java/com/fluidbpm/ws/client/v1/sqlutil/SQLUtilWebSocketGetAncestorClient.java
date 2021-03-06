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

package com.fluidbpm.ws.client.v1.sqlutil;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;

import com.fluidbpm.program.api.vo.FluidItem;
import com.fluidbpm.program.api.vo.Form;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.FluidClientException;
import com.fluidbpm.ws.client.v1.websocket.ABaseClientWebSocket;
import com.fluidbpm.ws.client.v1.websocket.GenericListMessageHandler;
import com.fluidbpm.ws.client.v1.websocket.IMessageReceivedCallback;

/**
 * Java Web Socket Client for {@code SQLUtil} related actions.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see JSONObject
 * @see WS.Path.FlowItem
 * @see FluidItem
 */
public class SQLUtilWebSocketGetAncestorClient extends
        ABaseClientWebSocket<SQLUtilWebSocketGetAncestorClient.GetAncestorMessageHandler> {

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param endpointBaseUrlParam URL to base endpoint.
     *
     * @param messageReceivedCallbackParam Callback for when a message is received.
     *
     * @param serviceTicketAsHexParam The Server issued Service Ticket.
     * @param timeoutInMillisParam The timeout of the request in millis.
     *
     * @param includeFieldDataParam Should Form Field data be included.
     * @param includeTableFieldsParam Should Table Fields be included.
     */
    public SQLUtilWebSocketGetAncestorClient(
            String endpointBaseUrlParam,
            IMessageReceivedCallback<Form> messageReceivedCallbackParam,
            String serviceTicketAsHexParam,
            long timeoutInMillisParam,
            boolean includeFieldDataParam,
            boolean includeTableFieldsParam) {
        super(endpointBaseUrlParam,
                new GetAncestorMessageHandler(messageReceivedCallbackParam),
                timeoutInMillisParam,
                WS.Path.SQLUtil.Version1.getAncestorWebSocket(
                        includeFieldDataParam,
                        includeTableFieldsParam,
                        serviceTicketAsHexParam));

        this.setServiceTicket(serviceTicketAsHexParam);
    }

    /**
     * Retrieves the Ancestor (Form) for the {@code formToGetTableFormsForParam}.
     *
     * @param formToGetAncestorForParam The Fluid Form to get Ancestor for.
     *
     * @return The {@code formToGetDescendantsForParam} Table Records as {@code Form}'s.
     */
    public Form getAncestorSynchronized(Form formToGetAncestorForParam) {

        this.getMessageHandler().clear();

        if(formToGetAncestorForParam == null)
        {
            return null;
        }

        //Send all the messages...
        if(formToGetAncestorForParam.getEcho() == null ||
                formToGetAncestorForParam.getEcho().trim().isEmpty())
        {
            formToGetAncestorForParam.setEcho(UUID.randomUUID().toString());
        }

        CompletableFuture<List<Form>> completableFuture = new CompletableFuture();

        //Set the future...
        this.getMessageHandler().setCompletableFuture(completableFuture);

        //Send the actual message...
        this.sendMessage(formToGetAncestorForParam);
        
        try {
            List<Form> returnValue = completableFuture.get(
                    this.getTimeoutInMillis(), TimeUnit.MILLISECONDS);

            if(returnValue == null || returnValue.isEmpty())
            {
                return null;
            }

            return returnValue.get(0);
        }
        //Interrupted...
        catch (InterruptedException exceptParam) {

            throw new FluidClientException(
                    "SQLUtil-WebSocket-Interrupted-GetAncestor: " +
                            exceptParam.getMessage(),
                    exceptParam,
                    FluidClientException.ErrorCode.STATEMENT_EXECUTION_ERROR);
        }
        //Error on the web-socket...
        catch (ExecutionException executeProblem) {

            Throwable cause = executeProblem.getCause();

            //Fluid client exception...
            if(cause instanceof FluidClientException)
            {
                throw (FluidClientException)cause;
            }
            else
            {
                throw new FluidClientException(
                        "SQLUtil-WebSocket-GetAncestor: " +
                                cause.getMessage(), cause,
                        FluidClientException.ErrorCode.STATEMENT_EXECUTION_ERROR);
            }
        }
        //Timeout...
        catch (TimeoutException eParam) {

            throw new FluidClientException(
                    "SQLUtil-WebSocket-GetAncestor: Timeout while waiting for all return data. There were '"
                            +this.getMessageHandler().getReturnValue().size()
                            +"' items after a Timeout of "+(
                            TimeUnit.MILLISECONDS.toSeconds(this.getTimeoutInMillis()))+" seconds."
                    ,FluidClientException.ErrorCode.IO_ERROR);
        }
    }

    /**
     * Gets the single form. Still relying on a single session.
     */
    public static class GetAncestorMessageHandler extends GenericListMessageHandler<Form>
    {
        /**
         * The default constructor that sets a ancestor message handler.
         *
         * @param messageReceivedCallbackParam The optional message callback.
         */
        public GetAncestorMessageHandler(IMessageReceivedCallback<Form> messageReceivedCallbackParam) {

            super(messageReceivedCallbackParam);
        }

        /**
         * New {@code Form} by {@code jsonObjectParam}
         *
         * @param jsonObjectParam The JSON Object to parse.
         * @return new {@code Form}.
         */
        @Override
        public Form getNewInstanceBy(JSONObject jsonObjectParam) {
            
            return new Form(jsonObjectParam);
        }
    }
}
