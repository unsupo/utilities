package utilities.plotting;

/**
 * Created by jarndt on 5/20/16.
 */

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class Plot extends ApplicationFrame {
	public static final Dimension FULL_SCREEN = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

	public static void main(String[] args) throws IOException {
		new Plot(new double[]{1, 2}, new double[]{1, 2}).setType(Plot.JUST_LINE)
				.addSeries("p1", Plot.JUST_POINTS, new double[]{3, 4}, new double[]{1, 5})
				.addSeries("p3", Plot.JUST_POINTS, new double[]{3, 4, 5, 5}, new double[]{1, 5, 3, 4})
				.addSeries("p2", Plot.JUST_POINTS, new double[]{3, 4, 3, 4}, new double[]{1, 5, 1, 1})
				.savePlot("C:/Users/jarndt/Desktop/a.png");
//				.showPlot();
	}

	public static final String JUST_POINTS = "points", LINES = "linesPoint", JUST_LINE = "line";
	private String[] allowedTypes = {JUST_LINE, JUST_POINTS, LINES};

	private HashMap<String, XYSeries> series = new HashMap<>();
	private HashMap<String, String> types = new HashMap<>();
	private HashMap<String, Color> colors = new HashMap<>();
	private Dimension size = new Dimension(500, 270);
	private static String title = "Title";
	private String xlable = "X", ylable = "Y", plotTitle = "title";

	public Plot() {
		super(title);
	}

	public Plot(String plotTitle) {
		super(title);
		title = plotTitle;
	}

	public Plot(double[] x, double[] y) {
		super(title);
		_addSeries("", x, y);
	}

	public Plot(String seriesTitle, double[] x, double[] y) {
		super(title);
		_addSeries(seriesTitle, x, y);
	}

	public Plot setWindowSize(int width, int height){
		this.size = new Dimension(width,height);
		return this;
	}public Plot setWindowSize(Dimension d){
		this.size = d;
		return this;
	}

	public Plot setType(String plotName, String plotType) {
		if (!Arrays.asList(allowedTypes).contains(plotType))
			throw new IllegalArgumentException("Type: " + plotType + " is not an allowed type");
		types.put(plotName, plotType);
		return this;
	}

	public Plot setType(String plotType) {
		return setType("", plotType);
	}

	public Plot setXLabel(String xlable) {
		this.xlable = xlable;
		return this;
	}

	public Plot setYLabel(String ylable) {
		this.ylable = ylable;
		return this;
	}

	public Plot setPlotTitle(String title) {
		plotTitle = title;
		return this;
	}

	public Plot size(int width, int height) {
		size.setSize(width, height);
		return this;
	}

	public Plot addSeries(String name, double[] x, double[] y) {
		_addSeries(name, x, y);
		return this;
	}

	public Plot addSeries(String name, String type, double[] x, double[] y) {
		_addSeries(name, x, y);
		return setType(name, type);
	}

	private void _addSeries(String name, double[] x, double[] y) {
		if (x.length != y.length)
			throw new IllegalArgumentException("x and y must be same size");
		final XYSeries s1 = new XYSeries(name);
		for (int i = 0; i < y.length; i++)
			s1.add(x[i], y[i]);
		series.put(name, s1);
		types.put(name, LINES);
		colors.put(name, Color.BLACK);
//		dataset.addSeries(s1);
	}

	public void showPlot() {
		final XYDataset dataset = getDataset();
		final JFreeChart chart = getChart(dataset);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(size);
		setContentPane(chartPanel);

		pack();
		setVisible(true);
	}

	private XYDataset getDataset() {
		final XYSeriesCollection dataset = new XYSeriesCollection();
		for (String key : series.keySet())
			dataset.addSeries(series.get(key));

		return dataset;
	}

	private JFreeChart getChart(final XYDataset dataset) {

		// create the chart...
		final JFreeChart chart = ChartFactory.createXYLineChart(
				plotTitle,      // chart title
				xlable,                      // x axis label
				ylable,                      // y axis label
				dataset,                  // data
				PlotOrientation.VERTICAL,
				true,                     // include legend
				true,                     // tooltips
				false                     // urls
		);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		chart.setBackgroundPaint(Color.white);

//        final StandardLegend legend = (StandardLegend) chart.getLegend();
		//      legend.setDisplaySeriesShapes(true);

		// get a reference to the plot for further customisation...
		final XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.lightGray);
		//    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		int i = 0;
		for (String key : types.keySet())
			modRenderer(renderer, i++, types.get(key));

		plot.setRenderer(renderer);

		// change the auto tick unit selection to integer units only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		// OPTIONAL CUSTOMISATION COMPLETED.

		return chart;

	}

	private void modRenderer(XYLineAndShapeRenderer renderer, int index, String change) {
		if (JUST_POINTS.equals(change))
			renderer.setSeriesLinesVisible(index, false);
		if (JUST_LINE.equals(change))
			renderer.setSeriesShapesVisible(index, false);

	}

	public void savePlot(String path) throws IOException {
		ChartUtilities.writeChartAsPNG(new FileOutputStream(new File(path)), getChart(getDataset()), size.width, size.height);
	}
}
