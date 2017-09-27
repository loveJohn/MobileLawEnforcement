package com.chinadci.mel.mleo.utils.draw;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.FillSymbol;
import com.esri.core.symbol.LineSymbol;
import com.esri.core.symbol.MarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.TextSymbol;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 画图实现类，支持画点、矩形、线、多边形、圆、手画线、手画多边形，可设置各种图形的symbol。
 */
public class DrawTool extends Subject {

    private MapView mapView;
    private GraphicsLayer tempLayer;//绘制图层--图像
    private GraphicsLayer annotationLayer;//绘制--点击的坐标
    private MarkerSymbol markerSymbol;
    private LineSymbol lineSymbol;
    private FillSymbol fillSymbol;
    private int drawType;
    private boolean active;
    private Point point;
    private Envelope envelope;
    private Polyline polyline;
    private Polygon polygon;
    private DrawTouchListener drawListener;
//    private MapOnTouchListener defaultListener;
    private Graphic drawGraphic;//绘制图层--图像
    private Graphic drawPointGraphic;//绘制图层--点
    private Point startPoint;
    private int graphicID;

    public static final int POINT = 1;
    public static final int ENVELOPE = 2;
    public static final int POLYLINE = 3;
    public static final int POLYGON = 4;
    public static final int CIRCLE = 5;
    public static final int ELLIPSE = 6;
    public static final int FREEHAND_POLYGON = 7;
    public static final int FREEHAND_POLYLINE = 8;

    public static final int JL_POLYLINE = 9;
    public static final int MJ_POLYGON = 10;

    DecimalFormat decFormat = new DecimalFormat("#.000");
    private List<Point> pointList;

    public DrawTool(MapView mapView) {
        this.mapView = mapView;
        this.tempLayer = new GraphicsLayer();
        this.annotationLayer = new GraphicsLayer();
        this.mapView.addLayer(this.tempLayer);
        this.mapView.addLayer(this.annotationLayer);
        drawListener = new DrawTouchListener(this.mapView.getContext(),
                this.mapView);
//        defaultListener = new MapOnTouchListener(this.mapView.getContext(),
//                this.mapView);
        this.markerSymbol = new SimpleMarkerSymbol(Color.RED, 5,
                SimpleMarkerSymbol.STYLE.CIRCLE);
        this.lineSymbol = new SimpleLineSymbol(Color.BLUE, 2);
        this.fillSymbol = new SimpleFillSymbol(Color.RED);
        this.fillSymbol.setAlpha(60);
    }

    public void activate(int drawType) {
        if (this.mapView == null)
            return;

        this.deactivate();

        this.mapView.setOnTouchListener(drawListener);
        this.drawType = drawType;
        this.active = true;
        switch (this.drawType) {
            case DrawTool.POINT:
                this.point = new Point();
                drawGraphic = new Graphic(this.point, this.markerSymbol);
                break;
            case DrawTool.ENVELOPE:
                this.envelope = new Envelope();
                drawGraphic = new Graphic(this.envelope, this.fillSymbol);
                break;
            case DrawTool.MJ_POLYGON:
            case DrawTool.POLYGON:
                this.point = new Point();
                this.polygon = new Polygon();
                drawGraphic = new Graphic(this.polygon, this.fillSymbol);
                drawPointGraphic = new Graphic(this.point, this.markerSymbol);
                break;
            case DrawTool.CIRCLE:
                this.polygon = new Polygon();
                drawGraphic = new Graphic(this.polygon, this.fillSymbol);
                break;
            case DrawTool.FREEHAND_POLYGON:
                this.polygon = new Polygon();
                drawGraphic = new Graphic(this.polygon, this.fillSymbol);
                break;
            case DrawTool.JL_POLYLINE:
            case DrawTool.POLYLINE:
            case DrawTool.FREEHAND_POLYLINE:
                this.polyline = new Polyline();
                drawGraphic = new Graphic(this.polyline, this.lineSymbol);
                break;
        }
        graphicID = this.tempLayer.addGraphic(drawGraphic);
    }

