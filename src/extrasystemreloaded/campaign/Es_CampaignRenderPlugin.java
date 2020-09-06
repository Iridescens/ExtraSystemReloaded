package extrasystemreloaded.campaign;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel;
import extrasystemreloaded.util.AchievementAnimationUnit;
import extrasystemreloaded.util.AchievementData;
import extrasystemreloaded.util.AchievementInfo;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Es_CampaignRenderPlugin implements EveryFrameScript{
	private static final List<AchievementAnimationUnit>ACHIEVEMENT_ANIMATION_UNITSUNITS = new ArrayList<>();
	public Es_CampaignRenderPlugin(){
		ACHIEVEMENT_ANIMATION_UNITSUNITS.clear();
	}
	@Override
	public boolean isDone() {
		return false;
	}

	@Override
	public boolean runWhilePaused() {
		return true;
	}

	@Override
	public void advance(float amount) {
		if (ACHIEVEMENT_ANIMATION_UNITSUNITS.isEmpty()) {
			return;
		}
		if (Global.getCurrentState() == GameState.TITLE) {
			return;
		}
		final int width = (int) (Display.getWidth() * Display
				.getPixelScaleFactor()), height = (int) (Display.getHeight() * Display
				.getPixelScaleFactor());
		glPushAttrib(GL_ALL_ATTRIB_BITS);
		glViewport(0, 0, width, height);

		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(0, width, 0, height, -1, 1);
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glLoadIdentity();

		glDisable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glTranslatef(0.01f, 0.01f, 0);
		
		Iterator<AchievementAnimationUnit>iterator = ACHIEVEMENT_ANIMATION_UNITSUNITS.iterator();
		while (iterator.hasNext()) {
			AchievementAnimationUnit unit = (AchievementAnimationUnit) iterator.next();
			if (!unit.isStarted()) {
				Global.getSoundPlayer().playUISound("AchievementUnlock", 1f, 1f);
			}
			unit.advance();
			if (unit.getExpired()) {
				iterator.remove();
			}else {
				break;
			}
		}

		glDisable(GL_BLEND);
		glMatrixMode(GL_MODELVIEW);
		glPopMatrix();
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glPopAttrib();
	}
	/*
	 * Can be used only if the achievement's boolean is false for each achievement. Unlock
	 * the Achievement and spawn the animation.
	 * @param achievementID The id to record the achievement.
	 * @param unit A bunch of animation. 
	 */
	public static final void unlockAchievementAndDraw(String achievementID){
		if (!Global.getSector().getPersistentData().containsKey(achievementID)) {
			return;
		}
		AchievementInfo info = AchievementData.getAchievementInfo(achievementID);
		if (info.isDone()) {
			return;
		}
		AchievementAnimationUnit unit = info.getUnit();
		ACHIEVEMENT_ANIMATION_UNITSUNITS.add(unit);
		info.setDone(true);
		CampaignUIAPI ui = Global.getSector().getCampaignUI();
		String name = info.getName();
		ui.addMessage(new MessageIntel(info.getTooltip2(), Color.green));
		ui.addMessage(new MessageIntel(info.getTooltip1()));
		ui.addMessage(new MessageIntel("Achievement Unlocked: "+name,  Color.yellow));
	}
}
