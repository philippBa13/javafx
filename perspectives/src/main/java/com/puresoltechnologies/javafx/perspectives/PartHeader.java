package com.puresoltechnologies.javafx.perspectives;

import java.io.IOException;

import com.puresoltechnologies.javafx.utils.ResourceUtils;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;

public class PartHeader extends HBox {

    private final Part part;

    public PartHeader(PartStack partStack, Part part) {
	super();
	this.part = part;
	try {
	    ImageView imageView = ResourceUtils.getImageView(this, "/icons/FatCow_Icons16x16/cross.png");
	    imageView.setScaleX(0.5);
	    imageView.setScaleY(0.5);
	    getChildren().add(new Label(part.getName()));
	    getChildren().add(imageView);

	    setOnDragDetected(event -> {
		/* drag was detected, start a drag-and-drop gesture */
		/* allow any transfer mode */
		Dragboard db = startDragAndDrop(TransferMode.MOVE);

		/* Put a string on a dragboard */
		ClipboardContent content = new ClipboardContent();
		content.put(PartDragDataFormat.get(), new PartDragData(partStack.getId(), part.getId()));
		db.setContent(content);

		event.consume();
	    });
	    imageView.setOnMouseClicked(event -> {
		partStack.removeElement(part);
	    });
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

}