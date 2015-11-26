package cl.sebastialonso.jaidef;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by seba on 11/25/15.
 */
public class FontContainer {
    private Context mContext;
    private static Typeface lightFace;
    private static Typeface regularFace;
    private static Typeface boldFace;

    public FontContainer(Context context){
        this.mContext = context;
    }

    public Typeface lightComfortaa(){
        if (lightFace == null){
            lightFace = Typeface.createFromAsset(mContext.getAssets(), "Comfortaa-Light.ttf");
        }
        return lightFace;
    }
}
