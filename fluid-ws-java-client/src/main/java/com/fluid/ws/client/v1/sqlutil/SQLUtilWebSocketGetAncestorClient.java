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

package com.fluid.ws.client.v1.sqlutil;

import org.json.JSONObject;

import com.fluid.program.api.vo.FluidItem;
import com.fluid.program.api.vo.Form;
import com.fluid.program.api.vo.ws.WS;
import com.fluid.ws.client.v1.websocket.ABaseClientWebSocket;
import com.fluid.ws.client.v1.websocket.GenericListMessageHandler;
import com.fluid.ws.client.v1.websocket.IMessageReceivedCallback;

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
     * @param messageReceivedCallbackParam Callback for when a message is received.
     *
     * @param serviceTicketAsHexParam The Server issued Service Ticket.
     * @param timeoutInMillisParam The timeout of the request in millis.
     *
     * @param includeFieldDataParam Should Form Field data be included.
     * @param includeTableFieldsParam Should Table Fields be included.
     */
    public SQLUtilWebSocketGetAncestorClient(
            IMessageReceivedCallback<Form> messageReceivedCallbackParam,
            String serviceTicketAsHexParam,
            long timeoutInMillisParam,
            boolean includeFieldDataParam,
            boolean includeTableFieldsParam) {
        super(new GetAncestorMessageHandler(messageReceivedCallbackParam),
                timeoutInMillisParam,
                WS.Path.SQLUtil.Version1.getAncestorWebSocket(
                        includeFieldDataParam,
                        includeTableFieldsParam,
                        serviceTicketAsHexParam));

        this.setServiceTicket(serviceTicketAsHexParam);
    }

    /**
     * Retrieves all the Ancestors (Forms) for the {@code formToGetAncestorsForForParam}
     * asynchronously.
     *
     * @param formToGetAncestorsForForParam The Fluid Form to get Ancestors for.
     */
    public void getAncestorAsynchronous(
            Form formToGetAncestorsForForParam) {

        if(formToGetAncestorsForForParam == null)
        {
            return;
        }

        //Send the actual message...
        this.sendMessage(formToGetAncestorsForForParam);
    }

    /**
     * Gets the single form. Still relying on a single session.
     */
    public static class GetAncestorMessageHandler extends GenericListMessageHandler<Form>
    {
        private Form returnedForm;

        /**
         *
         * @param messageReceivedCallbackParam
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

            this.returnedForm = new Form(jsonObjectParam);

            return this.returnedForm;
        }

        /**
         *
         * @return
         */
        public Form getReturnedForm() {
            return this.returnedForm;
        }
    }
}
