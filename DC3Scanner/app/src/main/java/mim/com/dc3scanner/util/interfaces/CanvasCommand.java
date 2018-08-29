package mim.com.dc3scanner.util.interfaces;

import java.util.List;

public interface CanvasCommand {
    public void setParentHeight(int height);
    public void moveLeft();

    public void moveRight();

    public void moveTop();

    public void moveBottom();

    public void placeMarker();

    public List<Integer> markPos();
}
