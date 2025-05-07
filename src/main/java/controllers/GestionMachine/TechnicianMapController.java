
package controllers.GestionMachine;

import entities.GestionMachine.Technicien;
import services.GestionMachine.TechnicienService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import javafx.concurrent.Worker;

import java.io.File;
import java.util.Comparator;
import java.util.List;

public class TechnicianMapController {

    @FXML
    private WebView webView;
    @FXML
    private Label resultLabel;
    @FXML
    private Button findNearestButton, backButton;

    private WebEngine webEngine;
    private TechnicienService technicienService = new TechnicienService();
    private double currentLat = 36.8065; // Default: Tunis
    private double currentLon = 10.1815;

    // Bridge object for JavaScript to Java communication
    public class JavaBridge {
        public void updateCoordinates(double lat, double lon) {
            currentLat = lat;
            currentLon = lon;
            resultLabel.setText(String.format("Pinned location: Lat %.4f, Lon %.4f", lat, lon));
        }
        public void log(String message) {
            System.out.println("JS Log: " + message);
        }
    }

    @FXML
    private void initialize() {
        // Set unique user data directory for WebView to avoid conflicts
        String userDataDir = System.getProperty("java.io.tmpdir") + File.separator + "webview_" + System.currentTimeMillis();
        System.setProperty("com.sun.webkit.userData", userDataDir);
        System.out.println("WebView user data directory set to: " + userDataDir);

        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);

        // Debug WebEngine errors
        webEngine.getLoadWorker().exceptionProperty().addListener((obs, old, ex) -> {
            if (ex != null) {
                System.out.println("WebEngine Exception: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        webEngine.setOnAlert(event -> System.out.println("JS Alert: " + event.getData()));
        webEngine.setOnError(event -> System.out.println("JS Error: " + event.getMessage()));

        // Load map when WebView is ready
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                JavaBridge bridge = new JavaBridge();
                window.setMember("javaBridge", bridge);
                // Override console.log and console.error to forward to Java
                try {
                    webEngine.executeScript("console.log = function(message) { javaBridge.log(message); };");
                    webEngine.executeScript("console.error = function(message) { javaBridge.log('JS Error: ' + message); };");
                } catch (Exception e) {
                    System.out.println("Error overriding console: " + e.getMessage());
                }
                loadMap(technicienService.getAll());
            }
        });
    }

