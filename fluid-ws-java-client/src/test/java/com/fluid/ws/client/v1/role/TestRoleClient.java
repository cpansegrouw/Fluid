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

package com.fluid.ws.client.v1.role;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fluid.program.api.vo.Form;
import com.fluid.program.api.vo.flow.Flow;
import com.fluid.program.api.vo.flow.FlowStep;
import com.fluid.program.api.vo.flow.JobView;
import com.fluid.program.api.vo.flow.JobViewListing;
import com.fluid.program.api.vo.role.*;
import com.fluid.program.api.vo.userquery.UserQuery;
import com.fluid.program.api.vo.ws.auth.AppRequestToken;
import com.fluid.ws.client.v1.ABaseClientWS;
import com.fluid.ws.client.v1.ABaseTestCase;
import com.fluid.ws.client.v1.flow.FlowClient;
import com.fluid.ws.client.v1.flow.FlowStepClient;
import com.fluid.ws.client.v1.flow.TestFlowStepClient;
import com.fluid.ws.client.v1.user.LoginClient;
import com.fluid.ws.client.v1.userquery.TestUserQueryClient;
import com.fluid.ws.client.v1.userquery.UserQueryClient;

/**
 * Created by jasonbruwer on 14/12/22.
 */
public class TestRoleClient extends ABaseTestCase {

    private LoginClient loginClient;

    /**
     *
     */
    public static final class TestStatics {

        /**
         *
         */
        public static final class Permission {

            public static final String CHANGE_OWN_PASSWORD = "change_own_password";
            public static final String EDIT_ROLES = "edit_roles";
            public static final String VIEW_ROLES = "view_roles";
            public static final String VIEW_USERS = "view_users";

            //Update...
            public static final String VIEW_AUDIT_LOG = "view_audit_log";
            public static final String VIEW_SERVER_LOG = "view_server_log";
            public static final String REVEAL_MASKED_FIELD = "reveal_masked_field";
        }

        /**
         *
         */
        public static final class Create {

            public static final String ROLE_NAME = "junit Testing Role";
            public static final String ROLE_DESCRIPTION = "junit Testing Role DESCRIPTION.";

            public static final List<String> PERMISSIONS = new ArrayList<>();
            static {
                PERMISSIONS.add(Permission.CHANGE_OWN_PASSWORD);
                PERMISSIONS.add(Permission.EDIT_ROLES);
                PERMISSIONS.add(Permission.VIEW_ROLES);
                PERMISSIONS.add(Permission.VIEW_USERS);
            }
        }

        /**
         *
         */
        public static final class Update {

            public static final String ROLE_NAME = "junit Testing Role - Update";
            public static final String ROLE_DESCRIPTION = "junit Testing Role DESCRIPTION. - Update";

            public static final List<String> PERMISSIONS = new ArrayList<>();
            static {
                PERMISSIONS.add(Permission.CHANGE_OWN_PASSWORD);
                PERMISSIONS.add(Permission.VIEW_USERS);
                PERMISSIONS.add(Permission.VIEW_AUDIT_LOG);
                PERMISSIONS.add(Permission.VIEW_SERVER_LOG);
                PERMISSIONS.add(Permission.REVEAL_MASKED_FIELD);
            }
        }

        /**
         *
         * @param formFieldToFormDefParam
         * @param canCreateAndModParam
         * @param canViewParam
         * @return
         */
        public static final List<RoleToFormFieldToFormDefinition> toRoleToFormFieldToFormDefinition(
                FormFieldToFormDefinition formFieldToFormDefParam,
                boolean canCreateAndModParam,
                boolean canViewParam){

            List<RoleToFormFieldToFormDefinition> returnVal = new ArrayList<>();

            RoleToFormFieldToFormDefinition roleToFormFieldToFormDef =
                    new RoleToFormFieldToFormDefinition();

            roleToFormFieldToFormDef.setCanCreateAndModify(canCreateAndModParam);
            roleToFormFieldToFormDef.setCanView(canViewParam);
            roleToFormFieldToFormDef.setFormFieldToFormDefinition(formFieldToFormDefParam);

            returnVal.add(roleToFormFieldToFormDef);

            return returnVal;
        }

