package com.puresoltechnologies.javafx.charts.plots.timeseries;

import java.time.Instant;
import java.util.List;
import java.util.function.Function;

import com.puresoltechnologies.javafx.charts.axes.Axis;
import com.puresoltechnologies.javafx.charts.axes.AxisRenderer;
import com.puresoltechnologies.javafx.charts.axes.InstantAxisRenderer;
import com.puresoltechnologies.javafx.charts.axes.NumberAxisRenderer;
import com.puresoltechnologies.javafx.charts.plots.AbstractPlot;
import com.puresoltechnologies.javafx.charts.plots.PlotRenderer;

import javafx.scene.canvas.Canvas;

public class TimeSeriesPlot<Y extends Number & Comparable<Y>, D> extends AbstractPlot<Instant, Y, D> {

    private final Function<D, Instant> xValueFactory;
    private final Function<D, Y> yValueFactory;

    public TimeSeriesPlot(Axis<Instant> xAxis, Axis<Y> yAxis, Function<D, Instant> xValueFactory,
	    Function<D, Y> yValueFactory) {
	super(xAxis, yAxis);
	this.xValueFactory = xValueFactory;
	this.yValueFactory = yValueFactory;
    }

    public TimeSeriesPlot(String title, Axis<Instant> xAxis, Axis<Y> yAxis, List<D> data,
	    Function<D, Instant> xValueFactory, Function<D, Y> yValueFactory) {
	super(title, xAxis, yAxis, data);
	this.xValueFactory = xValueFactory;
	this.yValueFactory = yValueFactory;
    }

    public TimeSeriesPlot(String title, Axis<Instant> xAxis, Axis<Y> yAxis, Function<D, Instant> xValueFactory,
	    Function<D, Y> yValueFactory) {
	super(title, xAxis, yAxis);
	this.xValueFactory = xValueFactory;
	this.yValueFactory = yValueFactory;
    }

    @Override
    public PlotRenderer getRenderer(Canvas canvas, AxisRenderer<Instant> xAxisRenderer, AxisRenderer<Y> yAxisRenderer) {
	return new TimeSeriesPlotRenderer<>(canvas, this, (InstantAxisRenderer) xAxisRenderer,
		(NumberAxisRenderer) yAxisRenderer);
    }

    @Override
    public Instant getAxisX(D date) {
	return xValueFactory.apply(date);
    }

    @Override
    public Y getAxisY(D date) {
	return yValueFactory.apply(date);
    }

}
