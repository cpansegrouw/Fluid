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

package com.fluidbpm.program.api.vo.flow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fluidbpm.program.api.vo.ABaseFluidJSONObject;

/**
 * Fluid consolidation of a workflow.
 *
 * @author jasonbruwer
 * @since v1.0
 *
 * @see FlowStep
 * @see FlowStepRule
 * @see ABaseFluidJSONObject
 */
public class Flow extends ABaseFluidJSONObject {

    public static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private Date dateCreated;
    private Date dateLastUpdated;
    private List<FlowStep> flowSteps;

    /**
     * The JSON mapping for the {@code Flow} object.
     */
    public static class JSONMapping
    {
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String DATE_CREATED = "dateCreated";
        public static final String DATE_LAST_UPDATED = "dateLastUpdated";
        public static final String FLOW_STEPS = "flowSteps";
    }

    /**
     * Default constructor.
     */
    public Flow() {
        super();
    }

    /**
     * Constructor with the unique Flow identifier.
     *
     * @param flowIdParam The Flow primary key.
     */
    public Flow(Long flowIdParam) {
        super();

        this.setId(flowIdParam);
    }

    /**
     * Constructor with the Flow name.
     *
     * @param flowNameParam The Flow name.
     */
    public Flow(String flowNameParam) {
        super();

        this.setName(flowNameParam);
    }

    /**
     * Populates local variables with {@code jsonObjectParam}.
     *
     * @param jsonObjectParam The JSON Object.
     */
    public Flow(JSONObject jsonObjectParam) {
        super(jsonObjectParam);

        if(this.jsonObject == null)
        {
            return;
        }

        //Name...
        if (!this.jsonObject.isNull(JSONMapping.NAME)) {
            this.setName(this.jsonObject.getString(JSONMapping.NAME));
        }

        //Description...
        if (!this.jsonObject.isNull(JSONMapping.DESCRIPTION)) {
            this.setDescription(this.jsonObject.getString(JSONMapping.DESCRIPTION));
        }

        //Date Created...
        this.setDateCreated(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_CREATED));

        //Date Last Updated...
        this.setDateLastUpdated(this.getDateFieldValueFromFieldWithName(JSONMapping.DATE_LAST_UPDATED));

        //Flow Steps...
        if (!this.jsonObject.isNull(JSONMapping.FLOW_STEPS)) {

            JSONArray entryRules = this.jsonObject.getJSONArray(JSONMapping.FLOW_STEPS);

            List<FlowStep> listOfFlowSteps = new ArrayList();
            for(int index = 0;index < entryRules.length();index++)
            {
                listOfFlowSteps.add(new FlowStep(entryRules.getJSONObject(index)));
            }

            this.setFlowSteps(listOfFlowSteps);
        }
    }

    /**
     * Gets the Name of the Flow.
     *
     * @return Flow name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the Name of the Flow.
     *
     * @param nameParam Flow name.
     */
    public void setName(String nameParam) {
        this.name = nameParam;
    }

    /**
     * Gets the Description of the Flow.
     *
     * @return Flow description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the Description of the Flow.
     *
     * @param descriptionParam Flow description.
     */
    public void setDescription(String descriptionParam) {
        this.description = descriptionParam;
    }

    /**
     * Gets the date the Flow was created.
     *
     * @return The Flow creation date.
     */
    public Date getDateCreated() {
        return this.dateCreated;
    }

    /**
     * Sets the date the Flow was created.
     *
     * @param dateCreatedParam The Flow creation date.
     */
    public void setDateCreated(Date dateCreatedParam) {
        this.dateCreated = dateCreatedParam;
    }

    /**
     * Gets the date the Flow was last updated.
     *
     * @return Last Updated Timestamp.
     */
    public Date getDateLastUpdated() {
        return this.dateLastUpdated;
    }

    /**
     * Sets the date the Flow was last updated.
     *
     * @param dateLastUpdatedParam Last Updated Timestamp.
     */
    public void setDateLastUpdated(Date dateLastUpdatedParam) {
        this.dateLastUpdated = dateLastUpdatedParam;
    }

    /**
     * Gets the Flow steps.
     *
     * @return Flow Steps.
     */
    public List<FlowStep> getFlowSteps() {
        return this.flowSteps;
    }

    /**
     * Sets the Flow steps.
     *
     * @param flowStepsParam The Flow Steps.
     */
    public void setFlowSteps(List<FlowStep> flowStepsParam) {
        this.flowSteps = flowStepsParam;
    }

    /**
     * Conversion to {@code JSONObject} from Java Object.
     *
     * @return {@code JSONObject} representation of {@code Flow}
     * @throws JSONException If there is a problem with the JSON Body.
     *
     * @see ABaseFluidJSONObject#toJsonObject()
     */
    @Override
    public JSONObject toJsonObject() throws JSONException
    {
        JSONObject returnVal = super.toJsonObject();

        //Name...
        if(this.getName() != null)
        {
            returnVal.put(JSONMapping.NAME,this.getName());
        }

        //Description...
        if(this.getDescription() != null)
        {
            returnVal.put(JSONMapping.DESCRIPTION, this.getDescription());
        }

        //Flow Steps...
        if(this.getFlowSteps() != null && !this.getFlowSteps().isEmpty())
        {
            JSONArray jsonArray = new JSONArray();

            for(FlowStep rule : this.getFlowSteps())
            {
                jsonArray.put(rule.toJsonObject());
            }
            
            returnVal.put(JSONMapping.FLOW_STEPS, jsonArray);
        }

        //Date Created...
        if(this.getDateCreated() != null)
        {
            returnVal.put(JSONMapping.DATE_CREATED, this.getDateAsLongFromJson(
                    this.getDateCreated()));
        }

        //Date Last Updated...
        if(this.getDateLastUpdated() != null)
        {
            returnVal.put(JSONMapping.DATE_LAST_UPDATED, this.getDateAsLongFromJson(
                    this.getDateLastUpdated()));
        }

        return returnVal;
    }
}