        /**
         *
         * @param formParam
         * @param canCreateParam
         * @return
         */
        public static final List<RoleToFormDefinition> toRoleToFormFormDefinition(
                Form formParam,
                boolean canCreateParam){

            List<RoleToFormDefinition> returnVal = new ArrayList<>();

            RoleToFormDefinition toAdd = new RoleToFormDefinition();
            toAdd.setCanCreate(canCreateParam);
            toAdd.setFormDefinition(formParam);

            returnVal.add(toAdd);

            return returnVal;
        }

        /**
         *
         * @param jobViewsParam
         * @return
         */
        public static final List<RoleToJobView> toRoleToJobView(
                JobView ... jobViewsParam){

            List<RoleToJobView> returnVal = new ArrayList<>();

            if(jobViewsParam != null)
            {
                for(JobView jobView : jobViewsParam)
                {
                    RoleToJobView toAdd = new RoleToJobView();
                    toAdd.setJobView(jobView);

                    returnVal.add(toAdd);
                }
            }

            return returnVal;
        }

        /**
         *
         * @param userQueriesParam
         * @return
         */
        public static final List<RoleToUserQuery> toRoleToUserQuery(
                UserQuery ... userQueriesParam){

            List<RoleToUserQuery> returnVal = new ArrayList<>();

            if(userQueriesParam != null)
            {
                for(UserQuery userQuery : userQueriesParam)
                {
                    RoleToUserQuery toAdd = new RoleToUserQuery();
                    toAdd.setUserQuery(userQuery);

                    returnVal.add(toAdd);
                }
            }

            return returnVal;
        }
    }

        /**
     *
     */
    @Before
    public void init() {
        ABaseClientWS.IS_IN_JUNIT_TEST_MODE = true;

        //this.loginClient = new LoginClient("http://fluid.sahousingclub.co.za/fluid-ws/");
        this.loginClient = new LoginClient();
    }

    /**
     *
     */
    @After
    public void destroy()
    {
        this.loginClient.closeAndClean();
    }

