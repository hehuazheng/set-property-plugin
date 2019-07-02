package com.hzz;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author: hezz
 */
public class ConverterConfigurable implements SearchableConfigurable {
    private final ConverterConfig config;
    private final Project project;
    private ConverterGui gui;

    public ConverterConfigurable(@NotNull Project project) {
       this.project = project;
       this.config = ConverterConfig.getInstance(project);
    }

    @NotNull
    @Override
    public String getId() {
        return "preference.ConverterConfigurable";
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "hezz's converter lugin";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        gui = new ConverterGui(config);

        return gui.getPanel();
    }

    @Override
    public boolean isModified() {
        return gui.isModified();
    }

    // when ok or apply is clicked this method will be invoked
    @Override
    public void apply() throws ConfigurationException {
        gui.apply();
    }

    @Override
    public void disposeUIResources() {
        gui = null;
    }
}
