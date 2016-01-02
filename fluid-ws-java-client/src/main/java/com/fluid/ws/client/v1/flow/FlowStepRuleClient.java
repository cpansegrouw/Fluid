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

import com.fluid.program.api.vo.flow.FlowStep;
import com.fluid.program.api.vo.flow.FlowStepRule;
import com.fluid.program.api.vo.ws.WS;
import com.fluid.ws.client.v1.ABaseClientWS;

/**
 * Used to change any of the Flow rules and
 * underlying steps and rules.
 *
 * This is ideal for doing automated tests against
 * the Fluid platform.
 *
 * Created by jasonbruwer on 15/12/19.
 */
public class FlowStepRuleClient extends ABaseClientWS {

    /**
     *
     */
    public FlowStepRuleClient() {
        super();
    }

    /**
     *
     * @param serviceTicketParam
     */
    public FlowStepRuleClient(String serviceTicketParam) {
        super();

        this.setServiceTicket(serviceTicketParam);
    }

    /**
     *
     * @param flowStepRuleParam
     * @return
     */
    public FlowStepRule createFlowStepEntryRule(FlowStepRule flowStepRuleParam)
    {
        if(flowStepRuleParam != null && this.serviceTicket != null)
        {
            flowStepRuleParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStepRule(this.putJson(
                flowStepRuleParam, WS.Path.FlowStepRule.Version1.flowStepRuleEntryCreate()));
    }

    /**
     *
     * @param flowStepRuleParam
     * @return
     */
    public FlowStepRule createFlowStepExitRule(FlowStepRule flowStepRuleParam)
    {
        if(flowStepRuleParam != null && this.serviceTicket != null)
        {
            flowStepRuleParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStepRule(this.putJson(
                flowStepRuleParam, WS.Path.FlowStepRule.Version1.flowStepRuleExitCreate()));
    }

    /**
     *
     * @param flowStepRuleParam
     * @return
     */
    public FlowStepRule createFlowStepViewRule(FlowStepRule flowStepRuleParam)
    {
        if(flowStepRuleParam != null && this.serviceTicket != null)
        {
            flowStepRuleParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStepRule(this.putJson(
                flowStepRuleParam, WS.Path.FlowStepRule.Version1.flowStepRuleViewCreate()));
    }

    /**
     *
     * @param flowStepRuleParam
     * @return
     */
    public FlowStepRule updateFlowStepEntryRule(FlowStepRule flowStepRuleParam)
    {
        if(flowStepRuleParam != null && this.serviceTicket != null)
        {
            flowStepRuleParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStepRule(this.postJson(
                flowStepRuleParam, WS.Path.FlowStepRule.Version1.flowStepRuleUpdateEntry()));
    }

    /**
     *
     * @param flowStepRuleParam
     * @return
     */
    public FlowStepRule updateFlowStepExitRule(FlowStepRule flowStepRuleParam)
    {
        if(flowStepRuleParam != null && this.serviceTicket != null)
        {
            flowStepRuleParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStepRule(this.postJson(
                flowStepRuleParam, WS.Path.FlowStepRule.Version1.flowStepRuleUpdateExit()));
    }

    /**
     *
     * @param flowStepRuleParam
     * @return
     */
    public FlowStepRule updateFlowStepViewRule(FlowStepRule flowStepRuleParam)
    {
        if(flowStepRuleParam != null && this.serviceTicket != null)
        {
            flowStepRuleParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStepRule(this.postJson(
                flowStepRuleParam, WS.Path.FlowStepRule.Version1.flowStepRuleUpdateView()));
    }

    /**
     *
     * @param flowStepRuleParam
     * @return
     */
    public FlowStep deleteFlowStepEntryRule(FlowStepRule flowStepRuleParam)
    {
        if(flowStepRuleParam != null && this.serviceTicket != null)
        {
            flowStepRuleParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStep(this.postJson(
                flowStepRuleParam, WS.Path.FlowStepRule.Version1.flowStepRuleDeleteEntry()));
    }

    /**
     *
     * @param flowStepRuleParam
     * @return
     */
    public FlowStep deleteFlowStepExitRule(FlowStepRule flowStepRuleParam)
    {
        if(flowStepRuleParam != null && this.serviceTicket != null)
        {
            flowStepRuleParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStep(this.postJson(
                flowStepRuleParam, WS.Path.FlowStepRule.Version1.flowStepRuleDeleteExit()));
    }

    /**
     *
     * @param flowStepRuleParam
     * @return
     */
    public FlowStep deleteFlowStepViewRule(FlowStepRule flowStepRuleParam)
    {
        if(flowStepRuleParam != null && this.serviceTicket != null)
        {
            flowStepRuleParam.setServiceTicket(this.serviceTicket);
        }

        return new FlowStep(this.postJson(
                flowStepRuleParam, WS.Path.FlowStepRule.Version1.flowStepRuleDeleteView()));
    }
}