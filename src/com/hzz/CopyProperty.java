package com.hzz;

import com.google.common.collect.Lists;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: hezz
 */
public class CopyProperty extends AnAction {

    private static final int visibleCount = 5;
    private static final int maxLength = 50;

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        ConverterConfig config = ConverterConfig.getInstance(project);
        SetterDialog sd = new SetterDialog(project, false, maxLength, new MyGenerateData(project, config.value));
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


                Editor editor = e.getData(PlatformDataKeys.EDITOR);
                int offset = editor.getCaretModel().getOffset();
                final String finalSourceCode = sourceCode;
                int endOffset = offset + finalSourceCode.length();
                Runnable r = () -> {
                    editor.getDocument().insertString(offset, finalSourceCode);
                    PsiFile file = psiClass.getContainingFile();
                    CodeStyleManager.getInstance(project).reformatText(file, offset, endOffset);
                    PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
                };
                WriteCommandAction.runWriteCommandAction(project, r);
            }
        }
    }

    protected static String format(String x, int maxLength) {
        return String.format("%-" + maxLength + "s", x);
    }

    static class MyGenerateData implements GenerateData {
        private Project project;
        private String includes;

        public MyGenerateData(Project project, String includes) {
            this.project = project;
            this.includes = includes;
        }

        @Override
        public String[] getData(String from) {
            if (from.indexOf('.') > 0) {
                PsiClass[] classes = JavaPsiFacade.getInstance(project).findClasses(from, GlobalSearchScope.allScope(project));
                if (classes.length > 0) {
                    String[] arr = new String[classes.length];
                    for (int i = 0; i < classes.length; i++) {
                        arr[i] = format(classes[i].getQualifiedName(), maxLength);
                    }
                    return arr;
                }
            } else {
                List<String> classNames = getQualifiedNamesIfExist(project, visibleCount, from);
                if (classNames.size() > 0) {
                    return classNames.stream().map(x -> format(x, maxLength)).collect(Collectors.toList()).toArray(new String[0]);
                }
            }

            String[] allClasses = PsiShortNamesCache.getInstance(project).getAllClassNames();
            List<String> matchedClasses = Arrays.stream(allClasses).filter(x -> x.indexOf('.') == -1).filter(x -> x.startsWith(from))
                    .sorted(Comparator.comparing(String::length)).collect(Collectors.toList());
            matchedClasses = getQualifiedNamesIfExist(project, visibleCount, matchedClasses.toArray(new String[0]));
            if(StringUtils.isNotEmpty(includes) && CollectionUtils.isNotEmpty(matchedClasses)) {
                List<String> includePrefixes = Arrays.stream(includes.split(",")).map(x->x.trim()).filter(x->StringUtils.isNotEmpty(x)).collect(Collectors.toList());
                matchedClasses = matchedClasses.stream().filter(x-> {
                    for(String s : includePrefixes) {
                        if(x.startsWith(s)) {
                            return true;
                        }
                    }
                    return false;
                }).collect(Collectors.toList());
            }
            matchedClasses = matchedClasses.subList(0, Math.min(matchedClasses.size(), visibleCount)).stream().map(x -> format(x, maxLength)).collect(Collectors.toList());
            return matchedClasses.toArray(new String[0]);
        }
    }

    protected static List<String> getQualifiedNamesIfExist(Project project, int maxCount, String... names) {
        List<String> list = Lists.newArrayList();
        for (String s : names) {
            PsiClass[] classes = PsiShortNamesCache.getInstance(project).getClassesByName(s, GlobalSearchScope.allScope(project));
            list.addAll(Arrays.stream(classes).map(x -> x.getQualifiedName()).collect(Collectors.toList()));
            if (list.size() >= maxCount) {
                break;
            }
        }
        return list;
    }

    protected String generateConvertCode(Project project, String from, String to) {
        PsiClass fromClazz = JavaPsiFacade.getInstance(project).findClass(from, GlobalSearchScope.allScope(project));
        PsiClass toClazz = JavaPsiFacade.getInstance(project).findClass(to, GlobalSearchScope.allScope(project));
        PsiField[] fromFields = fromClazz.getAllFields();
        Map<String, PsiField> map = Arrays.stream(fromFields).collect(Collectors.toMap(x -> x.getName(), x -> x));
        PsiField[] toFields = toClazz.getAllFields();
        StringBuilder sb = new StringBuilder();
        int lastDotPos = toClazz.getQualifiedName().lastIndexOf('.');
        String clazzName = toClazz.getQualifiedName().substring(lastDotPos + 1);
        sb.append("public ").append(clazzName).append(" convert(").append(fromClazz.getName()).append(" f) {\n")
                .append(toClazz.getName()).append(" t = new ").append(toClazz.getName()).append("();\n");
        for (PsiField f : toFields) {
            String fieldName = f.getName();
            if (map.containsKey(fieldName)) {
                String fieldNameInMethod = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                sb.append("t.set").append(fieldNameInMethod).append("(f.get").append(fieldNameInMethod).append("());\n");
            } else {
                sb.append("// ").append(fieldName).append("\n");
            }
        }
        sb.append("return t;\n").append("}\n");
        return sb.toString();
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
