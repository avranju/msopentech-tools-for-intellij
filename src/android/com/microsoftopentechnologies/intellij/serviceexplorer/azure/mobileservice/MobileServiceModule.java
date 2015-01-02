/**
 * Copyright 2014 Microsoft Open Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.microsoftopentechnologies.intellij.serviceexplorer.azure.mobileservice;

import com.intellij.openapi.application.ApplicationManager;
import com.microsoftopentechnologies.intellij.forms.CreateNewServiceForm;
import com.microsoftopentechnologies.intellij.helpers.UIHelper;
import com.microsoftopentechnologies.intellij.helpers.azure.AzureCmdException;
import com.microsoftopentechnologies.intellij.helpers.azure.AzureRestAPIManager;
import com.microsoftopentechnologies.intellij.model.Service;
import com.microsoftopentechnologies.intellij.model.Subscription;
import com.microsoftopentechnologies.intellij.serviceexplorer.Node;
import com.microsoftopentechnologies.intellij.serviceexplorer.NodeActionEvent;
import com.microsoftopentechnologies.intellij.serviceexplorer.NodeActionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MobileServiceModule extends Node {
    private static final String MOBILE_SERVICE_MODULE_ID = MobileServiceModule.class.getName();
    private static final String ICON_PATH = "mobileservices.png";
    private static final String BASE_MODULE_NAME = "Mobile Services";

    public MobileServiceModule(Node parent) {
        super(MOBILE_SERVICE_MODULE_ID, BASE_MODULE_NAME, parent, ICON_PATH, true);
    }

    @Override
    protected void refreshItems() throws AzureCmdException {
        // remove all child mobile service nodes
        removeAllChildNodes();

        // load all mobile services
        ArrayList<Subscription> subscriptionList = AzureRestAPIManager.getManager().getSubscriptionList();
        if(subscriptionList != null) {
            for (Subscription subscription : subscriptionList) {
                List<Service> mobileServices = AzureRestAPIManager.getManager().getServiceList(subscription.getId());
                for(Service mobileService : mobileServices) {
                    addChildNode(new MobileServiceNode(this, mobileService));
                }
            }
        }
    }

    @Override
    protected Map<String, Class<? extends NodeActionListener>> initActions() {
        // register the sole create service action
        addAction("Create service", new CreateServiceAction());
        return null;
    }

    public class CreateServiceAction extends NodeActionListener {
        @Override
        public void actionPerformed(NodeActionEvent e) {
            CreateNewServiceForm form = new CreateNewServiceForm();
            form.setServiceCreated(new Runnable() {
                @Override
                public void run() {
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                refreshItems();
                            } catch (AzureCmdException e1) {
                                UIHelper.showException("An error occurred while creating the mobile service.", e1);
                            }
                        }
                    });
                }
            });

            form.setModal(true);
            UIHelper.packAndCenterJDialog(form);
            form.setVisible(true);
        }
    }
}