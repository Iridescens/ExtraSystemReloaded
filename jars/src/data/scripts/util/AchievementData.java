package data.scripts.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;

import data.scripts.achievements.AchievementUIBaseAnimation;


public class AchievementData {
	private static final String ACHIEVEMENT_ID = "AchievementData";
	private static final String CSV_PATH ="data/config/achievements.csv"; 
	private static final int TOTAL_FRAME = 100;
	private Map<String,AchievementInfo>achievementMaps;
	private Map<String, Object>CustomData;
	public AchievementData(){
		achievementMaps = new HashMap<>();
		CustomData = new HashMap<>();
		try {
			loadAndCheck();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public void loadAndCheck() throws IOException, JSONException{
		JSONArray csvarray = Global.getSettings().loadCSV(CSV_PATH);
		for (int i = 0; i < csvarray.length(); i++) {
			final JSONObject entry = csvarray.getJSONObject(i);
			String id = entry.getString("id");
			if (achievementMaps.containsKey(id)) {
				AchievementInfo info = achievementMaps.get(id);
				String path = entry.getString("iconPath");
				Global.getSettings().loadTexture(path);
				AchievementAnimationUnit unit = spawnBaseUnit();
				SpriteAPI sprite = Global.getSettings().getSprite(path);
				Es_AchievementSprite asprite = new Es_AchievementSprite(sprite);
				unit.bindAchievementSprite(asprite);
				info.setUnit(unit);
				continue;
			}else {
				AchievementInfo info = new AchievementInfo();
				getAchievementMaps().put(id,info);
				info.setId(id);
				info.setName(entry.getString("name"));
				info.setToggleID(false);//The Connection
				info.setTooltip1(entry.getString("tooltip1"));
				info.setTooltip2(entry.getString("tooltip2"));
				//sprite
				String path = entry.getString("iconPath");
				Global.getSettings().loadTexture(path);
				AchievementAnimationUnit unit = spawnBaseUnit();
				SpriteAPI sprite = Global.getSettings().getSprite(path);
				Es_AchievementSprite asprite = new Es_AchievementSprite(sprite);
				unit.bindAchievementSprite(asprite);
				//
				info.setUnit(unit);
				info.setDone(false);
			}
		}
	}
	private static final AchievementAnimationUnit spawnBaseUnit(){
		Vector2f center = new Vector2f(Display.getWidth() / 2,Display.getHeight() / 7 * 6);
		SpriteAPI spriteAPI = Global.getSettings().getSprite("graphics/achievements/Es_Achievement_core.png");
		Es_AchievementSprite sprite = new Es_AchievementSprite(spriteAPI);
		AchievementUIBaseAnimation core = new AchievementUIBaseAnimation((int) center.x, (int) center.y, spriteAPI.getWidth(),
		spriteAPI.getHeight(), 1f, TOTAL_FRAME);
		core.bindAchievementSprite(sprite);
		return core;
	}
	/**
	 * Return the info, including if it is unlocked.
	 * @param achievementID
	 * @return
	 */
	public static final AchievementInfo getAchievementInfo(String achievementID){
		final AchievementData data = (AchievementData) Global.getSector().getPersistentData().get(ACHIEVEMENT_ID);
		final AchievementInfo info = data.getAchievementMaps().get(achievementID);
		return info;
	}
	public Map<String,AchievementInfo> getAchievementMaps(){
		return this.achievementMaps;
	}
	public Map<String, Object> getCustomData(){
		return this.CustomData;
	}
}
