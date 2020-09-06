package extrasystemreloaded.util;

import java.util.ArrayList;
import java.util.List;

public class BaseAchievementUICore implements AchievementAnimationUnit{
	 protected int totalFrame;
	 protected int nowFrame = 0;
	 protected int x;
	 protected int y;
	 protected float width;
	 protected float height;
	 protected float alphaMult;
	 protected float factor;
	 protected List<Es_AchievementSprite>sprites = new ArrayList<Es_AchievementSprite>();
	public BaseAchievementUICore(int x,int y,float width,float height,float alphaMult,int totalFrame){
		this.totalFrame = totalFrame;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.alphaMult = alphaMult;
	}	
	@Override
	public void advance() {
		if (getExpired()) {
			sprites.clear();
			return;
		}
		for (Es_AchievementSprite sprite : sprites) {
			drawWithFrameFactor(sprite);
		}
		nowFrame+=1;
	}
	@Override
	public void bindAchievementSprite(Es_AchievementSprite sprite) {
		this.sprites.add(sprite);
	}
	
	@Override
	public boolean getExpired() {
		return nowFrame>=totalFrame;
	}
	@Override
	public void drawWithFrameFactor(Es_AchievementSprite sprite) {
		factor = (float)Math.sin(Math.PI*nowFrame/totalFrame);
		sprite.draw(x, y, width*factor, height*factor, alphaMult*factor);
	}
	@Override
	public boolean isStarted() {
		return nowFrame>0;
	}
}
