package com.hzz;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction.Simple;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: hezz
 */
public class CopyProperty extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        SetterDialog sd = new SetterDialog(project, false);
        sd.show();
        if (sd.isOK()) {
            PsiClass psiClass = getPsiMethodFromContext(e);
            if (StringUtils.isNotEmpty(sd.getText1()) && StringUtils.isNotEmpty(sd.getText2())) {
                String sourceCode;
                try {
                    sourceCode = generateConvertCode(project, sd.getText1(), sd.getText2());
                } catch (Exception ex) {
                    sourceCode = ex.getMessage() + "\n" + ExceptionUtils.getStackTrace(ex);
                }
                generateConvertMethod(psiClass, sourceCode);
            }
        }
    }


    protected String generateConvertCode(Project project, String from, String to) {
        PsiClass fromClazz = JavaPsiFacade.getInstance(project).findClass(from, GlobalSearchScope.allScope(project));
        PsiClass toClazz = JavaPsiFacade.getInstance(project).findClass(to, GlobalSearchScope.allScope(project));
        PsiField[] fromFields = fromClazz.getAllFields();
        Map<String, PsiField> map = Arrays.stream(fromFields).collect(Collectors.toMap(x -> x.getName(), x -> x));
        PsiField[] toFields = toClazz.getAllFields();
        StringBuilder sb = new StringBuilder();
        sb.append("public ").append(toClazz.getQualifiedName()).append(" convert(").append(fromClazz.getName()).append(" f) {\n")
                .append(toClazz.getName()).append(" t = new ").append(toClazz.getName()).append("();\n");
        for (PsiField f : toFields) {
            String fieldName = f.getName();
            if (map.containsKey(fieldName)) {
                String fieldNameInMethod = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                sb.append("t.set").append(fieldNameInMethod).append("(f.get").append(fieldNameInMethod).append("());\n");
            }
        }
        sb.append("return t;\n").append("}\n");
        return sb.toString();
    }

    private void generateConvertMethod(final PsiClass psiMethod, final String body) {
        if (psiMethod != null && psiMethod.getProject() != null) {
            (new Simple(psiMethod.getProject(), new PsiFile[]{psiMethod.getContainingFile()}) {
                protected void run() throws Throwable {
                    create(psiMethod, body);
                }
            }).execute();
        }
    }

    private void create(PsiClass psiClass, String methodText) {
        PsiElementFactory elementFactory1 = JavaPsiFacade.getInstance(psiClass.getProject()).getElementFactory();
        PsiMethod toMethod = elementFactory1.createMethodFromText(methodText, psiClass);
        psiClass.add(toMethod);
    }

    private PsiClass getPsiMethodFromContext(AnActionEvent e) {
        PsiElement elementAt = this.getPsiElement(e);
        return elementAt == null ? null : PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
    }

    private PsiElement getPsiElement(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (psiFile != null && editor != null) {
            int offset = editor.getCaretModel().getOffset();
            return psiFile.findElementAt(offset);
        } else {
            e.getPresentation().setEnabled(false);
            return null;
        }
    }
}
