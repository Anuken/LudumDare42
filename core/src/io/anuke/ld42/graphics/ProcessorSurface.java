package io.anuke.ld42.graphics;

import com.badlogic.gdx.Gdx;
import com.bitfire.postprocessing.PostProcessor;
import com.bitfire.postprocessing.effects.Bloom;
import com.bitfire.postprocessing.effects.MotionBlur;
import io.anuke.ucore.graphics.CustomSurface;

public class ProcessorSurface extends CustomSurface{
    private PostProcessor processor;
    private Bloom bloom;

    public ProcessorSurface() {

    }

    private void addEffects(){
        if(bloom != null){
            processor.removeEffect(bloom);
        }

        bloom = new Bloom((int)(Gdx.graphics.getWidth() / 4f), (int)(Gdx.graphics.getHeight() / 4f));
        processor.addEffect(bloom);
        MotionBlur blur = new MotionBlur();
        blur.setBlurOpacity(0.99f);
        processor.addEffect(blur);
    }

    @Override
    public void begin(boolean clear, boolean viewport){
        if(clear){
            processor.capture();
        }else{
            processor.captureNoClear();
        }
    }

    @Override
    public void end(boolean render){
        processor.render();
    }

    @Override
    public void onResize(){
        if(processor != null){
            processor.dispose();
        }
        processor = new PostProcessor(false, true, true);

        addEffects();
    }

    @Override
    public void dispose(){
        processor.dispose();
    }

}