    public void deactivateOK() {
//        this.mapView.setOnTouchListener(defaultListener);
        this.active = false;
        this.drawType = -1;
        this.point = null;
        this.envelope = null;
        this.polygon = null;
        this.polyline = null;
        this.drawGraphic = null;
        this.startPoint = null;
        if (this.pointList != null) {
            this.pointList = null;
        }
    }

    public void deactivate() {
//        this.mapView.setOnTouchListener(defaultListener);
        this.tempLayer.removeAll();
        this.annotationLayer.removeAll();
        this.active = false;
        this.drawType = -1;
        this.point = null;
        this.envelope = null;
        this.polygon = null;
        this.polyline = null;
        this.drawGraphic = null;
        this.startPoint = null;
        if (this.pointList != null) {
            this.pointList = null;
        }
    }

    public MarkerSymbol getMarkerSymbol() {
        return markerSymbol;
    }

    public void setMarkerSymbol(MarkerSymbol markerSymbol) {
        this.markerSymbol = markerSymbol;
    }

    public LineSymbol getLineSymbol() {
        return lineSymbol;
    }

    public void setLineSymbol(LineSymbol lineSymbol) {
        this.lineSymbol = lineSymbol;
    }

    public FillSymbol getFillSymbol() {
        return fillSymbol;
    }

    public void setFillSymbol(FillSymbol fillSymbol) {
        this.fillSymbol = fillSymbol;
    }

    private void sendDrawEndEvent() {
        DrawEvent e = new DrawEvent(this, DrawEvent.DRAW_END,
                DrawTool.this.drawGraphic);
        DrawTool.this.notifyEvent(e);
        int type = this.drawType;
        this.deactivate();
        this.activate(type);
    }

    // 扩展MapOnTouchListener，实现画图功能
    class DrawTouchListener extends MapOnTouchListener {

