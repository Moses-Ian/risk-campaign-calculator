/*
	TODO MUCH LATER:
		finish set styles
		gracefully allow multiple data series
*/

/*
	HOW TO CONTRUCT
		If the view is defined in xml, do this in program
		graph = (GraphView) findViewById(R.id.graph1);
		double[] data = something;
		graph.setData(data);
		graph.createChart();
		graph.setVisibility(View.VISIBLE);
*/

package net.phoenixramen.risk;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Handler;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.View;
import android.view.MotionEvent;

import java.util.concurrent.CopyOnWriteArrayList;

import org.afree.chart.AFreeChart;
import org.afree.chart.ChartFactory;
import org.afree.chart.ChartRenderingInfo;
import org.afree.chart.ChartTouchListener;
import org.afree.chart.annotations.XYPointerAnnotation;
import org.afree.chart.axis.NumberAxis;
import org.afree.chart.axis.ValueAxis;
import org.afree.chart.event.ChartChangeEvent;
import org.afree.chart.event.ChartChangeListener;
import org.afree.chart.plot.Movable;
import org.afree.chart.plot.Plot;
import org.afree.chart.plot.PlotOrientation;
import org.afree.chart.plot.PlotRenderingInfo;
import org.afree.chart.plot.XYPlot;
import org.afree.chart.plot.Zoomable;
import org.afree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.afree.data.xy.XYDataset;
import org.afree.data.xy.XYSeries;
import org.afree.data.xy.XYSeriesCollection;
import org.afree.graphics.SolidColor;
import org.afree.graphics.geom.Dimension;
import org.afree.graphics.geom.Font;
import org.afree.graphics.geom.RectShape;
import org.afree.ui.RectangleInsets;  

