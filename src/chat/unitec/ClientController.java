package chat.unitec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class ClientController implements Runnable {
	
	Socket client;
	DataInputStream dis;
	DataOutputStream dos;
	ObservableList<String> names = FXCollections.observableArrayList();
	Color strokeColor = Color.BLACK;
	String figure = "pen";
	double initialX;
	double initialY;
	
	@FXML
	private ListView<String> lvUsers;
	
	@FXML
	private TextArea taChat;
	
	@FXML
	private TextField tfSend;

    @FXML
    private Button btnSend;
    
    @FXML
    void btnSendOnActionHandler(ActionEvent event) {
		try {
			if(tfSend.getText().charAt(0) == '@') {
				dos.writeInt(ServerConstants.PRIVATE_MESSAGE);
			} else {
				dos.writeInt(ServerConstants.CHAT_MESSAGE);
			}
			dos.writeUTF(tfSend.getText());
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		tfSend.clear();
    }
    
    @FXML
    void lvUsersOnMouseClickedHandler() {
    	String name = lvUsers.getSelectionModel().getSelectedItem();
    	tfSend.setText("@" + name + " ");
    }  
    
    @FXML
    private Button btnBlack;
    
    @FXML
    void btnBlackOnActionHandler(ActionEvent event) {
    		strokeColor = Color.BLACK;
    }
    
    @FXML
    private Button btnWhite;
    
    @FXML
    void btnWhiteOnActionHandler(ActionEvent event) {
    		strokeColor = Color.WHITE;
    }
    
    @FXML
    private Button btnRed;
    
    @FXML
    void btnRedOnActionHandler(ActionEvent event) {
    		strokeColor = Color.RED;
    }
    
    @FXML
    private Button btnGreen;
    
    @FXML
    void btnGreenOnActionHandler(ActionEvent event) {
    		strokeColor = Color.GREEN;
    }
    
    @FXML
    private Button btnYellow;
    
    @FXML
    void btnYellowOnActionHandler(ActionEvent event) {
    		strokeColor = Color.YELLOW;
    }
    
    @FXML
    private Button btnBlue;
    
    @FXML
    void btnBlueOnActionHandler(ActionEvent event) {
    		strokeColor = Color.BLUE;
    }
    
    @FXML
    private Button btnPen;
    
    @FXML
    void btnPenOnActionHandler(ActionEvent event) {
    		figure = "pen";
    }
    
    @FXML
    private Button btnOval;
    
    @FXML
    void btnOvalOnActionHandler(ActionEvent event) {
    		figure = "oval";
    }
    
    @FXML
    private Button btnLine;
    
    @FXML
    void btnLineOnActionHandler(ActionEvent event) {
    		figure = "line";
    }
    
    @FXML
    private Button btnRectangle;
    
    @FXML
    void btnRectangleOnActionHandler(ActionEvent event) {
    		figure = "rectangle";
    }
    
    @FXML
    private Canvas myCanvas;
    
    @FXML
    private Pane paneCanvas;
    
	@FXML
    protected void initialize()
	{
		final GraphicsContext graphicsContext = myCanvas.getGraphicsContext2D();
		Ellipse ellipse = new Ellipse();
		Rectangle rectangle = new Rectangle();
		Line line = new Line();

		
    		initDraw(graphicsContext);
    	
	    	myCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
	    		new EventHandler<MouseEvent>(){
	
	            @Override
	            public void handle(MouseEvent event)
	            {
		            	initialX = event.getX();
		            	initialY = event.getY();
		            	graphicsContext.setStroke(strokeColor);
		            	if(figure.equals("pen"))
		            	{
			            	graphicsContext.beginPath();
			            	graphicsContext.moveTo(initialX, initialY);			            	
			            	graphicsContext.stroke();
			        		try 
			        		{
			        			dos.writeInt(ServerConstants.CANVAS_BROADCAST);
			        			dos.writeUTF("pressed%"+initialX+"%"+initialY+"%"+strokeColor+"%"+figure+"%"+event.getX()+"%"+event.getY());
			        			dos.flush();
			        		}
			        		catch (IOException e)
			        		{
			        			e.printStackTrace();
			        		}
		        		}
		            	else if(figure.equals("oval"))
		            	{
		            		ellipse.setStrokeWidth(1.0);
		            		ellipse.setFill(Color.TRANSPARENT);
		            		ellipse.setStroke(strokeColor);
		            	    ellipse.setCenterX(initialX);
		            	    ellipse.setCenterY(initialY);
		            	    ellipse.setRadiusX(0);
		            	    ellipse.setRadiusY(0);
		            	    paneCanvas.getChildren().add(ellipse);
		            	}
		            	else if(figure.equals("line"))
		            	{
		            		line.setStrokeWidth(1.0);
		            		line.setStroke(strokeColor);
		            		line.setStartX(initialX);
		            		line.setStartY(initialY);
		            	    line.setEndX(initialX);
		            	    line.setEndY(initialY);
		            	    paneCanvas.getChildren().add(line);
		            	}
		            	else if(figure.equals("rectangle"))
		            	{
		            		rectangle.setStrokeWidth(1.0);
		            		rectangle.setFill(Color.TRANSPARENT);
		            		rectangle.setStroke(strokeColor);
		            		rectangle.setX(initialX);
		            		rectangle.setY(initialY);
		            	    rectangle.setWidth(0);
		            	    rectangle.setHeight(0);
		            	    paneCanvas.getChildren().add(rectangle);
		            	}
		            	
	//	            	else if(action.equals("zoom"))
	//	            	{
	//		        		myCanvas.setScaleX(2);
	//		        		myCanvas.setScaleY(2);
	//		        	}
	//	            	else if(action.equals("zoom-out"))
	//	            	{
	//		        		myCanvas.setScaleX(1);
	//		        		myCanvas.setScaleY(1);
	//		        	}
	//	            	else if(action.equals("rotate"))
	//	            	{
	//		        		
	//		        	}
	            }
	    		});
	    	
	    	myCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, 
	    		new EventHandler<MouseEvent>(){
	
	            @Override
	            public void handle(MouseEvent event)
	            {
		           	if(figure.equals("pen"))
		           	{
		            		graphicsContext.lineTo(event.getX(), event.getY());
				        graphicsContext.stroke();
			        		try 
			        		{
			        			dos.writeInt(ServerConstants.CANVAS_BROADCAST);
			        			dos.writeUTF("dragged%"+initialX+"%"+initialY+"%"+strokeColor+"%"+figure+"%"+event.getX()+"%"+event.getY());
			        			dos.flush();
			        		}
			        		catch (IOException e)
			        		{
			        			e.printStackTrace();
			        		}
		           	} 
		           	else if(figure.equals("oval"))
		           	{
		           		ellipse.setCenterX((event.getX() + initialX) / 2);
		           		ellipse.setCenterY((event.getY() + initialY) / 2);
		           		ellipse.setRadiusX(Math.abs((event.getX() - initialX) / 2));
		           		ellipse.setRadiusY(Math.abs((event.getY() - initialY) / 2));	
		           	}
		            	else if(figure.equals("line"))
		            	{
		            	    line.setEndX(event.getX());
		            	    line.setEndY(event.getY());
		            	}
		           	else if(figure.equals("rectangle"))
		           	{
		           		rectangle.setX(Math.min(initialX, event.getX()));
		           		rectangle.setY(Math.min(initialY, event.getY()));
		           		rectangle.setWidth(Math.abs(event.getX() - initialX));
		           		rectangle.setHeight(Math.abs(event.getY() - initialY));
		           	}
	            }
	        });
	
	    	myCanvas.addEventHandler(MouseEvent.MOUSE_RELEASED, 
	                new EventHandler<MouseEvent>(){
	
	            @Override
	            public void handle(MouseEvent event)
	            {
	            	 double width = Math.abs(event.getX() - initialX);
	            	 double height = Math.abs(event.getY() - initialY);
		            	
		            	if(figure.equals("oval"))
		            	{
		            		paneCanvas.getChildren().remove(ellipse);
			            	graphicsContext.strokeOval(Math.min(initialX, event.getX()), Math.min(initialY, event.getY()), width, height);
			        		try {
			        			dos.writeInt(ServerConstants.CANVAS_BROADCAST);
			        			dos.writeUTF("released%"+initialX+"%"+initialY+"%"+strokeColor+"%"+figure+"%"+event.getX()+"%"+event.getY());
			        			dos.flush();
			        		}
			        		catch (IOException e)
			        		{
			        			e.printStackTrace();
			        		}
		            	}
		            	else if(figure.equals("line"))
		            	{
		            		paneCanvas.getChildren().remove(line);
		            		graphicsContext.strokeLine(initialX, initialY, event.getX(), event.getY());
			        		try {
			        			dos.writeInt(ServerConstants.CANVAS_BROADCAST);
			        			dos.writeUTF("released%"+initialX+"%"+initialY+"%"+strokeColor+"%"+figure+"%"+event.getX()+"%"+event.getY());
			        			dos.flush();
			        		}
			        		catch (IOException e)
			        		{
			        			e.printStackTrace();
			        		}
		            	}
		            	else if(figure.equals("rectangle"))
		            	{
		            		paneCanvas.getChildren().remove(rectangle);
		            		graphicsContext.strokeRect(Math.min(initialX, event.getX()), Math.min(initialY, event.getY()), width , height);
			        		try {
			        			dos.writeInt(ServerConstants.CANVAS_BROADCAST);
			        			dos.writeUTF("released%"+initialX+"%"+initialY+"%"+strokeColor+"%"+figure+"%"+event.getX()+"%"+event.getY());
			        			dos.flush();
			        		}
			        		catch (IOException e)
			        		{
			        			e.printStackTrace();
			        		}
		            	}
	            }
	        });
    }
    
    public ClientController() {
		try {
			client = new Socket("localhost", 5000);
			dis = new DataInputStream(client.getInputStream());
			dos = new DataOutputStream(client.getOutputStream());
			
			dos.writeInt(ServerConstants.REGISTER_CLIENT);
			dos.writeUTF(ClientMain.getName());
			dos.flush();
			
			Thread clientThread = new Thread(this);
			clientThread.start();
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
    }	

	@Override
	public void run() {		
		while(true)
		{
			try {
				int messageType = dis.readInt();

				switch(messageType)
				{
					case ServerConstants.CHAT_BROADCAST:
						taChat.appendText(dis.readUTF()+"\n");
						break;
					case ServerConstants.REGISTER_BROADCAST:
						String name = dis.readUTF();
						Platform.runLater(new Runnable() {
							@Override public void run() {
								if(!names.contains(name))
								{
									taChat.appendText(name + " has joined the chat"+"\n");
									names.add(name);
								}
								lvUsers.setItems(names);
							}
						});
						break;
					case ServerConstants.CANVAS_BROADCAST:
						GraphicsContext graphicsContext = myCanvas.getGraphicsContext2D();

						String[] canvasChange = dis.readUTF().split("%");
						String recAction = canvasChange[0];
						Double recInitialX = Double.parseDouble(canvasChange[1]);
						Double recInitialY = Double.parseDouble(canvasChange[2]);
						Color recColor = Color.valueOf(canvasChange[3]);
						String recFigure = canvasChange[4];
						Double recFinalX = Double.parseDouble(canvasChange[5]);
						Double recFinalY = Double.parseDouble(canvasChange[6]);
						
    	    					graphicsContext.setStroke(recColor);
		            		if(recAction.equals("pressed"))
		            		{
		            			if(recFigure.equals("pen"))
		    		            	{
		    			            	graphicsContext.beginPath();
		    			            	graphicsContext.moveTo(recInitialX, recInitialY);			            	
		    			            	graphicsContext.stroke();
		    		        		}

		            		}
		            		else if(recAction.equals("dragged"))
		            		{
		            			if(recFigure.equals("pen"))
		    		           	{
		    		            		graphicsContext.lineTo(recFinalX, recFinalY);
		    				        graphicsContext.stroke();
		    		           	} 
		            		}
		            		else if(recAction.equals("released"))
		            		{
		            			double width = Math.abs(recFinalX - recInitialX);
		   	            	 	double height = Math.abs(recFinalY - recInitialY);
		   		            	
		   		            	if(recFigure.equals("oval"))
		   		            	{
		   			            	graphicsContext.strokeOval(Math.min(recInitialX, recFinalX), Math.min(recInitialY, recFinalY), width, height);
		   		            	}
		   		            	else if(recFigure.equals("line"))
		   		            	{
		   		            		graphicsContext.strokeLine(recInitialX, recInitialY, recFinalX, recFinalY);
		   		            	}
		   		            	else if(recFigure.equals("rectangle"))
		   		            	{
		   		            		graphicsContext.strokeRect(Math.min(recInitialX, recFinalX), Math.min(recInitialY, recFinalY), width , height);
		   		            	}
		            		}
		            		break;
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
    private void initDraw(GraphicsContext gc){
//        double canvasWidth = gc.getCanvas().getWidth();
//        double canvasHeight = gc.getCanvas().getHeight();
        
//        gc.setFill(Color.LIGHTGRAY);
//        gc.setStroke(Color.BLACK);
//        gc.setLineWidth(5);
//
//        gc.fill();
//        gc.strokeRect(
//                0,              //x of the upper left corner
//                0,              //y of the upper left corner
//                canvasWidth,    //width of the rectangle
//                canvasHeight);  //height of the rectangle
        
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);        
    }
}