    private String getMapHtml(List<Technicien> technicians) {
        StringBuilder technicianMarkers = new StringBuilder();
        if (technicians != null) {
            for (Technicien tech : technicians) {
                if (tech != null && tech.getName() != null && tech.getSpecialite() != null) {
                    technicianMarkers.append(String.format(
                            "L.marker([%f, %f], {icon: blueIcon}).addTo(map).bindPopup('%s - %s');\n",
                            tech.getLatitude(), tech.getLongitude(), tech.getName(), tech.getSpecialite()
                    ));
                }
            }
        }

        // Try local resources, fall back to external if not found
        String leafletCss = getClass().getResource("/leaflet/leaflet.css") != null
                ? getClass().getResource("/leaflet/leaflet.css").toExternalForm()
                : "https://unpkg.com/leaflet@1.9.4/dist/leaflet.css";
        String leafletJs = getClass().getResource("/leaflet/leaflet.js") != null
                ? getClass().getResource("/leaflet/leaflet.js").toExternalForm()
                : "https://unpkg.com/leaflet@1.9.4/dist/leaflet.js";
        String redIconUrl = getClass().getResource("/esprit/tn/demo/images/marker-icon-2x-red.png") != null
                ? getClass().getResource("/esprit/tn/demo/images/marker-icon-2x-red.png").toExternalForm()
                : "https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png";
        String blueIconUrl = getClass().getResource("/esprit/tn/demo/images/marker-icon-2x-blue.png") != null
                ? getClass().getResource("/esprit/tn/demo/images/marker-icon-2x-blue.png").toExternalForm()
                : "https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-blue.png";
        String shadowUrl = getClass().getResource("/esprit/tn/demo/images/marker-shadow.png") != null
                ? getClass().getResource("/esprit/tn/demo/images/marker-shadow.png").toExternalForm()
                : "https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png";

        // Debug resource paths
        System.out.println("Leaflet CSS: " + leafletCss);
        System.out.println("Leaflet JS: " + leafletJs);
        System.out.println("Red Icon: " + redIconUrl);
        System.out.println("Blue Icon: " + blueIconUrl);
        System.out.println("Shadow: " + shadowUrl);

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <link rel="stylesheet" href="%s" />
                <script src="%s"></script>
                <style>
                    #map { height: 400px; width: 800px; }
                    body { margin: 0; padding: 0; }
                </style>
                <script>
                    var redIcon = new L.Icon({
                        iconUrl: '%s',
                        shadowUrl: '%s',
                        iconSize: [25, 41], iconAnchor: [12, 41], popupAnchor: [1, -34], shadowSize: [41, 41]
                    });
                    var blueIcon = new L.Icon({
                        iconUrl: '%s',
                        shadowUrl: '%s',
                        iconSize: [25, 41], iconAnchor: [12, 41], popupAnchor: [1, -34], shadowSize: [41, 41]
                    });
                </script>
            </head>
            <body>
                <div id="map"></div>
                <script>
                    try {
                        var map = L.map('map').setView([%f, %f], 12);
                        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                            attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
                        }).addTo(map);
                        %s
                        var userMarker = L.marker([%f, %f], {draggable: true, icon: redIcon}).addTo(map)
                            .bindPopup('Your location');
                        userMarker.on('dragend', function(e) {
                            var latlng = userMarker.getLatLng();
                            window.javaBridge.updateCoordinates(latlng.lat, latlng.lng);
                        });
                        map.on('click', function(e) {
                            userMarker.setLatLng(e.latlng);
                            window.javaBridge.updateCoordinates(e.latlng.lat, e.latlng.lng);
                        });
                        console.log('Map initialized successfully');
                    } catch (e) {
                        console.error('Map initialization error: ' + e.message);
                    }
                </script>
            </body>
            </html>
            """.formatted(leafletCss, leafletJs, redIconUrl, shadowUrl, blueIconUrl, shadowUrl,
                currentLat, currentLon, technicianMarkers.toString(), currentLat, currentLon);
    }

    private void loadMap(List<Technicien> technicians) {
        System.out.println("Entering loadMap method");
        // Use local leaflet.js with fallback to external URL
        String leafletJs = getClass().getResource("/leaflet/leaflet.js") != null
                ? getClass().getResource("/leaflet/leaflet.js").toExternalForm()
                : "https://unpkg.com/leaflet@1.9.4/dist/leaflet.js";

        // Debug resource path
        System.out.println("Leaflet JS: " + leafletJs);

        // Minimal map HTML with embedded Leaflet CSS
        String html = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    /* Embedded Leaflet CSS */
                    .leaflet-pane,
                    .leaflet-tile,
                    .leaflet-marker-icon,
                    .leaflet-marker-shadow,
                    .leaflet-tile-container,
                    .leaflet-pane > svg,
                    .leaflet-pane > canvas,
                    .leaflet-zoom-box,
                    .leaflet-image-layer,
                    .leaflet-layer {
                        position: absolute;
                        left: 0;
                        top: 0;
                    }
                    .leaflet-container {
                        overflow: hidden;
                    }
                    .leaflet-tile,
                    .leaflet-marker-icon,
                    .leaflet-marker-shadow {
                        -webkit-user-select: none;
                           -moz-user-select: none;
                                user-select: none;
                          -webkit-user-drag: none;
                    }
                    .leaflet-tile::selection {
                        background: transparent;
                    }
                    .leaflet-safari .leaflet-tile {
                        image-rendering: -webkit-optimize-contrast;
                    }
                    .leaflet-safari .leaflet-tile-container {
                        width: 1600px;
                        height: 1600px;
                        -webkit-transform-origin: 0 0;
                    }
                    .leaflet-marker-icon,
                    .leaflet-marker-shadow {
                        display: block;
                    }
                    .leaflet-container .leaflet-overlay-pane svg {
                        max-width: none !important;
                        max-height: none !important;
                    }
                    .leaflet-container .leaflet-marker-pane img,
                    .leaflet-container .leaflet-shadow-pane img,
                    .leaflet-container .leaflet-tile-pane img,
                    .leaflet-container img.leaflet-image-layer,
                    .leaflet-container .leaflet-tile {
                        max-width: none !important;
                        max-height: none !important;
                        width: auto;
                        padding: 0;
                    }
                    .leaflet-container img.leaflet-tile {
                        mix-blend-mode: plus-lighter;
                    }
                    .leaflet-container.leaflet-touch-zoom {
                        -ms-touch-action: pan-x pan-y;
                        touch-action: pan-x pan-y;
                    }
                    .leaflet-container.leaflet-touch-drag {
                        -ms-touch-action: pinch-zoom;
                        touch-action: none;
                        touch-action: pinch-zoom;
                    }
                    .leaflet-container.leaflet-touch-drag.leaflet-touch-zoom {
                        -ms-touch-action: none;
                        touch-action: none;
                    }
                    .leaflet-container {
                        -webkit-tap-highlight-color: transparent;
                    }
                    .leaflet-container a {
                        -webkit-tap-highlight-color: rgba(51, 181, 229, 0.4);
                    }
                    .leaflet-tile {
                        filter: inherit;
                        visibility: hidden;
                    }
                    .leaflet-tile-loaded {
                        visibility: inherit;
                    }
                    .leaflet-zoom-box {
                        width: 0;
                        height: 0;
                        -moz-box-sizing: border-box;
                             box-sizing: border-box;
                        z-index: 800;
                    }
                    .leaflet-overlay-pane svg {
                        -moz-user-select: none;
                    }
                    .leaflet-pane         { z-index: 400; }
                    .leaflet-tile-pane    { z-index: 200; }
                    .leaflet-overlay-pane { z-index: 400; }
                    .leaflet-shadow-pane  { z-index: 500; }
                    .leaflet-marker-pane  { z-index: 600; }
                    .leaflet-tooltip-pane   { z-index: 650; }
                    .leaflet-popup-pane   { z-index: 700; }
                    .leaflet-map-pane canvas { z-index: 100; }
                    .leaflet-map-pane svg    { z-index: 200; }
                    .leaflet-vml-shape {
                        width: 1px;
                        height: 1px;
                    }
                    .lvml {
                        behavior: url(#default#VML);
                        display: inline-block;
                        position: absolute;
                    }
                    .leaflet-control {
                        position: relative;
                        z-index: 800;
                        pointer-events: visiblePainted;
                        pointer-events: auto;
                    }
                    .leaflet-top,
                    .leaflet-bottom {
                        position: absolute;
                        z-index: 1000;
                        pointer-events: none;
                    }
                    .leaflet-top {
                        top: 0;
                    }
                    .leaflet-right {
                        right: 0;
                    }
                    .leaflet-bottom {
                        bottom: 0;
                    }
                    .leaflet-left {
                        left: 0;
                    }
                    .leaflet-control {
                        float: left;
                        clear: both;
                    }
                    .leaflet-right .leaflet-control {
                        float: right;
                    }
                    .leaflet-top .leaflet-control {
                        margin-top: 10px;
                    }
                    .leaflet-bottom .leaflet-control {
                        margin-bottom: 10px;
                    }
                    .leaflet-left .leaflet-control {
                        margin-left: 10px;
                    }
                    .leaflet-right .leaflet-control {
                        margin-right: 10px;
                    }
                    .leaflet-fade-anim .leaflet-popup {
                        opacity: 0;
                        -webkit-transition: opacity 0.2s linear;
                           -moz-transition: opacity 0.2s linear;
                                transition: opacity 0.2s linear;
                    }
                    .leaflet-fade-anim .leaflet-map-pane .leaflet-popup {
                        opacity: 1;
                    }
                    .leaflet-zoom-animated {
                        -webkit-transform-origin: 0 0;
                            -ms-transform-origin: 0 0;
                                transform-origin: 0 0;
                    }
                    svg.leaflet-zoom-animated {
                        will-change: transform;
                    }
                    .leaflet-zoom-anim .leaflet-zoom-animated {
                        -webkit-transition: -webkit-transform 0.25s cubic-bezier(0,0,0.25,1);
                           -moz-transition:    -moz-transform 0.25s cubic-bezier(0,0,0.25,1);
                                transition:         transform 0.25s cubic-bezier(0,0,0.25,1);
                    }
                    .leaflet-zoom-anim .leaflet-tile,
                    .leaflet-pan-anim .leaflet-tile {
                        -webkit-transition: none;
                           -moz-transition: none;
                                transition: none;
                    }
                    .leaflet-zoom-anim .leaflet-zoom-hide {
                        visibility: hidden;
                    }
                    .leaflet-interactive {
                        cursor: pointer;
                    }
                    .leaflet-grab {
                        cursor: -webkit-grab;
                        cursor:    -moz-grab;
                        cursor:         grab;
                    }
                    .leaflet-crosshair,
                    .leaflet-crosshair .leaflet-interactive {
                        cursor: crosshair;
                    }
                    .leaflet-popup-pane,
                    .leaflet-control {
                        cursor: auto;
                    }
                    .leaflet-dragging .leaflet-grab,
                    .leaflet-dragging .leaflet-grab .leaflet-interactive,
                    .leaflet-dragging .leaflet-marker-draggable {
                        cursor: move;
                        cursor: -webkit-grabbing;
                        cursor:    -moz-grabbing;
                        cursor:         grabbing;
                    }
                    .leaflet-marker-icon,
                    .leaflet-marker-shadow,
                    .leaflet-image-layer,
                    .leaflet-pane > svg path,
                    .leaflet-tile-container {
                        pointer-events: none;
                    }
                    .leaflet-marker-icon.leaflet-interactive,
                    .leaflet-image-layer.leaflet-interactive,
                    .leaflet-pane > svg path.leaflet-interactive,
                    svg.leaflet-image-layer.leaflet-interactive path {
                        pointer-events: visiblePainted;
                        pointer-events: auto;
                    }
                    .leaflet-container {
                        background: #ddd;
                        outline-offset: 1px;
                    }
                    .leaflet-container a {
                        color: #0078A8;
                    }
                    .leaflet-zoom-box {
                        border: 2px dotted #38f;
                        background: rgba(255,255,255,0.5);
                    }
                    .leaflet-container {
                        font-family: "Helvetica Neue", Arial, Helvetica, sans-serif;
                        font-size: 12px;
                        font-size: 0.75rem;
                        line-height: 1.5;
                    }
                    .leaflet-bar {
                        box-shadow: 0 1px 5px rgba(0,0,0,0.65);
                        border-radius: 4px;
                    }
                    .leaflet-bar a {
                        background-color: #fff;
                        border-bottom: 1px solid #ccc;
                        width: 26px;
                        height: 26px;
                        line-height: 26px;
                        display: block;
                        text-align: center;
                        text-decoration: none;
                        color: black;
                    }
                    .leaflet-bar a,
                    .leaflet-control-layers-toggle {
                        background-position: 50% 50%;
                        background-repeat: no-repeat;
                        display: block;
                    }
                    .leaflet-bar a:hover,
                    .leaflet-bar a:focus {
                        background-color: #f4f4f4;
                    }
                    .leaflet-bar a:first-child {
                        border-top-left-radius: 4px;
                        border-top-right-radius: 4px;
                    }
                    .leaflet-bar a:last-child {
                        border-bottom-left-radius: 4px;
                        border-bottom-right-radius: 4px;
                        border-bottom: none;
                    }
                    .leaflet-bar a.leaflet-disabled {
                        cursor: default;
                        background-color: #f4f4f4;
                        color: #bbb;
                    }
                    .leaflet-touch .leaflet-bar a {
                        width: 30px;
                        height: 30px;
                        line-height: 30px;
                    }
                    .leaflet-touch .leaflet-bar a:first-child {
                        border-top-left-radius: 2px;
                        border-top-right-radius: 2px;
                    }
                    .leaflet-touch .leaflet-bar a:last-child {
                        border-bottom-left-radius: 2px;
                        border-bottom-right-radius: 2px;
                    }
                    .leaflet-control-zoom-in,
                    .leaflet-control-zoom-out {
                        font: bold 18px 'Lucida Console', Monaco, monospace;
                        text-indent: 1px;
                    }
                    .leaflet-touch .leaflet-control-zoom-in, .leaflet-touch .leaflet-control-zoom-out  {
                        font-size: 22px;
                    }
                    .leaflet-control-layers {
                        box-shadow: 0 1px 5px rgba(0,0,0,0.4);
                        background: #fff;
                        border-radius: 5px;
                    }
                    .leaflet-control-layers-toggle {
                        background-image: url(images/layers.png);
                        width: 36px;
                        height: 36px;
                    }
                    .leaflet-retina .leaflet-control-layers-toggle {
                        background-image: url(images/layers-2x.png);
                        background-size: 26px 26px;
                    }
                    .leaflet-touch .leaflet-control-layers-toggle {
                        width: 44px;
                        height: 44px;
                    }
                    .leaflet-control-layers .leaflet-control-layers-list,
                    .leaflet-control-layers-expanded .leaflet-control-layers-toggle {
                        display: none;
                    }
                    .leaflet-control-layers-expanded .leaflet-control-layers-list {
                        display: block;
                        position: relative;
                    }
                    .leaflet-control-layers-expanded {
                        padding: 6px 10px 6px 6px;
                        color: #333;
                        background: #fff;
                    }
                    .leaflet-control-layers-scrollbar {
                        overflow-y: scroll;
                        overflow-x: hidden;
                        padding-right: 5px;
                    }
                    .leaflet-control-layers-selector {
                        margin-top: 2px;
                        position: relative;
                        top: 1px;
                    }
                    .leaflet-control-layers label {
                        display: block;
                        font-size: 13px;
                        font-size: 1.08333em;
                    }
                    .leaflet-control-layers-separator {
                        height: 0;
                        border-top: 1px solid #ddd;
                        margin: 5px -10px 5px -6px;
                    }
                    .leaflet-default-icon-path {
                        background-image: url(images/marker-icon.png);
                    }
                    .leaflet-container .leaflet-control-attribution {
                        background: #fff;
                        background: rgba(255, 255, 255, 0.8);
                        margin: 0;
                    }
                    .leaflet-control-attribution,
                    .leaflet-control-scale-line {
                        padding: 0 5px;
                        color: #333;
                        line-height: 1.4;
                    }
                    .leaflet-control-attribution a {
                        text-decoration: none;
                    }
                    .leaflet-control-attribution a:hover,
                    .leaflet-control-attribution a:focus {
                        text-decoration: underline;
                    }
                    .leaflet-attribution-flag {
                        display: inline !important;
                        vertical-align: baseline !important;
                        width: 1em;
                        height: 0.6669em;
                    }
                    .leaflet-left .leaflet-control-scale {
                        margin-left: 5px;
                    }
                    .leaflet-bottom .leaflet-control-scale {
                        margin-bottom: 5px;
                    }
                    .leaflet-control-scale-line {
                        border: 2px solid #777;
                        border-top: none;
                        line-height: 1.1;
                        padding: 2px 5px 1px;
                        white-space: nowrap;
                        -moz-box-sizing: border-box;
                             box-sizing: border-box;
                        background: rgba(255, 255, 255, 0.8);
                        text-shadow: 1px 1px #fff;
                    }
                    .leaflet-control-scale-line:not(:first-child) {
                        border-top: 2px solid #777;
                        border-bottom: none;
                        margin-top: -2px;
                    }
                    .leaflet-control-scale-line:not(:first-child):not(:last-child) {
                        border-bottom: 2px solid #777;
                    }
                    .leaflet-touch .leaflet-control-attribution,
                    .leaflet-touch .leaflet-control-layers,
                    .leaflet-touch .leaflet-bar {
                        box-shadow: none;
                    }
                    .leaflet-touch .leaflet-control-layers,
                    .leaflet-touch .leaflet-bar {
                        border: 2px solid rgba(0,0,0,0.2);
                        background-clip: padding-box;
                    }
                    .leaflet-popup {
                        position: absolute;
                        text-align: center;
                        margin-bottom: 20px;
                    }
                    .leaflet-popup-content-wrapper {
                        padding: 1px;
                        text-align: left;
                        border-radius: 12px;
                    }
                    .leaflet-popup-content {
                        margin: 13px 24px 13px 20px;
                        line-height: 1.3;
                        font-size: 13px;
                        font-size: 1.08333em;
                        min-height: 1px;
                    }
                    .leaflet-popup-content p {
                        margin: 17px 0;
                        margin: 1.3em 0;
                    }
                    .leaflet-popup-tip-container {
                        width: 40px;
                        height: 20px;
                        position: absolute;
                        left: 50%;
                        margin-top: -1px;
                        margin-left: -20px;
                        overflow: hidden;
                        pointer-events: none;
                    }
                    .leaflet-popup-tip {
                        width: 17px;
                        height: 17px;
                        padding: 1px;
                        margin: -10px auto 0;
                        pointer-events: auto;
                        -webkit-transform: rotate(45deg);
                           -moz-transform: rotate(45deg);
                            -ms-transform: rotate(45deg);
                                transform: rotate(45deg);
                    }
                    .leaflet-popup-content-wrapper,
                    .leaflet-popup-tip {
                        background: white;
                        color: #333;
                        box-shadow: 0 3px 14px rgba(0,0,0,0.4);
                    }
                    .leaflet-container a.leaflet-popup-close-button {
                        position: absolute;
                        top: 0;
                        right: 0;
                        border: none;
                        text-align: center;
                        width: 24px;
                        height: 24px;
                        font: 16px/24px Tahoma, Verdana, sans-serif;
                        color: #757575;
                        text-decoration: none;
                        background: transparent;
                    }
                    .leaflet-container a.leaflet-popup-close-button:hover,
                    .leaflet-container a.leaflet-popup-close-button:focus {
                        color: #585858;
                    }
                    .leaflet-popup-scrolled {
                        overflow: auto;
                    }
                    .leaflet-oldie .leaflet-popup-content-wrapper {
                        -ms-zoom: 1;
                    }
                    .leaflet-oldie .leaflet-popup-tip {
                        width: 24px;
                        margin: 0 auto;
                        -ms-filter: "progid:DXImageTransform.Microsoft.Matrix(M11=0.70710678, M12=0.70710678, M21=-0.70710678, M22=0.70710678)";
                        filter: progid:DXImageTransform.Microsoft.Matrix(M11=0.70710678, M12=0.70710678, M21=-0.70710678, M22=0.70710678);
                    }
                    .leaflet-oldie .leaflet-control-zoom,
                    .leaflet-oldie .leaflet-control-layers,
                    .leaflet-oldie .leaflet-popup-content-wrapper,
                    .leaflet-oldie .leaflet-popup-tip {
                        border: 1px solid #999;
                    }
                    .leaflet-div-icon {
                        background: #fff;
                        border: 1px solid #666;
                    }
                    .leaflet-tooltip {
                        position: absolute;
                        padding: 6px;
                        background-color: #fff;
                        border: 1px solid #fff;
                        border-radius: 3px;
                        color: #222;
                        white-space: nowrap;
                        -webkit-user-select: none;
                        -moz-user-select: none;
                        -ms-user-select: none;
                        user-select: none;
                        pointer-events: none;
                        box-shadow: 0 1px 3px rgba(0,0,0,0.4);
                    }
                    .leaflet-tooltip.leaflet-interactive {
                        cursor: pointer;
                        pointer-events: auto;
                    }
                    .leaflet-tooltip-top:before,
                    .leaflet-tooltip-bottom:before,
                    .leaflet-tooltip-left:before,
                    .leaflet-tooltip-right:before {
                        position: absolute;
                        pointer-events: none;
                        border: 6px solid transparent;
                        background: transparent;
                        content: "";
                    }
                    .leaflet-tooltip-bottom {
                        margin-top: 6px;
                    }
                    .leaflet-tooltip-top {
                        margin-top: -6px;
                    }
                    .leaflet-tooltip-bottom:before,
                    .leaflet-tooltip-top:before {
                        left: 50%;
                        margin-left: -6px;
                    }
                    .leaflet-tooltip-top:before {
                        bottom: 0;
                        margin-bottom: -12px;
                        border-top-color: #fff;
                    }
                    .leaflet-tooltip-bottom:before {
                        top: 0;
                        margin-top: -12px;
                        margin-left: -6px;
                        border-bottom-color: #fff;
                    }
                    .leaflet-tooltip-left {
                        margin-left: -6px;
                    }
                    .leaflet-tooltip-right {
                        margin-left: 6px;
                    }
                    .leaflet-tooltip-left:before,
                    .leaflet-tooltip-right:before {
                        top: 50%;
                        margin-top: -6px;
                    }
                    .leaflet-tooltip-left:before {
                        right: 0;
                        margin-right: -12px;
                        border-left-color: #fff;
                    }
                    .leaflet-tooltip-right:before {
                        left: 0;
                        margin-left: -12px;
                        border-right-color: #fff;
                    }
                    @media print {
                        .leaflet-control {
                            -webkit-print-color-adjust: exact;
                            print-color-adjust: exact;
                        }
                    }
                    /* Custom styles */
                    #map { height: 400px; width: 800px; }
                    body { margin: 0; padding: 0; }
                </style>
                <script src="%s"></script>
            </head>
            <body>
                <div id="map"></div>
                <script>
                    try {
                        console.log = function(message) { window.javaBridge.log(message); };
                        console.error = function(message) { window.javaBridge.log('JS Error: ' + message); };
                        console.log('Starting minimal map initialization');
                        var map = L.map('map').setView([36.8065, 10.1815], 12);
                        console.log('Map created');
                        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                            attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
                        }).addTo(map);
                        console.log('Minimal map loaded');
                    } catch (e) {
                        console.error('Minimal map initialization error: ' + e.message);
                    }
                </script>
            </body>
            </html>
            """.formatted(leafletJs);

        // Save HTML for browser testing
        String filePath = System.getProperty("user.home") + "/minimal_map.html";
        try (java.io.FileWriter writer = new java.io.FileWriter(filePath)) {
            writer.write(html);
            System.out.println("Saved minimal_map.html to: " + filePath);
        } catch (Exception e) {
            System.out.println("Error saving minimal_map.html to " + filePath + ": " + e.getMessage());
        }

        webEngine.loadContent(html);
    }

    @FXML
    private void findNearestTechnician(ActionEvent event) {
        List<Technicien> technicians = technicienService.getAll();
        if (technicians == null || technicians.isEmpty()) {
            resultLabel.setText("No technicians found.");
            return;
        }

        // Find the nearest technician using Haversine distance
        Technicien nearest = technicians.stream()
                .filter(tech -> tech != null && tech.getName() != null && tech.getSpecialite() != null)
                .min(Comparator.comparingDouble(tech -> calculateDistance(
                        currentLat, currentLon, tech.getLatitude(), tech.getLongitude()
                ))).orElse(null);

        if (nearest == null) {
            resultLabel.setText("No nearby technicians found.");
            return;
        }

        // Update map to show only the nearest technician
        String script = String.format("""
            if (typeof map !== 'undefined') {
                map.eachLayer(function(layer) {
                    if (layer instanceof L.Marker && layer !== userMarker) map.removeLayer(layer);
                });
                L.marker([%f, %f], {icon: blueIcon}).addTo(map).bindPopup('%s - %s').openPopup();
                map.setView([%f, %f], 12);
            }
            """, nearest.getLatitude(), nearest.getLongitude(), nearest.getName(), nearest.getSpecialite(),
                currentLat, currentLon);
        try {
            webEngine.executeScript(script);
        } catch (Exception e) {
            System.out.println("Error executing map update script: " + e.getMessage());
        }

        // Display result
        double distance = calculateDistance(currentLat, currentLon, nearest.getLatitude(), nearest.getLongitude());
        resultLabel.setText(String.format(
                "Nearest Technician: %s %s (%.2f km away)\nLat: %.4f, Lon: %.4f",
                nearest.getName(), nearest.getSpecialite(), distance,
                nearest.getLatitude(), nearest.getLongitude()
        ));
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @FXML
    private void goBack(ActionEvent event) {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