public class GraphView	extends View 
						implements ChartChangeListener
{
	//logcat
	//private final String LOGTAG = "GraphView";
	//some defaults
	public static final boolean DEFAULT_BUFFER_USED = true;			
	public static final int DEFAULT_WIDTH = 680;        			
	public static final int DEFAULT_HEIGHT = 420;            		
	public static final int DEFAULT_MINIMUM_DRAW_WIDTH = 10;        
	public static final int DEFAULT_MINIMUM_DRAW_HEIGHT = 10;       
	public static final int DEFAULT_MAXIMUM_DRAW_WIDTH = 1024;      
	public static final int DEFAULT_MAXIMUM_DRAW_HEIGHT = 1000;   
	private final float MAX_SCALE = 10.0f;
	private final float MIN_SCALE = 0.01f;
	//delete maybe?
	private boolean domainMovable = false;        					
	private boolean rangeMovable = false;            
	//some global constants
	private PlotOrientation orientation = PlotOrientation.VERTICAL; 
	final private int MAX_LONG_PRESS_RANGE = 10;	//if the action_up is outside this box, it wasn't a long press
	final private int MIN_MOVE_RANGE = 5;
	private int DARKGRAY = Color.rgb(0xbe, 0xbe, 0xbe);
	private int ORANGE = Color.rgb(0xff, 0x88, 0x00);
	private int LTBLUE = Color.rgb(0, 125, 255);
	//some important objects
	private Context myContext;
	private AFreeChart chart;       
	private transient CopyOnWriteArrayList<ChartTouchListener> chartMotionListeners;        
	private ChartRenderingInfo info;        						
	private PointF anchor;  	
	private CountDownTimer timer;
	private Handler mHandler;  
	//some important values
	private float scaleX = 1f;        									
	private float scaleY = 1f;        									
	private RectangleInsets insets = null;              			
	private int minimumDrawWidth;        							
	private	int minimumDrawHeight;       							
	private int maximumDrawWidth;        							
	private int maximumDrawHeight;        
	private Dimension size = null;        							
	private int[][][] coords = new int[2][3][2];	//first index is pointer id, second is start,old,new, third is x,y
	private float currentScale = 1.0f;
	//the data
	private XYSeriesCollection data = new XYSeriesCollection();
	//xml default values
	private String title = "";
	private String domainLabel = "";
	private String rangeLabel = "";
	
	private boolean includeLegend = false;
	private boolean includeTooltips = true;		//annotations are lumped into this
	private boolean includeUrls = false;
	private boolean showCrosshairs = true;
	private boolean lockOnData = true;				//implicitly sets the annotation to lock on data to same value
	
	private int chartBgColor = Color.BLACK;
	private int plotBgColor = Color.LTGRAY;
	private int gridColor = Color.WHITE;
	private int labelColor = DARKGRAY;
	private int crosshairColor = LTBLUE;
	private int tooltipColor = Color.BLACK;
	//could be xml later
	private boolean borderVisible = false;
	private float crosshairStroke = 1.5f;
	private float seriesStroke = 1.5f;
	private double seriesShapeSize = 6.0;
	private int labelFontSize = 12;
	private int tooltipFontSize = 12;
	private int tickFontSize = 10;


	

// Constructors
	public GraphView(Context context) 
	{          
		super(context);          
		mHandler = new Handler();          
		initializeContext(context);
	}
	
	public GraphView(Context context, AttributeSet attrs) 
	{          
		super(context, attrs);          
		mHandler = new Handler();          
		initializeContext(context);
		initializeAttributes(attrs);
	}        

// Creation Methods
	private void initializeContext(Context context) 
	{          
		this.chartMotionListeners = new CopyOnWriteArrayList<ChartTouchListener>();          
		this.info = new ChartRenderingInfo();          
		this.minimumDrawWidth = DEFAULT_MINIMUM_DRAW_WIDTH;          
		this.minimumDrawHeight = DEFAULT_MINIMUM_DRAW_HEIGHT;          
		this.maximumDrawWidth = DEFAULT_MAXIMUM_DRAW_WIDTH;          
		this.maximumDrawHeight = DEFAULT_MAXIMUM_DRAW_HEIGHT;          
		new java.util.ArrayList();      
		myContext = context;
	}

	private void initializeAttributes(AttributeSet attrs)
	{
		TypedArray a = getContext().obtainStyledAttributes(attrs,R.styleable.GraphView);
		title = a.getString(R.styleable.GraphView_title);
		domainLabel = a.getString(R.styleable.GraphView_domain_label);
		rangeLabel = a.getString(R.styleable.GraphView_range_label);
		
		includeLegend = a.getBoolean(R.styleable.GraphView_include_legend,false);	//the false here in the default value
		includeTooltips = a.getBoolean(R.styleable.GraphView_include_tooltips,true);
		includeUrls = a.getBoolean(R.styleable.GraphView_include_urls,false);
		showCrosshairs = a.getBoolean(R.styleable.GraphView_show_crosshairs,true);
		lockOnData = a.getBoolean(R.styleable.GraphView_lock_on_data,true);
		
		chartBgColor = a.getColor(R.styleable.GraphView_chart_bg_color,Color.BLACK);
		plotBgColor = a.getColor(R.styleable.GraphView_plot_bg_color,Color.LTGRAY);
		gridColor = a.getColor(R.styleable.GraphView_grid_color,Color.WHITE);
		labelColor = a.getColor(R.styleable.GraphView_label_color,DARKGRAY);
		crosshairColor = a.getColor(R.styleable.GraphView_crosshair_color,LTBLUE);
		tooltipColor = a.getColor(R.styleable.GraphView_tooltip_color,Color.BLACK);
	}
	
	public void createChart() 
	{
		//if it has no size, wait until onSizeChanged is call so you don't waste energy
		if (getWidth() == 0 || getHeight() == 0)	return;
		
		XYDataset dataset = data;
		// create the chart...
		AFreeChart chart = ChartFactory.createXYLineChart(
			title,
			domainLabel,
			rangeLabel,
			dataset,					// data
			orientation,				//should be vertical
			includeLegend,				// don't include legend
			includeTooltips,			// tooltips
			includeUrls					// urls
		);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		chart.setBackgroundPaintType(new SolidColor(chartBgColor));
		chart.setBorderVisible(borderVisible);

		// get a reference to the plot for further customisation...
		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaintType(new SolidColor(plotBgColor));				//any colors might be allowed to be external variables

		plot.setDomainGridlinePaintType(new SolidColor(gridColor));
		plot.setRangeGridlinePaintType(new SolidColor(gridColor));

		plot.setDomainCrosshairLockedOnData(lockOnData);
		plot.setRangeCrosshairLockedOnData(lockOnData);

		plot.setDomainCrosshairVisible(showCrosshairs);
		plot.setRangeCrosshairVisible(showCrosshairs);
		
		plot.setDomainCrosshairPaintType(new SolidColor(crosshairColor));
		plot.setRangeCrosshairPaintType(new SolidColor(crosshairColor));
		
		plot.setDomainCrosshairStroke(crosshairStroke*scaleX);
		plot.setRangeCrosshairStroke(crosshairStroke*scaleY);

		// change the auto tick unit selection to integer units only...
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		
		rangeAxis.setLabelPaintType(new SolidColor(labelColor));
		domainAxis.setLabelPaintType(new SolidColor(labelColor));
		
		rangeAxis.setTickLabelPaintType(new SolidColor(labelColor));		//for now, the axis label color is the tick label color
		domainAxis.setTickLabelPaintType(new SolidColor(labelColor));
		
		Font label = new Font("sans serif", 1, (int) Math.round(labelFontSize*scaleY));
		rangeAxis.setLabelFont(label);
		domainAxis.setLabelFont(label);
		
		Font tick = new Font("sans serif", 1, (int) Math.round(tickFontSize*scaleY));
		rangeAxis.setTickLabelFont(tick);
		domainAxis.setTickLabelFont(tick);
		
		//renderer
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		plot.setRenderer(renderer);
		renderer.setSeriesShapesFilled(0, true);
		renderer.setSeriesPaintType(0, new SolidColor(ORANGE));
		renderer.setSeriesStroke(0, seriesStroke*scaleX, false);
		renderer.setSeriesFillPaintType(0, new SolidColor(Color.YELLOW));
		renderer.setSeriesOutlinePaintType(0, new SolidColor(Color.GRAY));
		renderer.setUseFillPaint(true);
		
		double size = seriesShapeSize*scaleX;
        double delta = size / 2.0;
		renderer.setSeriesShape(0, new RectShape(-delta, -delta, size, size) );
	
		// OPTIONAL CUSTOMISATION COMPLETED.
		invalidate();
		setChart(chart);
	}

// Update Methods
	public void repaint() 
	{          
		mHandler.post(new Runnable() 
		{              
			public void run() 
			{                 
				invalidate();              
			}          
		});    
		
	}            

	public void chartChanged(ChartChangeEvent event) 
	{  
		repaint();      
	}        

	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{          
		this.insets = new RectangleInsets(0,0,0,0);
		this.size = new Dimension(w, h);
		scaleX = w/320f;	//these are the values I originally calibrated for
		scaleY = h/404f;
		createChart();
	}

	protected void onDraw(Canvas canvas) 
	{          
		super.onDraw(canvas);                    
           
		paintComponent(canvas);
		createAnnotation(canvas);
		//getting tooltips data - may be unecessary
		XYPlot xyp = chart.getXYPlot();
				double dc = xyp.getDomainCrosshairValue();
				double rc = xyp.getRangeCrosshairValue();
				String s = String.format("(%2.0f,%5.2f%%)",dc,rc);
	}

	public void paintComponent(Canvas canvas) 
	{            
		// first determine the size of the chart rendering area...          
		Dimension size = getSize();          
		RectShape available = new RectShape(insets.getLeft(), insets.getTop(),                  
			size.getWidth() - insets.getLeft() - insets.getRight(),                  
			size.getHeight() - insets.getTop() - insets.getBottom());            
		float drawWidth = (float)available.getWidth();          
		float drawHeight = (float)available.getHeight();          
		this.scaleX = 1f;          
		this.scaleY = 1f;            
		if (drawWidth < this.minimumDrawWidth) 
		{              
			this.scaleX = drawWidth / this.minimumDrawWidth;              
			drawWidth = this.minimumDrawWidth;          
		} else if (drawWidth > this.maximumDrawWidth) 
		{              
			this.scaleX = drawWidth / this.maximumDrawWidth;              
			drawWidth = this.maximumDrawWidth;          
		}            
		if (drawHeight < this.minimumDrawHeight) 
		{              
			this.scaleY = drawHeight / this.minimumDrawHeight;              
			drawHeight = this.minimumDrawHeight;          
		} else if (drawHeight > this.maximumDrawHeight) 
		{              
			this.scaleY = drawHeight / this.maximumDrawHeight;              
			drawHeight = this.maximumDrawHeight;          
		}            
		RectShape chartArea = new RectShape(0.0, 0.0, drawWidth, drawHeight);            
		this.chart.draw(canvas, chartArea, this.anchor, this.info);  
		this.anchor = null;        
	}
	
	public void setChart(AFreeChart chart) 
	{            
		// stop listening for changes to the existing chart          
		if (this.chart != null) 
		{              
			this.chart.removeChangeListener(this);              
		}            
		// add the new chart          
		this.chart = chart;          
		if (chart != null) 
		{              
			this.chart.addChangeListener(this);              
			Plot plot = chart.getPlot();              
			Zoomable z = (Zoomable) plot;                  
			z.isRangeZoomable();                  
			this.orientation = z.getOrientation();                                          
			this.domainMovable = false;              
			this.rangeMovable = false;
			Movable m = (Movable) plot;                  
			this.domainMovable = m.isDomainMovable(); 
			this.rangeMovable = m.isRangeMovable();                  
			this.orientation = m.getOrientation();          
		} else 
		{              
			this.domainMovable = false;              
			this.rangeMovable = false;          
		}          
		repaint();        
	}        
	
// Annotation Methods
	public void createAnnotation(Canvas canvas)
	{
		if (! includeTooltips) return;
		
		XYPlot xyp = chart.getXYPlot();
		PlotRenderingInfo plotInfo = this.info.getPlotInfo();
		//prepare some data
		RectShape dataArea = plotInfo.getDataArea();
		ValueAxis domainAxis = xyp.getDomainAxis();
		ValueAxis rangeAxis = xyp.getRangeAxis();
		double xCoord = xyp.getDomainCrosshairValue();
		double yCoord = xyp.getRangeCrosshairValue();
		
		//This should all be xml stuff
		double angle = - Math.PI * 7/8;	//in radians, spinning clockwise (because the designer is an idiot)
		String label = String.format("%2.0f , %5.2f",xCoord,yCoord);
		double labelOffset;	
		int size = Math.round(tooltipFontSize*scaleX);
		
		//create the annotation
		XYPointerAnnotation annotation = new XYPointerAnnotation(label, xCoord, yCoord, angle);
		
		//edit the annotation
		labelOffset = - (size/2) / Math.sin(Math.PI - angle);	//the negative because again, idiot
		annotation.setLabelOffset(labelOffset);
		annotation.setFont(new Font("sans serif", 1, size )); 		//might not be working
		annotation.setPaintType(new SolidColor(tooltipColor));
		annotation.setArrowPaintType(new SolidColor(tooltipColor));
		
		//draw the annotation
		annotation.draw(canvas, xyp, dataArea, domainAxis, rangeAxis, 0, plotInfo);
		//the annotation is automatically destroyed and erased when the graph is redrawn
	}
	
// Touch Control Methods
	public boolean onTouchEvent(MotionEvent event) 
	{     
		int eventAction = event.getAction();      
		
		//prepare some info
		//event info
		int pIndex = event.getActionIndex();
		final int pID = event.getPointerId(pIndex);
		int count = event.getPointerCount();

		if (count > 2 || pID >= 2)
			return false;	//if there's a third finger, let someone else handle it
		
		coords[pID][2][0] = (int)event.getX();     
		coords[pID][2][1] = (int)event.getY(); 
		PointF touchLocation = new PointF(event.getX(), event.getY());
		anchor = touchLocation;
		
		//the whole view
		PlotRenderingInfo plotInfo = this.info.getPlotInfo();
		Plot p = this.chart.getPlot();
		//the area of the actual graph without the axis labels
		RectShape dataArea = this.info.getPlotInfo().getDataArea();
		double dataAreaWidth = dataArea.getWidth();              
		double dataAreaHeight = dataArea.getHeight(); 
		
//		printCoords();
		
		switch (count)
		{
			case 1:
				//check whether it was a press, swipe, or release
				switch (eventAction) 
				{
					case MotionEvent.ACTION_DOWN: 
		
						// finger touches the screen 
						coords[pID][0][0] = coords[pID][1][0] = coords[pID][2][0];	//hey look at that
						coords[pID][0][1] = coords[pID][1][1] = coords[pID][2][1];	//you can assign things into multiple things
						
						//long press stuff
						timer = new CountDownTimer(1000L, 1000L) 
						{
							public void onTick(long unused) {/*does nothing*/}
							public void onFinish() 
							{
								if (Math.abs(coords[pID][2][0]-coords[pID][0][0])<MAX_LONG_PRESS_RANGE 
									&& Math.abs(coords[pID][2][1]-coords[pID][0][1])<MAX_LONG_PRESS_RANGE)
								{
									Vibrator v = (Vibrator) myContext.getSystemService(Context.VIBRATOR_SERVICE);
									v.vibrate(50L);	//in milliseconds
									resetZoom();     
								}
							}  
						}.start();
						break;	//case event was action down
					
					case MotionEvent.ACTION_MOVE:             
	
						Movable m = (Movable) p;
						double xMovePercent = (double) (coords[pID][1][0] - coords[pID][2][0])/dataAreaWidth;		//if new>old, you are dragging to the right
						double yMovePercent = (double) (coords[pID][2][1] - coords[pID][1][1])/dataAreaHeight;		//means you are trying to see the lower values
																							//y-axis is positive from top to bottom
																							//coords is is screen values and data Area is not, but it cancels out
						/*	
							How this works:
							if range was 0-10, 1.0 (100%) makes the range 10-20
							so, to move it some smaller amount, figure out what percentage of the graph the swipe occupied
							move it by that amount
						*/
						m.moveDomainAxes(xMovePercent, plotInfo, touchLocation); 
						m.moveRangeAxes( yMovePercent, plotInfo, touchLocation);  
						coords[pID][1][0]=coords[pID][2][0];
						coords[pID][1][1]=coords[pID][2][1];
		
						break;	//case event was action move
						
					case MotionEvent.ACTION_UP:
					
						timer.cancel();
						repaint();
						break;	//case event was action up
				}
				break;	//case count == 1
			
			case 2:
				if (timer != null)
					timer.cancel();
				//define the new coords here, the above doesnt work for this case
				for (int i=0; i<2; i++)
				{
					coords[i][2][0]=(int)event.getX(i);
					coords[i][2][1]=(int)event.getY(i);
				}
				//if there was a movement, do a zoom
				if (eventAction == MotionEvent.ACTION_MOVE)
				{
					//the midpoint of the pointers
					PointF anchor = new PointF((event.getX(0) + event.getX(1)) / 2, (event.getY(0) + event.getY(1)) / 2);
					//create the scale factor
					double startDistance = Math.sqrt(Math.pow(coords[0][1][0] - coords[1][1][0], 2 ) 
						+ Math.pow(coords[0][1][1] -coords[1][1][1], 2));
					double endDistance = Math.sqrt(Math.pow(coords[0][2][0] - coords[1][2][0],2) 
						+ Math.pow(coords[0][2][1] - coords[1][2][1],2));
					float scale = (float)(startDistance / endDistance);
					
					//make sure it doesn't get absurdly big or small
					if (currentScale * scale < MAX_SCALE && currentScale * scale > MIN_SCALE) 
					{                  
						currentScale *= scale;      
						Zoomable z = (Zoomable) p;
						z.zoomDomainAxes(scale, plotInfo, anchor, true);                  
						z.zoomRangeAxes(scale, plotInfo, anchor, true);              
					}
				}
				//update the coords
				for(int i=0; i<2; i++)
					for(int j=0; j<2; j++)
						coords[i][1][j] = coords[i][2][j];
				break;	//case count == 2
		}	//switch count
		// tell the system that we handled the event and no further processing is required   
		return true;  
	}
	
	public void resetZoom()
	{
		//get some data
		PlotRenderingInfo plotInfo = this.info.getPlotInfo();
		Zoomable z = (Zoomable) this.chart.getPlot();
		//reset original axis values
		z.zoomDomainAxes(0.0, plotInfo, new PointF(0,0));	//for this, the focus location doesn't matter
		z.zoomRangeAxes( 0.0, plotInfo, new PointF(0,0));
		currentScale = 1.0f;
	}	
	
// Setters
	public void setData(double[] yVals)			//later, overload this
	{
		double[][] xyVals = new double[2][];
		double[] countingNums = new double[yVals.length];
		for(int i=0; i<countingNums.length; i++)
			countingNums[i] = i;
		xyVals[0] = countingNums;
		xyVals[1] = yVals;
		setData(xyVals);
	}
	
	public void setData(double[][] xyVals)
	{
		// xyVals[0][] is x
		// xyVals[1][] is y
		data = new XYSeriesCollection();
		XYSeries series = new XYSeries("");
		for(int i=0; i<xyVals[0].length; i++)
			series.add(xyVals[0][i], xyVals[1][i]);
		data.addSeries(series);
		XYDataset dataset = data;
	}
		
	// public void setData(double[] yVals)
	// {
		// data = new XYSeriesCollection();
		// XYSeries series = new XYSeries("");
		// for(int i=0; i<yVals.length; i++)
			// series.add(i, yVals[i]);
		// data.addSeries(series);
		// XYDataset dataset = data;
	// }
		
// Getters	
	public Dimension getSize() 
	{          
		return this.size;      
	}
	
/*	private void printCoords()
	{
		String s = "";
		for(int i=0; i<2; i++)
		{
			s += "Pointer ";
			s += i;
			s += "\n";
			for (int j=0; j<3; j++)
			{
				for(int k=0; k<2; k++)
				{
					if(coords==null || coords[i]==null || coords[i][j]==null)
						s += "---";
					else
						s += String.format("%3d",coords[i][j][k]);
					s += " ";
				}
				s += "\n";
			}
		}
		Log.i(LOGTAG,s);
	}
*/
}    
