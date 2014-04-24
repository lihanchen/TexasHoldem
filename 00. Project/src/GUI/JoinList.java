package GUI;

import java.awt.Font;
import java.util.*;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;

public class JoinList {

	private static final int[] position = {0, 50};
	private static final int[] size = {1000, 500};
	
	private final TrueTypeFont headerFont =
			new TrueTypeFont(new java.awt.Font("Segoe UI Semibold", Font.PLAIN, 20), true);
	private final TrueTypeFont listFont =
			new TrueTypeFont(new java.awt.Font("Segoe UI Light", Font.PLAIN, 20), true);
	private final TrueTypeFont joinButtonsFont =
			new TrueTypeFont(new java.awt.Font("Segoe UI Light", Font.PLAIN, 12), true);
	private final TrueTypeFont pageLabelFont =
			new TrueTypeFont(new java.awt.Font("Segoe UI", Font.PLAIN, 16), true);
	private final TrueTypeFont navButtonsFont =
			new TrueTypeFont(new java.awt.Font("Segoe UI Light", Font.PLAIN, 20), true);
	
	
	private final int listYOffset = 50;	// vertical space between top of list and top of frame
	private final int rowHeight = 36;
	private final int numRowsDisplayed = 8;
	private final int indexColumnWidth = 50;
	
	private final int[] pageLabelOffset = {500, 412};
	
	private final int[] prevButtonOffset = {330, 400};
	private final int[] nextButtonOffset = {630, 400};
	private final int[] refreshButtonOffset = {80, 400};
	private final int[] cancelButtonOffset = {770, 400};
	
	private final int[] loadingAnimationOffset = {500, 220};
	
	private String[] columnNames;
	private int[] columnWidths;		// in pixels
	
	
	private List<String[]> rowsData;
	private List<Boolean> rowsJoinable;
	
	
	private Button[] joinButtons;
	private Button prevButton;
	private Button nextButton;
	protected Button refreshButton;
	private Button cancelButton;
	
	private boolean visible;
	
