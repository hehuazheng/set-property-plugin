package com.hzz;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author: hezz
 */
@State(
        name="ConverterConfig",
        storages = {
                @Storage("converterConfig.xml")}
)
public class ConverterConfig implements PersistentStateComponent<ConverterConfig> {
    public String value;

    public ConverterConfig() {
    }

    @Nullable
    @Override
    public ConverterConfig getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ConverterConfig converterConfig) {
        XmlSerializerUtil.copyBean(converterConfig, this);
    }

    public static ConverterConfig getInstance(Project project) {
        ConverterConfig inst = ServiceManager.getService(project, ConverterConfig.class);
        return inst;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
