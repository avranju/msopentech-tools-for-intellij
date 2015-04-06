package com.microsoftopentechnologies.intellij.serviceexplorer.azure.mobileservice;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.microsoftopentechnologies.intellij.components.DefaultLoader;
import com.microsoftopentechnologies.intellij.forms.TableForm;
import com.microsoftopentechnologies.intellij.helpers.Name;
import com.microsoftopentechnologies.intellij.helpers.azure.AzureCmdException;
import com.microsoftopentechnologies.intellij.helpers.azure.rest.AzureRestAPIManagerImpl;
import com.microsoftopentechnologies.intellij.model.ms.MobileService;
import com.microsoftopentechnologies.intellij.model.ms.Table;
import com.microsoftopentechnologies.intellij.serviceexplorer.NodeActionEvent;
import com.microsoftopentechnologies.intellij.serviceexplorer.NodeActionListener;

@Name("Edit table")
public class EditTableAction extends NodeActionListener {
    private TableNode tableNode;

    public EditTableAction(TableNode tableNode) {
        this.tableNode = tableNode;
    }

    @Override
    public void actionPerformed(NodeActionEvent e) {
        // get the parent MobileServiceNode node
        MobileServiceNode mobileServiceNode = (MobileServiceNode) tableNode.findParentByType(MobileServiceNode.class);
        final MobileService mobileService = mobileServiceNode.getMobileService();

        runAsBackground("Editing table " + tableNode.getName() + "...", new Runnable() {
            @Override
            public void run() {
                Table selectedTable = null;
                try {
                    selectedTable = AzureRestAPIManagerImpl.getManager().showTableDetails(
                            mobileService.getSubcriptionId(),
                            mobileService.getName(),
                            tableNode.table.getName());

                    TableForm form = new TableForm();
                    form.setServiceName(mobileService.getName());
                    form.setSubscriptionId(mobileService.getSubcriptionId());
                    form.setEditingTable(selectedTable);
                    form.setProject((Project) tableNode.getProject());
                    DefaultLoader.getUIHelper().packAndCenterJDialog(form);
                    form.setVisible(true);
                } catch (AzureCmdException e1) {
                    DefaultLoader.getUIHelper().showException("Error editing table", e1);
                }
            }
        });
    }

    public void runAsBackground(final String status, final Runnable runnable) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                ProgressManager.getInstance().run(
                        new Task.Backgroundable((Project) tableNode.getProject(), status, false) {
                            @Override
                            public void run(ProgressIndicator progressIndicator) {
                                runnable.run();
                            }
                        });
            }
        });
    }
}
