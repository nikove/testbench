package com.vaadin.testbench.util;

import java.awt.event.KeyEvent;
import java.util.Date;

import com.vaadin.testbench.util.SeleniumHTMLTestCaseParser.Command;

public class JavaFileBuilder {

    private StringBuilder javaSource;
    private String testName;

    private String browserIdentifier;

    public JavaFileBuilder(String testName, String browser) {
        javaSource = new StringBuilder();
        this.testName = testName;
        this.browserIdentifier = browser;

    }

    public void appendPause(String delay) {
        javaSource.append("pause(");
        javaSource.append(replaceParameters(delay));
        javaSource.append(");\n");
    }

    public void appendCommandInfo(String command, String value) {
        // setCommand assumes second parameter is not null
        if (value == null) {
            value = "";
        }

        javaSource.append("cmd.setCommand(");
        javaSource.append(quotedSafeParameterString(command));
        javaSource.append(", ");
        javaSource.append(quotedSafeParameterString(value));
        javaSource.append(");\n");

    }

    /**
     * Append a command to the file. The command and all parameters are
     * sanitized to be java safe (newlines, quotes are escaped).
     * 
     * @param command
     * @param parameters
     */
    public void appendCommand(String command, String locator, String value) {
        javaSource.append("doCommand(");
        javaSource.append(quotedSafeParameterString(command));
        javaSource.append(",new String[] {");

        if (locator != null) {
            javaSource.append(quotedSafeParameterString(locator));
        }
        // TODO: Can locator be null and value != null. What should even happen
        // then?
        if (value != null) {
            if (locator != null) {
                javaSource.append(",");
            }

            javaSource
                    .append(quotedSafeParameterString(replaceParameters(value)));
        }
        javaSource.append("});\n");

        // if (command.endsWith("AndWait"))
        appendCode("waitForVaadin();\n");
    }

    private String replaceParameters(String value) {
        // Replace parameters
        value = ParameterUtil.translate(value);
        // Replace current day functions
        value = DateUtil.replaceDateFunctions(value, new Date());

        return value;
    }

    /**
     * @param string
     *            String to make safe. Must not be null.
     * @return
     */
    private static String makeJavaStringSafe(String string) {
        // The string must not contain quotes or newlines as this will cause
        // compilation errors
        string = string.replace("\\", "\\\\");
        string = string.replace("\n", "\\n");

        return string;
    }

    public void appendScreenshot(double errorTolerance, String imageIdentifier) {
        javaSource.append("validateScreenshot(");
        javaSource.append(quotedSafeParameterString(testName));
        javaSource.append(", " + errorTolerance + ", ");
        javaSource
                .append(quotedSafeParameterString(replaceParameters(imageIdentifier)));
        javaSource.append(");\n");
    }

    /**
     * Makes the string safe to be added as a parameter to a java function. The
     * return value is quoted if not null and any quotes in the supplied string
     * are escaped.
     * 
     * @param parameter
     *            The parameter to quote and make safe.
     * @return Parameter safe to be added as a parameter to a java function.
     */
    private String quotedSafeParameterString(String parameter) {
        if (parameter == null) {
            return "null";
        } else {
            return "\"" + makeJavaStringSafe(parameter) + "\"";
        }
    }

    public void appendCode(String string) {
        javaSource.append(string);

    }

    public void appendKeyModifierDown(boolean ctrl, boolean alt, boolean shift) {
        if (ctrl) {
            javaSource.append("selenium.keyDownNative(\"" + KeyEvent.VK_CONTROL
                    + "\");\n");
        }
        if (alt) {
            javaSource.append("selenium.keyDownNative(\"" + KeyEvent.VK_ALT
                    + "\");\n");
        }
        if (shift) {
            javaSource.append("selenium.keyDownNative(\"" + KeyEvent.VK_SHIFT
                    + "\");\n");
        }
    }

    public void appendKeyModifierUp(boolean ctrl, boolean alt, boolean shift) {
        if (ctrl) {
            javaSource.append("selenium.keyUpNative(\"" + KeyEvent.VK_CONTROL
                    + "\");\n");
        }
        if (alt) {
            javaSource.append("selenium.keyUpNative(\"" + KeyEvent.VK_ALT
                    + "\");\n");
        }
        if (shift) {
            javaSource.append("selenium.keyUpNative(\"" + KeyEvent.VK_SHIFT
                    + "\");\n");
        }
    }

    /**
     * Add native keypress command.
     * 
     * @param value
     *            ???. Must not be null.
     */
    public void appendKeyPressNative(String value) {
        javaSource.append("selenium.keyPressNative("
                + quotedSafeParameterString(value) + ");\n");

    }

    public void appendOpen(Command command) {
        javaSource.append("try {");
        appendCommand(command);
        javaSource.append("} catch (Exception e) {\n");
        javaSource.append("System.out.println(\"Open failed, retrying\");\n");
        javaSource.append("selenium.stop();\n");
        javaSource.append("selenium.start();\n");
        javaSource.append("clearDimensions();\n");

        // Use true here as screenshot is not correctly set before this.
        // Tests could probably always be run in maximized mode anyway.
        javaSource.append(TestConverter.getWindowInitFunctions(true));
        appendCommand(command);
        javaSource.append("}\n");
    }

    public void appendCommand(Command command) {
        appendCommand(command.getCmd(), command.getLocator(),
                command.getValue());
    }

    public String getJavaSource() {
        return javaSource.toString();
    }

    public String getPackageName() {
        return TestConverter.getJavaPackageName(testName, browserIdentifier);
    }

    public byte[] getJavaHeader() {
        return TestConverter.getJavaHeader(TestConverter.getSafeName(testName),
                getPackageName());
    }

    public void appendMouseClick(String locator, String value) {
        javaSource.append("doMouseClick(");
        javaSource.append(quotedSafeParameterString(locator));
        javaSource.append(",");
        javaSource.append(quotedSafeParameterString(replaceParameters(value)));
        javaSource.append(");\n");
    }

    public byte[] getBrowserTestMethod() {
        /* Create a test method for the browser. */
        StringBuilder browserInit = new StringBuilder();
        browserInit
                .append("public void test" + TestConverter.getSafeName(browserIdentifier)
                        + "() throws Throwable{\n");

        browserInit.append("setBrowserIdentifier(\"" + browserIdentifier + "\");\n");
        browserInit.append("super.setUp();\n");

        browserInit.append(TestConverter.getTestMethodName(testName) + "();");
        browserInit.append("\n}\n\n");

        return browserInit.toString().getBytes();
    }

    public String getTestName() {
        return testName;
    }

}
