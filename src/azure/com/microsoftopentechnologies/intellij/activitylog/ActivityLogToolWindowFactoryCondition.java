/**
 * Copyright 2015 Microsoft Open Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.microsoftopentechnologies.intellij.activitylog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.microsoftopentechnologies.intellij.AzurePlugin;

public class ActivityLogToolWindowFactoryCondition implements Condition<Project> {
    @Override
    public boolean value(Project project) {
        return !AzurePlugin.IS_ANDROID_STUDIO && AzurePlugin.IS_WINDOWS;
    }
}
