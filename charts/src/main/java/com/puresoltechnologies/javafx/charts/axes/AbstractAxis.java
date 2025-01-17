package com.puresoltechnologies.javafx.charts.axes;

import java.util.UUID;

import com.puresoltechnologies.javafx.charts.preferences.ChartsProperties;
import com.puresoltechnologies.javafx.extensions.fonts.FontDefinition;
import com.puresoltechnologies.javafx.preferences.Preferences;

import javafx.beans.property.ObjectProperty;

public abstract class AbstractAxis<T> implements Axis<T> {

    private final ObjectProperty<FontDefinition> titleFontProperty = Preferences
	    .getProperty(ChartsProperties.AXIS_TITLE_FONT);
    private final ObjectProperty<FontDefinition> labelFontProperty = Preferences
	    .getProperty(ChartsProperties.AXIS_LABEL_FONT);

    private final UUID id = UUID.randomUUID();
    private final String title;
    private final String unit;
    private final AxisType axisType;
    private final Class<T> valueType;

    public AbstractAxis(String title, String unit, AxisType type, Class<T> valueType) {
	super();
	this.title = title;
	this.unit = unit;
	this.axisType = type;
	this.valueType = valueType;
    }

    @Override
    public ObjectProperty<FontDefinition> titleFontProperty() {
	return titleFontProperty;
    }

    @Override
    public void setTitleFont(FontDefinition titleFont) {
	titleFontProperty.setValue(titleFont);
    }

    @Override
    public FontDefinition getTitleFont() {
	return labelFontProperty.getValue();
    }

    @Override
    public ObjectProperty<FontDefinition> labelFontProperty() {
	return labelFontProperty;
    }

    @Override
    public void setLabelFont(FontDefinition tickFont) {
	labelFontProperty.setValue(tickFont);
    }

    @Override
    public FontDefinition getLabelFont() {
	return labelFontProperty.getValue();
    }

    @Override
    public final String getId() {
	return id.toString();
    }

    @Override
    public final String getTitle() {
	return title;
    }

    @Override
    public final String getUnit() {
	return unit;
    }

    @Override
    public final AxisType getAxisType() {
	return axisType;
    }

    @Override
    public final Class<T> getValueType() {
	return valueType;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = (prime * result) + ((id == null) ? 0 : id.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	AbstractAxis<?> other = (AbstractAxis<?>) obj;
	if (id == null) {
	    if (other.id != null) {
		return false;
	    }
	} else if (!id.equals(other.id)) {
	    return false;
	}
	return true;
    }

}