	private Animation loadingAnimation;
	private boolean isLoading;
	
	
	// meta-info
	private int currentPage;
	private int rowWidth;	// including join button
	
	
	public static interface IndexedComponentListener {
		public void componentActivated(AbstractComponent arg0, int index);
	}
	
	
	public JoinList(GUIContext container, boolean initialVisible, Animation loadingAnimation,
			String[] columnNames, int[] columnWidths,
			final IndexedComponentListener joinListener, ComponentListener refreshListener,
			ComponentListener cancelListener) throws SlickException{
		
		this.visible = initialVisible;
		this.loadingAnimation = loadingAnimation;
		isLoading = false;
		this.columnNames = columnNames;
		this.columnWidths = columnWidths;
		rowsData = new ArrayList<String[]>();
		rowsJoinable = new ArrayList<Boolean>();
		currentPage = 0;
				
		
		Image joinButtonNormalImage = new Image(GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_join.png");
		Image joinButtonPressedImage = new Image(GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_join_down.png");
		
		rowWidth = indexColumnWidth + joinButtonNormalImage.getWidth();
		for (int i=0; i<columnWidths.length; ++i)
			rowWidth += columnWidths[i];
		
		joinButtons = new Button[numRowsDisplayed];
		int[] buttonPosition = new int[2];
		buttonPosition[0] = position[0] + (rowWidth+size[0])/2 - joinButtonNormalImage.getWidth();
		buttonPosition[1] = position[1] + listYOffset + rowHeight
				+(rowHeight-joinButtonNormalImage.getHeight())/2;
		
		for (int i=0; i<numRowsDisplayed; ++i) {
			
			joinButtons[i] = new Button(container, joinButtonNormalImage,
					joinButtonPressedImage, buttonPosition[0], buttonPosition[1],
					new ComponentListener() {
						@Override
						public void componentActivated(AbstractComponent source) {
							int i;
							for (i=0; i<numRowsDisplayed; ++i) {
								if (source==joinButtons[i])
									break;
							}
							joinListener.componentActivated(source, currentPage*numRowsDisplayed+i);
						}
					});
			buttonPosition[1] += rowHeight;
		}
		
		Image navButtonNormalImage = new Image(GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_nav.png");
		Image navButtonPressedImage = new Image(GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_nav_down.png");
		prevButton = new Button(container, navButtonNormalImage, navButtonPressedImage,
				position[0]+prevButtonOffset[0], position[1]+prevButtonOffset[1],
				new ComponentListener() {
					@Override
					public void componentActivated(AbstractComponent arg0) {
						currentPage--;
						updatePrevNextButtons();
						updateJoinButtons();
					}
				});
	
		nextButton = new Button(container, navButtonNormalImage, navButtonPressedImage,
				position[0]+nextButtonOffset[0], position[1]+nextButtonOffset[1],
				new ComponentListener() {
					@Override
					public void componentActivated(AbstractComponent arg0) {
						currentPage++;
						updatePrevNextButtons();
						updateJoinButtons();
					}
				});
		
		
		refreshButton = new Button(container, GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_blue.png",
				GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_blue_down.png",
				position[0]+refreshButtonOffset[0], position[1]+refreshButtonOffset[1],
				refreshListener);
		
		cancelButton = new Button(container, GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_red.png",
				GUI.RESOURCES_PATH+GUI.BUTTONS_FOLDER+"button_red_down.png",
				position[0]+cancelButtonOffset[0], position[1]+cancelButtonOffset[1],
				cancelListener);
	}
	
	
	private void setButtonsEnable(boolean enable) {
		if (!enable) {
			for (Button b : joinButtons)
				b.setEnable(false);
			prevButton.setEnable(false);
			nextButton.setEnable(false);
		} else {
			updateJoinButtons();
			updatePrevNextButtons();
		}
		refreshButton.setEnable(enable);
		cancelButton.setEnable(enable);
	}
	
	
	
	private void updatePrevNextButtons() {
		int numPages;
		if (rowsData.size()==0) {
			numPages = 1;
		} else {
			numPages = (rowsData.size()-1)/numRowsDisplayed + 1;
		}
		
		if (currentPage < 0)
			currentPage = 0;
		
		if (currentPage >= numPages)
			currentPage = numPages-1;
		
		prevButton.setEnable(currentPage > 0);
		nextButton.setEnable(currentPage < numPages-1);
	}
	
	private void updateJoinButtons() {
		int row = currentPage*numRowsDisplayed;
		for (int i=0; i<numRowsDisplayed; ++i) {
			if (row < rowsData.size()) {
				boolean joinable = rowsJoinable.get(row).booleanValue();
				joinButtons[i].setEnable(joinable);
			} else {
				// disable buttons for rows that don't exist
				joinButtons[i].setEnable(false);
			}
			row++;
		}
	}
	
	
	public void setLoading(boolean loading) {
		isLoading = loading;
		setButtonsEnable(!loading);
	}
	
	public boolean isLoading() {
		return isLoading;
	}
		
	public void setRowsData(List<String[]> rowsData) {
		this.rowsData = rowsData;
		updatePrevNextButtons();
	}
	
	public void setRowsJoinable(List<Boolean> rowsJoinable) {
		this.rowsJoinable = rowsJoinable;
		updateJoinButtons();
	}
	
	public String[] getRow(int i) {
		return rowsData.get(i);
	}
	
	
	public void setVisible(boolean visible) {
		this.visible = visible;
		setButtonsEnable(visible);
	}
	
	
	public void render(GUIContext container, Graphics g) {
	
		if (visible) {
			
			g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.85f));
			g.fillRect(position[0], position[1], size[0], size[1]);
			
			
			int textXLeft = position[0] + (size[0]-rowWidth)/2;
			int textYCenter = position[1] + listYOffset + rowHeight/2;
			int textX;
			
			// draw column headers
			textX = textXLeft + indexColumnWidth;
			for (int i=0; i<columnNames.length; ++i) {
				GUI.drawStringLeftCenter(g, headerFont, Color.white,
						columnNames[i], textX, textYCenter);
				textX += columnWidths[i];
			}
			
			
			if (isLoading) {
				
				loadingAnimation.draw(position[0]+loadingAnimationOffset[0]-loadingAnimation.getWidth()/2,
						position[1]+loadingAnimationOffset[1]-loadingAnimation.getHeight()/2);
				
			} else {
				// draw each row
				if (!rowsData.isEmpty()) {
					textYCenter += rowHeight;
					for (int i=currentPage*numRowsDisplayed;
							i < Math.min((currentPage+1)*numRowsDisplayed, rowsData.size());
							++i) {
						textX = textXLeft;
						GUI.drawStringLeftCenter(g, headerFont, Color.white, Integer.toString(i+1), textX, textYCenter);
						textX += indexColumnWidth;
						
						String[] rowData = rowsData.get(i);
						for (int j=0; j<columnNames.length; ++j) {
							GUI.drawStringLeftCenter(g, listFont, Color.white, rowData[j], textX, textYCenter);
							textX += columnWidths[j];
						}
						joinButtons[i%numRowsDisplayed].render(container, g, joinButtonsFont, Color.white, "Join");
						textYCenter += rowHeight;
					}
				}
				
				// draw page label
				String labelString;
				if (rowsData.size()==0) {
					labelString = "No games found.";
				} else {
					int firstIndex = currentPage * numRowsDisplayed + 1;
					int lastIndex = Math.min(firstIndex+numRowsDisplayed-1, rowsData.size());
					labelString = firstIndex+" to "+lastIndex+
							" of "+rowsData.size()+" games found";
				}
				GUI.drawStringCenter(g, pageLabelFont, Color.white, 
						labelString, position[0]+pageLabelOffset[0], position[1]+pageLabelOffset[1]);
				
			}
			
			// draw prev, next, refresh buttons
			prevButton.render(container, g, navButtonsFont, Color.white, "<");
			nextButton.render(container, g, navButtonsFont, Color.white, ">");
			refreshButton.render(container, g, navButtonsFont, Color.white, "Refresh");
			cancelButton.render(container, g, navButtonsFont, Color.white, "Cancel");
		}
	}
}
