package View;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

public class ShakeTransition extends CachedTimelineTransition {

	private final static int x = 20;
	public ShakeTransition(final Node node) {
		super(node,
				new Timeline(new KeyFrame(Duration.millis(0), new KeyValue(node.translateXProperty(), 0, WEB_EASE)),
						new KeyFrame(Duration.millis(50), new KeyValue(node.translateXProperty(), -x, WEB_EASE)),
						new KeyFrame(Duration.millis(150), new KeyValue(node.translateXProperty(), x, WEB_EASE)),
						new KeyFrame(Duration.millis(200), new KeyValue(node.translateXProperty(), -x, WEB_EASE)),
						new KeyFrame(Duration.millis(350), new KeyValue(node.translateXProperty(), x, WEB_EASE)),
						new KeyFrame(Duration.millis(450), new KeyValue(node.translateXProperty(), -x, WEB_EASE)),
						new KeyFrame(Duration.millis(550), new KeyValue(node.translateXProperty(), 0, WEB_EASE))
						
						));
		setCycleDuration(Duration.seconds(1));
		setDelay(Duration.seconds(0.2));
	}
}