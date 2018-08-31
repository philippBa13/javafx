package com.puresoltechnologies.javafx.charts.plots.xy;

import com.puresoltechnologies.javafx.charts.axes.NumberAxisRenderer;
import com.puresoltechnologies.javafx.charts.plots.AbstractPlotRenderer;
import com.puresoltechnologies.javafx.charts.plots.Plot;

import javafx.scene.canvas.Canvas;

public class XYPlotRenderer<X extends Number & Comparable<X>, Y extends Number & Comparable<Y>, D>
	extends AbstractPlotRenderer<X, Y, D, NumberAxisRenderer, NumberAxisRenderer> {

    public XYPlotRenderer(Canvas canvas, Plot<X, Y, D> plot, NumberAxisRenderer xAxisRenderer,
	    NumberAxisRenderer yAxisRenderer) {
	super(canvas, plot, xAxisRenderer, yAxisRenderer);
    }

    @Override
    public void renderTo(double x, double y, double width, double height) {
	// TODO Auto-generated method stub

    }

}