package com.puresoltechnologies.javafx.perspectives;

import java.util.Arrays;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public abstract class AbstractPerspective implements Perspective {

    private static final long serialVersionUID = 2759295790738773633L;

    private BorderPane borderPane = null;
    private PerspectiveElement element = null;

    private final PerspectiveHandler perspectiveHandler;
    private final String name;

    public AbstractPerspective(String name) {
	super();
	this.perspectiveHandler = new PerspectiveHandler(this);
	this.name = name;
    }

    private void createNewContent() {
	element = createContent();
	setContext(element);
	borderPane.setCenter(element.getContent());
	((AbstractPerspectiveElement) element).setPerspectiveHandler(perspectiveHandler);
    }

    protected void setContext(PerspectiveElement element) {
	if (element instanceof PartSplit) {
	    ((PartSplit) element).setParent(this);
	    ((PartSplit) element).setPerspectiveHandler(perspectiveHandler);
	} else if (element instanceof PartStack) {
	    ((PartStack) element).setParent(this);
	    ((PartStack) element).setPerspectiveHandler(perspectiveHandler);
	} else {
	    throw new IllegalStateException("Element of type '" + element.getClass().getName() + "' is not supported.");
	}
    }

    protected abstract PerspectiveElement createContent();

    @Override
    public final String getName() {
	return name;
    }

    @Override
    public final void reset() {
	createNewContent();
    }

    @Override
    public final PerspectiveElement getRootElement() {
	return element;
    }

    @Override
    public final Node getContent() {
	if (borderPane == null) {
	    borderPane = new BorderPane();
	    createNewContent();
	}
	return borderPane;
    }

    @Override
    public PerspectiveElement getParent() {
	// There is no parent for a perspective.
	return null;
    }

    @Override
    public List<PerspectiveElement> getElements() {
	return Arrays.asList(element);
    }

    @Override
    public void addElement(PerspectiveElement e) {
	if (element != null) {
	    throw new IllegalStateException("Root element was already set.");
	}
	element = e;
	setContext(element);
	borderPane.setCenter(element.getContent());
    }

    @Override
    public void removeElement(String id) {
	if (id.equals(element.getId())) {
	    element = null;
	    borderPane.setCenter(null);
	}
    }

    @Override
    public void removeElement(PerspectiveElement element) {
	removeElement(element.getId());
    }

    @Override
    public boolean isSplit() {
	return false;
    }

}
