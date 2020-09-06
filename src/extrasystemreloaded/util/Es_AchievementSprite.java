package extrasystemreloaded.util;

import com.fs.starfarer.api.graphics.SpriteAPI;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class Es_AchievementSprite {
	private final SpriteAPI sprite;
	private final int blendSrc, blendDest;
	private final float textureWidth, textureHeight, hScale;
	public Es_AchievementSprite(SpriteAPI sprite){
		this(sprite, GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}
	public Es_AchievementSprite(SpriteAPI sprite,int blendSrc,int blendDest){
		 this.sprite = sprite;
	     this.blendSrc = blendSrc;
	     this.blendDest = blendDest;
	     this.textureWidth = sprite.getTextureWidth();
	     this.textureHeight = sprite.getTextureHeight();
	     this.hScale = sprite.getWidth() / sprite.getHeight();
	}
	public void draw(float x,float y,float width,float height,float alphaMult){
//		Global.getSector().getCampaignUI().addMessage("now");
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(blendSrc, blendDest);
		sprite.bindTexture();
		glPushMatrix();
		glTranslatef(x, y, 0f);
		glRotatef(0f, 0f, 0f, 1f);
		glTranslatef(-textureWidth * 0.5f-width/2, -textureHeight * 0.5f, 0f);
//		glColor4ub((byte)Color.white.getRed() , (byte)Color.white.getGreen(), (byte)Color.white.getBlue(), (byte)Color.white.getAlpha());
		glColorRGBA(Color.white,alphaMult);
		glBegin(GL_QUADS);
		glTexCoord2f(0f, 0f);
		glVertex2f(0f, 0f);
		glTexCoord2f(textureWidth, 0f);
		glVertex2f(width, 0f);
		glTexCoord2f(textureWidth, textureHeight);
		glVertex2f(width, height);
		glTexCoord2f(0f, textureHeight);
		glVertex2f(0f,  height);
		glEnd();
		glPopMatrix();
		glDisable(GL_BLEND);
		glDisable(GL_TEXTURE_2D);
	}
	private static void glColorRGBA(Color color,float alphaMult){
		glColor4ub((byte)color.getRed() , (byte)color.getGreen(), (byte)color.getBlue(), (byte)(color.getAlpha()*alphaMult));
	}
}
