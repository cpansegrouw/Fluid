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

package com.fluid.ws.client.v1.flow;

import org.json.JSONObject;

import com.fluid.program.api.vo.flow.Flow;
import com.fluid.program.api.vo.flow.FlowStep;
import com.fluid.program.api.vo.flow.FlowStepListing;
import com.fluid.program.api.vo.flow.JobViewListing;
import com.fluid.program.api.vo.ws.WS;
import com.fluid.ws.client.v1.ABaseClientWS;

/**
 * Used to change any of the Flow Steps.
 *
 * This is ideal for doing automated tests against
 * the Fluid platform.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see JSONObject
 * @see com.fluid.program.api.vo.ws.WS.Path.FlowStep
 * @see FlowStep
 * @see ABaseClientWS
 */
public class FlowStepClient extends ABaseClientWS {

    /**
     * Constructor that sets the Service Ticket from authentication.
     *
     * @param endpointBaseUrlParam URL to base endpoint.
     * @param serviceTicketParam The Server issued Service Ticket.
     */
    public FlowStepClient(
            String endpointBaseUrlParam,
            String serviceTicketParam) {
        super(endpointBaseUrlParam);

        this.setServiceTicket(serviceTicketParam);
    }

    /**
     * Creates a new Flow Step.
     *
     * @param flowStepParam The step to create.
     * @return The created step.
     */
    public FlowStep createFlowStep(FlowStep flowStepParam)
    {
        if(flowStepParam != null && this.serviceTicket != null)
        {
            flowStepParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStep(this.putJson(
                flowStepParam, WS.Path.FlowStep.Version1.flowStepCreate()));
    }

    /**
     * Updates an existing Flow Step.
     *
     * @param flowStepParam The updated Flow Step values.
     * @return The updated Step.
     */
    public FlowStep updateFlowStep(FlowStep flowStepParam)
    {
        if(flowStepParam != null && this.serviceTicket != null)
        {
            flowStepParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStep(this.postJson(
                flowStepParam, WS.Path.FlowStep.Version1.flowStepUpdate()));
    }

    /**
     * Retrieves an existing Flow Step via Primary key.
     *
     * @param flowStepIdParam The Flow Step Primary Key.
     * @param flowStepTypeParam The type of step.
     * @return The Step retrieved by Primary key.
     *
     * @see com.fluid.program.api.vo.flow.FlowStep.StepType
     */
    public FlowStep getFlowStepById(
            Long flowStepIdParam, String flowStepTypeParam)
    {
        FlowStep flowStep = new FlowStep(flowStepIdParam);
        flowStep.setFlowStepType(flowStepTypeParam);

        if(this.serviceTicket != null)
        {
            flowStep.setServiceTicket(this.serviceTicket);
        }

        return new FlowStep(this.postJson(
                flowStep, WS.Path.FlowStep.Version1.getById()));
    }

    /**
     * Retrieves an existing Flow Step via Step.
     *
     * Lookup will include id, then;
     * Name.
     *
     * @param flowStepParam The Flow Step to use as lookup.
     * @return The Step retrieved by provided {@code flowStepParam}.
     *
     * @see com.fluid.program.api.vo.flow.FlowStep.StepType
     */
    public FlowStep getFlowStepByStep(FlowStep flowStepParam)
    {
        if(this.serviceTicket != null && flowStepParam != null)
        {
            flowStepParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStep(this.postJson(
                flowStepParam, WS.Path.FlowStep.Version1.getByStep()));
    }

    /**
     * Retrieves all Assignment {@link com.fluid.program.api.vo.flow.JobView}s
     * via Flow Step Primary key.
     *
     * @param flowStepIdParam The Flow Step Primary Key.
     * @return The Step retrieved by Primary key.
     *
     * @see com.fluid.program.api.vo.flow.FlowStep.StepType
     */
    public JobViewListing getJobViewsByStepId(Long flowStepIdParam)
    {
        return this.getJobViewsByStep(new FlowStep(flowStepIdParam));
    }

    /**
     * Retrieves all Assignment {@link com.fluid.program.api.vo.flow.JobView}s
     * via Flow Step Name key.
     *
     * @param flowStepNameParam The Flow Step Name.
     * @param flowParam The Flow.
     * @return The Step retrieved by Primary key.
     *
     * @see com.fluid.program.api.vo.flow.FlowStep.StepType
     */
    public JobViewListing getJobViewsByStepName(
            String flowStepNameParam, Flow flowParam)
    {
        FlowStep step = new FlowStep();
        step.setName(flowStepNameParam);
        step.setFlow(flowParam);
        
        return this.getJobViewsByStep(step);
    }

    /**
     * Retrieves all Assignment {@link com.fluid.program.api.vo.flow.JobView}s
     * via Flow Step Primary key.
     *
     * @param flowStepParam The Flow Step.
     * @return The Step retrieved by Primary key.
     *
     * @see com.fluid.program.api.vo.flow.FlowStep.StepType
     */
    public JobViewListing getJobViewsByStep(FlowStep flowStepParam)
    {
        if(this.serviceTicket != null && flowStepParam != null)
        {
            flowStepParam.setServiceTicket(this.serviceTicket);
        }

        return new JobViewListing(this.postJson(
                flowStepParam, WS.Path.FlowStep.Version1.getAllViewsByStep()));
    }

    /**
     * Retrieves all Steps via Flow.
     *
     * @param flowParam The Flow.
     * @return The Step retrieved by Primary key.
     *
     * @see com.fluid.program.api.vo.flow.Flow
     * @see FlowStepListing
     */
    public FlowStepListing getStepsByFlow(Flow flowParam)
    {
        if(this.serviceTicket != null && flowParam != null)
        {
            flowParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStepListing(this.postJson(
                flowParam, WS.Path.FlowStep.Version1.getAllStepsByFlow()));
    }

    /**
     * Delete an existing Flow Step.
     *
     * @param flowStepParam The Flow Step to delete.
     * @return The deleted Flow Step.
     */
    public FlowStep deleteFlowStep(FlowStep flowStepParam)
    {
        if(flowStepParam != null && this.serviceTicket != null)
        {
            flowStepParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStep(this.postJson(
                flowStepParam, WS.Path.FlowStep.Version1.flowStepDelete()));
    }

    /**
     * Forcefully delete an existing Flow Step.
     *
     * Only 'admin' can forcefully delete a step.
     *
     * @param flowStepParam The Flow Step to delete.
     * @return The deleted Flow Step.
     */
    public FlowStep forceDeleteFlowStep(FlowStep flowStepParam)
    {
        if(flowStepParam != null && this.serviceTicket != null)
        {
            flowStepParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStep(this.postJson(
                flowStepParam, WS.Path.FlowStep.Version1.flowStepDelete(true)));
    }
}
