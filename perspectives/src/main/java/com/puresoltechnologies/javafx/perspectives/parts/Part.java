package com.puresoltechnologies.javafx.perspectives.parts;

import java.util.Optional;

import com.puresoltechnologies.javafx.perspectives.PartHeaderToolBar;
import com.puresoltechnologies.javafx.perspectives.PerspectiveElement;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

/**
 * This is the interface for a part of the perspective which provides the actual
 * content. These parts can handles to be replaced, closed and altered.
 *
 * @author Rick-Rainer Ludwig
 */
public interface Part extends PerspectiveElement, AutoCloseable {

    /**
     * This method returns the title of the part.
     *
     * @return A {@link String} with the {@link Part}'s title is returned.
     */
    String getTitle();

    void setTitle(String title);

    StringProperty titleProperty();

    Optional<Image> getImage();

    void setImage(Image image);

    ObjectProperty<Image> imageProperty();

    Optional<PartHeaderToolBar> getToolBar();

    /**
     * This method tells whether or not this part is allowed to be opened by a user
     * interaction like the Show View Dialog.
     *
     * @return The {@link PartOpenMode} is returned.
     */
    PartOpenMode getOpenMode();

    /**
     * This method tells the system how many instances of the part are allowed in
     * the whole system or the perspective.
     *
     * @return A {@link PartContentType} value is returned signaling how many
     *         instances are allowed.
     */
    PartContentType getContentType();

    void initialize();

    /**
     * This method is called after the part was opened by a user interaction to
     * implement some user interaction to retrieve needed input parameters.
     */
    void manualInitialization();

    @Override
    void close();

}
