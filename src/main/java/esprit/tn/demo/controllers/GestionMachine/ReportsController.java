package esprit.tn.demo.controllers.GestionMachine;

import esprit.tn.demo.services.GestionMachine.MachineService;
import esprit.tn.demo.services.GestionMachine.MaintenanceService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ReportsController {

    @FXML private WebView costChartView, timeChartView, frequencyChartView;
    @FXML private Button costBarBtn, costLineBtn, costPieBtn, timeBarBtn, timeLineBtn, timePieBtn;
    @FXML private ComboBox<String> frequencyChartType;

    private WebEngine costEngine, timeEngine, frequencyEngine;
    private MachineService machineService;
    private MaintenanceService maintenanceService;

    // Flag to track if charts are already loaded
    private boolean chartsInitialized = false;

    // Store chart HTML content for faster reloading
    private String costChartHtml, timeChartHtml, frequencyChartHtml;

    // Static initialization for WebView directories
    static {
        try {
            // Set explicit Java temp dir to avoid Windows user profile issues
            String uniqueId = UUID.randomUUID().toString();
            Path tempPath = Files.createTempDirectory("javafx-webview-" + uniqueId);
            File tempDir = tempPath.toFile();
            tempDir.deleteOnExit();

            // Set the property before any WebView is created
            System.setProperty("javafx.web.userDataDirectory", tempDir.getAbsolutePath());
            System.setProperty("javafx.web.userAgentStylesheetUrl", "NONE");
            System.out.println("Set global WebView user data directory to: " + tempDir.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to set WebView directories: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ReportsController() {
        try {
            this.machineService = new MachineService();
            System.out.println("MachineService initialized: " + (machineService != null));
        } catch (Exception e) {
            System.err.println("Failed to initialize MachineService: " + e.getMessage());
            e.printStackTrace();
            this.machineService = null;
        }
        try {
            this.maintenanceService = new MaintenanceService();
            System.out.println("MaintenanceService initialized: " + (maintenanceService != null));
        } catch (Exception e) {
            System.err.println("Failed to initialize MaintenanceService: " + e.getMessage());
            e.printStackTrace();
            this.maintenanceService = null;
        }
    }

    @FXML
    private void initialize() {
        // Initialize all components
        Platform.runLater(() -> {
            try {
                // First ensure WebView instances are created
                initializeWebViews();

                // Set up ComboBox items
                frequencyChartType.setItems(FXCollections.observableArrayList("Bar", "Line", "Pie"));
                frequencyChartType.setValue("Bar");

                // Set up button handlers
                setupButtonHandlers();

                // Wait a bit and then try to load charts
                CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS).execute(() -> {
                    Platform.runLater(this::loadInitialCharts);
                });
            } catch (Exception e) {
                System.err.println("Error in initialize: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void initializeWebViews() {
        try {
            // Set each WebView's initial content to blank page
            costChartView.getEngine().loadContent("<html><body></body></html>");
            timeChartView.getEngine().loadContent("<html><body></body></html>");
            frequencyChartView.getEngine().loadContent("<html><body></body></html>");

            // Get WebEngine references
            costEngine = costChartView.getEngine();
            timeEngine = timeChartView.getEngine();
            frequencyEngine = frequencyChartView.getEngine();

            // Set up JavaScript bridges
            setupWebView(costEngine, "cost");
            setupWebView(timeEngine, "time");
            setupWebView(frequencyEngine, "frequency");

            // Check if chart.min.js exists
            if (getClass().getResource("/js/chart.min.js") == null) {
                System.out.println("Error: chart.min.js not found in src/main/resources/js/");
                showError(costEngine, "chart.min.js not found");
                showError(timeEngine, "chart.min.js not found");
                showError(frequencyEngine, "chart.min.js not found");
            }
        } catch (Exception e) {
            System.err.println("Error initializing WebViews: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupWebView(WebEngine engine, String prefix) {
        try {
            engine.setJavaScriptEnabled(true);

            // Add error handler
            engine.setOnError(event -> {
                System.out.println(prefix + " WebView Error: " + event.getMessage());
            });

            // Add alert handler
            engine.setOnAlert(event -> {
                System.out.println(prefix + " JS Alert: " + event.getData());
            });

            // Add state change listener
            engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                System.out.println(prefix + " WebView state changed: " + newState);
            });

            // Load initial blank content
            engine.loadContent("<html><body><h3>Initializing chart...</h3></body></html>");
        } catch (Exception e) {
            System.err.println("Error setting up WebView " + prefix + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(WebEngine engine, String message) {
        if (engine != null) {
            engine.loadContent("<html><body><h3 style='color:red'>Error: " + message + "</h3></body></html>");
        }
    }

    private void loadInitialCharts() {
        try {
            System.out.println("Loading initial charts...");

            // Load the actual charts
            if (machineService != null) {
                loadCostChart("bar");
                loadFrequencyChart("bar");
            } else {
                showError(costEngine, "MachineService not initialized");
                showError(frequencyEngine, "MachineService not initialized");
            }

            if (maintenanceService != null) {
                loadTimeChart("bar");
            } else {
                showError(timeEngine, "MaintenanceService not initialized");
            }

            chartsInitialized = true;
        } catch (Exception e) {
            System.err.println("Error loading initial charts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupButtonHandlers() {
        costBarBtn.setOnAction(event -> loadCostChart("bar"));
        costLineBtn.setOnAction(event -> loadCostChart("line"));
        costPieBtn.setOnAction(event -> loadCostChart("pie"));

        timeBarBtn.setOnAction(event -> loadTimeChart("bar"));
        timeLineBtn.setOnAction(event -> loadTimeChart("line"));
        timePieBtn.setOnAction(event -> loadTimeChart("pie"));

        frequencyChartType.setOnAction(event ->
                loadFrequencyChart(frequencyChartType.getValue().toLowerCase()));
    }

    private void loadCostChart(String type) {
        try {
            if (machineService == null) {
                showError(costEngine, "MachineService not initialized");
                return;
            }

            List<Object[]> data = machineService.findTotalMaintenanceCostPerMachine();
            if (data == null || data.isEmpty()) {
                showError(costEngine, "No data available");
                return;
            }

            try {
                String chartJsPath = getClass().getResource("/js/chart.min.js").toExternalForm();
                costChartHtml = createChartHtml(chartJsPath, type, data, "Total Maintenance Cost");
                costEngine.loadContent(costChartHtml);
            } catch (Exception e) {
                System.err.println("Error loading cost chart: " + e.getMessage());
                e.printStackTrace();
                showError(costEngine, "Failed to load chart: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error in loadCostChart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadTimeChart(String type) {
        try {
            if (maintenanceService == null) {
                showError(timeEngine, "MaintenanceService not initialized");
                return;
            }

            List<Object[]> data = maintenanceService.findMaintenanceCostOverTime();
            if (data == null || data.isEmpty()) {
                showError(timeEngine, "No data available");
                return;
            }

            try {
                String chartJsPath = getClass().getResource("/js/chart.min.js").toExternalForm();
                timeChartHtml = createChartHtml(chartJsPath, type, data, "Maintenance Cost Over Time");
                timeEngine.loadContent(timeChartHtml);
            } catch (Exception e) {
                System.err.println("Error loading time chart: " + e.getMessage());
                e.printStackTrace();
                showError(timeEngine, "Failed to load chart: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error in loadTimeChart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadFrequencyChart(String type) {
        try {
            if (machineService == null) {
                showError(frequencyEngine, "MachineService not initialized");
                return;
            }

            List<Object[]> data = machineService.findMaintenanceFrequency();
            if (data == null || data.isEmpty()) {
                showError(frequencyEngine, "No data available");
                return;
            }

            try {
                String chartJsPath = getClass().getResource("/js/chart.min.js").toExternalForm();
                frequencyChartHtml = createChartHtml(chartJsPath, type, data, "Maintenance Frequency");
                frequencyEngine.loadContent(frequencyChartHtml);
            } catch (Exception e) {
                System.err.println("Error loading frequency chart: " + e.getMessage());
                e.printStackTrace();
                showError(frequencyEngine, "Failed to load chart: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error in loadFrequencyChart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String createChartHtml(String chartJsPath, String type, List<Object[]> data, String title) {
        try {
            // Create safe and structured data for the chart
            StringBuilder labels = new StringBuilder("[");
            StringBuilder values = new StringBuilder("[");

            for (int i = 0; i < data.size(); i++) {
                Object labelObj = data.get(i)[0];
                Object valueObj = data.get(i)[1];

                // Safely handle null values
                String label = labelObj != null ? labelObj.toString() : "Unknown";
                String value = valueObj != null ? valueObj.toString() : "0";

                // Escape quotes in labels
                label = label.replace("'", "\\'");

                labels.append("'").append(label).append("'");
                values.append(value);
                if (i < data.size() - 1) {
                    labels.append(",");
                    values.append(",");
                }
            }
            labels.append("]");
            values.append("]");

            // Use embedded Chart.js if external loading fails
            String chartJsScript;
            if (chartJsPath != null && !chartJsPath.isEmpty()) {
                chartJsScript = "<script src=\"" + chartJsPath + "\"></script>";
            } else {
                // Note: In a real app, you would embed the Chart.js library here
                chartJsScript = "<script>console.error('Chart.js not found');</script>";
            }

            // Create the HTML with inline Chart.js and ResizeObserver polyfill
            return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <script>
                    // ResizeObserver polyfill
                    if (!window.ResizeObserver) {
                        window.ResizeObserver = function(callback) {
                            this.elements = [];
                            this.callback = callback;
                            
                            // Use a simple timer to check for size changes
                            const self = this;
                            this.interval = setInterval(function() {
                                self.checkSizes();
                            }, 1000);
                        };
                        
                        ResizeObserver.prototype.observe = function(element) {
                            if (!element) return;
                            
                            if (!element._resizeObserverData) {
                                element._resizeObserverData = {
                                    width: element.offsetWidth,
                                    height: element.offsetHeight
                                };
                            }
                            
                            if (this.elements.indexOf(element) === -1) {
                                this.elements.push(element);
                            }
                        };
                        
                        ResizeObserver.prototype.unobserve = function(element) {
                            const index = this.elements.indexOf(element);
                            if (index !== -1) {
                                this.elements.splice(index, 1);
                            }
                        };
                        
                        ResizeObserver.prototype.disconnect = function() {
                            this.elements = [];
                            if (this.interval) {
                                clearInterval(this.interval);
                                this.interval = null;
                            }
                        };
                        
                        ResizeObserver.prototype.checkSizes = function() {
                            const changedEntries = [];
                            
                            for (let i = 0; i < this.elements.length; i++) {
                                const element = this.elements[i];
                                const newWidth = element.offsetWidth;
                                const newHeight = element.offsetHeight;
                                
                                if (newWidth !== element._resizeObserverData.width || 
                                    newHeight !== element._resizeObserverData.height) {
                                    
                                    const entry = {
                                        target: element,
                                        contentRect: {
                                            width: newWidth,
                                            height: newHeight,
                                            top: 0,
                                            left: 0,
                                            bottom: newHeight,
                                            right: newWidth,
                                            x: 0,
                                            y: 0
                                        }
                                    };
                                    
                                    changedEntries.push(entry);
                                    
                                    // Update stored dimensions
                                    element._resizeObserverData.width = newWidth;
                                    element._resizeObserverData.height = newHeight;
                                }
                            }
                            
                            if (changedEntries.length > 0 && typeof this.callback === 'function') {
                                this.callback(changedEntries);
                            }
                        };
                    }
                    
                    // MutationObserver polyfill (simple version if needed)
                    if (!window.MutationObserver) {
                        window.MutationObserver = function(callback) {
                            this.callback = callback;
                        };
                        MutationObserver.prototype.observe = function() {}; // Dummy implementation
                        MutationObserver.prototype.disconnect = function() {};
                    }
                    </script>
                    %s
                    <style>
                        body { margin: 0; padding: 0; background-color: #f8f9fa; font-family: Arial, sans-serif; }
                        #chart-container { width: 100%%; height: 380px; display: flex; justify-content: center; align-items: center; }
                        #chart { width: 100%%; height: 100%%; }
                        .chart-title { text-align: center; margin: 10px 0; font-size: 18px; color: #333; }
                    </style>
                </head>
                <body>
                    <h3 class="chart-title">%s</h3>
                    <div id="chart-container">
                        <canvas id="chart"></canvas>
                    </div>
                    <script>
                        // Chart configuration options - minimized version
                        const chartConfig = {
                            type: '%s',
                            data: {
                                labels: %s,
                                datasets: [{
                                    label: '%s',
                                    data: %s,
                                    backgroundColor: [],  // Will be filled in by code
                                    borderColor: [],      // Will be filled in by code
                                    borderWidth: 1
                                }]
                            },
                            options: {
                                responsive: true,
                                maintainAspectRatio: false,
                                plugins: {
                                    legend: {
                                        display: true,
                                        position: 'top'
                                    }
                                },
                                scales: %s
                            }
                        };
                        
                        function generateColors(count) {
                            // Predefined colors for consistency
                            const colors = [
                                [65, 105, 225],   // Royal Blue
                                [60, 179, 113],   // Medium Sea Green
                                [255, 99, 71],    // Tomato
                                [106, 90, 205],   // Slate Blue
                                [255, 165, 0],    // Orange
                                [75, 0, 130],     // Indigo
                                [220, 20, 60],    // Crimson
                                [34, 139, 34],    // Forest Green
                                [218, 112, 214],  // Orchid
                                [0, 139, 139]     // Dark Cyan
                            ];
                            
                            for (let i = 0; i < count; i++) {
                                const idx = i %% colors.length;
                                const [r, g, b] = colors[idx];
                                chartConfig.data.datasets[0].backgroundColor.push(`rgba(${r},${g},${b},0.2)`);
                                chartConfig.data.datasets[0].borderColor.push(`rgba(${r},${g},${b},1)`);
                            }
                        }
                        
                        function initChart() {
                            try {
                                console.log('Initializing chart...');
                                // Generate colors based on data points
                                generateColors(%d);
                                
                                // Get the canvas context and create chart
                                const ctx = document.getElementById('chart').getContext('2d');
                                new Chart(ctx, chartConfig);
                                console.log('Chart created successfully');
                            } catch (err) {
                                console.error('Chart creation error:', err);
                                document.body.innerHTML = '<div style="color:red;padding:20px;">Error: ' + err.message + '</div>';
                            }
                        }
                        
                        // Initialize chart once everything is loaded
                        if (document.readyState === 'loading') {
                            document.addEventListener('DOMContentLoaded', initChart);
                        } else {
                            // DOM already loaded, create chart now
                            setTimeout(initChart, 100);
                        }
                    </script>
                </body>
                </html>
                """, chartJsScript, title, type, labels, title, values,
                    type.equals("pie") ? "{}" : "{ y: { beginAtZero: true } }", data.size());
        } catch (Exception e) {
            System.err.println("Error creating chart HTML: " + e.getMessage());
            e.printStackTrace();
            return "<html><body><h3 style='color:red'>Error creating chart: " + e.getMessage() + "</h3></body></html>";
        }
    }

    /**
     * Cleanup method to properly close WebView resources
     * Call this when closing the tab or exiting the application
     */
    public void cleanup() {
        try {
            // Clear all WebView content
            if (costEngine != null) costEngine.loadContent("");
            if (timeEngine != null) timeEngine.loadContent("");
            if (frequencyEngine != null) frequencyEngine.loadContent("");

            // Set to null to help garbage collection
            costEngine = null;
            timeEngine = null;
            frequencyEngine = null;

            // Clear cached HTML
            costChartHtml = null;
            timeChartHtml = null;
            frequencyChartHtml = null;

            System.out.println("ReportsController resources cleaned up");
        } catch (Exception e) {
            System.err.println("Error in cleanup: " + e.getMessage());
            e.printStackTrace();
        }
    }
}