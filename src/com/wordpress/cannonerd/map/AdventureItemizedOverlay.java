package com.wordpress.cannonerd.map;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import java.util.ArrayList;
import java.util.List;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

/**
 * Draws the marker for the player
 * 
 *
 * @param   gps positioning
 * 
 * @return marker for the player
 */

public class AdventureItemizedOverlay extends ItemizedOverlay {
    private List items;
	private Drawable marker;
 
	public AdventureItemizedOverlay(Drawable defaultMarker) {
		super(defaultMarker);
		items = new ArrayList();
		marker = defaultMarker;
	}
 
	@Override
	protected OverlayItem createItem(int index) {
		return (OverlayItem)items.get(index);
	}
 
	@Override
	public int size() {
		return items.size();
 
	}
 
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.google.android.maps.ItemizedOverlay#draw(android.graphics.Canvas,
	 * com.google.android.maps.MapView, boolean)
	 */
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		boundCenterBottom(marker);
 
	}
 
	public void addItem(OverlayItem item) {
		items.add(item);
		populate();
	}
    
    public void removeItem(OverlayItem item) {
        items.remove(item);
        populate();
    }
}
