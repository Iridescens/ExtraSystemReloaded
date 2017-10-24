package data.scripts.util;
/**
 * If getExpired == true, it won't impact the AchievementInfo, either.
 * @author Truth Originem
 *
 */
public interface AchievementAnimationUnit {
	/**
	 * For each frame.
	 */
	void advance();
	void drawWithFrameFactor(Es_AchievementSprite sprite);
	/**
	 * Bind a specific sprite({@link Es_AchievementSprite}) with this unit, 
	 * it will be removed while {getExpired} is true.
	 * @param sprite
	 */
	void bindAchievementSprite(Es_AchievementSprite sprite);
	boolean getExpired();
	boolean isStarted();
}
