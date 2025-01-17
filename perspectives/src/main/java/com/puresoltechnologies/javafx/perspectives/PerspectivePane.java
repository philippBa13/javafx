package com.puresoltechnologies.javafx.perspectives;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.puresoltechnologies.javafx.extensions.toolbar.DraggableToolBar;
import com.puresoltechnologies.javafx.extensions.toolbar.ToolBarDockPane;
import com.puresoltechnologies.javafx.perspectives.dialogs.PerspectiveSelectionDialog;
import com.puresoltechnologies.javafx.perspectives.parts.Part;
import com.puresoltechnologies.javafx.perspectives.tasks.OpenPartTask;
import com.puresoltechnologies.javafx.preferences.Preferences;
import com.puresoltechnologies.javafx.utils.FXThreads;
import com.puresoltechnologies.javafx.utils.ResourceUtils;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PerspectivePane extends ToolBarDockPane {

    private static final ObjectProperty<ContentDisplay> toolBarContentDisplay = Preferences
	    .getProperty(PerspectiveProperties.perspectiveToolbarContentDisplay);

    private final DraggableToolBar toolBar;
    private final SplitMenuButton openPerspectiveButton;
    private final List<Perspective> perspectives = new ArrayList<>();
    private Perspective currentPerspective = null;
    private final PerspectiveContainerPane perspectiveContainerPane = new PerspectiveContainerPane();

    PerspectivePane() {
	super();
	try {
	    ImageView switchWidowsImage = ResourceUtils.getImageView(this,
		    "icons/FatCow_Icons16x16/switch_windows.png");
	    openPerspectiveButton = new SplitMenuButton();
	    openPerspectiveButton.setText("Open...");
	    openPerspectiveButton.setGraphic(switchWidowsImage);
	    openPerspectiveButton.setId("OpenPerspectivesButton");
	    openPerspectiveButton.setContentDisplay(toolBarContentDisplay.get());
	    updateOpenPerspectiveButton();

	    ImageView watchWidowImage = ResourceUtils.getImageView(this, "icons/FatCow_Icons16x16/watch_window.png");
	    Button showViewButton = new Button("Show Part...", watchWidowImage);
	    showViewButton.setId("ShowViewButton");
	    showViewButton.setContentDisplay(toolBarContentDisplay.get());

	    ImageView resetPerspectiveImage = ResourceUtils.getImageView(this, "icons/FatCow_Icons16x16/undo.png");
	    Button resetButton = new Button("Reset", resetPerspectiveImage);
	    resetPerspectiveImage.setId("resetPerspectiveButton");
	    resetButton.setContentDisplay(toolBarContentDisplay.get());

	    ImageView closePerspectiveImage = ResourceUtils.getImageView(this, "icons/FatCow_Icons16x16/cross.png");
	    Button closeButton = new Button("Close", closePerspectiveImage);
	    closePerspectiveImage.setId("ClosePerspectiveButton");
	    closeButton.setContentDisplay(toolBarContentDisplay.get());

	    openPerspectiveButton.setOnAction(event -> openNewPerspective());
	    showViewButton.setOnAction(event -> {
		FXThreads.runOnFXThread(new OpenPartTask());
		event.consume();
	    });
	    resetButton.setOnAction(event -> resetCurrentPerspective());
	    closeButton.setOnAction(event -> closeCurrentPerspective());

	    toolBar = new DraggableToolBar();
	    toolBar.getItems().addAll(openPerspectiveButton, showViewButton, resetButton, closeButton);
	    setTop(toolBar);
	    setCenter(perspectiveContainerPane);
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

    private void updateOpenPerspectiveButton() {
	ObservableList<MenuItem> items = openPerspectiveButton.getItems();
	items.clear();
	perspectives.forEach(perspective -> {
	    MenuItem item = new MenuItem(perspective.getName());
	    Optional<Image> image = perspective.getImage();
	    if (image.isPresent()) {
		item.setGraphic(new ImageView(image.get()));
	    }
	    item.setOnAction(event -> {
		PerspectivePane.this.selectPerspective(perspective.getId());
		event.consume();
	    });
	    openPerspectiveButton.getItems().add(item);
	});
    }

    private void openNewPerspective() {
	Optional<Perspective> perspective = new PerspectiveSelectionDialog().showAndWait();
	if (perspective.isPresent()) {
	    addPerspective(perspective.get());
	}
    }

    private void resetCurrentPerspective() {
	if (currentPerspective != null) {
	    currentPerspective.reset();
	    perspectiveContainerPane.setRootElement(currentPerspective.getRootElement());
	}
    }

    private void closeCurrentPerspective() {
	removePerspective(currentPerspective);
    }

    public void addPerspective(Class<? extends Perspective> clazz) {
	try {
	    Perspective perspective = clazz.getConstructor().newInstance();
	    addPerspective(perspective);
	} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
		| NoSuchMethodException | SecurityException e) {
	    throw new RuntimeException();
	}
    }

    public void addPerspective(Perspective perspective) {
	this.perspectives.add(perspective);
	currentPerspective = perspective;
	FXThreads.proceedOnFXThread(() -> {
	    updateOpenPerspectiveButton();
	    perspectiveContainerPane.setRootElement(currentPerspective.getRootElement());
	});
    }

    public void removeAllPerspectives() {
	perspectives.forEach(perspective -> closeAllParts(perspective));
	perspectives.clear();
	currentPerspective = null;
	FXThreads.proceedOnFXThread(() -> {
	    perspectiveContainerPane.setRootElement(null);
	    updateOpenPerspectiveButton();
	});
    }

    public void removePerspective(Perspective perspective) {
	closeAllParts(perspective);
	this.perspectives.remove(perspective);
	FXThreads.proceedOnFXThread(() -> {
	    if (perspectives.size() > 0) {
		currentPerspective = perspectives.get(perspectives.size() - 1);
		perspectiveContainerPane.setRootElement(currentPerspective.getRootElement());
	    } else {
		currentPerspective = null;
		perspectiveContainerPane.setRootElement(null);
	    }
	    updateOpenPerspectiveButton();
	});
    }

    public void removePerspective(String perspectiveId) {
	Iterator<Perspective> iterator = perspectives.iterator();
	while (iterator.hasNext()) {
	    Perspective perspective = iterator.next();
	    if (perspective.getId().toString().equals(perspectiveId)) {
		closeAllParts(perspective);
		iterator.remove();
	    }
	}
	if (perspectives.size() > 0) {
	    currentPerspective = perspectives.get(perspectives.size() - 1);
	    perspectiveContainerPane.setRootElement(currentPerspective.getRootElement());
	} else {
	    perspectiveContainerPane.setRootElement(null);
	}
    }

    private void closeAllParts(Perspective perspective) {
	perspective.getElements().forEach(element -> closeAllParts(element));
    }

    private Object closeAllParts(PerspectiveElement element) {
	if (element instanceof PartStack) {
	    PartStack partStack = (PartStack) element;
	    partStack.getParts().forEach(part -> part.close());
	} else if (element instanceof PartSplit) {
	    element.getElements().forEach(e -> closeAllParts(e));
	}
	return null;
    }

    public void selectPerspective(UUID perspectiveId) {
	Iterator<Perspective> iterator = perspectives.iterator();
	while (iterator.hasNext()) {
	    Perspective perspective = iterator.next();
	    if (perspective.getId().equals(perspectiveId)) {
		currentPerspective = perspective;
		perspectiveContainerPane.setRootElement(currentPerspective.getRootElement());
	    }
	}
    }

    public Perspective getCurrentPerspective() {
	return currentPerspective;
    }

    public void setActive(UUID id) {
	Part part = currentPerspective.findPartById(id);
	((PartStack) part.getParent()).setActive(id);
    }
}
