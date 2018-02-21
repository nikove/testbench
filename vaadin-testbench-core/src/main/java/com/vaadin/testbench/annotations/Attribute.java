package com.vaadin.testbench.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Additional qualifier to be used on element classes which do not use an unique
 * tag name.
 * <p>
 * While custom elements can be uniquely identified using their tag name, e.g.
 * <code>&lt;vaadin-button&gt;</code>. Flow views and server side composites
 * cannot be identified this way but typically needs an additional filter which
 * can be defined using this annotation.
 * <p>
 * For instance, given <code>public class MyView extends Div</code> and the
 * corresponding element class
 * <code>public class MyViewElement extends DivElement</code> would match any
 * <code>&lt;div&gt;</code> on the page. To uniquely match
 * <code>MyViewElement</code>, you could add e.g.<code>setId("myview");</code>
 * in the view and a corresponding
 * <code>@Attribute(name="id", value="myview")</code> for
 * <code>MyViewElement</code>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Attribute {

    public static final String DEFAULT_VALUE = "THE_DEFAULT_VALUE_WHICH_YOU_SURELY_NEVER_EVER_WILL_USE_FOR_REAL, RIGHT?!";

    /**
     * The name of the attribute to check.
     * <p>
     * Matches the value of the attribute with the value in {@link #value()} or
     * matches a token inside this value with {@link #contains()}. If neither
     * {@link #value()} nor {@link #contains()} is defined, only ensures that
     * the attribute is present.
     *
     * @return the name of the attribute to match
     */
    String name();

    /**
     * The value to match with the attribute value.
     * <p>
     * This will match the given value to the full value of the attribute. To
     * match only a token, use {@link #contains()}.
     *
     * @return the value to match with the attribute value
     */
    String value() default DEFAULT_VALUE;

    /**
     * The value to find within the attribute value.
     * <p>
     * This will match if the given string is one matches one of the space
     * separated tokens the attribute value consists of. To match the whole
     * attribute value, use {@link #value()}.
     *
     * @return the value find inside the attribute value
     */
    String contains() default DEFAULT_VALUE;
}
