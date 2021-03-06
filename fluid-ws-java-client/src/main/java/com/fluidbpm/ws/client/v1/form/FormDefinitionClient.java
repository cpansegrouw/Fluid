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

package com.fluidbpm.ws.client.v1.form;

import org.json.JSONObject;

import com.fluidbpm.program.api.vo.Form;
import com.fluidbpm.program.api.vo.ws.WS;
import com.fluidbpm.ws.client.v1.ABaseClientWS;

/**
 * Java Web Service Client for Form Definition related actions.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see JSONObject
 * @see com.fluidbpm.program.api.vo.ws.WS.Path.FormDefinition
 * @see Form
 */
public class FormDefinitionClient extends ABaseClientWS {

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param endpointBaseUrlParam URL to base endpoint.
     * @param serviceTicketParam The Server issued Service Ticket.
     */
    public FormDefinitionClient(
            String endpointBaseUrlParam,
            String serviceTicketParam) {
        super(endpointBaseUrlParam);

        this.setServiceTicket(serviceTicketParam);
    }

    /**
     * Creates a new Form Definition with the Fields inside the definition.
     *
     * @param formDefinitionParam The Definition to create.
     * @return The Created Form Definition.
     *
     * @see com.fluidbpm.program.api.vo.Field
     * @see Form
     */
    public Form createFormDefinition(Form formDefinitionParam)
    {
        if(formDefinitionParam != null && this.serviceTicket != null)
        {
            formDefinitionParam.setServiceTicket(this.serviceTicket);
        }

        return new Form(this.putJson(
                formDefinitionParam, WS.Path.FormDefinition.Version1.formDefinitionCreate()));
    }

    /**
     * Updates an existing Form Definition with the Fields inside the definition.
     *
     * @param formDefinitionParam The Definition to update.
     * @return The Updated Form Definition.
     *
     * @see com.fluidbpm.program.api.vo.Field
     * @see Form
     */
    public Form updateFormDefinition(Form formDefinitionParam)
    {
        if(formDefinitionParam != null && this.serviceTicket != null)
        {
            formDefinitionParam.setServiceTicket(this.serviceTicket);
        }

        return new Form(this.postJson(
                formDefinitionParam,
                WS.Path.FormDefinition.Version1.formDefinitionUpdate()));
    }

    /**
     * Retrieves the Form Definition by Primary key.
     *
     * @param formDefinitionIdParam The Form Definition primary key.
     * @return Form by Primary key.
     */
    public Form getFormDefinitionById(Long formDefinitionIdParam)
    {
        Form form = new Form(formDefinitionIdParam);

        if(this.serviceTicket != null)
        {
            form.setServiceTicket(this.serviceTicket);
        }

        return new Form(this.postJson(
                form, WS.Path.FormDefinition.Version1.getById()));
    }

    /**
     * Retrieves the Form Definition by Name.
     *
     * @param formDefinitionNameParam The Form Definition name.
     * @return Form by Name.
     */
    public Form getFormDefinitionByName(String formDefinitionNameParam)
    {
        Form form = new Form(formDefinitionNameParam);

        if(this.serviceTicket != null)
        {
            form.setServiceTicket(this.serviceTicket);
        }

        return new Form(this.postJson(
                form, WS.Path.FormDefinition.Version1.getByName()));
    }

    /**
     * Deletes the Form Definition provided.
     * Id must be set on the Form Definition.
     *
     * @param formDefinitionParam The Form Definition to Delete.
     * @return The deleted Form Definition.
     */
    public Form deleteFormDefinition(Form formDefinitionParam)
    {
        if(formDefinitionParam != null && this.serviceTicket != null)
        {
            formDefinitionParam.setServiceTicket(this.serviceTicket);
        }

        return new Form(this.postJson(formDefinitionParam,
                WS.Path.FormDefinition.Version1.formDefinitionDelete()));
    }

}