    /**
     *
     */
    @Test
    public void testRole_CRUD() {
        if (!this.loginClient.isConnectionValid()) {
            return;
        }

        AppRequestToken appRequestToken = this.loginClient.login(USERNAME, PASSWORD);
        TestCase.assertNotNull(appRequestToken);

        String serviceTicket = appRequestToken.getServiceTicket();

        //JOB VIEW...
        FlowClient flowClient = new FlowClient(serviceTicket);
        FlowStepClient flowStepClient = new FlowStepClient(serviceTicket);

        //1. The Test Flow Step...
        Flow createdFlow = new Flow();
        createdFlow.setName("JUnit Test Flow");
        createdFlow.setDescription("Test Flow Description.");
        createdFlow = flowClient.createFlow(createdFlow);

        FlowStep toCreate = new FlowStep();
        toCreate.setName(TestFlowStepClient.TestStatics.FLOW_STEP_NAME);
        toCreate.setDescription(TestFlowStepClient.TestStatics.FLOW_STEP_DESCRIPTION);
        toCreate.setFlow(createdFlow);
        toCreate.setFlowStepType(FlowStep.StepType.ASSIGNMENT);

        //2. CREATE...
        FlowStep createdFlowStep = flowStepClient.createFlowStep(toCreate);

        TestCase.assertNotNull(
                "RoleToJobView 'Step' not set.", createdFlowStep);
        TestCase.assertNotNull(
                "RoleToJobView 'Step Id' not set.", createdFlowStep.getId());

        JobViewListing jobViewListing =
                flowStepClient.getJobViewsByStepId(createdFlowStep.getId());

        TestCase.assertNotNull(
                "RoleToJobView 'Job Views' not set.", jobViewListing);

        JobView firstJobView = jobViewListing.getListing().get(0);

        TestCase.assertNotNull(
                "RoleToJobView 'First Job View' not set.", firstJobView);

        //-- Role to User Query...
        UserQueryClient userQueryClient = new UserQueryClient(serviceTicket);

        //1. Text...
        UserQuery userQueryFirst = new UserQuery();
        userQueryFirst.setName(TestUserQueryClient.TestStatics.NAME);
        userQueryFirst.setDescription(TestUserQueryClient.TestStatics.DESCRIPTION);
        userQueryFirst.setRules(TestUserQueryClient.TestStatics.toRuleListing(TestUserQueryClient.TestStatics.RULE_NR_1));
        userQueryFirst.setInputs(TestUserQueryClient.TestStatics.toFieldListing(
                TestUserQueryClient.TestStatics.RESULT_FIELD_1,
                TestUserQueryClient.TestStatics.RESULT_FIELD_1_NAME));

        //2. Create...
        userQueryFirst = userQueryClient.createUserQuery(userQueryFirst);

        UserQuery userQuerySecond = new UserQuery();
        userQuerySecond.setName(TestUserQueryClient.TestStatics.UPDATE_NAME);
        userQuerySecond.setDescription(TestUserQueryClient.TestStatics.UPDATE_DESCRIPTION);
        userQuerySecond.setRules(TestUserQueryClient.TestStatics.toRuleListing(TestUserQueryClient.TestStatics.RULE_NR_1));
        userQuerySecond.setInputs(TestUserQueryClient.TestStatics.toFieldListing(
                TestUserQueryClient.TestStatics.RESULT_FIELD_1,
                TestUserQueryClient.TestStatics.RESULT_FIELD_1_NAME));

        userQuerySecond = userQueryClient.createUserQuery(userQuerySecond);


        RoleClient roleClient = new RoleClient(serviceTicket);

        Role roleToCreate = new Role();
        roleToCreate.setName(TestStatics.Create.ROLE_NAME);
        roleToCreate.setDescription(TestStatics.Create.ROLE_DESCRIPTION);
        roleToCreate.setAdminPermissions(TestStatics.Create.PERMISSIONS);
        roleToCreate.setRoleToFormFieldToFormDefinitions(
                TestStatics.toRoleToFormFieldToFormDefinition(
                        new FormFieldToFormDefinition(1L), true,true));
        roleToCreate.setRoleToFormDefinitions(
                TestStatics.toRoleToFormFormDefinition(new Form(1L), true));
        roleToCreate.setRoleToJobViews(TestStatics.toRoleToJobView(firstJobView));
        roleToCreate.setRoleToUserQueries(TestStatics.toRoleToUserQuery(userQueryFirst));

        roleToCreate = roleClient.createRole(roleToCreate);

        //Created... Test...
        TestCase.assertNotNull(roleToCreate);
        TestCase.assertNotNull("The 'Id' needs to be set.", roleToCreate.getId());
        TestCase.assertEquals("'Name' mismatch.", TestStatics.Create.ROLE_NAME,
                roleToCreate.getName());
        TestCase.assertEquals("'Description' mismatch.", TestStatics.Create.ROLE_DESCRIPTION,
                roleToCreate.getDescription());

        //Admin Permissions...
        TestCase.assertNotNull("'Admin Permissions' not set.",roleToCreate.getAdminPermissions());
        //Due to login permission...
        TestCase.assertEquals("'Admin Permission' count mismatch.",
                TestStatics.Create.PERMISSIONS.size() + 1, roleToCreate.getAdminPermissions().size());

        //Role To Form Field To Form Definitions...
        TestCase.assertNotNull(
                "'Role To Form Field To Form Definitions' not set.",
                roleToCreate.getRoleToFormFieldToFormDefinitions());

        RoleToFormFieldToFormDefinition firstRTFFTFD =
                roleToCreate.getRoleToFormFieldToFormDefinitions().get(0);

        TestCase.assertNotNull(
                "'RoleToFormFieldToFormDefinition' not set.", firstRTFFTFD);

        TestCase.assertNotNull(
                "RoleToFormFieldToFormDefinition 'Id' not set.", firstRTFFTFD.getId());

        //Role to Form Definition...
        TestCase.assertNotNull(
                "'Role To Form Definitions' not set.",
                roleToCreate.getRoleToFormDefinitions());

        RoleToFormDefinition firstRTFD = roleToCreate.getRoleToFormDefinitions().get(0);

        TestCase.assertNotNull(
                "'RoleToFormDefinition' not set.", firstRTFD);

        TestCase.assertNotNull(
                "RoleToFormDefinition 'Id' not set.", firstRTFD.getId());

        //Role to JobView...
        TestCase.assertNotNull(
                "'Role To Job View' not set.",
                roleToCreate.getRoleToJobViews());

        RoleToJobView firstRTJV = roleToCreate.getRoleToJobViews().get(0);

        TestCase.assertNotNull(
                "'RoleToJobView' not set.", firstRTJV);

        TestCase.assertNotNull(
                "RoleToJobView 'Id' not set.", firstRTJV.getId());

        TestCase.assertNotNull(
                "'Role To User Queries' not set.",
                roleToCreate.getRoleToUserQueries());

        RoleToUserQuery firstRTUQ = roleToCreate.getRoleToUserQueries().get(0);

        TestCase.assertNotNull(
                "'RoleToUserQuery' not set.", firstRTUQ);

        TestCase.assertNotNull(
                "RoleToUserQuery 'Id' not set.", firstRTUQ.getId());

        //2. FETCH...
        RoleListing roleListing = roleClient.getAllRoles();

        TestCase.assertNotNull(roleListing);
        TestCase.assertTrue("Role listing must be greater than '0'.",
                roleListing.getListingCount() > 0);
        TestCase.assertNotNull("Role Listing must be set.",roleListing.getListing());
        TestCase.assertNotNull("Role must be set.",roleListing.getListing().get(0));

        //Get by Id
        Role role = roleClient.getRoleById(roleToCreate.getId());
        TestCase.assertNotNull(role);

        //3. UPDATE...
        Role roleToUpdate = new Role();
        roleToUpdate.setId(roleToCreate.getId());
        roleToUpdate.setName(TestStatics.Update.ROLE_NAME);
        roleToUpdate.setDescription(TestStatics.Update.ROLE_DESCRIPTION);
        roleToUpdate.setAdminPermissions(TestStatics.Update.PERMISSIONS);
        roleToUpdate.setRoleToFormFieldToFormDefinitions(new ArrayList<RoleToFormFieldToFormDefinition>());
        roleToUpdate.setRoleToFormDefinitions(new ArrayList<RoleToFormDefinition>());
        JobView secondJobView = jobViewListing.getListing().get(1);
        roleToUpdate.setRoleToJobViews(TestStatics.toRoleToJobView(
                firstJobView,secondJobView));
        roleToUpdate.setRoleToUserQueries(TestStatics.toRoleToUserQuery(
                userQueryFirst,userQuerySecond));

        roleToUpdate = roleClient.updateRole(roleToUpdate);

        //Updated... Test...
        TestCase.assertNotNull(roleToUpdate);
        TestCase.assertNotNull("UPDATE: The 'Id' needs to be set.", roleToUpdate.getId());
        TestCase.assertEquals("UPDATE: 'Name' mismatch.", TestStatics.Update.ROLE_NAME,
                roleToUpdate.getName());
        TestCase.assertEquals("UPDATE: 'Description' mismatch.", TestStatics.Update.ROLE_DESCRIPTION,
                roleToUpdate.getDescription());

        //Admin Permissions...
        TestCase.assertNotNull("UPDATE: 'Admin Permissions' not set.",roleToUpdate.getAdminPermissions());
        //Due to login permission...
        TestCase.assertEquals("UPDATE: 'Admin Permission' count mismatch.",
                TestStatics.Update.PERMISSIONS.size() + 1, roleToUpdate.getAdminPermissions().size());

        //Role To Form Field To Form Definitions...
        TestCase.assertNull(
                "UPDATE: 'Role To Form Field To Form Definitions' must not be set.",
                roleToUpdate.getRoleToFormFieldToFormDefinitions());

        //Role to Form Definition...
        TestCase.assertNull(
                "UPDATE: 'Role To Form Definitions' must not be set.",
                roleToUpdate.getRoleToFormDefinitions());

        //Role to JobView...
        TestCase.assertNotNull(
                "UPDATE: 'Role To Job View' not set.",
                roleToUpdate.getRoleToJobViews());

        TestCase.assertEquals(
                "UPDATE: 'Role To Job View' incorrect count.",
                2,roleToUpdate.getRoleToJobViews().size());

        //Role to UserQuery...
        TestCase.assertNotNull(
                "UPDATE: 'Role To User Query' not set.",
                roleToUpdate.getRoleToUserQueries());

        TestCase.assertEquals(
                "UPDATE: 'Role To User Query' incorrect count.",
                2,roleToUpdate.getRoleToUserQueries().size());

        //First delete role...Cleanup...
        roleClient.deleteRole(roleToUpdate,true);

        //Delete User Query...
        flowClient.deleteFlow(createdFlow);
        userQueryClient.deleteUserQuery(userQueryFirst,true);
        userQueryClient.deleteUserQuery(userQuerySecond,true);
    }
}
