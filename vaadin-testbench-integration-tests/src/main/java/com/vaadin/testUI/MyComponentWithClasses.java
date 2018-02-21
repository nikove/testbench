package com.vaadin.testUI;

import com.vaadin.ui.html.Div;

public class MyComponentWithClasses extends Div {

    public MyComponentWithClasses() {
        addClassName("my-component-first");
        addClassName("my-component");
        addClassName("my-component-last");
        setText(getClass().getSimpleName());
    }

}
