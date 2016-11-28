package com.fgl.clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.fgl.utility.Cluster;
import com.fgl.utility.Point;

public class Main extends Application {

	/* Data set */
	private static File file;
	private static boolean hasHeader;
	private static String separator;
	private static boolean scale;
	private static int[] dataColumns;

	private static List<Point> points;

	/* k-means */
	private static int clusterNumber;

	/* DBSCAN */
	private static double epsilon;
	private static int minPts;

	public static void main(String[] args) {
		launch(args);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void start(final Stage primaryStage) {

		primaryStage.setTitle(":: Clustering Setup ::");

		/* Load file grid */
		GridPane gridLoad = new GridPane();
		gridLoad.setPadding(new Insets(10, 10, 10, 10));
		gridLoad.setVgap(5);
		gridLoad.setHgap(5);

		Text textImport = new Text("Import Dataset");
		textImport.setFont(Font.font("Arial", FontWeight.BLACK, 20));
		gridLoad.add(textImport, 0, 0);

		Button buttonLoad = new Button("Load CSV File ...");
		Text textFileName = new Text();
		buttonLoad.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				FileChooser fileChooser = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
						"CSV files (*.csv)", "*.csv");
				fileChooser.getExtensionFilters().add(extFilter);
				file = fileChooser.showOpenDialog(primaryStage);
				if (file != null) {
					textFileName.setText(file.getName());
				}
			}
		});

		gridLoad.add(buttonLoad, 0, 1);
		gridLoad.add(textFileName, 1, 1);

		Text textHeader = new Text("Header: ");
		final ToggleGroup groupHeader = new ToggleGroup();
		RadioButton rbYes = new RadioButton("Yes");
		rbYes.setToggleGroup(groupHeader);
		RadioButton rbNo = new RadioButton("No");
		rbNo.setToggleGroup(groupHeader);
		gridLoad.add(textHeader, 0, 2);
		gridLoad.add(rbYes, 1, 2);
		gridLoad.add(rbNo, 2, 2);

		Text textSeparator = new Text("Separator: ");
		final ToggleGroup groupSeparator = new ToggleGroup();
		RadioButton rbComma = new RadioButton("Comma");
		rbComma.setToggleGroup(groupSeparator);
		rbComma.setSelected(true);
		RadioButton rbSpace = new RadioButton("Space");
		rbSpace.setToggleGroup(groupSeparator);
		gridLoad.add(textSeparator, 0, 3);
		gridLoad.add(rbComma, 1, 3);
		gridLoad.add(rbSpace, 2, 3);

		Text textScale = new Text("Scale: ");
		final ToggleGroup groupScale = new ToggleGroup();
		RadioButton rbScaleYes = new RadioButton("Yes");
		rbScaleYes.setToggleGroup(groupScale);
		RadioButton rbScaleNo = new RadioButton("No");
		rbScaleNo.setToggleGroup(groupScale);
		rbScaleNo.setSelected(true);
		gridLoad.add(textScale, 0, 4);
		gridLoad.add(rbScaleYes, 1, 4);
		gridLoad.add(rbScaleNo, 2, 4);

		Text textDataColumn = new Text("Data Column: ");
		TextField textFieldDataColumn1 = new TextField();
		TextField textFieldDataColumn2 = new TextField();
		textFieldDataColumn1.setPrefWidth(5);
		textFieldDataColumn2.setPrefWidth(5);
		gridLoad.add(textDataColumn, 0, 5);
		gridLoad.add(textFieldDataColumn1, 1, 5);
		gridLoad.add(textFieldDataColumn2, 2, 5);

		/* K-mean grid */
		GridPane gridK = new GridPane();
		gridK.setPadding(new Insets(10, 10, 10, 10));
		gridK.setVgap(5);
		gridK.setHgap(5);

		Text textKmeans = new Text("Parameters for K-means");
		textKmeans.setFont(Font.font("Arial", FontWeight.BLACK, 20));
		gridK.add(textKmeans, 0, 0);

		Text textK = new Text("K: ");
		gridK.add(textK, 0, 1);

		final ComboBox comboBoxK = new ComboBox();
		for (int i = 1; i <= 10; i++) {
			comboBoxK.getItems().add(i);
		}
		comboBoxK.setValue(3);
		gridK.add(comboBoxK, 1, 1);

		/* DBSCAN grid */
		GridPane gridD = new GridPane();
		gridD.setPadding(new Insets(10, 10, 10, 10));
		gridD.setVgap(5);

		Text textD = new Text("Parameters for DBSCAN");
		textD.setFont(Font.font("Arial", FontWeight.BLACK, 20));
		gridD.add(textD, 0, 0);

		Text textEps = new Text("Epsilon: ");
		gridD.add(textEps, 0, 1);

		TextField textFieldEps = new TextField();
		textFieldEps.setPrefColumnCount(10);
		gridD.add(textFieldEps, 1, 1);

		Text textMinPts = new Text("minPts: ");
		gridD.add(textMinPts, 0, 2);

		final ComboBox comboBoxD = new ComboBox();
		for (int i = 1; i <= 10; i++) {
			comboBoxD.getItems().add(i);
		}
		comboBoxD.setValue(4);
		gridD.add(comboBoxD, 1, 2);

		/* Run grid */
		GridPane gridRun = new GridPane();
		gridRun.setPadding(new Insets(10, 10, 10, 10));
		gridRun.setVgap(5);
		Button buttonRun = new Button("Run");
		buttonRun.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				RadioButton selectedRadio = (RadioButton) groupHeader
						.getSelectedToggle();
				hasHeader = selectedRadio.getText().equalsIgnoreCase("Yes") ? true
						: false;

				selectedRadio = (RadioButton) groupSeparator
						.getSelectedToggle();
				separator = selectedRadio.getText().equalsIgnoreCase("Comma") ? "\\s*,\\s*"
						: "\\s+";

				RadioButton selectedScale = (RadioButton) groupScale
						.getSelectedToggle();
				scale = selectedScale.getText().equalsIgnoreCase("Yes") ? true
						: false;

				dataColumns = new int[] {
						Integer.parseInt(textFieldDataColumn1.getText()),
						Integer.parseInt(textFieldDataColumn2.getText()) };

				if (comboBoxK.getValue() != null) {
					clusterNumber = Integer.parseInt(comboBoxK.getValue()
							.toString());
				}

				epsilon = Double.parseDouble(textFieldEps.getText());
				if (comboBoxD.getValue() != null) {
					minPts = Integer.parseInt(comboBoxD.getValue().toString());
				}

				points = readFile(file, hasHeader, dataColumns, separator);

				if (scale) {
					scale();
				}

				Stage resultStage = showResultStage();

				resultStage.show();

			}

		});

		gridRun.add(buttonRun, 0, 0);

		VBox vBox = new VBox(gridLoad, gridK, gridD, gridRun);
		vBox.setPadding(new Insets(30, 30, 30, 30));
		vBox.setSpacing(10);
		vBox.setStyle("-fx-background-color: #ecf0f1;");

		Scene scene = new Scene(vBox, 500, 500);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	private static List<Point> readFile(File file, boolean hasHeader,
			int[] columns, String split) {

		List<Point> points = new ArrayList<>();

		String line = "";
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			if (hasHeader) {
				line = br.readLine();
			}
			int id = 1;
			while ((line = br.readLine()) != null) {
				if (line.length() < 1)
					continue;
				double x = Double
						.parseDouble(line.split(split)[columns[0] - 1]);
				double y = Double
						.parseDouble(line.split(split)[columns[1] - 1]);
				Point p = new Point(x, y);
				p.setID(id++);
				points.add(p);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return points;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Stage showResultStage() {
		Stage stage = new Stage();
		stage.setTitle(":: Clustering Result ::");

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(5);
		grid.setHgap(5);

		/* K-means */
		Kmeans kmeans = new Kmeans(clusterNumber, points);
		kmeans.init();
		kmeans.calculate();
		List<Cluster> clustersKmeans = kmeans.getClusters();

		List<Double> sse = kmeans.getSSE();
		final NumberAxis xAxis3 = new NumberAxis();
		final NumberAxis yAxis3 = new NumberAxis();
		xAxis3.setLabel("Iteration");
		yAxis3.setLabel("SSE");
		// creating the chart
		final LineChart<Number, Number> lineChartKmeans = new LineChart<Number, Number>(
				xAxis3, yAxis3);
		lineChartKmeans.setLegendVisible(false);
		XYChart.Series seriesK = new XYChart.Series();
		// populating the series with data
		for (int i = 0; i < sse.size(); i++) {
			seriesK.getData().add(new XYChart.Data(i + 1, sse.get(i)));
		}
		lineChartKmeans.getData().add(seriesK);

		final NumberAxis xAxisK = new NumberAxis();
		final NumberAxis yAxisK = new NumberAxis();
		final ScatterChart<Number, Number> scatterChartKmeans = new ScatterChart<Number, Number>(
				xAxisK, yAxisK);

		xAxisK.setLabel("Feature X");
		yAxisK.setLabel("Feature Y");

		XYChart.Series seriesCentroid = new XYChart.Series();
		seriesCentroid.setName("Centroid");
		for (int i = 0; i < clustersKmeans.size(); i++) {
			Cluster cluster = clustersKmeans.get(i);
			seriesCentroid.getData().add(
					new XYChart.Data(cluster.getCentroid().getX(), cluster
							.getCentroid().getY()));
			XYChart.Series series = new XYChart.Series();
			series.setName("Cluster" + (i + 1));
			for (Point p : cluster.getPoints()) {
				series.getData().add(new XYChart.Data(p.getX(), p.getY()));
			}
			scatterChartKmeans.getData().add(series);
		}
		scatterChartKmeans.getData().add(seriesCentroid);

		// set symbol size
		Set<Node> nodes = scatterChartKmeans.lookupAll(".chart-symbol");
		for (Node node : nodes) {
			node.setStyle("-fx-background-radius: 3px;-fx-padding: 3px;");
		}

		/* DBSCAN */
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		final ScatterChart<Number, Number> scatterChart = new ScatterChart<Number, Number>(
				xAxis, yAxis);

		DBSCAN dbscan = new DBSCAN(points, epsilon, minPts);
		dbscan.buildKDTree();
		dbscan.dbscan();
		double[] kNNDist = dbscan.kNNdist();

		List<Cluster> clusters = dbscan.getClusters();
		for (int i = 0; i < clusters.size(); i++) {
			Cluster cluster = clusters.get(i);
			XYChart.Series series = new XYChart.Series();
			if (cluster.getId() != 0) {
				series.setName("Cluster" + (i + 1));
			} else {
				series.setName("Noise");
			}
			for (Point p : cluster.getPoints()) {
				series.getData().add(new XYChart.Data(p.getX(), p.getY()));
			}
			scatterChart.getData().add(series);
		}

		Set<Node> nodes2 = scatterChart.lookupAll(".chart-symbol");
		for (Node node : nodes2) {
			node.setStyle("-fx-background-radius: 3px;-fx-padding: 3px;");
		}

		final NumberAxis xAxis2 = new NumberAxis();
		final NumberAxis yAxis2 = new NumberAxis();
		xAxis2.setLabel("Points (sample) sorted by distance");
		yAxis2.setLabel(minPts + "-NN distance");
		// creating the chart
		final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(
				xAxis2, yAxis2);
		lineChart.setCreateSymbols(false);
		lineChart.setLegendVisible(false);

		XYChart.Series series = new XYChart.Series();
		// populating the series with data
		for (int i = 0; i < kNNDist.length; i++) {
			series.getData().add(new XYChart.Data(i + 1, kNNDist[i]));
		}
		lineChart.getData().add(series);

		Node line = series.getNode().lookup(".chart-series-line");
		line.setStyle("-fx-stroke-width:2px;-fx-effect: null; -fx-stroke: #e9967a;");

		Text textK = new Text(String.format("K-means (k = %d)", clusterNumber));
		textK.setFont(Font.font("Arial", FontWeight.BLACK, 20));
		Text textD = new Text(String.format(
				"DBSCAN (epsilon = %s, minPts = %s)", epsilon, minPts));
		textD.setFont(Font.font("Arial", FontWeight.BLACK, 20));
		grid.add(textK, 0, 0);
		grid.add(textD, 1, 0);
		grid.add(scatterChartKmeans, 0, 1);
		grid.add(lineChartKmeans, 0, 2);
		grid.add(scatterChart, 1, 1);
		grid.add(lineChart, 1, 2);

		VBox vBox = new VBox(grid);
		vBox.setPadding(new Insets(30, 30, 30, 30));
		vBox.setSpacing(10);
		vBox.setStyle("-fx-background-color: #ecf0f1;");
		vBox.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
		vBox.lookup(".chart-series-line").setStyle(
				"-fx-stroke-width:2px;-fx-effect: null; -fx-stroke: #e9967a;");

		stage.setScene(new Scene(vBox));

		return stage;
	}

	private void scale() {
		int size = points.size();
		double sumX = 0, sumY = 0;
		for (Point p : points) {
			sumX += p.getX();
			sumY += p.getY();
		}
		double meanX = sumX / size;
		double meanY = sumY / size;

		double squareSumX = 0, squareSumY = 0;

		for (Point p : points) {
			squareSumX += Math.pow(p.getX() - meanX, 2);
			squareSumY += Math.pow(p.getY() - meanY, 2);
		}

		double stdX = Math.sqrt((squareSumX) / (size - 1));
		double stdY = Math.sqrt((squareSumY) / (size - 1));

		for (Point p : points) {
			p.setX((p.getX() - meanX) / stdX);
			p.setY((p.getY() - meanY) / stdY);
		}
	}

}
