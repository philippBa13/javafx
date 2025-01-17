package com.puresoltechnologies.javafx.perspectives;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import com.puresoltechnologies.javafx.perspectives.parts.Part;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public abstract class AbstractPerspective implements Perspective {

    private PerspectiveElement element = null;

    private final BorderPane borderPane = new BorderPane();
    private final UUID id = UUID.randomUUID();
    private final PerspectiveHandler perspectiveHandler;
    private final String name;

    public AbstractPerspective(String name) {
	super();
	this.perspectiveHandler = new PerspectiveHandler(this);
	this.name = name;
	createNewContent();
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
    public final UUID getId() {
	return id;
    }

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
	return borderPane;
    }

    @Override
    public final PerspectiveElement getParent() {
	// There is no parent for a perspective.
	return null;
    }

    @Override
    public final List<PerspectiveElement> getElements() {
	return Arrays.asList(element);
    }

    @Override
    public final void addElement(PerspectiveElement e) {
	if (element != null) {
	    throw new IllegalStateException("Root element was already set.");
	}
	element = e;
	setContext(element);
	borderPane.setCenter(element.getContent());
    }

    @Override
    public final void addElement(int index, PerspectiveElement e) {
	addElement(e);
    }

    @Override
    public final void removeElement(UUID id) {
	if (id.equals(element.getId())) {
	    element = null;
	    borderPane.setCenter(null);
	}
    }

    @Override
    public final void removeElement(PerspectiveElement element) {
	removeElement(element.getId());
    }

    @Override
    public final void openPart(Part part) {
	openPart(element, part);
    }

    private final void openPart(PerspectiveElement element, Part part) {
	if (element instanceof PartSplit) {
	    openPart(element.getElements().get(0), part);
	} else if (element instanceof PartStack) {
	    ((PartStack) element).openPart(part);
	}
    }

    @Override
    public Set<Part> findPart(Predicate<Part> filter) {
	Set<Part> parts = new HashSet<>();
	findPart(element, filter, parts);
	return parts;
    }

    private void findPart(PerspectiveElement parent, Predicate<Part> filter, Set<Part> parts) {
	for (PerspectiveElement element : parent.getElements()) {
	    if (PartStack.class.isAssignableFrom(element.getClass())) {
		PartStack partStack = (PartStack) element;
		partStack.getParts().stream().filter(filter).forEach(part -> parts.add(part));
	    } else {
		findPart(element, filter, parts);
	    }
	}
    }

}
