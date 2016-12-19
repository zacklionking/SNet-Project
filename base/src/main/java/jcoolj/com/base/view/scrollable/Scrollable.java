package jcoolj.com.base.view.scrollable;

/**
 * Interface for providing common API for observable and scrollable widgets.
 */
public interface Scrollable {

    /**
     * Add a callback listener.
     *
     * @param listener Listener to add.
     * @since 1.7.0
     */
    void addScrollViewCallbacks(ScrollBehavior listener);

    /**
     * Remove a callback listener.
     *
     * @param listener Listener to remove.
     * @since 1.7.0
     */
    void removeScrollViewCallbacks(ScrollBehavior listener);

    /**
     * Clear callback listeners.
     *
     * @since 1.7.0
     */
    void clearScrollViewCallbacks();

    /**
     * Scroll vertically to the absolute Y.<br>
     * Implemented classes are expected to scroll to the exact Y pixels from the top,
     * but it depends on the type of the widget.
     *
     * @param y Vertical position to scroll to.
     */
    void scrollVerticallyTo(int y);

    /**
     * Return the current Y of the scrollable view.
     *
     * @return Current Y pixel.
     */
    int getCurrentScrollY();

}
