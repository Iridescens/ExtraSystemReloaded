package data.scripts.achievements;

import data.scripts.util.BaseAchievementUICore;
import data.scripts.util.Es_AchievementSprite;

public class AchievementUIBaseAnimation extends BaseAchievementUICore{
	private static final float STEP[]={0.1f,0.95f};
	public AchievementUIBaseAnimation(int x, int y, float width, float height,
			float alphaMult, int totalFrame) {
		super(x, y, width, height, alphaMult, totalFrame);
	}
	@Override
	public void drawWithFrameFactor(Es_AchievementSprite sprite) {
		if ((float)nowFrame/totalFrame<=STEP[0]) {			
			factor = (float)Math.sin(Math.PI*nowFrame/totalFrame*0.5f/STEP[0]);
			sprite.draw(x, y+0.5f*height*(1f-factor), width*(4f-3f*factor), height*factor, alphaMult*factor);
		}else if ((float)nowFrame/totalFrame<=STEP[1]) {
			factor = 1f;
			sprite.draw(x, y, width*factor, height*factor, alphaMult*factor);
		}else {			
			factor = 1f-(1f/(1f-STEP[1]))*((float)nowFrame/totalFrame-STEP[1]);
			sprite.draw(x, y+0.5f*height*(1f-factor), width*(3f-2f*factor), height*factor, alphaMult*factor);
		}
	}
}
