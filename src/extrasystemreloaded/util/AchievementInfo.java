package extrasystemreloaded.util;

import com.fs.starfarer.api.Global;

public class AchievementInfo {
	private boolean isDone;
	private String tooltip1;
	private String tooltip2;
	private String name;
	private String id;
	private AchievementAnimationUnit unit;
	public boolean isDone() {
		return isDone;
	}
	public AchievementAnimationUnit getUnit() {
		return unit;
	}
	public void setUnit(AchievementAnimationUnit unit) {
		this.unit = unit;
	}
	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}
	public String getTooltip1() {
		return tooltip1;
	}
	public void setTooltip1(String tooltip1) {
		this.tooltip1 = tooltip1;
	}
	public String getTooltip2() {
		return tooltip2;
	}
	public void setTooltip2(String tooltip2) {
		this.tooltip2 = tooltip2;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setToggleID(boolean toggle) {
		Global.getSector().getPersistentData().put(id, toggle);
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isToggle() {
		return (boolean) Global.getSector().getPersistentData().get(id);
	}
	public void setToggle(boolean toggle) {
		Global.getSector().getPersistentData().put(id, toggle);
	}
}