        public DrawTouchListener(Context context, MapView view) {
            super(context, view);
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            try {
                if (active
                        && (drawType == POINT || drawType == ENVELOPE
                        || drawType == CIRCLE
                        || drawType == FREEHAND_POLYLINE || drawType == FREEHAND_POLYGON)
                        && event.getAction() == MotionEvent.ACTION_DOWN) {
                    Point point = mapView.toMapPoint(event.getX(), event.getY());
                    switch (drawType) {
                        case DrawTool.POINT:
                            DrawTool.this.point.setXY(point.getX(), point.getY());
                            sendDrawEndEvent();
                            break;
                        case DrawTool.ENVELOPE:
                            startPoint = point;
                            envelope.setCoords(point.getX(), point.getY(),
                                    point.getX(), point.getY());
                            break;
                        case DrawTool.CIRCLE:
                            startPoint = point;
                            break;
                        case DrawTool.FREEHAND_POLYGON:
                            polygon.startPath(point);
                            break;
                        case DrawTool.FREEHAND_POLYLINE:
                            polyline.startPath(point);
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return super.onTouch(view, event);
        }

        @Override
        public boolean onDragPointerMove(MotionEvent from, MotionEvent to) {
            try {
                if (active
                        && (drawType == ENVELOPE || drawType == FREEHAND_POLYGON
                        || drawType == FREEHAND_POLYLINE || drawType == CIRCLE)) {
                    Point point = mapView.toMapPoint(to.getX(), to.getY());
                    switch (drawType) {
                        case DrawTool.ENVELOPE:
                            envelope.setXMin(startPoint.getX() > point.getX() ? point
                                    .getX() : startPoint.getX());
                            envelope.setYMin(startPoint.getY() > point.getY() ? point
                                    .getY() : startPoint.getY());
                            envelope.setXMax(startPoint.getX() < point.getX() ? point
                                    .getX() : startPoint.getX());
                            envelope.setYMax(startPoint.getY() < point.getY() ? point
                                    .getY() : startPoint.getY());
                            DrawTool.this.tempLayer.updateGraphic(graphicID, envelope.copy());
                            break;
                        case DrawTool.FREEHAND_POLYGON:
                            polygon.lineTo(point);
                            DrawTool.this.tempLayer.updateGraphic(graphicID, polygon);
                            break;
                        case DrawTool.FREEHAND_POLYLINE:
                            polyline.lineTo(point);
                            DrawTool.this.tempLayer.updateGraphic(graphicID, polyline);
                            break;
                        case DrawTool.CIRCLE:
                            double radius = Math.sqrt(Math.pow(startPoint.getX()
                                    - point.getX(), 2)
                                    + Math.pow(startPoint.getY() - point.getY(), 2));
                            getCircle(startPoint, radius, polygon);
                            DrawTool.this.tempLayer.updateGraphic(graphicID, polygon);
                            break;
                    }
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return super.onDragPointerMove(from, to);
        }

        public boolean onDragPointerUp(MotionEvent from, MotionEvent to) {
            try {
                if (active && (drawType == ENVELOPE || drawType == FREEHAND_POLYGON
                        || drawType == FREEHAND_POLYLINE || drawType == CIRCLE)) {
                    Point point = mapView.toMapPoint(to.getX(), to.getY());
                    switch (drawType) {
                        case DrawTool.ENVELOPE:
                            envelope.setXMin(startPoint.getX() > point.getX() ? point
                                    .getX() : startPoint.getX());
                            envelope.setYMin(startPoint.getY() > point.getY() ? point
                                    .getY() : startPoint.getY());
                            envelope.setXMax(startPoint.getX() < point.getX() ? point
                                    .getX() : startPoint.getX());
                            envelope.setYMax(startPoint.getY() < point.getY() ? point
                                    .getY() : startPoint.getY());
                            break;
                        case DrawTool.FREEHAND_POLYGON:
                            polygon.lineTo(point);
                            break;
                        case DrawTool.FREEHAND_POLYLINE:
                            polyline.lineTo(point);
                            break;
                        case DrawTool.CIRCLE:
                            double radius = Math.sqrt(Math.pow(startPoint.getX()
                                    - point.getX(), 2)
                                    + Math.pow(startPoint.getY() - point.getY(), 2));
                            getCircle(startPoint, radius, polygon);
                            break;
                    }
                    sendDrawEndEvent();
                    startPoint = null;
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return super.onDragPointerUp(from, to);
        }

        public boolean onSingleTap(MotionEvent event) {
            try {
                Point point = mapView.toMapPoint(event.getX(), event.getY());
                if (active && (drawType == POLYGON || drawType == POLYLINE ||drawType == MJ_POLYGON || drawType == JL_POLYLINE)) {
                    switch (drawType) {
                        case DrawTool.POLYGON:
                            if (startPoint == null) {
                                startPoint = point;
                                polygon.startPath(point);
                            } else {
                                polygon.lineTo(point);
                            }
                            DrawTool.this.tempLayer.updateGraphic(graphicID, polygon);
                            drawPointGraphic = new Graphic(point, markerSymbol);
                            DrawTool.this.tempLayer.addGraphic(drawPointGraphic);
                            String txt = String.format("X:%1$s Y:%2$s",
                                    decFormat.format(point.getX()),
                                    decFormat.format(point.getY()));
                            TextSymbol textSymbol = new TextSymbol(18, txt, 0xffff6400);
                            Graphic annotation = new Graphic(point, textSymbol);
                            DrawTool.this.annotationLayer.removeAll();
                            DrawTool.this.annotationLayer.addGraphic(annotation);
                            if (pointList == null) {
                                pointList = new ArrayList<Point>();
                            }
                            pointList.add(point);
                            break;
                        case DrawTool.MJ_POLYGON:
                            if (startPoint == null) {
                                startPoint = point;
                                polygon.startPath(point);
                            } else {
                                polygon.lineTo(point);
                            }
                            DrawTool.this.tempLayer.updateGraphic(graphicID, polygon);
                            drawPointGraphic = new Graphic(point, markerSymbol);
                            DrawTool.this.tempLayer.addGraphic(drawPointGraphic);
                            if (pointList == null) {
                                pointList = new ArrayList<Point>();
                            }
                            pointList.add(point);

                            double centerX=startPoint.getX();
                            double centerY=startPoint.getY();
                            int pointCount = pointList.size();
                            for (int i = 1; i < pointCount; i++) {
                                centerX+= pointList.get(i).getX();
                                centerY+= pointList.get(i).getY();
                            }
                            centerX=centerX/pointCount;
                            centerY=centerY/pointCount;
                            //计算面积
                            Geometry geo = GeometryEngine.project(
                                    polygon,
                                    SpatialReference
                                            .create(SpatialReference.WKID_WGS84),
                                    SpatialReference
                                            .create(SpatialReference.WKID_WGS84_WEB_MERCATOR));
                            long num = Math.abs(Math.round(geo
                                    .calculateArea2D()));
                            String txtMj = num+"m²";
                            TextSymbol textSymbolMJ = new TextSymbol(18, txtMj,0xffff6400);
                            Graphic mjGrap = new Graphic(new Point(centerX, centerY), textSymbolMJ);
                            DrawTool.this.annotationLayer.removeAll();
                            DrawTool.this.annotationLayer.addGraphic(mjGrap);
                            break;
                        case DrawTool.POLYLINE:
                            if (startPoint == null) {
                                startPoint = point;
                                polyline.startPath(point);
                            } else {
                                polyline.lineTo(point);
                            }
                            DrawTool.this.tempLayer.updateGraphic(graphicID, polyline);
                            drawPointGraphic = new Graphic(point, markerSymbol);
                            DrawTool.this.tempLayer.addGraphic(drawPointGraphic);
                            String txt2 = String.format("X:%1$s Y:%2$s",
                                    decFormat.format(point.getX()),
                                    decFormat.format(point.getY()));
                            TextSymbol textSymbol2 = new TextSymbol(18, txt2, 0xff00008B);
                            Graphic annotation2 = new Graphic(point, textSymbol2);
                            DrawTool.this.annotationLayer.removeAll();
                            DrawTool.this.annotationLayer.addGraphic(annotation2);
                            break;
                        case DrawTool.JL_POLYLINE:
                            if (startPoint == null) {
                                startPoint = point;
                                polyline.startPath(point);
                            } else
                                polyline.lineTo(point);
                            DrawTool.this.tempLayer.updateGraphic(graphicID, polyline);
                            drawPointGraphic = new Graphic(point, markerSymbol);
                            DrawTool.this.tempLayer.addGraphic(drawPointGraphic);
                            if (pointList == null) {
                                pointList = new ArrayList<Point>();
                            }
                            pointList.add(point);
                            double centerX0=startPoint.getX();
                            double centerY0=startPoint.getY();
                            int pointCount0 = pointList.size();
                            for (int i = 1; i < pointCount0; i++) {
                                centerX0+= pointList.get(i).getX();
                                centerY0+= pointList.get(i).getY();
                            }
                            centerX0=centerX0/pointCount0;
                            centerY0=centerY0/pointCount0;
                            //计算距离
                            Geometry p = GeometryEngine.project(
                                    polyline,
                                    SpatialReference
                                            .create(SpatialReference.WKID_WGS84),
                                    SpatialReference
                                            .create(SpatialReference.WKID_WGS84_WEB_MERCATOR));
                            long num0 = Math.abs(Math.round(p.calculateLength2D()));
                            float figureL = (float) num0;
                            DecimalFormat kmFormat = new DecimalFormat("#.###");
                            DecimalFormat mFormat = new DecimalFormat("#");
                            String txtJL;
                            if (figureL < 1000) {
                                txtJL = mFormat.format(figureL) + "m";
                            } else{
                                txtJL = kmFormat.format(figureL / 1000f) + "km";
                            }
                            TextSymbol textSymbolJL = new TextSymbol(18, txtJL,0xff00008B);
                            Graphic jlGrap = new Graphic(new Point(centerX0, centerY0), textSymbolJL);
                            DrawTool.this.annotationLayer.removeAll();
                            DrawTool.this.annotationLayer.addGraphic(jlGrap);
                            break;
                    }
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        public boolean onDoubleTap(MotionEvent event) {
//            try {
//                if (active && (drawType == POLYGON || drawType == POLYLINE ||drawType == MJ_POLYGON || drawType == JL_POLYLINE)) {
//                    Point point = mapView.toMapPoint(event.getX(), event.getY());
//                    switch (drawType) {
//                        case DrawTool.MJ_POLYGON:
//                        case DrawTool.POLYGON:
//                            polygon.lineTo(point);
//                            break;
//                        case DrawTool.JL_POLYLINE:
//                        case DrawTool.POLYLINE:
//                            polyline.lineTo(point);
//                            break;
//                    }
//                    sendDrawEndEvent();
//                    startPoint = null;
//                    return true;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            return super.onDoubleTap(event);
        }

        @Override
        public void onLongPress(MotionEvent event) {
            Point point = mapView.toMapPoint(event.getX(), event.getY());

            super.onLongPress(event);
        }

        private void getCircle(Point center, double radius, Polygon circle) {
            circle.setEmpty();
            Point[] points = getPoints(center, radius);
            circle.startPath(points[0]);
            for (int i = 1; i < points.length; i++)
                circle.lineTo(points[i]);
        }

        private Point[] getPoints(Point center, double radius) {
            Point[] points = new Point[50];
            double sin;
            double cos;
            double x;
            double y;
            for (double i = 0; i < 50; i++) {
                sin = Math.sin(Math.PI * 2 * i / 50);
                cos = Math.cos(Math.PI * 2 * i / 50);
                x = center.getX() + radius * sin;
                y = center.getY() + radius * cos;
                points[(int) i] = new Point(x, y);
            }
            return points;
        }
    }

    public List<Point> getPointListRedLine() {
        return pointList;
    }

    public void setPointListRedLine(List<Point> pointListRedLine) {
        this.pointList = pointListRedLine;
    }

    /**
     * geoCrossSelf
     * 判定红线是否有自交现象
     */
    public boolean geoCrossSelf(List<Point> points) {
        if (points == null || points.size() < 4)
            return false;
        int pcount = points.size();
        for (int n = 0; n < pcount; n++) {
            Point p1 = points.get((n + 0) % pcount);
            Point p2 = points.get((n + 1) % pcount);
            MultiPath line1 = new Polyline();
            line1.startPath(p1);
            line1.lineTo(p2);
            for (int m = 0; m < pcount - 2; m++) {
                Point p3 = points.get((m + 2) % pcount);
                Point p4 = points.get((m + 3) % pcount);
                MultiPath line2 = new Polyline();
                line2.startPath(p3);
                line2.lineTo(p4);
                if (GeometryEngine.crosses(line1, line2, null))
                    return true;
            }
        }
        return false;
    }

    /**
     * 点是否在红线区域内
     */
    public boolean pointIsInside(Point point, Envelope extent) {
        return (point.getX() > extent.getXMin() && point.getX() < extent.getXMax()
                && point.getY() > extent.getYMin() && point.getY() < extent.getYMax());
    }

    public static String polygonToJson(List<Point> points) {
        if (points == null || points.size() < 1) {
            return null;
        }
        try {
            MultiPath poly;
            poly = new Polygon();
            poly.startPath(points.get(0));
            for (int i = 1; i < points.size(); i++) {
                poly.lineTo(points.get(i));
            }
            Geometry mercatorPoly = GeometryEngine.project(poly,
                    SpatialReference.create(SpatialReference.WKID_WGS84),
                    SpatialReference
                            .create(SpatialReference.WKID_WGS84_WEB_MERCATOR));
            double area = mercatorPoly.calculateArea2D();
            if (area > 0) {
                return GeometryEngine.geometryToJson(null, poly);
            } else {
                area = Math.abs(area);
                poly.reverseAllPaths();
                return GeometryEngine.geometryToJson(null, poly);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
